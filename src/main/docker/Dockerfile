FROM openjdk:8-jre-alpine
COPY maven/ratpack-demo-service.jar /usr/local/application/
COPY maven/lib                      /usr/local/application/lib
EXPOSE 8080
WORKDIR /usr/local/application
CMD exec java -cp "ratpack-demo-service.jar:lib/*" com.github.phoswald.sample.Application
