FROM openjdk:21-alpine
WORKDIR /app
EXPOSE 9081
COPY target/artwoodcba.jar /app/artwoodcba.jar
ENTRYPOINT ["java","-jar","/app/artwoodcba.jar"]
