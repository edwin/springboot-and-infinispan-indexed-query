# About
A sample Spring Boot 3 and Infinispan integration. Doing a rest api to Spring Boot to do a query searching for records based on specific property.

## Running Infinispan
Pull from Docker Hub
```
$ docker pull infinispan/server:14.0.2.Final
```

Run the first instance
```
$ docker run -p 11222:11222 -e USER=admin -e PASS=password \
        --add-host=HOST:192.168.56.1 \ 
        infinispan/server:14.0.2.Final
```

## Build the App
```
$ mvn clean package -s settings.xml
```