package com.dspro.enums;

public class RedisConst {
    public static final String CTNS = "ctns";//set
    public static final String PROFILE = "profile:";//hmap
    public static final String CL = "cl:";
    public static final String PROFILE_TAGS = "pt:";//set pt:hobby:<ctn> t1,t2
    public static final String PROFILE_TAGS_PUB = "ptp:";

    public static final String TAGS_BY_CAT = "tbc:";//яset tbc:hobby 1 t1, 14 t2
    public static final String TAGS_BY_CAT_PUB = "tbcp:";
    public static final String CTNS_BY_CAT_TAG = "cbct:";
    public static final String CTNS_BY_CAT_TAG_PUB = "cbctp:";
    public static final String UBU = "ubu:";

    public static final String OV = "ov:";
    public static final String OP = "op:";

    public static final String MQ_FLUSHDB = "flushdb";

}
