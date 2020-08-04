# GIT
각 마이크로 서비스 별로 GIT REPOSITORY를 생성하여 코드를 업로드 한다.
예시) https://github.com/l2skcc



# aws set up
## aws iam
access key를 전달 받는다   

## aws iam
aws configure    
- 전달받은 access key입력
- 리전 : ap-northeast-2


# aws eks 
#### 클러스터 생성   
eksctl create cluster --name [클러스터명] --version 1.15 --nodegroup-name standard-workers --node-type t3.medium --nodes 3 --nodes-min 1 --nodes-max 3
#### 클러스터 접속 정보 받아오기 
aws eks --region ap-northeast-2 update-kubeconfig --name [클러스터명]
#### 노드 리스트 확인   
kubectl get node

# aws ecr   
#### aws repository 로그인   
aws ecr get-login-password --region ap-northeast-2 | docker login --username AWS --password-stdin 496278789073.dkr.ecr.ap-northeast-2.amazonaws.com   


#### 각 마이크로서비스에서 사용할 aws repository생성   
aws ecr create-repository --repository-name [ecr이름] --image-scanning-configuration scanOnPush=true --region ap-northeast-2   

aws ecr create-repository --repository-name ecr-skcc-team2-gateway --image-scanning-configuration scanOnPush=true --region ap-northeast-2   
aws ecr create-repository --repository-name ecr-skcc-team2-management --image-scanning-configuration scanOnPush=true --region ap-northeast-2   
aws ecr create-repository --repository-name ecr-skcc-team2-rental --image-scanning-configuration scanOnPush=true --region ap-northeast-2   
aws ecr create-repository --repository-name ecr-skcc-team2-reservation --image-scanning-configuration scanOnPush=true --region ap-northeast-2   
aws ecr create-repository --repository-name ecr-skcc-team2-veiw --image-scanning-configuration scanOnPush=true --region ap-northeast-2   
aws ecr create-repository --repository-name ecr-skcc-team2-payment --image-scanning-configuration scanOnPush=true --region ap-northeast-2   



# UBUNTU
## git clone to ubuntu
#### 위에서 생성한 깃 레파지토리를 우분투의 각 폴더에 다운로드   
mkdir [폴더명]   
git clone https://github.com/[경로]   
cd [폴더명]/[경로]   


## mvn package
#### 각 경로에서 메이븐 빌드     
mvn package
 
 
## docker build
docker build -t 496278789073.dkr.ecr.ap-northeast-2.amazonaws.com/[ecr이름]:v1 .   
docker push 496278789073.dkr.ecr.ap-northeast-2.amazonaws.com/[[ecr이름]:v1   

docker build -t 496278789073.dkr.ecr.ap-northeast-2.amazonaws.com/ecr-skcc-team2-gateway:v1 .   
docker push 496278789073.dkr.ecr.ap-northeast-2.amazonaws.com/ecr-skcc-team2-gateway:v1   

docker build -t 496278789073.dkr.ecr.ap-northeast-2.amazonaws.com/ecr-skcc-team2-management:v1 .   
docker push 496278789073.dkr.ecr.ap-northeast-2.amazonaws.com/ecr-skcc-team2-management:v1   

docker build -t 496278789073.dkr.ecr.ap-northeast-2.amazonaws.com/ecr-skcc-team2-rental:v1 .   
docker push 496278789073.dkr.ecr.ap-northeast-2.amazonaws.com/ecr-skcc-team2-rental:v1   

docker build -t 496278789073.dkr.ecr.ap-northeast-2.amazonaws.com/ecr-skcc-team2-reservation:v1 .   
docker push 496278789073.dkr.ecr.ap-northeast-2.amazonaws.com/ecr-skcc-team2-reservation:v1   

docker build -t 496278789073.dkr.ecr.ap-northeast-2.amazonaws.com/ecr-skcc-team2-veiw:v1 .   
docker push 496278789073.dkr.ecr.ap-northeast-2.amazonaws.com/ecr-skcc-team2-veiw:v1   

docker build -t 496278789073.dkr.ecr.ap-northeast-2.amazonaws.com/ecr-skcc-team2-payment:v1 .   
docker push 496278789073.dkr.ecr.ap-northeast-2.amazonaws.com/ecr-skcc-team2-payment:v1   



## gateway deploy
kubectl create deploy gateway --image=496278789073.dkr.ecr.ap-northeast-2.amazonaws.com/[ecr경로]-gateway:v1   
kubectl create deploy gateway --image=496278789073.dkr.ecr.ap-northeast-2.amazonaws.com/ecr-skcc-team2-gateway:v1     
kubectl expose deployment.apps/gateway  --type=LoadBalancer --port=8080



## 나머지 서비스deploy
### ~/kubernetes/deploy.yaml 수정! > image: username/management:latest    
####deploy.yaml의 이미지 값을 각 ecr경로 값으로 변경한다 (ex)496278789073.dkr.ecr.ap-northeast-2.amazonaws.com/ecr-skcc-team2-reservation:v1   
kubectl apply -f .   

## install kafka
curl https://raw.githubusercontent.com/kubernetes/helm/master/scripts/get | bash   
kubectl --namespace kube-system create sa tiller   
kubectl create clusterrolebinding tiller --clusterrole cluster-admin --serviceaccount=kube-system:tiller   
helm init --service-account tiller   
kubectl patch deploy --namespace kube-system tiller-deploy -p '{"spec":{"template":{"spec":{"serviceAccount":"tiller"}}}}'   
helm repo add incubator http://storage.googleapis.com/kubernetes-charts-incubator   
helm repo update      
helm install --name my-kafka --namespace kafka incubator/kafka   

watch kubectl get all -n kafka    




## 확인하기
kubectl get svc   
NAME          TYPE           CLUSTER-IP      EXTERNAL-IP                                                                    PORT(S)          AGE   
gateway       LoadBalancer   10.100.51.99    ****-1329116461.ap-northeast-2.elb.amazonaws.com   8080:31699/TCP   41m   

http://****-1329116461.ap-northeast-2.elb.amazonaws.com:8080/서비스 로 접속한다   






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
siege -c20 -t30S  -v --content-type "application/json" 'http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}'
** SIEGE 3.0.8
** Preparing 20 concurrent users for battle.
The server is now under siege...
HTTP/1.1 503   0.01 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.01 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.01 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.03 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.04 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.01 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.02 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.05 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.05 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.04 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.10 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.04 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.09 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.04 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.11 secs:      95 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.03 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.04 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.05 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.02 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.03 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.06 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.06 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.06 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.06 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.06 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.06 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.06 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.06 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.08 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.08 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.02 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.08 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.09 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.02 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.04 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.05 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.15 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.15 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.12 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.18 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.03 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.10 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.03 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.10 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.03 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.02 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.01 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.01 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.03 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.01 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.12 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.02 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.05 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.02 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.02 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.01 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.02 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.02 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.05 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.11 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.03 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.00 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.00 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.04 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.08 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.11 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.01 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.13 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.11 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.01 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.12 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.14 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.02 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.14 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.02 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.03 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.04 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.05 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.08 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.03 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.01 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.05 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.07 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.15 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.02 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.03 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.05 secs:      81 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.05 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.07 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.01 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.02 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.03 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.28 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.09 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.02 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 503   0.08 secs:      95 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.02 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.02 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.02 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.06 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
```

#### 서킷 브레이커 삭제 
```
$ kubectl delete dr --all
siege -c20 -t30S  -v --content-type "application/json" 'http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}'
** SIEGE 3.0.8
** Preparing 20 concurrent users for battle.
The server is now under siege...
HTTP/1.1 201   0.05 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.07 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.07 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.07 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.09 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.09 secs:     332 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.08 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.08 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.09 secs:     332 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.04 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.02 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.02 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.13 secs:     332 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.05 secs:     332 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.19 secs:     332 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.13 secs:     332 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.27 secs:     332 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.20 secs:     332 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.27 secs:     332 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.29 secs:     332 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.02 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.20 secs:     332 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.07 secs:     332 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.01 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.01 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.02 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.02 secs:     332 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.03 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.03 secs:     332 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.05 secs:     332 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.06 secs:     332 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.06 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.08 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.01 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.05 secs:     332 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.02 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.03 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.02 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.02 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.00 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.08 secs:     332 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.07 secs:     332 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.02 secs:     334 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
HTTP/1.1 201   0.09 secs:     332 bytes ==> POST http://gateway:8080/carManagements POST {"carNo":"test", "rentalAmt":"10000", "procStatus":"AITING", "carRegDt":"20200701"}
```


# 리트라이
### 리트라이 적용
```
```
