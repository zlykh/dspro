package com.dspro.enums;

public enum CategoryField {
    NAME("NAME", "name"),
    ID("CATEGORY_ID", "id");


    public final String db;
    public final String redis;

    private CategoryField(String dbKey, String redisKey) {
        this.db = dbKey;
        this.redis = redisKey;
    }

}
