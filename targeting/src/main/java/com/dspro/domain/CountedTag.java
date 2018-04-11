package com.dspro.domain;

public class CountedTag {
    public Integer count;
    public String tag;
    String ctn;

    public CountedTag(String ctn, String tag, Integer count) {
        this.ctn = ctn;
        this.count = count;
        this.tag = tag;
    }
}
