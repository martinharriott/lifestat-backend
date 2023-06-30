FROM eclipse-temurin:19
COPY target/lifestat-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]