#!/bin/bash
image=time-clock-stamper-ws
version=1.2
network=mynetwork
hostport=8081
sudo docker login -u trundicho
sudo docker build -t $image:$version .
sudo docker tag $image:$version trundicho/$image:$version
sudo docker push trundicho/$image:$version
sudo docker network create $network
sudo docker container stop $image
sudo docker container rm $image
sudo docker run --network $network --name $image -p $hostport:8080 $image:$version
