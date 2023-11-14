# About
A sample Spring Boot 3 and Infinispan integration. Doing a rest api to Spring Boot to do a query searching for records based on specific property.

## Problem Statement
There are WARN logs inside Infinispan server logs regarding `non-indexed query`
```
2023-11-14 15:02:24,141 WARN  (blocking-thread--p3-t1) [org.infinispan.query.core.impl.BaseEmbeddedQuery] ISPN014827: 
        Distributed sort not supported for non-indexed query 'FROM user.User WHERE address = :address order by name desc, age asc'. Consider using an index for optimal performance.
2023-11-14 15:02:30,932 WARN  (blocking-thread--p3-t1) [org.infinispan.query.core.impl.BaseEmbeddedQuery] ISPN014827: 
        Distributed sort not supported for non-indexed query 'FROM user.User WHERE address = :address order by name desc, age asc'. Consider using an index for optimal performance.
```

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

## How to Solve
- import `infinispan-api` from `pom.xml`
- use `@Basic` annotation on the required properties in `User.java`
- create `user-cache` in `Infinispan` using below XML configuration, having `User` proto as indexed entity.

```xml
<?xml version="1.0"?>
<distributed-cache name="user-cache" owners="1" mode="SYNC" statistics="true">
    <indexing enabled="true"
              storage="local-heap">
        <index-reader refresh-interval="1000"/>
        <indexed-entities>
            <indexed-entity>user.User</indexed-entity>
        </indexed-entities>
    </indexing>
    <encoding>
        <key media-type="application/x-protostream"/>
        <value media-type="application/x-protostream"/>
    </encoding>
    <locking isolation="REPEATABLE_READ"/>
</distributed-cache>
```

## Build the App
```
$ mvn clean package -s settings.xml
```