FROM amazoncorretto:21-alpine-jdk
COPY target/*.jar java-ozon-hub.jar
ENTRYPOINT ["java","-jar","/java-ozon-hub.jar"]