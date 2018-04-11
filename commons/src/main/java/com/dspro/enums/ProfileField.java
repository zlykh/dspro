package com.dspro.enums;

public enum ProfileField {
    NAME("NAME", "name"),
    CTN("PROFILE_ID", "ctn"),
    AGE("AGE", "age"),
    CITY("CITY", "city");

    public final String db;
    public final String redis;

    private ProfileField(String dbKey, String redisKey) {
        this.db = dbKey;
        this.redis = redisKey;
    }

}
