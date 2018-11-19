FROM gcr.io/distroless/java:latest

COPY maven /usr/local/application/

EXPOSE 8080

WORKDIR /usr/local/application

ENTRYPOINT []

CMD ["/usr/bin/java", "-cp", "./*", "com.github.phoswald.ratpack.demo.service.Application"]
