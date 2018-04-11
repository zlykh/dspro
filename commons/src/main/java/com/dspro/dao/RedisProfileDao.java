package com.dspro.dao;

import com.dspro.ProfileMapper;
//import com.dspro.dao.iface.ProfileDao;
import com.dspro.dao.iface.ProfileDao;
import com.dspro.domain.Profile;
import com.dspro.enums.RedisConst;
import com.dspro.StringRedisCallback;
import com.google.common.collect.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

import static com.dspro.enums.RedisConst.*;

/**
 * Created by dmitry.zlykh
 */
@Repository
public class RedisProfileDao implements ProfileDao {
    private static final Logger log = LoggerFactory.getLogger(RedisProfileDao.class);

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Override
    public Set<String> getRandomCtns(int limit) {
        Set<String> random = stringRedisTemplate.boundSetOps(CTNS).distinctRandomMembers(limit);
        return random;
    }

    @Override
    public boolean exists(String ctn) {
        return stringRedisTemplate.boundSetOps(CTNS).isMember(ctn);
    }

    @Override
    public Set<String> getProfileTags(String cat, String ctn) {
        return stringRedisTemplate.boundSetOps(PROFILE_TAGS + cat + ":" + ctn).members();
    }

    @Override
    public Set<String> getProfileTagsPublic(String cat, String ctn) {
        return stringRedisTemplate.boundSetOps(PROFILE_TAGS_PUB + cat + ":" + ctn).members();
    }

    @Override
    public Profile getProfile(String ctn) {
        Profile p = stringRedisTemplate.execute(new StringRedisCallback<Profile>() {
            @Override
            public Profile doInRedis(StringRedisConnection redis) {
                Map<String, String> profileMap = redis.hGetAll(PROFILE + ctn);
                return ProfileMapper.fromMap(profileMap);
            }
        });

        p.ctn = ctn;

        return p;
    }

    @Override
    public void savePublicTags(Profile oldProfile, Multimap<String, String> newPublicTags) {
        log.debug("save public tags {}: {} -> {}", oldProfile.ctn, oldProfile.categories, newPublicTags);

        List<String> categories = Lists.newArrayList(Profile.HOBBY, Profile.JOB, Profile.AIMS);
        //pipeline prevents exists to return
        stringRedisTemplate.execute(new StringRedisCallback<Object>() {
            @Override
            public Object doInRedis(StringRedisConnection redis) {
                for (String category : categories) {

                    final Set<String> nTags = (Set<String>) newPublicTags.get(category);//(Set<String>) entry.getValue();
                    final Set<String> oTags = (Set<String>) oldProfile.categories.get(category);//(Set<String>) entry.getValue();
                    final String ptKey = PROFILE_TAGS_PUB + category + ":" + oldProfile.ctn;

                    if (nTags == null || nTags.isEmpty()) {
                        if (oTags == null || oTags.isEmpty()) {
                            continue;
                        } else {
                            removePublicTags(redis, ptKey, category, oldProfile, oTags);
                        }
                        continue;
                    }

                    if (!redis.exists(ptKey)) {
                        addPublicTags(redis, ptKey, category, oldProfile, nTags);
                        continue;
                    }

                    final Set<String> toRemove = Sets.difference(oTags, nTags);
                    final Set<String> toAdd = Sets.difference(nTags, oTags);

                    if (!toAdd.isEmpty()) {
                        addPublicTags(redis, ptKey, category, oldProfile, toAdd);
                    }

                    if (!toRemove.isEmpty()) {
                        removePublicTags(redis, ptKey, category, oldProfile, toRemove);

                    }
                }

                return null;
            }
        });
    }


    @Override
    public void saveTags(Profile oldProfile, Multimap<String, String> newTags) {
        log.debug("save private tags {}: {} -> {}", oldProfile.ctn, oldProfile.tags, newTags);

        List<String> categories = Lists.newArrayList(Profile.HOBBY, Profile.JOB, Profile.AIMS);
        //pipeline prevents exists to return
        stringRedisTemplate.execute(new StringRedisCallback<Object>() {
            @Override
            public Object doInRedis(StringRedisConnection redis) {
                for (String category : categories) {

                    final Set<String> nTags = (Set<String>) newTags.get(category);//(Set<String>) entry.getValue();
                    final Set<String> oTags = (Set<String>) oldProfile.tags.get(category);//(Set<String>) entry.getValue();
                    final String ptKey = PROFILE_TAGS + category + ":" + oldProfile.ctn;

                    if (nTags == null || nTags.isEmpty()) {
                        if (oTags == null || oTags.isEmpty()) {
                            continue;
                        } else {
                            removeTags(redis, ptKey, category, oldProfile, oTags);
                        }
                        continue;
                    }

                    if (!redis.exists(ptKey)) {
                        addTags(redis, ptKey, category, oldProfile, nTags);
                        continue;
                    }

                    final Set<String> toRemove = Sets.difference(oTags, nTags);
                    final Set<String> toAdd = Sets.difference(nTags, oTags);

                    if (!toAdd.isEmpty()) {
                        addTags(redis, ptKey, category, oldProfile, toAdd);
                    }

                    if (!toRemove.isEmpty()) {
                        removeTags(redis, ptKey, category, oldProfile, toRemove);

                    }
                }

                return null;
            }
        });
    }

    private void removeTags(StringRedisConnection redis, String ptKey, String category, Profile oldProfile, Set<String> oTags) {
        redis.sRem(ptKey, oTags.toArray(new String[oTags.size()]));
        oTags.forEach(tag -> {
            redis.zIncrBy(TAGS_BY_CAT + category, -1, tag);
            redis.sRem(CTNS_BY_CAT_TAG + category + ":" + tag, oldProfile.ctn);
        });
        redis.zRemRangeByScore(TAGS_BY_CAT + category, -Double.MIN_VALUE, 0); //clear negative
    }

    private void removePublicTags(StringRedisConnection redis, String ptKey, String category, Profile oldProfile, Set<String> oTags) {
        redis.sRem(ptKey, oTags.toArray(new String[oTags.size()]));
        oTags.forEach(tag -> {
            redis.zIncrBy(TAGS_BY_CAT_PUB + category, -1, tag);
            redis.sRem(CTNS_BY_CAT_TAG_PUB + category + ":" + tag, oldProfile.ctn);
        });
        redis.zRemRangeByScore(TAGS_BY_CAT_PUB + category, -Double.MIN_VALUE, 0); //clear negative
    }

    private void addTags(StringRedisConnection redis, String ptKey, String category, Profile oldProfile, Set<String> toAdd) {
        redis.sAdd(ptKey, toAdd.toArray(new String[toAdd.size()]));
        toAdd.forEach(tag -> {
            redis.zIncrBy(TAGS_BY_CAT + category, 1, tag);
            redis.sAdd(CTNS_BY_CAT_TAG + category + ":" + tag, oldProfile.ctn);
        });
    }

    private void addPublicTags(StringRedisConnection redis, String ptKey, String category, Profile oldProfile, Set<String> toAdd) {
        redis.sAdd(ptKey, toAdd.toArray(new String[toAdd.size()]));
        toAdd.forEach(tag -> {
            redis.zIncrBy(TAGS_BY_CAT_PUB + category, 1, tag);
            redis.sAdd(CTNS_BY_CAT_TAG_PUB + category + ":" + tag, oldProfile.ctn);
        });
    }


    @Override
    public void saveProfile(Profile profile) {
        stringRedisTemplate.boundHashOps(PROFILE + profile.ctn).putAll(ProfileMapper.toMap(profile));
    }

    @Override
    public Set<String> getCtns() {
        return stringRedisTemplate.opsForSet().members(RedisConst.CTNS);
    }

    @Override
    public List<Profile> getProfiles(List<String> ctns) {
        final ImmutableList.Builder<Profile> builder = ImmutableList.builder();

        List<Object> profileResult = stringRedisTemplate.executePipelined(new StringRedisCallback<Object>() {
            @Override
            public Object doInRedis(StringRedisConnection redis) {
                for (String ctn : ctns) {
                    redis.hGetAll(PROFILE + ctn);
                }
                return null;
            }
        });

        List<Object> tagResult = stringRedisTemplate.executePipelined(new StringRedisCallback<Object>() {
            @Override
            public Object doInRedis(StringRedisConnection redis) {
                for (String ctn : ctns) {
                    redis.sMembers(PROFILE_TAGS + Profile.HOBBY + ":" + ctn);
                    redis.sMembers(PROFILE_TAGS + Profile.AIMS + ":" + ctn);
                    redis.sMembers(PROFILE_TAGS + Profile.JOB + ":" + ctn);
                }
                return null;
            }
        });

        List<Object> tagPublicResult = stringRedisTemplate.executePipelined(new StringRedisCallback<Object>() {
            @Override
            public Object doInRedis(StringRedisConnection redis) {
                for (String ctn : ctns) {
                    redis.sMembers(PROFILE_TAGS_PUB + Profile.HOBBY + ":" + ctn);
                    redis.sMembers(PROFILE_TAGS_PUB + Profile.AIMS + ":" + ctn);
                    redis.sMembers(PROFILE_TAGS_PUB + Profile.JOB + ":" + ctn);
                }
                return null;
            }
        });

        int j = 0;
        for (int i = 0; i < ctns.size(); i++) {
            Profile p = ProfileMapper.fromMap((Map<String, String>) profileResult.get(i));
            p.ctn = ctns.get(i);

            p.tags.putAll(Profile.HOBBY, (LinkedHashSet<String>) tagResult.get(j));
            p.categories.putAll(Profile.HOBBY, (LinkedHashSet<String>) tagPublicResult.get(j));
            j++;

            p.tags.putAll(Profile.AIMS, (LinkedHashSet<String>) tagResult.get(j));
            p.categories.putAll(Profile.AIMS, (LinkedHashSet<String>) tagPublicResult.get(j));

            j++;

            p.tags.putAll(Profile.JOB, (LinkedHashSet<String>) tagResult.get(j));
            p.categories.putAll(Profile.JOB, (LinkedHashSet<String>) tagPublicResult.get(j));
            j++;

            builder.add(p);
        }


        return builder.build();
    }


    @Override
    public Set<String> getOVmembers(String ctn, int count) {
        return stringRedisTemplate.boundZSetOps(OV + ctn).reverseRange(0, count);
    }

    @Override
    public void fromOVtoOP(String subjectCtn, String objectCtn) {
        stringRedisTemplate.executePipelined(new StringRedisCallback<Object>() {
            @Override
            public Object doInRedis(StringRedisConnection redis) {
                redis.zRem(OV + subjectCtn, objectCtn);
                redis.zAdd(OP + subjectCtn, System.currentTimeMillis(), objectCtn);
                return null;
            }
        });
    }

    @Override
    public Set<String> getOPmembers(String ctn, long unixTime) {
        if (unixTime == -1) {
            return stringRedisTemplate.boundZSetOps(OP + ctn).range(0, -1);
        }
        return stringRedisTemplate.boundZSetOps(OP + ctn).reverseRangeByScore(0, unixTime);
    }

    @Override
    public void addOVmembers(String ctn, Map<String, Double> ctnRatings) {
        stringRedisTemplate.executePipelined(new StringRedisCallback<Object>() {
            @Override
            public Object doInRedis(StringRedisConnection redis) {
                ctnRatings.forEach((ctn2, rating) -> {
                    redis.zAdd(OV + ctn, rating, ctn2);
                });
                return null;
            }
        });
    }

    @Override
    public void cleanOVmembers(String ctn) {
        stringRedisTemplate.delete(OV + ctn);
    }

    @Override
    public Map<String, Double> getConnections(String ctn, boolean onlyUnlocked) {
        Set<ZSetOperations.TypedTuple<String>> tuples;
        if (onlyUnlocked) {
            tuples = stringRedisTemplate.boundZSetOps(CL + ctn).rangeByScoreWithScores(1, Double.MAX_VALUE);
            return tuples.stream().collect(Collectors.toMap(p -> p.getValue(), p -> p.getScore()));
        } else {
            tuples = stringRedisTemplate.boundZSetOps(CL + ctn).rangeWithScores(0, -1);
            return tuples.stream().collect(Collectors.toMap(p -> p.getValue(), p -> p.getScore()));
        }
    }

    @Override
    public HashMultimap<String, String> getUnlockedTagsUserbyUser(String category, String ctnSubject, Collection<String> objectCtnCollection) {
        List<String> ctnObjectList = Lists.newArrayList(objectCtnCollection);
        final HashMultimap<String, String> ctnToTags = HashMultimap.create();
        List<String> tagStrings = stringRedisTemplate.<String, String>boundHashOps(UBU + category + ":" + ctnSubject).multiGet(ctnObjectList);

        for (int i = 0; i < ctnObjectList.size(); i++) {
            if (StringUtils.isNotBlank(tagStrings.get(i))) {
                ctnToTags.putAll(ctnObjectList.get(i), Arrays.asList(tagStrings.get(i).split(",")));
            }
        }
        return ctnToTags;
    }

    @Override
    public Set<String> getCategoryTags(String category) {
        Set<String> members = stringRedisTemplate.boundZSetOps(TAGS_BY_CAT + category).range(0, Integer.MAX_VALUE);
        return members;
    }

    @Override
    public Set<String> getCategoryPublicTags(String category) {
        Set<String> members = stringRedisTemplate.boundZSetOps(TAGS_BY_CAT_PUB + category).range(0, Integer.MAX_VALUE);
        return members;
    }

    @Override
    public void addToCtnGlobalList(String ctn) {
        stringRedisTemplate.boundSetOps(RedisConst.CTNS).add(ctn);
    }

    @Override
    public void addCtnToCategoryTag(String category, String tag, String ctn) {
        stringRedisTemplate.boundSetOps(RedisConst.CTNS_BY_CAT_TAG + category + ":" + tag).add(ctn);

    }

    @Override
    public void addCtnToCategoryPublicTag(String category, String tag, String ctn) {
        stringRedisTemplate.boundSetOps(RedisConst.CTNS_BY_CAT_TAG_PUB + category + ":" + tag).add(ctn);

    }

    @Override
    public void unlock(String from, String to, String category, String tag) {
        Object tagStringObject = stringRedisTemplate.boundHashOps(RedisConst.UBU + category + ":" + from).get(to);
        if (tagStringObject != null) { //otherwise null serialize as "null" string
            String tagString = (String) tagStringObject;
            stringRedisTemplate.boundHashOps(RedisConst.UBU + category + ":" + from).put(to, tagString + "," + tag);
        } else {
            stringRedisTemplate.boundHashOps(RedisConst.UBU + category + ":" + from).put(to, tag);
        }

    }

    @Override
    public void addConnection(String ctnSubject, String ctnObject, double unlockLevel) {
        stringRedisTemplate.boundZSetOps(RedisConst.CL + ctnSubject).add(ctnObject, unlockLevel);
    }

}
