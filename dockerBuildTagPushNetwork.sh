#!/bin/bash
sudo docker login -u trundicho
sudo docker build -t time-clock-stamper-ws:1.2 .
sudo docker tag time-clock-stamper-ws:1.2 trundicho/time-clock-stamper-ws:1.2
sudo docker push trundicho/time-clock-stamper-ws:1.2
sudo docker network create mynetwork
sudo docker network connect mynetwork time-clock-stamper-ws