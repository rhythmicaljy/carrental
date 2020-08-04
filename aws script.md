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

## aws eks 
#### 클러스터 생성   
eksctl create cluster --name [클러스터명] --version 1.15 --nodegroup-name standard-workers --node-type t3.medium --nodes 3 --nodes-min 1 --nodes-max 3
#### 클러스터 접속 정보 받아오기 
aws eks --region ap-northeast-2 update-kubeconfig --name [클러스터명]
#### 노드 리스트 확인   
kubectl get node

## aws ecr   
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
kubectl expose deployment.apps/gateway  --type=LoadBalancer --port=808


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
