# ratpack-demo-service

Experiments with Ratpack

## Run Standalone 

    $ mvn clean verify
    $ java -cp 'target/*:target/lib/*' com.github.phoswald.ratpack.demo.service.Application

## Run with Docker 

    $ mvn clean verify -P docker
    $ docker run -it --rm -p 8080:8080 ratpack-demo-service:1.0.0-SNAPSHOT

## URLs

- http://localhost:8080/

~~~
$ curl 'http://localhost:8080/about'
$ curl 'http://localhost:8080/now'
$ curl 'http://localhost:8080/mem'
$ curl 'http://localhost:8080/mem/foo?value=bar' -X PUT
$ curl 'http://localhost:8080/file?path=/tmp/message.txt'
$ curl 'http://localhost:8080/file?path=/tmp/message.txt&content=hello' -X PUT
$ curl 'http://localhost:8080/log?message=hello' -X POST
$ curl 'http://localhost:8080/exit' -X POST
~~~
