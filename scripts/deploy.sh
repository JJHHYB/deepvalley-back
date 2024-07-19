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

nohup java -jar $JAR_NAME --spring.profiles.active=prod >> $REPOSITORY/nohup.out 2>&1 &

# 배포 로그 기록
if [ -f "$REPOSITORY/commit_hash.txt" ]; then
  echo "> 배포 로그 기록"
  cat "$REPOSITORY/commit_hash.txt" >> "$REPOSITORY/deploy.log"
  echo "Deployment completed with commit $(cat "$REPOSITORY/commit_hash.txt")" >> "$REPOSITORY/deploy.log"
else
  echo "> commit_hash.txt 파일을 찾을 수 없습니다."
fi