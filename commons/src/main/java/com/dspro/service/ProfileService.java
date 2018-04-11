package com.dspro.service;


import com.dspro.dao.RedisProfileDao;
import com.dspro.domain.Profile;
import com.google.common.collect.HashMultimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

//import com.dspro.dao.iface.ProfileDao;

@Service
public class ProfileService {
    private static final Logger log = LoggerFactory.getLogger(ProfileService.class);

    private RedisProfileDao profileDao;

    @Autowired
    public void setProfileDao(RedisProfileDao profileDao) {
        this.profileDao = profileDao;
    }

    public Set<String> getRandomCtns(int limit) {
        return profileDao.getRandomCtns(limit);
    }

    public Set<String> getViewedProfiles(String ctn, long unixTime) {
        return profileDao.getOPmembers(ctn, unixTime);
    }

    public Profile getNextMatchedProfile(String ctn) {
        // mb return by 5 profiles to lower api requests
        final Set<String> offeredProfiles = getOVmembers(ctn, 1);
        String offerCtn;

        if (offeredProfiles.isEmpty()) { //get random from a (set w/o viewed)
            // way 1. just give random ctn
            final List<String> ctns = new ArrayList<>(profileDao.getCtns());
            final Set<String> viewedProfiles = getViewedProfiles(ctn, System.currentTimeMillis());
            ctns.removeAll(viewedProfiles);
            ctns.remove(ctn);

            if (ctns.isEmpty()) {
                log.error("no profiles left, unsupported dummy profile");
                return new Profile("9999", "dummy", 99);//todo return predefined dummy ctn profile (e.g. no prfoiles found name)
            }

            final int randIndex = ThreadLocalRandom.current().nextInt(ctns.size());
            offerCtn = ctns.get(randIndex);

            // way 2. give random ctns BUT with same tags
            // need new rule for matcher
        } else {
            offerCtn = offeredProfiles.iterator().next();
        }

        markAsViewed(ctn, offerCtn);

        return getProfile(offerCtn);
    }

    public Set<String> getOVmembers(String ctn, int count) {
        return profileDao.getOVmembers(ctn, count);
    }

    public void markAsViewed(String subjectCtn, String objectCtn) {
        profileDao.fromOVtoOP(subjectCtn, objectCtn);
    }

    public Profile getProfile(String ctn) {
        if (profileDao.exists(ctn)) {
            Profile p = profileDao.getProfile(ctn);
            p.tags.putAll(Profile.HOBBY, profileDao.getProfileTags(Profile.HOBBY, ctn));
            p.tags.putAll(Profile.AIMS, profileDao.getProfileTags(Profile.AIMS, ctn));
            p.tags.putAll(Profile.JOB, profileDao.getProfileTags(Profile.JOB, ctn));

            p.categories.putAll(Profile.HOBBY, profileDao.getProfileTagsPublic(Profile.HOBBY, ctn));
            p.categories.putAll(Profile.AIMS, profileDao.getProfileTagsPublic(Profile.AIMS, ctn));
            p.categories.putAll(Profile.JOB, profileDao.getProfileTagsPublic(Profile.JOB, ctn));
            return p;
        }

        return null;
    }

    public List<Profile> getProfiles(List<String> ctns) {
        return profileDao.getProfiles(ctns);
    }

    public List<Profile> getAllProfiles() {
        Set<String> ctns = profileDao.getCtns();
        return getProfiles(new ArrayList<>(ctns));
    }

    public Map<String, Double> getConnections(String ctn, boolean onlyUnlocked) {
        return profileDao.getConnections(ctn, onlyUnlocked);
    }

    public void addConnection(String ctnSubject, String ctnObject, double unlockLevel) {
        profileDao.addConnection(ctnSubject, ctnObject, unlockLevel);
    }

    public void saveProfile(Profile newProfile) {
        //saves profile (name, city, etc) and tags separately and independently
        if (profileDao.exists(newProfile.ctn)) {
            log.debug("saving existing profile..");

            Profile oldProfile = getProfile(newProfile.ctn); //load with service to get olds tags
            profileDao.saveProfile(newProfile);
            profileDao.saveTags(oldProfile, newProfile.tags);
            profileDao.savePublicTags(oldProfile, newProfile.categories);
            return;
        }

        //create new
        log.debug("create new profile..");

        profileDao.addToCtnGlobalList(newProfile.ctn);
        profileDao.saveProfile(newProfile);
        profileDao.saveTags(newProfile, newProfile.tags);
        profileDao.savePublicTags(newProfile, newProfile.categories);
    }

    public Set<String> getCategoryTags(String category) {
        return profileDao.getCategoryTags(category);
    }

    public HashMultimap<String, String> getUnlockedTagsUserbyUser(String category, String ctnSubject, Collection<String> objectCtnCollection) {
        return profileDao.getUnlockedTagsUserbyUser(category, ctnSubject, objectCtnCollection);
    }


    public void fillOfferQueue(String ctn, Map<String, Double> ctnRatings, Map<String, Double> ctnPublicRatings) {
        //ctn here treat as final and public appended
        profileDao.addOVmembers(ctn, ctnRatings);
    }

    public void unlock(String from, String to, String category, String tag) {
        Map<String, Double> connections = profileDao.getConnections(from, false);
        if (connections.get(to) != null) {
            log.debug("adding unlock... {} {}", category, tag);
            profileDao.unlock(from, to, category, tag);
            profileDao.addConnection(from, to, connections.get(to) + 1);
        } else {
            log.debug("adding unlock '{}'-'{}' to {} from {}, but they are not connected!", category, tag, from, to);
            throw new IllegalArgumentException("unlock of not connected profiles");
        }
    }

    public void cleanOffersQueue(String ctn) {
        profileDao.cleanOVmembers(ctn);
    }
}
