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

## How to Test
```
$ curl -kv http://localhost:8080/add-user?name=cccc&age=92&address=Jogja
*   Trying ::1:8080...
* TCP_NODELAY set
* Connected to localhost (::1) port 8080 (#0)
> GET /add-user?name=cccc&age=92&address=Jogja HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.65.0
> Accept: */*
>
* Mark bundle as not supporting multiuse
< HTTP/1.1 200
< Content-Type: application/json
< Transfer-Encoding: chunked
< Date: Tue, 14 Nov 2023 15:55:56 GMT
<
* Connection #0 to host localhost left intact
{"name":"cccc","age":92,"address":"Jogja"}


$ curl -kv http://localhost:8080/add-user?name=bbb&age=101&address=Jogja
*   Trying ::1:8080...
* TCP_NODELAY set
* Connected to localhost (::1) port 8080 (#0)
> GET /add-user?name=bbb&age=101&address=Jogja HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.65.0
> Accept: */*
* Mark bundle as not supporting multiuse
< HTTP/1.1 200
< Content-Type: application/json
< Transfer-Encoding: chunked
< Date: Tue, 14 Nov 2023 15:55:35 GMT
<
* Connection #0 to host localhost left intact
{"name":"bbb","age":101,"address":"Jogja"} 


$ curl -kv http://localhost:8080/add-user?name=aaaa&age=102&address=Jogja
*   Trying ::1:8080...
* TCP_NODELAY set
* Connected to localhost (::1) port 8080 (#0)
> GET /add-user?name=aaaa&age=102&address=Jogja HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.65.0
> Accept: */*
>
* Mark bundle as not supporting multiuse
< HTTP/1.1 200
< Content-Type: application/json
< Transfer-Encoding: chunked
< Date: Tue, 14 Nov 2023 15:55:49 GMT
<
* Connection #0 to host localhost left intact
{"name":"aaaa","age":102,"address":"Jogja"} 


$ curl -kv http://localhost:8080/get-user-from-address?address=Jogja"
*   Trying ::1:8080...
* TCP_NODELAY set
* Connected to localhost (::1) port 8080 (#0)
> GET /get-user-from-address?address=Jogja HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.65.0
> Accept: */*
>
* Mark bundle as not supporting multiuse
< HTTP/1.1 200
< Content-Type: application/json
< Transfer-Encoding: chunked
< Date: Tue, 14 Nov 2023 15:55:59 GMT
<
* Connection #0 to host localhost left intact
[{"name":"cccc","age":92,"address":"Jogja"},{"name":"bbb","age":101,"address":"Jogja"},{"name":"aaaa","age":102,"address":"Jogja"}] 
```

## Sample Proto Result
```
// File name: user.proto

syntax = "proto2";

package user;



/**
 * @Indexed(index="index01", enabled=true)
 */
message User {
   
   /**
    * @Basic(name="", sortable=true, projectable=false, aggregable=false, indexNullAs="__Infinispan_indexNullAs_doNotIndexNull", searchable=true)
    */
   required string name = 1;
   
   /**
    * @Basic(name="", sortable=true, projectable=false, aggregable=false, indexNullAs="__Infinispan_indexNullAs_doNotIndexNull", searchable=true)
    */
   optional int32 age = 2;
   
   /**
    * @Basic(name="", sortable=false, projectable=false, aggregable=false, indexNullAs="__Infinispan_indexNullAs_doNotIndexNull", searchable=true)
    */
   optional string address = 3;
}
```