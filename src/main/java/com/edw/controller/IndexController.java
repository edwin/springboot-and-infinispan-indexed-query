package com.edw.controller;

import com.edw.bean.User;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.Search;
import org.infinispan.query.dsl.Query;
import org.infinispan.query.dsl.QueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

/**
 * <pre>
 *     com.edw.controller.IndexController
 * </pre>
 *
 * @author Muhammad Edwin < edwin at redhat dot com >
 * 26 Jun 2023 10:37
 */
@RestController
public class IndexController {

    @Autowired
    private RemoteCacheManager cacheManager;

    @GetMapping(path = "/")
    public HashMap index() {
        return new HashMap(){{
            put("hello", "world");
        }};
    }

    @GetMapping(path = "/get-user")
    public User getUsers(@RequestParam String name) {
        return (User) cacheManager.getCache("user-cache").getOrDefault(name, new User());
    }

    @GetMapping(path = "/get-user-from-address")
    public List<User> getUsersFromAddress(@RequestParam String address) {
        RemoteCache remoteCache = cacheManager.getCache("user-cache");
        QueryFactory queryFactory = Search.getQueryFactory(remoteCache);
        Query<User> query = queryFactory.create("FROM user.User WHERE address = :address order by name desc, age asc");
        query.setParameter("address", address);

        return query.execute().list();
    }

    @GetMapping(path = "/add-user")
    public User addUsers(@RequestParam String name, @RequestParam Integer age, @RequestParam String address) {
        cacheManager.getCache("user-cache").put(name, new User(name, age, address));
        return (User) cacheManager.getCache("user-cache").getOrDefault(name, new User());
    }

}
