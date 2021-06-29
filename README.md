# Time clock stamper
is a simple webservice that lets you stamp in and stamp out. It gives you an overview 
about your overtime per month.

![Screenshot](API.png)

### Prerequisites
- Java 11 JDK
- A JAVA IDE

## How to start?
Just run the "Application.java" out of your IDE.
Configuration parameters can be adjusted in the application.properties

## Built With
* [Maven](https://maven.apache.org/) - Dependency Management
* [Spring-Boot](http://spring.io/projects/spring-boot) - Spring-Boot

## Docker
Go to project folder

### Login to docker
docker login -u <username>

### Build docker image
docker build -t time-clock-stamper-ws:1.1 .

### Tagging
docker tag time-clock-stamper-ws:1.1 trundicho/time-clock-stamper-ws:1.1

### Push to docker hub
docker push trundicho/time-clock-stamper-ws:1.1

### How to build container
![Screenshot](DockerContainerConfig.png)

## Authors
* **Angelo Romito** - *Initial work* - [Trundicho](https://github.com/Trundicho)

## License
This project is licensed under Apache License Version 2.0 - see the [LICENSE](LICENSE) file for details
