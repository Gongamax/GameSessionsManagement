FROM gradle:7.4.2-jdk17 as builder

WORKDIR /opt/isel/ls
COPY build.gradle.kts ./
COPY src ./src
COPY static-content ./static-content
COPY gradle ./gradle
RUN gradle build && gradle jar

FROM openjdk:17-jdk-alpine3.13
WORKDIR /opt/isel/ls
COPY --from=builder /opt/isel/ls/ .
EXPOSE 1904
CMD ["java", "-jar", "./build/libs/ls.jar"]
