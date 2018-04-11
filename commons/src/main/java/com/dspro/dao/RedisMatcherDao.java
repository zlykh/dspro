package com.dspro.dao;

import com.dspro.dao.iface.MatcherDao;
import com.dspro.StringRedisCallback;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.dspro.enums.RedisConst.CTNS_BY_CAT_TAG;

/**
 * Created by dmitry.zlykh
 */
@Repository
public class RedisMatcherDao implements MatcherDao {
    private static final Logger log = LoggerFactory.getLogger(RedisMatcherDao.class);

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Override
    public Set<String> getSimilarOnTag(String category, String tag, int count) {
        return stringRedisTemplate.boundSetOps(CTNS_BY_CAT_TAG + category + ":" + tag).distinctRandomMembers(count);
    }

    @Override
    public Multimap<String, String> getSimilarOnTags(String category, Collection<String> tagsCollection) {
        List<String> tagList = Lists.newArrayList(tagsCollection);
        final List<Object> result = stringRedisTemplate.executePipelined(new StringRedisCallback<List<Object>>() {
            @Override
            public List<Object> doInRedis(StringRedisConnection redis) {
                for (String tag : tagList) {
                    redis.sMembers(CTNS_BY_CAT_TAG + category + ":" + tag);
                }
                return null;
            }
        });

        final Multimap<String, String> tagsToCtns = HashMultimap.create();

        for (int i = 0; i < tagList.size(); i++) {
            final Set<String> ctnList = (Set<String>) result.get(i);
            if (ctnList != null && !ctnList.isEmpty()) {
                tagsToCtns.putAll(tagList.get(i), ctnList);
            }
        }

        return tagsToCtns;
    }

}
