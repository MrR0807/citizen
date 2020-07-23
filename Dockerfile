FROM openjdk:14
WORKDIR /app
COPY target/*.jar /app/application.jar
EXPOSE 8080
ENTRYPOINT ["java"]
CMD ["-jar", "application.jar"]