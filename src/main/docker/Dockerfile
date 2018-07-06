FROM openjdk:8-jre-alpine

COPY maven /usr/local/application/

EXPOSE 8080

WORKDIR /usr/local/application

CMD java -cp "./*" com.github.phoswald.ratpack.demo.service.Application
