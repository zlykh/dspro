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
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import static org.junit.Assert.*;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//public class RedisUnlockDaoTest extends BaseRedisTest {
//
//    private RedisUnlockDao unlockDao;
//    private final String ctnObject = "79081331031";
//    private final String ctnObject2 = "79081331032";
//    private final String ctnObject3 = "79081331033";
//    private final String ctnObject4 = "79081331034";
//    private final String ctnSubject = "79507707885";
//
//    @Before
//    public void setUp(){
//        super.setUp();
//
//        unlockDao = new RedisUnlockDao();
//        unlockDao.stringRedisTemplate = this.stringRedisTemplate;
//    }
//
//    @Test
//    public void getUnlockedTagsUserbyUserSINGLE(){
//        stringRedisTemplate.execute(new StringRedisCallback<Object>() {
//            @Override
//            public Object doInRedis(StringRedisConnection redis) {
//                Map<String,String> profile = new HashMap<>();
//                profile.put(ctnObject,"music,movie");
//                profile.put(ctnObject2,"music,walking,books");
//                profile.put(ctnObject3,"");
//                redis.hMSet(RedisConst.UBU+"hobby:"+ctnSubject,profile);
//                return null;
//            }
//        });
//
//        String result = unlockDao.getUnlockedTagsUserbyUser("hobby",ctnSubject,ctnObject2);
//        assertEquals("music,walking,books",result);
//
//        result = unlockDao.getUnlockedTagsUserbyUser("hobby",ctnSubject,ctnObject3);
//        assertTrue(result.isEmpty());
//
//        result = unlockDao.getUnlockedTagsUserbyUser("hobby",ctnSubject,ctnObject4);
//        assertNull(result);
//    }
//
//    @Test
//    public void getUnlockedTagsUserbyUserMULTI(){
//        stringRedisTemplate.execute(new StringRedisCallback<Object>() {
//            @Override
//            public Object doInRedis(StringRedisConnection redis) {
//                Map<String,String> profile = new HashMap<>();
//                profile.put(ctnObject,"music,movie");
//                profile.put(ctnObject2,"music,walking,books");
//                profile.put(ctnObject3,"sex,walking,rocknroll,music");
//                profile.put(ctnObject4,"");
//                redis.hMSet(RedisConst.UBU+"hobby:"+ctnSubject,profile);
//                return null;
//            }
//        });
//
//        List<String> result = unlockDao.getUnlockedTagsUserbyUser("hobby",ctnSubject, Arrays.asList(new String[]{ctnObject3,ctnObject2,ctnObject4}));
//        assertEquals(3,result.size());
//        assertEquals("sex,walking,rocknroll,music",result.get(0));
//        assertEquals("music,walking,books",result.get(1));
//        assertEquals("",result.get(2));
//    }
//}
