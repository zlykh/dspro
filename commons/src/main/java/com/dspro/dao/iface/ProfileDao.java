package com.dspro.dao.iface;

import com.dspro.domain.Profile;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ProfileDao {
    Set<String> getRandomCtns(int limit);

    boolean exists(String ctn);

    Set<String> getProfileTags(String cat, String ctn);

    Set<String> getProfileTagsPublic(String cat, String ctn);

    //  @Override
    Profile getProfile(String ctn);

    void savePublicTags(Profile oldProfile, Multimap<String, String> newPublicTags);

    void saveTags(Profile oldProfile, Multimap<String, String> newTags);

    void saveProfile(Profile profile);

    Set<String> getCtns();

    List<Profile> getProfiles(List<String> ctns);

    Set<String> getOVmembers(String ctn, int count);

    void fromOVtoOP(String subjectCtn, String objectCtn);

    Set<String> getOPmembers(String ctn, long unixTime);

    void addOVmembers(String ctn, Map<String, Double> ctnRatings);

    void cleanOVmembers(String ctn);

    Map<String, Double> getConnections(String ctn, boolean onlyUnlocked);

    HashMultimap<String, String> getUnlockedTagsUserbyUser(String category, String ctnSubject, Collection<String> objectCtnCollection);

    Set<String> getCategoryTags(String category);

    Set<String> getCategoryPublicTags(String category);

    void addToCtnGlobalList(String ctn);

    void addCtnToCategoryTag(String category, String tag, String ctn);

    void addCtnToCategoryPublicTag(String category, String tag, String ctn);

    void unlock(String from, String to, String category, String tag);

    void addConnection(String ctnSubject, String ctnObject, double unlockLevel);
}
