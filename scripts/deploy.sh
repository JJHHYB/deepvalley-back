#!/usr/bin/env bash

REPOSITORY=/home/ubuntu/app

echo "> 현재 구동 중인 애플리케이션 pid 확인"

#CURRENT_PID=$(ps aux | grep 'java -jar.*deepvalley' | awk '{print $2}')
CURRENT_PID=$(pgrep -f deepvalley.*.jar)

echo "현재 구동 중인 애플리케이션 pid: $CURRENT_PID"

if [ -z "$CURRENT_PID" ]; then
  echo "현재 구동 중인 애플리케이션이 없으므로 종료하지 않습니다."
else
  echo "> kill -15 $CURRENT_PID"
  kill -15 $CURRENT_PID
  sleep 5
fi

echo "> 새 애플리케이션 배포"

#JAR_NAME=$(ls $REPOSITORY | grep 'deepvalley' | tail -n 1)
JAR_NAME=$(ls $REPOSITORY/ | grep jar | tail -n 1)

if [ -z "$JAR_NAME" ]; then
  echo "Error: JAR 파일을 찾을 수 없습니다."
  exit 1
fi

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

# 배포 로그 기록
if [ -f "$REPOSITORY/commit_hash.txt" ]; then
  echo "> 배포 로그 기록"
  cat "$REPOSITORY/commit_hash.txt" >> "$REPOSITORY/deploy.log"
  echo "Deployment completed with commit $(cat "$REPOSITORY/commit_hash.txt")" >> "$REPOSITORY/deploy.log"
else
  echo "> commit_hash.txt 파일을 찾을 수 없습니다."
fi
