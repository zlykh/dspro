package com.dspro.enums;

public enum LNKField {
    NAME("NAME", "name"),
    ID("CATEGORY_ID", "id");


    public final String db;
    public final String redis;

    private LNKField(String dbKey, String redisKey) {
        this.db = dbKey;
        this.redis = redisKey;
    }

}
