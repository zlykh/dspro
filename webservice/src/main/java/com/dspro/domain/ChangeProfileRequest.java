package com.dspro.domain;

import com.google.common.collect.HashMultimap;

/**
 * Created by dmitry.zlykh
 */
public class ChangeProfileRequest {
    public String ctn;
    public String name;
    public Integer age;
    public String city;
    public HashMultimap<String, String> tags;

    public ChangeProfileRequest() {
    }


    @Override
    public String toString() {
        return "ChangeProfileRequest{" +
                "ctn='" + ctn + '\'' +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", city='" + city + '\'' +
                ", city='" + tags + '\'' +
                '}';
    }
}
