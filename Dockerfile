FROM adoptopenjdk:11-jre-hotspot
EXPOSE 8080
ARG JAR_FILE=target/time-clock-stamper-ws-0.0.1-SNAPSHOT.war
ADD ${JAR_FILE} app.war
ENTRYPOINT ["java","-jar","/app.war"]