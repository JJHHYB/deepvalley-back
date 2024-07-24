#!/usr/bin/env bash

REPOSITORY=/home/ubuntu/app

echo "> 현재 구동 중인 애플리케이션 pid 확인"

CURRENT_PID=$(pgrep -fla java | grep hayan | awk '{print $1}')

echo "현재 구동 중인 애플리케이션 pid: $CURRENT_PID"

if [ -z "$CURRENT_PID" ]; then
  echo "현재 구동 중인 애플리케이션이 없으므로 종료하지 않습니다."
else
  echo "> kill -15 $CURRENT_PID"
  kill -15 $CURRENT_PID
  sleep 5
fi

echo "> 새 애플리케이션 배포"

JAR_NAME=$(ls -tr $REPOSITORY/*SNAPSHOT.jar | tail -n 1)

echo "> JAR NAME: $JAR_NAME"

echo "> $JAR_NAME 에 실행권한 추가"

chmod +x $JAR_NAME

echo "> $JAR_NAME 실행"

nohup java -Dcloud.aws.s3.bucket=${CLOUD_AWS_S3_BUCKET} \
            -Dcloud.aws.region.static=${CLOUD_AWS_REGION_STATIC} \
            -Dcloud.aws.credentials.accessKey=${CLOUD_AWS_CREDENTIALS_ACCESS_KEY} \
            -Dcloud.aws.credentials.secretKey=${CLOUD_AWS_CREDENTIALS_SECRET_KEY} \
            -jar $JAR_NAME --spring.profiles.active=prod \
            >> $REPOSITORY/nohup.out 2>&1 &