package com.dspro.logic.profile;

import com.dspro.ProfileMapper;
import com.dspro.domain.Profile;
import com.dspro.enums.RedisConst;
import com.dspro.logic.ListRedisWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.StringRedisConnection;

import java.util.Map;

public class CtnProfileWriter extends ListRedisWriter<Profile> {
    private static final Logger log = LoggerFactory.getLogger(CtnProfileWriter.class);

    @Override
    protected void process(Profile item, StringRedisConnection redis) {
        redis.sAdd(RedisConst.CTNS, item.ctn);
        redis.hMSet(getKey(item), getValue(item));

    }

    public String getKey(Profile item) {
        return "_" + RedisConst.PROFILE + item.ctn;
    }

    public Map<String, String> getValue(Profile item) {
        return ProfileMapper.toMap(item);
    }
}
