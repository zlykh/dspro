package com.dspro.logic;

import com.dspro.domain.Profile;
import com.dspro.dto.TagDTO;
import com.dspro.enums.RedisConst;
import org.springframework.data.redis.connection.StringRedisConnection;

import java.util.HashMap;
import java.util.Map;

public class UserByUserWriter extends ListRedisWriter<TagDTO> {
    private final Map<String, String> map = new HashMap<>();

    @Override
    protected void process(TagDTO item, StringRedisConnection redis) {
        String unlockedTags = redis.hGet(RedisConst.UBU + Profile.AIMS + ":" + item.ctn, item.ctn);

        if (unlockedTags == null) {
            unlockedTags = item.tag;
        } else {
            unlockedTags = unlockedTags + "," + item.tag;
        }

        map.put(item.ctn, unlockedTags);

        redis.hMSet(RedisConst.UBU + Profile.AIMS + ":" + item.ctn, map);

        map.clear();

    }
}
