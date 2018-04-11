package com.dspro;


import com.dspro.domain.Profile;
import com.dspro.logic.MatcherProcess;
import com.dspro.service.ProfileService;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.dspro.enums.RedisConst.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@TestPropertySource(value = {
        "classpath:conf/local-test.properties",
        "classpath:${config_path}"
})
public class TargetingTest extends BaseRedisTest {
    private static final Logger log = LoggerFactory.getLogger(TargetingTest.class);
    @Autowired
    MatcherProcess process;
    @Autowired
    ProfileService profileService;

    public static final String ctn11 = "111";
    public static final String ctn12 = "122";
    public static final String ctn13 = "133";
    public static final String NoUnlock1 = "000";
    public static final String NoCL1 = "999";
    public static final String NoCL2 = "666";

    public static final String hobby_tag_1 = "hobby_tag_1";
    public static final String hobby_tag_2 = "hobby_tag_2";
    public static final String hobby_tag_3 = "hobby_tag_3";

    public static final String job_tag_1 = "job_tag_1";
    public static final String job_tag_2 = "job_tag_2";
    public static final String job_tag_3 = "job_tag_3";

    public static final String aims_tag_1 = "aims_tag_1";


    @Before
    public void setUp() {
        super.setUp();
        fill();
    }


    @Test
    public void perctTest() {
        System.out.println("p= " + (27 * 100.0) / 46);
    }

    @Test
    public void batch_8() {
        process.startBatch();

        int size = profileService.getOVmembers(ctn11, 999).size();
        assertEquals(2, size);
        size = profileService.getOVmembers(ctn12, 999).size();
        assertEquals(2, size);
        size = profileService.getOVmembers(ctn13, 999).size();
        assertEquals(2, size);
    }

    @Test
    public void SameTagOnDifferentProfilesIncreaseRating_7() {
        Profile p = new Profile();
        p.ctn = "114";
        p.age = 30;
        p.name = "4(144)";
        p.city = "msk";
        p.tags = HashMultimap.create();
        p.categories = HashMultimap.create();

        p.tags.put(Profile.HOBBY, hobby_tag_1);
        p.tags.put(Profile.HOBBY, hobby_tag_3);

        profileService.saveProfile(p);

        p = new Profile();
        p.ctn = "115";
        p.age = 30;
        p.name = "4(145)";
        p.city = "msk";
        p.tags = HashMultimap.create();
        p.categories = HashMultimap.create();

        p.tags.put(Profile.HOBBY, hobby_tag_1);
        p.tags.put(Profile.HOBBY, hobby_tag_2);
        p.tags.put(Profile.HOBBY, hobby_tag_3);

        profileService.saveProfile(p);
        //


        profileService.addConnection(ctn11, ctn12, 0);
        profileService.addConnection(ctn11, ctn13, 0);
        profileService.unlock(ctn11, ctn12, Profile.HOBBY, hobby_tag_1);
        profileService.unlock(ctn11, ctn13, Profile.HOBBY, hobby_tag_1);
        profileService.unlock(ctn11, ctn13, Profile.HOBBY, hobby_tag_2);

        process.startSingle(ctn11);

        assertEquals("115", profileService.getNextMatchedProfile(ctn11).ctn);
        assertEquals("114", profileService.getNextMatchedProfile(ctn11).ctn);

        //todo weight tags??? not used now???
    }

    @Test
    public void BestTagMatchBestRating_6() {
        Profile p = new Profile();
        p.ctn = "114";
        p.age = 21;
        p.name = "4(114)";
        p.city = "msk";
        p.tags = HashMultimap.create();
        p.categories = HashMultimap.create();

        p.tags.put(Profile.HOBBY, hobby_tag_2);
        p.tags.put(Profile.HOBBY, hobby_tag_3);

        p.categories.put(Profile.HOBBY, hobby_tag_1);
        p.categories.put(Profile.HOBBY, hobby_tag_2);

        profileService.saveProfile(p);
        log.debug("---- end  create one more profile 114  ----");
        log.debug("---- start add connection between {} and {}  ----", ctn11, ctn12);


        profileService.addConnection(ctn11, ctn12, 0);
        log.debug("---- end add connection between {} and {}  ----", ctn11, ctn12);
        log.debug("---- start unlock private HOBBY tag_1 between {} and {}  ----", ctn11, ctn12);

        profileService.unlock(ctn11, ctn12, Profile.HOBBY, hobby_tag_1);
        log.debug("---- start unlock private HOBBY tag_2 between {} and {}  ----", ctn11, ctn12);

        profileService.unlock(ctn11, ctn12, Profile.HOBBY, hobby_tag_2);
        log.debug("---- end unlocking between {} and {}  ----", ctn11, ctn12);
        log.debug("---- start update tags for {}  ----", ctn13);


        p = profileService.getProfile(ctn13);
        p.tags = HashMultimap.create();
        p.categories = HashMultimap.create();
        p.tags.put(Profile.JOB, job_tag_1);

        p.tags.put(Profile.HOBBY, hobby_tag_1);
        p.tags.put(Profile.HOBBY, hobby_tag_2);
        p.tags.put(Profile.HOBBY, hobby_tag_3);

        p.categories.put(Profile.HOBBY, hobby_tag_1);

        profileService.saveProfile(p);

        log.debug("---- end update tags for {}  ----", ctn13);
        log.debug(" << start matcher process for {} >> ", ctn11);
        process.startSingle(ctn11);

        assertEquals("114", profileService.getNextMatchedProfile(ctn11).ctn);
        assertEquals(ctn13, profileService.getNextMatchedProfile(ctn11).ctn);
    }

    @Test
    public void OtherTagsNotTargetedTargeting_5_1() {

//
        profileService.addConnection(ctn11, ctn12, 0);
        profileService.unlock(ctn11, ctn12, Profile.HOBBY, hobby_tag_1);


        // here we check that ctn13 dont have such tag and its not considered for offering
        Profile p = profileService.getProfile(ctn13);
        p.tags = HashMultimap.create();
        p.categories = HashMultimap.create();
        // no hobby_tag_1 !!
        p.tags.put(Profile.HOBBY, hobby_tag_2);
        p.tags.put(Profile.HOBBY, hobby_tag_3);

        p.tags.put(Profile.JOB, job_tag_1);
        p.tags.put(Profile.JOB, job_tag_2);
        p.tags.put(Profile.JOB, job_tag_3);

        profileService.saveProfile(p);

        process.startSingle(ctn11);

        // ctn12 in CL - not participate
        // ctn13 doesnt have hobby_tag_1 - not participate
        // self not participate
        // 0 / 3 offers
        assertEquals(0, profileService.getOVmembers(ctn11, 5).size());

    }

    @Test
    public void PrivateWithNoPublicNotUsedInOV_5() {
        // just one more profile
        Profile p = new Profile();
        p.ctn = "test-14";
        p.age = 30;
        p.name = "4(144)";
        p.city = "msk";
        p.tags = HashMultimap.create();
        p.categories = HashMultimap.create();

        p.tags.put(Profile.HOBBY, hobby_tag_1);
        p.tags.put(Profile.HOBBY, hobby_tag_2);
        p.tags.put(Profile.HOBBY, hobby_tag_3);

        p.tags.put(Profile.JOB, job_tag_1);
        p.tags.put(Profile.JOB, job_tag_2);
        p.tags.put(Profile.JOB, job_tag_3);

        profileService.saveProfile(p);


        profileService.addConnection(ctn11, ctn12, 0);
        profileService.unlock(ctn11, ctn12, Profile.HOBBY, hobby_tag_1);


        process.startSingle(ctn11);

        //it is ok that found not only ctn12, but more profiles,
        // because ctn12 only used for determining searchable tags, as they are opened by ctn11
        // ctn12 is in connections, he will not be here
        assertEquals(1, profileService.getOVmembers(ctn11, 5).size());

    }

    @Test
    public void UnlockMoreIncreaseConnRank_4() {
        // unlock if connection exists
        profileService.addConnection(ctn11, ctn12, 0);
        profileService.unlock(ctn11, ctn12, Profile.HOBBY, hobby_tag_1);
        profileService.unlock(ctn11, ctn12, Profile.HOBBY, hobby_tag_2);
        profileService.unlock(ctn11, ctn12, Profile.JOB, job_tag_2);

        HashMultimap<String, String> unlockedTagsUserbyUser = profileService.getUnlockedTagsUserbyUser(Profile.HOBBY, ctn11, Lists.newArrayList(ctn12));
        log.debug("unlockedTagsUserbyUser {}", unlockedTagsUserbyUser);
        assertTrue(unlockedTagsUserbyUser.get(ctn12).contains(hobby_tag_1));
        assertTrue(unlockedTagsUserbyUser.get(ctn12).contains(hobby_tag_2));

        unlockedTagsUserbyUser = profileService.getUnlockedTagsUserbyUser(Profile.JOB, ctn11, Lists.newArrayList(ctn12));
        assertTrue(unlockedTagsUserbyUser.get(ctn12).contains(job_tag_2));

        Map<String, Double> connections = profileService.getConnections(ctn11, false);
        log.debug("connections {}", connections);

        assertEquals(3, connections.get(ctn12).intValue());

    }


    @Test(expected = RuntimeException.class)
    public void CantUnlockIfNoConnections_3_2_1() {
        profileService.unlock(ctn11, ctn12, Profile.HOBBY, hobby_tag_1);

        HashMultimap<String, String> unlockedTagsUserbyUser = profileService.getUnlockedTagsUserbyUser(Profile.HOBBY, ctn11, Lists.newArrayList(ctn12));
        assertTrue(unlockedTagsUserbyUser.isEmpty());

        // unlock if connection exists
        profileService.addConnection(ctn11, ctn12, 0);
        profileService.unlock(ctn11, ctn12, Profile.HOBBY, hobby_tag_1);

        unlockedTagsUserbyUser = profileService.getUnlockedTagsUserbyUser(Profile.HOBBY, ctn11, Lists.newArrayList(ctn12));
        assertTrue(unlockedTagsUserbyUser.containsKey(ctn12));
        assertTrue(unlockedTagsUserbyUser.get(ctn12).contains(hobby_tag_1));
    }

    @Ignore
    @Test
    public void NotUnlockedNotUsedButMatchRuleUsed_3_2() {


        Profile p = profileService.getProfile(ctn12);
        p.tags = HashMultimap.create();
        p.categories = HashMultimap.create();

        p.tags.put(Profile.HOBBY, hobby_tag_1);
        p.tags.put(Profile.HOBBY, hobby_tag_2);
        p.tags.put(Profile.HOBBY, hobby_tag_3);

        p.tags.put(Profile.JOB, job_tag_2);
        p.tags.put(Profile.JOB, job_tag_3);

        profileService.saveProfile(p);

        profileService.addConnection(ctn11, ctn12, 0);

        process.startSingle(ctn11);//ctn12 has rating 5. ctn13 = 6

        assertEquals(2, profileService.getOVmembers(ctn11, 5).size());
    }

    //when no users, give default 9999
    @Test
    public void newProfileUsesMatcherRandProfile_3_1_1() {
        //clear
        stringRedisTemplate.execute((RedisCallback<Object>) redisConnection -> {
            redisConnection.flushDb();
            return null;
        });

        // setup only one profile
        Profile p = new Profile();
        p.ctn = ctn11;
        p.age = 30;
        p.name = "perviy(111)";
        p.city = "msk";
        p.tags = HashMultimap.create();
        p.categories = HashMultimap.create();

        p.tags.put(Profile.HOBBY, hobby_tag_1);
        p.tags.put(Profile.HOBBY, hobby_tag_2);
        p.tags.put(Profile.HOBBY, hobby_tag_3);

        p.tags.put(Profile.JOB, job_tag_1);
        p.tags.put(Profile.JOB, job_tag_2);
        p.tags.put(Profile.JOB, job_tag_3);
        ///

        process.startSingle(ctn11);
        //6 = 1,2,3 in hobby + 1,2,3 injob

        Profile nextMatchedProfile = profileService.getNextMatchedProfile(ctn11);
        log.debug("{}", nextMatchedProfile);

        assertEquals("9999", nextMatchedProfile.ctn);
    }


    @Test
    public void newProfileUsesMatcherRandProfile_3_1() {
        //clear
        stringRedisTemplate.execute((RedisCallback<Object>) redisConnection -> {
            redisConnection.flushDb();
            return null;
        });

        // setup only two profiles
        Profile p = new Profile();
        p.ctn = ctn11;
        p.age = 30;
        p.name = "perviy(111)";
        p.city = "msk";
        p.tags = HashMultimap.create();

        p.tags.put(Profile.HOBBY, hobby_tag_1);
        p.tags.put(Profile.HOBBY, hobby_tag_2);
        p.tags.put(Profile.HOBBY, hobby_tag_3);

        p.tags.put(Profile.JOB, job_tag_1);
        p.tags.put(Profile.JOB, job_tag_2);
        p.tags.put(Profile.JOB, job_tag_3);
        ///
        p = new Profile();
        p.ctn = ctn12;
        p.age = 30;
        p.name = "vtoroy(122)";
        p.city = "msk";
        p.tags = HashMultimap.create();
        p.categories = HashMultimap.create();

        p.tags.put(Profile.HOBBY, hobby_tag_1);
        p.tags.put(Profile.HOBBY, hobby_tag_2);
        p.tags.put(Profile.HOBBY, hobby_tag_3);

        p.tags.put(Profile.JOB, job_tag_1);
        p.tags.put(Profile.JOB, job_tag_2);
        p.tags.put(Profile.JOB, job_tag_3);

        profileService.saveProfile(p);
        ///
        /// 3rd profile with no common, should be randomly given
        p = new Profile();
        p.ctn = ctn13;
        p.age = 30;
        p.name = "tretiy(133)";
        p.city = "msk";
        p.tags = HashMultimap.create();
        p.categories = HashMultimap.create();

        profileService.saveProfile(p);
        //

        profileService.markAsViewed(ctn11, ctn12);


        process.startSingle(ctn11);

        Profile nextMatchedProfile = profileService.getNextMatchedProfile(ctn11);

        assertEquals(ctn13, nextMatchedProfile.ctn);
    }

    @Test
    public void newProfileUsesMatchRule_3() {
        process.startSingle(ctn11);
        assertEquals(2, profileService.getOVmembers(ctn11, 5).size());

    }


    @Test
    public void UpdateProfileUpdates_TagsByCat_and_CtnByCatTag_2() {
        assertEquals(3, stringRedisTemplate.opsForSet().members(CTNS_BY_CAT_TAG + Profile.HOBBY + ":" + hobby_tag_1).size());
        assertEquals(3, stringRedisTemplate.opsForSet().members(CTNS_BY_CAT_TAG + Profile.HOBBY + ":" + hobby_tag_2).size());
        assertEquals(3, stringRedisTemplate.opsForSet().members(CTNS_BY_CAT_TAG + Profile.HOBBY + ":" + hobby_tag_3).size());

        assertEquals(3, stringRedisTemplate.opsForSet().members(CTNS_BY_CAT_TAG + Profile.JOB + ":" + job_tag_1).size());
        assertEquals(3, stringRedisTemplate.opsForSet().members(CTNS_BY_CAT_TAG + Profile.JOB + ":" + job_tag_2).size());
        assertEquals(3, stringRedisTemplate.opsForSet().members(CTNS_BY_CAT_TAG + Profile.JOB + ":" + job_tag_3).size());


        Profile p = profileService.getProfile(ctn11);
        log.warn("in test {}", p);
        p.tags = HashMultimap.create();
        p.categories = HashMultimap.create();

        p.tags.put(Profile.JOB, job_tag_2);
        p.tags.put(Profile.JOB, job_tag_3);

        p.tags.put(Profile.AIMS, aims_tag_1);

        profileService.saveProfile(p);


        //1_2_1
        assertTrue(stringRedisTemplate.hasKey(TAGS_BY_CAT + Profile.HOBBY));
        assertTrue(stringRedisTemplate.hasKey(TAGS_BY_CAT + Profile.JOB));
        assertTrue(stringRedisTemplate.hasKey(TAGS_BY_CAT + Profile.AIMS));

        Set<String> tagsInCat = stringRedisTemplate.boundZSetOps(TAGS_BY_CAT + Profile.HOBBY).range(0, Integer.MAX_VALUE);
        assertEquals(3, tagsInCat.size());
        assertTrue(tagsInCat.contains(hobby_tag_1));
        assertTrue(tagsInCat.contains(hobby_tag_2));
        assertTrue(tagsInCat.contains(hobby_tag_3));

        tagsInCat = stringRedisTemplate.boundZSetOps(TAGS_BY_CAT + Profile.JOB).range(0, Integer.MAX_VALUE);
        assertEquals(3, tagsInCat.size());
        assertTrue(tagsInCat.contains(job_tag_1));
        assertTrue(tagsInCat.contains(job_tag_2));
        assertTrue(tagsInCat.contains(job_tag_3));

        tagsInCat = stringRedisTemplate.boundZSetOps(TAGS_BY_CAT + Profile.AIMS).range(0, Integer.MAX_VALUE);
        assertEquals(1, tagsInCat.size());
        assertTrue(tagsInCat.contains(aims_tag_1));


        // 1_2_2
        assertEquals(2, stringRedisTemplate.opsForSet().members(CTNS_BY_CAT_TAG + Profile.HOBBY + ":" + hobby_tag_1).size());
        assertEquals(2, stringRedisTemplate.opsForSet().members(CTNS_BY_CAT_TAG + Profile.HOBBY + ":" + hobby_tag_2).size());
        assertEquals(2, stringRedisTemplate.opsForSet().members(CTNS_BY_CAT_TAG + Profile.HOBBY + ":" + hobby_tag_3).size());

        assertEquals(2, stringRedisTemplate.opsForSet().members(CTNS_BY_CAT_TAG + Profile.JOB + ":" + job_tag_1).size());
        assertEquals(3, stringRedisTemplate.opsForSet().members(CTNS_BY_CAT_TAG + Profile.JOB + ":" + job_tag_2).size());
        assertEquals(3, stringRedisTemplate.opsForSet().members(CTNS_BY_CAT_TAG + Profile.JOB + ":" + job_tag_3).size());

        assertEquals(1, stringRedisTemplate.opsForSet().members(CTNS_BY_CAT_TAG + Profile.AIMS + ":" + aims_tag_1).size());

    }


    @Test
    public void CtnsByCategoryTagFilled_1_2_2() {
        assertEquals(3, stringRedisTemplate.opsForSet().members(CTNS_BY_CAT_TAG + Profile.HOBBY + ":" + hobby_tag_1).size());
        assertEquals(3, stringRedisTemplate.opsForSet().members(CTNS_BY_CAT_TAG + Profile.HOBBY + ":" + hobby_tag_2).size());
        assertEquals(3, stringRedisTemplate.opsForSet().members(CTNS_BY_CAT_TAG + Profile.HOBBY + ":" + hobby_tag_3).size());

        assertEquals(3, stringRedisTemplate.opsForSet().members(CTNS_BY_CAT_TAG + Profile.JOB + ":" + job_tag_1).size());
        assertEquals(3, stringRedisTemplate.opsForSet().members(CTNS_BY_CAT_TAG + Profile.JOB + ":" + job_tag_2).size());
        assertEquals(3, stringRedisTemplate.opsForSet().members(CTNS_BY_CAT_TAG + Profile.JOB + ":" + job_tag_3).size());
    }

    @Test
    public void TagsByCategoryFilled_1_2_1() { //check if change existing tag inc dec
        assertTrue(stringRedisTemplate.hasKey(TAGS_BY_CAT + Profile.HOBBY));
        assertTrue(stringRedisTemplate.hasKey(TAGS_BY_CAT + Profile.JOB));
        assertFalse(stringRedisTemplate.hasKey(TAGS_BY_CAT + Profile.AIMS));

        Set<String> tagsInCat = stringRedisTemplate.boundZSetOps(TAGS_BY_CAT + Profile.HOBBY).range(0, Integer.MAX_VALUE);
        assertEquals(3, tagsInCat.size());
        assertTrue(tagsInCat.contains(hobby_tag_1));
        assertTrue(tagsInCat.contains(hobby_tag_2));
        assertTrue(tagsInCat.contains(hobby_tag_3));

        tagsInCat = stringRedisTemplate.boundZSetOps(TAGS_BY_CAT + Profile.JOB).range(0, Integer.MAX_VALUE);
        assertEquals(3, tagsInCat.size());
        assertTrue(tagsInCat.contains(job_tag_1));
        assertTrue(tagsInCat.contains(job_tag_2));
        assertTrue(tagsInCat.contains(job_tag_3));
    }

    @Test
    public void userProfilesSaved_1_2() {
        List<Profile> allProfiles = profileService.getAllProfiles();
        assertEquals(3, allProfiles.size());

        assertEquals(ctn11, allProfiles.get(0).ctn);
        assertEquals("perviy(111)", allProfiles.get(0).name);

        assertEquals(ctn12, allProfiles.get(1).ctn);
        assertEquals("vtoroy(122)", allProfiles.get(1).name);

        assertEquals(ctn13, allProfiles.get(2).ctn);
        assertEquals("tretiy(133)", allProfiles.get(2).name);
    }

    @Test
    public void CtnsSetFilled_1_1() {
        assertTrue(stringRedisTemplate.hasKey(CTNS));
        assertEquals(3, stringRedisTemplate.boundSetOps(CTNS).members().size());
    }


    private void fill() {
        Profile p = new Profile();
        p.ctn = ctn11;
        p.age = 30;
        p.name = "perviy(111)";
        p.city = "msk";
        p.tags = HashMultimap.create();
        p.categories = HashMultimap.create();

        p.tags.put(Profile.HOBBY, hobby_tag_1);
        p.tags.put(Profile.HOBBY, hobby_tag_2);
        p.tags.put(Profile.HOBBY, hobby_tag_3);

        p.categories.put(Profile.HOBBY, hobby_tag_1);
        p.categories.put(Profile.HOBBY, hobby_tag_2);
        p.categories.put(Profile.HOBBY, hobby_tag_3);

        p.tags.put(Profile.JOB, job_tag_1);
        p.tags.put(Profile.JOB, job_tag_2);
        p.tags.put(Profile.JOB, job_tag_3);

        p.tags.put(Profile.AIMS, aims_tag_1);


        p.categories.put(Profile.JOB, job_tag_1);
        p.categories.put(Profile.JOB, job_tag_2);
        p.categories.put(Profile.JOB, job_tag_3);

        profileService.saveProfile(p);

        p = new Profile();
        p.ctn = ctn12;
        p.age = 31;
        p.name = "vtoroy(122)";
        p.city = "msk";
        p.tags = HashMultimap.create();
        p.categories = HashMultimap.create();

        p.tags.put(Profile.HOBBY, hobby_tag_1);
        p.tags.put(Profile.HOBBY, hobby_tag_2);
        p.tags.put(Profile.HOBBY, hobby_tag_3);

        p.categories.put(Profile.HOBBY, hobby_tag_1);
        p.categories.put(Profile.HOBBY, hobby_tag_2);
        p.categories.put(Profile.HOBBY, hobby_tag_3);

        p.tags.put(Profile.JOB, job_tag_1);
        p.tags.put(Profile.JOB, job_tag_2);
        p.tags.put(Profile.JOB, job_tag_3);

        p.categories.put(Profile.JOB, job_tag_1);
        p.categories.put(Profile.JOB, job_tag_2);
        p.categories.put(Profile.JOB, job_tag_3);

        profileService.saveProfile(p);

        p = new Profile();
        p.ctn = ctn13;
        p.age = 27;
        p.name = "tretiy(133)";
        p.city = "msk";
        p.tags = HashMultimap.create();
        p.categories = HashMultimap.create();

        p.tags.put(Profile.HOBBY, hobby_tag_1);
        p.tags.put(Profile.HOBBY, hobby_tag_2);
        p.tags.put(Profile.HOBBY, hobby_tag_3);

        p.categories.put(Profile.HOBBY, hobby_tag_1);
        p.categories.put(Profile.HOBBY, hobby_tag_2);
        p.categories.put(Profile.HOBBY, hobby_tag_3);

        p.tags.put(Profile.JOB, job_tag_1);
        p.tags.put(Profile.JOB, job_tag_2);
        p.tags.put(Profile.JOB, job_tag_3);

        p.categories.put(Profile.JOB, job_tag_1);
        p.categories.put(Profile.JOB, job_tag_2);
        p.categories.put(Profile.JOB, job_tag_3);

        profileService.saveProfile(p);

        log.warn("  ---- user creation end -----");
    }
}
