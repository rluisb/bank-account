FROM gradle:6.7.1-jdk11 as gradle

COPY build.gradle.kts /home/gradle/code/
COPY settings.gradle.kts /home/gradle/code/

RUN rm -rf /home/gradle/code/src
COPY ./src /home/gradle/code/src

WORKDIR /home/gradle/code

RUN gradle bootJar -i -s


FROM openjdk:11-jre-slim

COPY --from=gradle /home/gradle/code/build/libs/bankaccount-service-0.0.1-SNAPSHOT.jar bankaccount-service-0.0.1-SNAPSHOT.jar

EXPOSE 8080

ENV JAVA_OPTS -Xmx1024m -Xms1024m -Djava.security.egd=file:/dev/./urandom -Duser.timezone=America/Sao_Paulo

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "bankaccount-service-0.0.1-SNAPSHOT.jar"]