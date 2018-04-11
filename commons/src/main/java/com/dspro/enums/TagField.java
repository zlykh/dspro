package com.dspro.enums;

public enum TagField {
    NAME("NAME", "name"),
    ID("TAG_ID", "id");


    public final String db;
    public final String redis;

    private TagField(String dbKey, String redisKey) {
        this.db = dbKey;
        this.redis = redisKey;
    }

}
