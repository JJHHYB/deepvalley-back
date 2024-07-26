#!/usr/bin/env bash

echo "> 현재 구동 중인 애플리케이션 pid 확인"

CURRENT_PID=$(pgrep -f deepvalley)

echo "현재 구동 중인 애플리케이션 pid: $CURRENT_PID"

if [ -z "$CURRENT_PID" ]; then
  echo "현재 구동 중인 애플리케이션이 없으므로 종료하지 않습니다."
else
  echo "> kill -15 $CURRENT_PID"
  kill -15 $CURRENT_PID
  sleep 5
fi

echo "> 새 애플리케이션 배포"

JAR_NAME=$(ls /home/ubuntu/app | grep 'deepvalley' | tail -n 1)

if [ -z "$JAR_NAME" ]; then
  echo "Error: JAR 파일을 찾을 수 없습니다."
  exit 1
fi

echo "> JAR NAME: $JAR_NAME"

echo "> $JAR_NAME 에 실행권한 추가"

chmod +x $JAR_NAME

echo "> $JAR_NAME 실행"

CLOUD_AWS_REGION_STATIC=$(yq '.CLOUD_AWS_REGION_STATIC' ./secrets.yml)
CLOUD_AWS_CREDENTIALS_ACCESS_KEY=$(yq '.CLOUD_AWS_CREDENTIALS_ACCESS_KEY' ./secrets.yml)
CLOUD_AWS_CREDENTIALS_SECRET_KEY=$(yq '.CLOUD_AWS_CREDENTIALS_SECRET_KEY' ./secrets.yml)
CLOUD_AWS_S3_BUCKET=$(yq '.CLOUD_AWS_S3_BUCKET' ./secrets.yml)
JWT_SECRETKEY=$(yq '.JWT_SECRETKEY' ./secrets.yml)
JWT_EXPIRETIME=$(yq '.JWT_EXPIRETIME' ./secrets.yml)
KAKAO_CLIENT_ID=$(yq '.KAKAO_CLIENT_ID' ./secrets.yml)
KAKAO_REDIRECT_URI=$(yq '.KAKAO_REDIRECT_URI' ./secrets.yml)
SWAGGER_PRODUCTION_URL=$(yq '.SWAGGER_PRODUCTION_URL' ./secrets.yml)
SWAGGER_DEVELOPMENT_URL=$(yq '.SWAGGER_DEVELOPMENT_URL' ./secrets.yml)
MYSQL_URL=$(yq '.MYSQL_URL' ./secrets.yml)
MYSQL_USERNAME=$(yq '.MYSQL_USERNAME' ./secrets.yml)
MYSQL_PASSWORD=$(yq '.MYSQL_PASSWORD' ./secrets.yml)

echo "버킷 이름 : $CLOUD_AWS_S3_BUCKET"
echo "버킷 지역 : $CLOUD_AWS_REGION_STATIC"

CMD="nohup java -jar $JAR_NAME \
                 --cloud.aws.s3.bucket=${CLOUD_AWS_S3_BUCKET} \
                 --cloud.aws.region.static=${CLOUD_AWS_REGION_STATIC} \
                 --cloud.aws.credentials.accessKey=${CLOUD_AWS_CREDENTIALS_ACCESS_KEY} \
                 --cloud.aws.credentials.secretKey=${CLOUD_AWS_CREDENTIALS_SECRET_KEY} \
                 --jwt.secretkey=${JWT_SECRETKEY} \
                 --jwt.expiretime=${JWT_EXPIRETIME} \
                 --oauth.kakao.client-id=${KAKAO_CLIENT_ID} \
                 --oauth.kakao.redirect-url=${KAKAO_REDIRECT_URI} \
                 --swagger.production.url=${SWAGGER_PRODUCTION_URL} \
                 --swagger.development.url=${SWAGGER_DEVELOPMENT_URL} \
                 --spring.datasource.url=${MYSQL_URL} \
                 --spring.datasource.username=${MYSQL_USERNAME} \
                 --spring.datasource.password=${MYSQL_PASSWORD} \
                 --spring.profiles.active=prod \
                 >> /home/ubuntu/app/nohup.out 2>&1 &"

eval "$CMD"

# 배포 로그 기록
if [ -f "/home/ubuntu/app/commit_hash.txt" ]; then
  echo "> 배포 로그 기록"
  cat "/home/ubuntu/app/commit_hash.txt" >> "/home/ubuntu/app/deploy.log"
  echo "Deployment completed with commit $(cat "/home/ubuntu/app/commit_hash.txt")" >> "/home/ubuntu/app/deploy.log"
else
  echo "> commit_hash.txt 파일을 찾을 수 없습니다."
fi
