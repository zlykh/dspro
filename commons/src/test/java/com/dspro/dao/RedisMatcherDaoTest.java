//package com.dspro.dao;
//
//import com.dspro.enums.RedisConst;
//import com.dspro.StringRedisCallback;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.data.redis.connection.StringRedisConnection;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import java.util.*;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertTrue;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//public class RedisMatcherDaoTest extends BaseRedisTest {
//
//    private RedisMatcherDao matcherDao;
//    private final int hardLimitInDao = 1;
//    private final String ctn1 = "79081331031";
//    private final String ctn2 = "79081331032";
//    private final String ctn3 = "79081331033";
//    private final String ctn4 = "79081331034";
//    private final String ctn5 = "79081331035";
//    private final String myCtn = "79507707885";
//
//    private final String field = "hobby";
//    private final String tag = "music";
//    private final String tag1 = "video";
//    private final String tag2 = "game";
//    private final String tag3 = "books";
//    private final String tag4 = "walking";
//    private final String tag5 = "sex";
//    private final String tagSim = "similar";
//    private final String tagSim2 = "similar2";
//
//    @Before
//    public void setUp() {
//        super.setUp();
//
//        matcherDao = new RedisMatcherDao();
//        matcherDao.stringRedisTemplate = this.stringRedisTemplate;
//    }
//
//    @Test
//    public void getSimilarOnTag_COMMON(){
//        stringRedisTemplate.execute(new StringRedisCallback<Object>() {
//            @Override
//            public Object doInRedis(StringRedisConnection redis) {
//                redis.sAdd(RedisConst.PRT_CTNS +field+":"+tag,ctn1,ctn2,ctn3,ctn4,ctn5);
//                return null;
//            }
//        });
//
//        Set<String> result = matcherDao.getSimilarOnTag(field,tag1);
//        assertEquals(0,result.size());
//
//        result = matcherDao.getSimilarOnTag("ASDf",tag1);
//        assertEquals(0,result.size());
//    }
//
//    @Test
//    public void getSimilarOnTag_SINGLE(){
//        stringRedisTemplate.execute(new StringRedisCallback<Object>() {
//            @Override
//            public Object doInRedis(StringRedisConnection redis) {
//                redis.sAdd(RedisConst.PRT_CTNS +field+":"+tag,ctn1,ctn2,ctn3,ctn4,ctn5);
//                return null;
//            }
//        });
//        Set<String> result = matcherDao.getSimilarOnTag(field,tag);
//        assertEquals(hardLimitInDao,result.size());
//    }
//
//    @Test
//    public void getSimilarOnTag_SINGLE_LIMIT(){
//        stringRedisTemplate.execute(new StringRedisCallback<Object>() {
//            @Override
//            public Object doInRedis(StringRedisConnection redis) {
//                redis.sAdd(RedisConst.PRT_CTNS +field+":"+tag,ctn1,ctn2,ctn3,ctn4,ctn5);
//                return null;
//            }
//        });
//        Set<String> result = matcherDao.getSimilarOnTag(field,tag,3);
//        assertEquals(3,result.size());
//    }
//
//    @Test
//    public void getSimilarOnTag_SINGLE_LIMIT_NOT_ENOUGH(){
//        stringRedisTemplate.execute(new StringRedisCallback<Object>() {
//            @Override
//            public Object doInRedis(StringRedisConnection redis) {
//                redis.sAdd(RedisConst.PRT_CTNS +field+":"+tag,ctn1);
//                return null;
//            }
//        });
//        Set<String> result = matcherDao.getSimilarOnTag(field,tag,3);
//        assertEquals(hardLimitInDao,result.size());
//    }
//    @Test
//    public void getSimilarOnTags_MULTI(){
//        stringRedisTemplate.execute(new StringRedisCallback<Object>() {
//            @Override
//            public Object doInRedis(StringRedisConnection redis) {
//                redis.sAdd(RedisConst.PRT_CTNS +field+":"+tag,ctn1,ctn2,ctn3,ctn4,ctn5);
//                redis.sAdd(RedisConst.PRT_CTNS +field+":"+tag1,ctn2,ctn4,ctn5);
//                redis.sAdd(RedisConst.PRT_CTNS +field+":"+tag2,ctn3,ctn4);
//                redis.sAdd(RedisConst.PRT_CTNS +field+":"+tag3,ctn2,ctn1);
//                redis.sAdd(RedisConst.PRT_CTNS +field+":"+tag4,"");
//                return null;
//            }
//        });
//
//        Set<String> result = matcherDao.getSimilarOnTags(field, Collections.emptyList());
//        assertTrue(result.isEmpty());
//
//        List<String> tags = new ArrayList<>();
//        tags.add(tag5);
//        result = matcherDao.getSimilarOnTags(field,tags);
//        assertTrue(result.isEmpty());
//
//
//        tags = new ArrayList<>();
//        tags.add(tag4);
//        result = matcherDao.getSimilarOnTags(field,tags);
//        assertEquals(0,result.size());
//
//        tags = new ArrayList<>();
//        tags.add(tag1);
//        tags.add(tag2);
//        tags.add(tag3);
//        tags.add(tag4);
//        tags.add(tag5);
//        Set<String> newresult = new HashSet<>();
//
//        for(int i =0; i < 1000; i++){
//            newresult.addAll(matcherDao.getSimilarOnTags(field,tags));
//        }
//        assertTrue(newresult.size() > 1 && newresult.size() < 6);
//
//    }
//
//    @Test
//    public void getSimilarOnTags_MULTI_LIMIT(){
//        stringRedisTemplate.execute(new StringRedisCallback<Object>() {
//            @Override
//            public Object doInRedis(StringRedisConnection redis) {
//                redis.sAdd(RedisConst.PRT_CTNS +field+":"+tag2,ctn3,ctn4);
//                redis.sAdd(RedisConst.PRT_CTNS +field+":"+tag3,ctn2,ctn1);
//                redis.sAdd(RedisConst.PRT_CTNS +field+":"+tag4,"");
//                return null;
//            }
//        });
//
//        Set<String> result = matcherDao.getSimilarOnTags(field, Collections.emptyList(),3);
//        assertTrue(result.isEmpty());
//
//        List<String> tags = new ArrayList<>();
//        tags.add(tag5);
//        result = matcherDao.getSimilarOnTags(field,tags,3);
//        assertTrue(result.isEmpty());
//
//
//        tags = new ArrayList<>();
//        tags.add(tag4);
//        result = matcherDao.getSimilarOnTags(field,tags,3);
//        assertEquals(0,result.size());
//
//        tags = new ArrayList<>();
//        tags.add(tag2);
//        tags.add(tag3);
//        Set<String> newresult = new HashSet<>();
//        System.out.println(newresult);
//        for(int i =0; i < 1000; i++){
//            newresult.addAll(matcherDao.getSimilarOnTags(field,tags,3));//no berem po 2, tk vsego 2
//        }
//        assertEquals(4,newresult.size());//vse 2 po 2 raznie
//    }
//}