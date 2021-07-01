#!/bin/bash
sudo docker login -u trundicho
sudo docker build -t time-clock-stamper-ws:1.2 .
sudo docker tag time-clock-stamper-ws:1.2 trundicho/time-clock-stamper-ws:1.2
sudo docker push trundicho/time-clock-stamper-ws:1.2
sudo docker network create mynetwork
sudo docker container stop time-clock-stamper-ws
sudo docker container rm time-clock-stamper-ws
sudo docker run --network mynetwork --name time-clock-stamper-ws --mount type=bind,src=/Users/angeloromito/Desktop/Stempeluhr/,dst=/workspace/ -p 8081:8080 time-clock-stamper-ws:1.2
