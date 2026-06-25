FROM eclipse-temurin:21-jre-jammy
VOLUME /tmp
COPY server/target/*.jar app.jar
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar"]
ENV TZ=Europe/Moscow
EXPOSE 9090