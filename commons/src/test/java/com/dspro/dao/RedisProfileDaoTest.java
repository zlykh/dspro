//package com.dspro.dao;
//
//import com.dspro.domain.Profile;
//import com.dspro.enums.RedisConst;
//import com.dspro.StringRedisCallback;
//import org.apache.commons.lang3.StringUtils;
//import org.junit.Before;
//import org.junit.Ignore;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.data.redis.connection.StringRedisConnection;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import java.util.*;
//
//import static org.junit.Assert.*;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//public class RedisProfileDaoTest extends BaseRedisTest{
//
//    private RedisProfileDao profileDao;
//    private final String ctn = "79081331031";
//
//    @Before
//    public void setUp(){
//        super.setUp();
//
//        profileDao = new RedisProfileDao();
//        profileDao.stringRedisTemplate = this.stringRedisTemplate;
//    }
//
//    @Test
//    public void getProfile(){
//        stringRedisTemplate.execute(new StringRedisCallback<Object>() {
//            @Override
//            public Object doInRedis(StringRedisConnection redis) {
//                Map<String,String> profile = new HashMap<>();
//                profile.put("name","ivan");// сделатьв се на статике PRofileFields или типа того
//                profile.put("hobby","music,movie");
//                redis.hMSet(RedisConst.PROFILE+ctn,profile);
//
//                return null;
//            }
//        });
//
//        Profile result = profileDao.getProfile(ctn);
//        List<String> hobby = Arrays.asList(StringUtils.split(result.get("hobby"),","));
//        assertEquals(2,hobby.size());
//        assertTrue(hobby.contains("music"));
//        assertTrue(hobby.contains("movie"));
//    }
//
//
//    @Test
//    public void getRandomCtns(){
//        stringRedisTemplate.execute(new StringRedisCallback<Object>() {
//            @Override
//            public Object doInRedis(StringRedisConnection redis) {
//                redis.sAdd("ctns","ctn1","ctn2","ctn3","ctn4");
//               /* Map<String,String> profile = new HashMap<>();
//                profile.put("name","ivan");
//                profile.put("hobby","music,movie,books");
//                redis.hMSet(RedisConst.PROFILE+ctn,profile);
//
//                profile = new HashMap<>();
//                profile.put("name","marina");
//                profile.put("hobby","sex,rocknroll");
//                redis.hMSet(RedisConst.PROFILE+ctn,profile);
//
//                profile = new HashMap<>();
//                profile.put("name","slavik");
//                profile.put("hobby","drugs");
//                redis.hMSet(RedisConst.PROFILE+ctn,profile);*/
//
//                return null;
//            }
//        });
//
//        Set<String> result = profileDao.getRandomCtns(2);
//
//        assertEquals(2,result.size());
//    }
//
//    @Test
//    public void getRandomNotEnoughCtns(){
//        stringRedisTemplate.execute(new StringRedisCallback<Object>() {
//            @Override
//            public Object doInRedis(StringRedisConnection redis) {
//                redis.sAdd("ctns","ctn1");
//
//
//                return null;
//            }
//        });
//
//        Set<String> result = profileDao.getRandomCtns(3);
//
//        assertEquals(1,result.size());
//    }
//
//    @Test
//    public void getProfileField(){
//        stringRedisTemplate.execute(new StringRedisCallback<Object>() {
//            @Override
//            public Object doInRedis(StringRedisConnection redis) {
//                Map<String,String> profile = new HashMap<>();
//                profile.put("hobby","music,movie");
//                profile.put("jobType","worker");
//                redis.hMSet(RedisConst.PROFILE+ctn,profile);
//
//                return null;
//            }
//        });
//
//        String result = profileDao.getProfileField(ctn,"hobby");
//        assertEquals("music,movie",result);
//
//        result = profileDao.getProfileField(ctn,"jobType");
//        assertEquals("worker",result);
//
//        result = profileDao.getProfileField(ctn,"notexistedfiled");
//        assertNull(result);
//
//        result = profileDao.getProfileField(ctn,"");
//        assertNull(result);
//
//    }
//
//    @Test
//    public void getConnections(){
//        stringRedisTemplate.execute(new StringRedisCallback<Object>() {
//            @Override
//            public Object doInRedis(StringRedisConnection redis) {
//                redis.zAdd(RedisConst.CL+ctn,0,"ctn1");
//                redis.zAdd(RedisConst.CL+ctn,0,"ctn2");
//                redis.zAdd(RedisConst.CL+ctn,1,"ctn3");
//                redis.zAdd(RedisConst.CL+ctn,1,"ctn4");
//                redis.zAdd(RedisConst.CL+ctn,1,"ctn5");
//                redis.zAdd(RedisConst.CL+ctn,2,"ctn6");
//                redis.zAdd(RedisConst.CL+ctn,2,"ctn7");
//
//                return null;
//            }
//        });
//
//        Set<String> result = profileDao.getConnections(ctn,true);
//        assertEquals(5,result.size());
//
//        result = profileDao.getConnections(ctn,false);
//        assertEquals(7,result.size());
//
//    }
//}
