#!/bin/bash

DEPLOY_LOG_PATH="/home/ec2-user/deploy.log"
DEPLOY_PATH="/home/ec2-user/Another-Art-BE"

START_TIME=$(date)
echo "> ##### START [$START_TIME] #####" >> $DEPLOY_LOG_PATH

cd $DEPLOY_PATH
echo "> 0. 배포 위치 = $DEPLOY_PATH" >> $DEPLOY_LOG_PATH

CURRENT_RUNNING_PID=$(sudo docker container ls -aq -f name=application)
echo "> 1. 현재 관리중인 WAS Docker Container PID = $CURRENT_RUNNING_PID" >> $DEPLOY_LOG_PATH

if [ -z $CURRENT_RUNNING_PID ]
then
  echo "> 2-1. 현재 관리중인 WAS Docker Container가 없습니다" >> $DEPLOY_LOG_PATH
else
  echo "> 2-2-1. 현재 관리중인 WAS Docker Container Stop & Remove = $CURRENT_RUNNING_PID" >> $DEPLOY_LOG_PATH
  echo "> 2-2-2. sudo docker stop $CURRENT_RUNNING_PID" >> $DEPLOY_LOG_PATH
  sudo docker stop $CURRENT_RUNNING_PID
  echo "> 2-2-3. sudo docker rm $CURRENT_RUNNING_PID" >> $DEPLOY_LOG_PATH
  sudo docker rm $CURRENT_RUNNING_PID
  sleep 5
fi

DOCKER_IMAGE="sjiwon/another-art-be"
DOCKER_IMAGE_TAG=$(date +%Y%m%d_%H%M%S)
echo "> 3-1. Docker Image Run..." >> $DEPLOY_LOG_PATH
echo "> 3-2. Docker Image = $DOCKER_IMAGE:$DOCKER_IMAGE_TAG" >> $DEPLOY_LOG_PATH

sudo docker build -t $DOCKER_IMAGE:$DOCKER_IMAGE_TAG .
sudo docker run \
      -d \
      --name application \
      -p 8080:8080 \
      -v $DEPLOY_PATH/logs:/app/logs \
      $DOCKER_IMAGE:$DOCKER_IMAGE_TAG

NEW_RUNNING_PID=$(sudo docker container ls -q -f name=application)
echo "> 4. 새로 실행된 WAS Docker Container PID = $NEW_RUNNING_PID" >> $DEPLOY_LOG_PATH

echo -e "> ##### END #####\n" >> $DEPLOY_LOG_PATH
