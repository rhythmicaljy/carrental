# 과제 - 렌트카 서비스

# Table of contents

- [과제 - 렌트카 서비스](#---)
  - [서비스 시나리오](#서비스-시나리오)
  - [분석/설계](#분석설계)
  - [구현:](#구현-)
    - [DDD 의 적용](#ddd-의-적용)
    - [폴리글랏 퍼시스턴스](#폴리글랏-퍼시스턴스)
    - [폴리글랏 프로그래밍](#폴리글랏-프로그래밍)
    - [동기식 호출 과 Fallback 처리](#동기식-호출-과-Fallback-처리)
    - [비동기식 호출 과 Eventual Consistency](#비동기식-호출-과-Eventual-Consistency)
  - [운영](#운영)
    - [CI/CD 설정](#cicd설정)
    - [동기식 호출 / 서킷 브레이킹 / 장애격리](#동기식-호출-서킷-브레이킹-장애격리)
    - [오토스케일 아웃](#오토스케일-아웃)
    - [무정지 재배포](#무정지-재배포)
  - [신규 개발 조직의 추가](#신규-개발-조직의-추가)

# 서비스 시나리오

기능적 요구사항
1. 관리자는 회사가 보유하고 있는 렌트 차량을 등록 한다
1. 고객이 차량을 선택해 예약하면서 차량 결제를 진행 한다
1. 결제가 성공하면 차량이 대여된다
1. 차량 대여 시 차량 상태를 "대여가능" 상태를 "대여중"으로 변경 한다
1. 대여중인 차량은 차량 원부 관리 영역에서 "대여중"으로 변경한다
1. 고객이 예약을 취소 시 결제 취소도 동시에 실행 된다
1. 결제가 취소되면 차량 대여도 취소된다
1. 차량 대여 취소 시 차량 상태를 "대여 중" 상태를 "대여 가능"으로 변경 한다
1. 취소된 차량은 차량 원부 관리 영역에서 "대여가능"으로 변경한다
1. 관리자는 회사가 보유하고 있는 렌트 차량을 등록 가능하다
1. 고객의 개인 정보와 차량 대여/취소 이력 정보를 "My Page"에서 확인 가능하다


비기능적 요구사항
1. 트랜잭션
    1. 결제가 되지 않은 예약건은 차량 대여가 성립하지 않는다  Sync 호출 
1. 장애격리
    1. 관리자 차량관리 기능이 수행되지 않더라도 예약은 항상 받을 수 있어야 한다  Async (event-driven), Eventual Consistency
    1. 결제시스템이 과중되면 사용자를 잠시동안 받지 않고 결제를 잠시후에 하도록 유도한다  Circuit breaker, fallback
1. 성능
    1. 고객이 대여 현황을 예약 시스템에서 항상 확인 할 수 있어야 한다.(CQRS)  CQRS
    1. 결제, 대여/반납 정보가 변경 될 때 마다 상태를 확인 가능하여야 한다  Event driven


# 분석/설계

## Event Storming 결과
* MSAEz 로 모델링한 이벤트스토밍 결과:  http://msaez.io/#/storming/YwOQgvEmhERptfuUxzgz2bPRQ5L2/share/e704bbe175813f193c92819f141a16a3/-MDn_Bksfd3DwJHUdzY

### 이벤트 도출
![image](https://lh4.googleusercontent.com/tKoqblyQq9-QlG0OBciZ4BXh59p59lMsx-Jbs907Xr9J_tpr4bvcTHxinFFf6TpPWG2utG85tsxDWAkOmxCxG9-rwc6D4UhhvuA0Jck1N5Nv71fRPoWMD4dpHLClkmy6lHPc1oeb)

### 어그리게잇으로 묶기
![image](https://lh6.googleusercontent.com/8ZV500Uxo0pRZxt7VY0xZr0shoECh6_5tFelREC_9X4ypLL4T2GpIdlItgXr3XHciv1lehkyCYHKUTZfMzH8vQv4p3IPUnzkRHiEpjds806-CvBXq_h1tQUJQpR8HsjybRHLHNAa)

    - 차량 예약, 결제, 차량 렌탈, 차량관리 등은 그와 연결된 command 와 event 들에 의하여 트랜잭션이 유지되어야 하는 단위로 그들 끼리 묶어줌

### 바운디드 컨텍스트로 묶기

![image](https://lh5.googleusercontent.com/wVaXQ9KZlWgjqOQ4dcGuqyAA-yfzEgE5XT-9OuMViNr4C1Vg4L_JGDKTdlNEFKEQeAg4rGZWRoWcgBsl4o4YFUrqJlWIxqrKbBKhnOfk0Slt4c85joaj7UILsatnKAe7ymHzFiq5)

    - 도메인 서열 분리 
        - Core Domain:  areservation , rental 은 핵심 코어 서비스로 연간 Up-time SLA 수준을 99.999% 목표, 배포주기는 reservation 의 경우 1주일 1회 미만, rental 의 경우 1개월 1회 미만
        - Supporting Domain:  management 은 차량 관리 관련 경쟁력을 내기위한 서비스이며, SLA 수준은 연간 60% 이상 uptime 목표, 배포주기는 각 팀의 자율이나 표준 스프린트 주기가 1주일 이므로 1주일 1회 이상을 기준으로 함.
        - General Domain:   payment 결제서비스로 3rd Party 외부 서비스를 사용하는 것이 경쟁력이 높음 (핑크색으로 이후 전환할 예정)

### 폴리시 부착 (괄호는 수행주체, 폴리시 부착을 둘째단계에서 해놔도 상관 없음. 전체 연계가 초기에 드러남)

![image](https://lh6.googleusercontent.com/MHw86hOJW9mDVSa7m7W42jZVPRzKoDWAEvqkGFpjfOeSDOiuCXAITGKZMDA7ZmCffUjj0R6pzqr5edzRCCcqZpCRDWgCfLayZ4caHy2bd0Haybwb3BdsnQplHgqVsAAy8os8uQT_)

### 폴리시의 이동과 컨텍스트 매핑 (점선은 Pub/Sub, 실선은 Req/Resp)

![image](https://lh4.googleusercontent.com/lQYX03iEk9i1wyKOMwP2cOWH_BkgIQQxK_h1AhTMuqP_s9T_mI0sEZbKa2iC94FYWKg9O4g5oHrHA_hXJeb_Xvzyfj2Ysidy3SuAaUdEYrBQbqJkF1kFoRYFvkO6cv09Khi6tJYF)

### 완성된 1차 모형

![image](https://lh3.googleusercontent.com/i0XBM9IcrXyjE16f9j0IhEETrsxZCF2AnNvsUJeBq79TpY8eYSQDwVs1I7DhtuLw-jtGmBSqnhFiJanSyadFqQ4iDFtqx2yGPpHtA0W6BZ7cx09-3s5DjC52MKat_ZDLKRa5jz1g)

### 1차 완성본에 대한 기능적/비기능적 요구사항을 커버하는지 검증

![image](https://lh5.googleusercontent.com/VRDwND8JzXYwltI3FRphn2BVQU0rCCb1mGCpp-vdHaIJSZWSwPraQnGa2MuCJcyARnr_kiiGvpb97IeTIEFTWAnm7Dw3RI2L5Q_lHnEeDKHPA8pqVE8DhzY22U2V6QsVAwp5fS9x)

    - 고객이 차량을 선택해 예약한다. (ok)
    - 고객이 예약한 차량을 결제한다. (ok)
    - 결제가 성공하면 차량이 대여 된다. (ok)
    - 대여된 차량은 차량 관리 원부에 대여중으로 처리 된다. (ok)

![image](https://lh5.googleusercontent.com/vKOVC6ra2Ch_gKHFq3BwlwrD2VpOBOKm2hq85heqLGOIojDtvkazJWB_VwGOHMkB4GaTYt2v7lcX4PRxr4UwmXkH6hx4yACyCKadEPQ523HgH1H-KLr_7qs7tsCiCtTHSloJUmZH)

    - 고객이 예약한 렌트 차량을 취소할 수 있다 (ok)
    - 고객이 예약 취소가 되면 차량 대여 취소된다 (ok)
    - 고객이 주문상태를 중간중간 조회한다 (?) 
    - 고객의 개인 정보와 차량 대여/취소 이력 정보 확인 가능하다 (?)


### 모델 수정

![image](https://lh4.googleusercontent.com/mrEoqRNGCbd034MK1k8Uy1blJAT9Sw6UiG02BwCy1bfpa6YhdWv-gruwwolSn9l7hZ7W1aUU-6ke4razrZTyUO6g0wjkiG9Bx1pWna1ynGSG9Nk4IvpF7gLrD8EsErP-W0cvatj_)
    
    - 수정된 모델은 모든 요구사항을 커버함.

### 비기능 요구사항에 대한 검증

![image](https://lh3.googleusercontent.com/PV2RtlqdTL89Wal8kIE8AXhhLt71EDOHDc_yvSZ4XTgeBxhU0cxL_I_FARrfTvvEuJ7EI3ap-rFN_hrVjkc5U0Dm6dE8_ZmNhtaNvy55CAZ2E7-vhH8ipXDKCUK2LDE9c26ylbWU)

    - 마이크로 서비스를 넘나드는 시나리오에 대한 트랜잭션 처리
        - 차량 예약과 동시에 결제 처리 : 결제가 완료되지 않은 차량 렌트는 불가, ACID  트랜잭션 적용, 예약 완료시 결제 처리에 대해서 Req-Res 방식 처리.
        - 결제 완료 시 렌트 및 차량 원부에서 예약 처리 : Rental에서 마이크로 서비스가 별도의 배포주기를 가지기 때문에 Eventual Consistency 방식으로 트랜잭션 처리함.
        - 나머지 모든 inter-microservice 트랜잭션: Rental 및 management 이벤트에 대해, 데이터 일관성의 시점이 크리티컬하지 않은 모든 경우가 대부분이라 판단, Eventual Consistency 를 기본으로 채택함.

## 헥사고날 아키텍처 다이어그램 도출
    
![image](https://lh4.googleusercontent.com/fOS-lSfMqTzHT5h-KVTjDa5am7162EaWOtILX8Rkry1--ZsniD3KeRQRhvAT45sxlXpF8Q9pcw4ASCSWhmPd6HPjykpLxEUqeZcuY1pfhvD3oz7vBV0mS5_2c_oLRdqDXSCyD1Z2)

    - Chris Richardson, MSA Patterns 참고하여 Inbound adaptor와 Outbound adaptor를 구분함
    - 호출관계에서 PubSub 과 Req/Resp 를 구분함
    - 서브 도메인과 바운디드 컨텍스트의 분리:  각 팀의 KPI 별로 아래와 같이 관심 구현 스토리를 나눠가짐















# 운영

## CI/CD 설정


각 구현체들은 각자의 source repository 에 구성되었고, 사용한 CI/CD 플랫폼은 GCP를 사용하였으며, pipeline build script 는 각 프로젝트 폴더 이하에 cloudbuild.yml 에 포함되었다.


# Liveness
/tmp/healthy 파일의 존재를 확인하는 liveness를 적용하였다.   
3초동안 파드가 뜨기를 기다렸다가 파드가 뜨지 않았을 경우 최대 5번까지 재시도 한다. 
이때, /tmp/healthy를 지워버리므로 liveness는 pod상태가 정상이 아니라고 판단한다.   
5번 재시도 후에도 파드가 뜨지 않았을 경우 CrashLoopBackOff 상태가 된다.   
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: payment
  labels:
    app: payment
spec:
  replicas: 1
  selector:
    matchLabels:
      app: payment
  template:
    metadata:
      labels:
        app: payment
    spec:
      containers:
        - name: payment
          image: 496278789073.dkr.ecr.ap-northeast-2.amazonaws.com/ecr-skcc-team2-payment:v1
          args:
          - /bin/sh
          - -c
          - touch /tmp/healthy; sleep 10; rm -rf /tmp/healthy; sleep 600;
          ports:
            - containerPort: 8080
          readinessProbe:
            httpGet:
              path: '/actuator/health'
              port: 8080
            initialDelaySeconds: 10
            timeoutSeconds: 2
            periodSeconds: 5
            failureThreshold: 10
          livenessProbe:                 #적용 부분
            exec:
              command:
              - cat
              - /tmp/healthy
            initialDelaySeconds: 3
            timeoutSeconds: 2
            periodSeconds: 5
            failureThreshold: 5
```
```
$ k get pod -w
NAME                           READY   STATUS    RESTARTS   AGE
gateway-849986759f-9w56j       1/1     Running   0          101m
management-57bdb8b8c-gvrkq     1/1     Running   0          74m
payment-5664c755cc-4tgn7       0/1     Running   1          48s
rental-c697b7d78-xl8kf         1/1     Running   0          61m
reservation-559fd5d9f8-4ldrg   1/1     Running   0          59m
view-6484f74b85-6ql85          1/1     Running   0          57m
payment-5664c755cc-4tgn7       1/1     Running   1          49s
payment-5664c755cc-4tgn7       0/1     Running   2          53s
payment-5664c755cc-4tgn7       1/1     Running   2          74s
payment-5664c755cc-4tgn7       0/1     Running   3          78s
payment-5664c755cc-4tgn7       1/1     Running   3          99s
payment-5664c755cc-4tgn7       0/1     Running   4          103s
payment-5664c755cc-4tgn7       1/1     Running   4          2m4s
payment-5664c755cc-4tgn7       0/1     CrashLoopBackOff   4          2m8s

$ k get pod
NAME                           READY   STATUS             RESTARTS   AGE
gateway-849986759f-9w56j       1/1     Running            0          103m
management-57bdb8b8c-gvrkq     1/1     Running            0          76m
payment-5664c755cc-4tgn7       0/1     CrashLoopBackOff   4          2m27s
rental-c697b7d78-xl8kf         1/1     Running            0          63m
reservation-559fd5d9f8-4ldrg   1/1     Running            0          60m
view-6484f74b85-6ql85          1/1     Running            0          58m
```


# ISTIO
istio 설치 후 deploy재기동 
```
cd istio
curl -L https://git.io/getLatestIstio | ISTIO_VERSION=1.4.5 sh -
cd istio-1.4.5
export PATH=$PWD/bin:$PATH
for i in install/kubernetes/helm/istio-init/files/crd*yaml; do kubectl apply -f $i; done
kubectl apply -f install/kubernetes/istio-demo.yaml
kubectl get pod -n istio-system
kubectl label ns default istio-injection=enabled
```
테스트 결과 
```
$ k exec -it management-57bdb8b8c-2z7lr -- /bin/sh
Defaulting container name to management.
/ # wget http://management.default:8080
Connecting to management.default:8080 (10.100.60.100:8080)
index.html           100% |*********************************************************************************************************************************|   240  0:00:00 ETA
/ # wget http://view.default:8080/myPages
Connecting to view.default:8080 (10.100.71.102:8080)
myPages              100% |*********************************************************************************************************************************|   300  0:00:00 ETA
```
httpie 설치 및 테스트
```
kubectl exec -it httpie bin/bash
http http://gateway:8080/carManagements carNo=test rentalAmt=10000 procStatus=WAITING carRegDt=20200701
http http://gateway:8080/carManagements carNo=car01 rentalAmt=10000 procStatus=WAITING carRegDt=20200701
http http://gateway:8080/carManagements carNo=car02 rentalAmt=20000 procStatus=WAITING carRegDt=20200702
http http://view:8080/carInformations
```
  

# 서킷 브레이커
```
$ kubectl scale deploy management --replicas=2

$ kubectl apply -f - <<EOF
apiVersion: networking.istio.io/v1alpha3
kind: DestinationRule
metadata:
  name: management
spec:
  host: management
  trafficPolicy:
    connectionPool:
      tcp:
        maxConnections: 1
      http:
        http1MaxPendingRequests: 1
        maxRequestsPerConnection: 1
    outlierDetection:
      consecutiveErrors: 5
      interval: 10s
      baseEjectionTime: 30s
      maxEjectionPercent: 100
EOF
```
#### 적용 시 
```
siege -c20 -t30S  -v --content-type "application/json" 'http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}'
** SIEGE 3.0.8
** Preparing 20 concurrent users for battle.
The server is now under siege...
HTTP/1.1 503   0.01 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.01 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.01 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.03 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.04 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.01 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.02 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.05 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.05 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.04 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.10 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.04 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.09 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.04 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.11 secs:      95 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.03 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.04 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.05 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.02 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.03 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.06 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.06 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.06 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.06 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.06 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.06 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.06 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.06 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.08 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.08 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.02 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.08 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.09 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.02 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.04 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.05 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.15 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.15 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.12 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.18 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.03 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.10 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.03 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.10 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.03 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.02 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.01 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.01 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.03 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.01 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.12 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.02 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.05 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.02 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.02 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.01 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.02 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.02 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.05 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.11 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.03 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.00 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.00 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.04 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.08 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.11 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.01 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.13 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.11 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.01 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.12 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.14 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.02 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.14 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.02 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.03 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.04 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.05 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.08 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.03 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.01 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.05 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.07 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.15 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.02 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.03 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.05 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.05 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.07 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.01 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.02 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.03 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.28 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.09 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.02 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.08 secs:      95 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.02 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.02 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.02 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.06 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
```

#### 서킷 브레이커 삭제 
```
$ kubectl delete dr --all
siege -c20 -t30S  -v --content-type "application/json" 'http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}'
** SIEGE 3.0.8
** Preparing 20 concurrent users for battle.
The server is now under siege...
HTTP/1.1 201   0.05 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.07 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.07 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.07 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.09 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.09 secs:     332 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.08 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.08 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.09 secs:     332 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.04 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.02 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.02 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.13 secs:     332 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.05 secs:     332 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.19 secs:     332 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.13 secs:     332 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.27 secs:     332 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.20 secs:     332 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.27 secs:     332 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.29 secs:     332 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.02 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.20 secs:     332 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.07 secs:     332 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.01 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.01 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.02 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.02 secs:     332 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.03 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.03 secs:     332 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.05 secs:     332 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.06 secs:     332 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.06 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.08 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.01 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.05 secs:     332 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.02 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.03 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.02 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.02 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.00 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.08 secs:     332 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.07 secs:     332 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.02 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.09 secs:     332 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"WAITING", "carRegDt":"20200701"}
```


# 리트라이
### 리트라이 
```
$ kubectl apply -f - <<EOF
apiVersion: networking.istio.io/v1alpha3
kind: VirtualService
metadata:
  name: reservation
spec:
  hosts:
  - reservation
  http:
  - route:
    - destination:
        host: reservation
    retries:
      attempts: 3
      perTryTimeout: 2s
EOF
```
#### 적용
```
$ siege -c5 -t30S  -v --content-type "application/json" 'http://gateway:8080/carReservations POST {"carNo":"car01", "custNo":"cus01", "paymtNo":"pay20200801Seq0001", "procStatus":"RESERVED", "rentalAmt":"10000", "resrvNo":"res20200801Seq0001", "resrvDt":"20200801", "rentalDt":"20200802", "returnDt":"20200805"}'
** SIEGE 3.0.8
** Preparing 5 concurrent users for battle.
The server is now under siege...
HTTP/1.1 500   0.04 secs:     257 bytes ==> POST http://gateway:8080/carReservations POST {"carNo":"car01", "custNo":"cus01", "paymtNo":"pay20200801Seq0001", "procStatus":"RESERVED", "rentalAmt":"10000", "resrvNo":"res20200801Seq0001", "resrvDt":"20200801", "rentalDt":"20200802", "returnDt":"20200805"}
HTTP/1.1 500   0.04 secs:     257 bytes ==> POST http://gateway:8080/carReservations POST {"carNo":"car01", "custNo":"cus01", "paymtNo":"pay20200801Seq0001", "procStatus":"RESERVED", "rentalAmt":"10000", "resrvNo":"res20200801Seq0001", "resrvDt":"20200801", "rentalDt":"20200802", "returnDt":"20200805"}
HTTP/1.1 500   0.04 secs:     257 bytes ==> POST http://gateway:8080/carReservations POST {"carNo":"car01", "custNo":"cus01", "paymtNo":"pay20200801Seq0001", "procStatus":"RESERVED", "rentalAmt":"10000", "resrvNo":"res20200801Seq0001", "resrvDt":"20200801", "rentalDt":"20200802", "returnDt":"20200805"}
HTTP/1.1 500   0.04 secs:     257 bytes ==> POST http://gateway:8080/carReservations POST {"carNo":"car01", "custNo":"cus01", "paymtNo":"pay20200801Seq0001", "procStatus":"RESERVED", "rentalAmt":"10000", "resrvNo":"res20200801Seq0001", "resrvDt":"20200801", "rentalDt":"20200802", "returnDt":"20200805"}
HTTP/1.1 500   0.02 secs:     257 bytes ==> POST http://gateway:8080/carReservations POST {"carNo":"car01", "custNo":"cus01", "paymtNo":"pay20200801Seq0001", "procStatus":"RESERVED", "rentalAmt":"10000", "resrvNo":"res20200801Seq0001", "resrvDt":"20200801", "rentalDt":"20200802", "returnDt":"20200805"}
HTTP/1.1 500   0.07 secs:     257 bytes ==> POST http://gateway:8080/carReservations POST {"carNo":"car01", "custNo":"cus01", "paymtNo":"pay20200801Seq0001", "procStatus":"RESERVED", "rentalAmt":"10000", "resrvNo":"res20200801Seq0001", "resrvDt":"20200801", "rentalDt":"20200802", "returnDt":"20200805"}
HTTP/1.1 500   0.04 secs:     257 bytes ==> POST http://gateway:8080/carReservations POST {"carNo":"car01", "custNo":"cus01", "paymtNo":"pay20200801Seq0001", "procStatus":"RESERVED", "rentalAmt":"10000", "resrvNo":"res20200801Seq0001", "resrvDt":"20200801", "rentalDt":"20200802", "returnDt":"20200805"}
HTTP/1.1 500   0.03 secs:     257 bytes ==> POST http://gateway:8080/carReservations POST {"carNo":"car01", "custNo":"cus01", "paymtNo":"pay20200801Seq0001", "procStatus":"RESERVED", "rentalAmt":"10000", "resrvNo":"res20200801Seq0001", "resrvDt":"20200801", "rentalDt":"20200802", "returnDt":"20200805"}
HTTP/1.1 500   0.04 secs:     257 bytes ==> POST http://gateway:8080/carReservations POST {"carNo":"car01", "custNo":"cus01", "paymtNo":"pay20200801Seq0001", "procStatus":"RESERVED", "rentalAmt":"10000", "resrvNo":"res20200801Seq0001", "resrvDt":"20200801", "rentalDt":"20200802", "returnDt":"20200805"}
HTTP/1.1 500   0.04 secs:     257 bytes ==> POST http://gateway:8080/carReservations POST {"carNo":"car01", "custNo":"cus01", "paymtNo":"pay20200801Seq0001", "procStatus":"RESERVED", "rentalAmt":"10000", "resrvNo":"res20200801Seq0001", "resrvDt":"20200801", "rentalDt":"20200802", "returnDt":"20200805"}
HTTP/1.1 500   0.03 secs:     257 bytes ==> POST http://gateway:8080/carReservations POST {"carNo":"car01", "custNo":"cus01", "paymtNo":"pay20200801Seq0001", "procStatus":"RESERVED", "rentalAmt":"10000", "resrvNo":"res20200801Seq0001", "resrvDt":"20200801", "rentalDt":"20200802", "returnDt":"20200805"}
HTTP/1.1 500   0.03 secs:     257 bytes ==> POST http://gateway:8080/carReservations POST {"carNo":"car01", "custNo":"cus01", "paymtNo":"pay20200801Seq0001", "procStatus":"RESERVED", "rentalAmt":"10000", "resrvNo":"res20200801Seq0001", "resrvDt":"20200801", "rentalDt":"20200802", "returnDt":"20200805"}
HTTP/1.1 500   0.03 secs:     257 bytes ==> POST http://gateway:8080/carReservations POST {"carNo":"car01", "custNo":"cus01", "paymtNo":"pay20200801Seq0001", "procStatus":"RESERVED", "rentalAmt":"10000", "resrvNo":"res20200801Seq0001", "resrvDt":"20200801", "rentalDt":"20200802", "returnDt":"20200805"}
```


# 
```


siege -r 2000 -c 200 -v http://gateway:8080/carManagements

```

### HPA : 오토스케일 아웃
시스템을 안정적으로 운영할 수 있게 자동화된 확장 기능을 적용


- 결제서비스에 대한 replica 를 동적으로 늘려주도록 HPA 를 설정한다. 설정은 CPU 사용량이 20프로를 넘어서면 replica 를 10개까지 늘려준다:
```
kubectl autoscale deploy management --min=1 --max=10 --cpu-percent=20
kubectl get po -l run=management -w
```
- 워크로드를 걸어준다.
```
siege -r 2000 -c 200 -v http://gateway:8080/carManagements

```
- 오토스케일이 어떻게 되고 있는지 모니터링을 걸어둔다:
```
kubectl get deploy pay -w
```
- 어느정도 시간이 흐른 후 (약 30초) 스케일 아웃이 벌어지는 것을 확인할 수 있다:
```
NAME    DESIRED   CURRENT   UP-TO-DATE   AVAILABLE   AGE
pay     1         1         1            1           17s
pay     1         2         1            1           45s
pay     1         4         1            1           1m
:
```
- siege 의 로그를 통해 전체적인 성공률이 높아진 것을 확인 
```
Transactions:		        5078 hits
Availability:		       92.45 %
Elapsed time:		       120 secs
Data transferred:	        0.34 MB
Response time:		        5.60 secs
Transaction rate:	       17.15 trans/sec
Throughput:		        0.01 MB/sec
Concurrency:		       96.02
```
