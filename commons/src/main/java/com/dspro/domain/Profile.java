package com.dspro.domain;


import com.google.common.collect.Multimap;

public class Profile implements RandomText, CtnOwner {
    public static final String HOBBY = "hobby";
    public static final String AIMS = "aims";
    public static final String JOB = "job";

    public String ctn;
    public String name;
    public Integer age;
    public String city;
    public Multimap<String, String> tags;
    public Multimap<String, String> categories;

    public Profile(String ctn, String name, Integer age) {
        this.ctn = ctn;
        this.name = name;
        this.age = age;
    }


    public Profile() {
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Profile profile = (Profile) o;

        return ctn.equals(profile.ctn);
    }

    @Override
    public int hashCode() {
        return ctn.hashCode();
    }


    public String getCtn() {
        return ctn;
    }

    @Override
    public String randText(int len) {
        return null;
    }

    @Override
    public String toString() {
        return "Profile{" +
                "ctn='" + ctn + '\'' +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", city='" + city + '\'' +
                ", tags=" + tags +
                ", categories=" + categories +
                // ", quote='" + quote + '\'' +
                '}';
    }
}
