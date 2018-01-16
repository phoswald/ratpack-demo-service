Build:

    $ mvn clean verify
    $ ./run.sh
    
Docker:

    $ mvn clean verify -P docker
    $ docker run -d --rm --name my-rat -p 8080:8080 -e MY_CALLER="Running in Docker" ratpack-demo-service:latest

Usage:

    $ curl http://localhost:8080/
    $ curl http://localhost:8080/health
    $ curl http://localhost:8080/now
    $ curl http://localhost:8080/req?foo=bar
    $ curl http://localhost:8080/greet
    $ curl http://localhost:8080/greet?name=Philip
    $ curl http://localhost:8080/args
    $ curl http://localhost:8080/env
    $ curl http://localhost:8080/env/PATH
    $ curl http://localhost:8080/prop
    $ curl http://localhost:8080/prop/os.name
    $ curl http://localhost:8080/mem
    $ curl http://localhost:8080/mem/foo
    $ curl http://localhost:8080/mem/foo?value=bar -X PUT
    $ curl http://localhost:8080/session
    $ curl http://localhost:8080/session?logout
    $ curl http://localhost:8080/log?message=hello -X POST
    $ curl http://localhost:8080/exit -X POST
