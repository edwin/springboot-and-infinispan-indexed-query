package com.edw.bean;

import org.infinispan.api.annotations.indexing.Basic;
import org.infinispan.api.annotations.indexing.Indexed;
import org.infinispan.protostream.annotations.ProtoField;

import java.io.Serializable;

/**
 * <pre>
 *     com.edw.bean.User
 * </pre>
 *
 * @author Muhammad Edwin < edwin at redhat dot com >
 * 14 Nov 2023 21:42
 */
@Indexed(index = "index01")
public class User implements Serializable {

    private String name;

    private Integer age;

    private String address;

    public User() {
    }

    public User(String name, Integer age, String address) {
        this.name = name;
        this.age = age;
        this.address = address;
    }

    @ProtoField(number = 1, required = true)
    @Basic(sortable = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ProtoField(number = 2)
    @Basic(sortable = true)
    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @ProtoField(number = 3)
    @Basic
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
