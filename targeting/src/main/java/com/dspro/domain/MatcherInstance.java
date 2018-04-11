package com.dspro.domain;

import com.dspro.service.ProfileService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by dmitry.zlykh
 */
public class MatcherInstance {
    public String ctn;
    @JsonIgnore
    public Map<String, Double> totalRatings = new HashMap<>();
    @JsonIgnore
    public Map<String, Double> tagRatings = new HashMap<>();
    @JsonIgnore
    public Map<String, Double> ageRatings = new HashMap<>();
    @JsonIgnore
    public List<Map<String, Double>> rulesLog = new ArrayList<>();
    @JsonIgnore
    public Map<String, Double> allConnections;
    @JsonIgnore
    public Set<String> opMembers;
    @JsonIgnore
    private Map<String, Profile> allProfiles;

    private MatcherInstance() {
    }


    public MatcherInstance(String ctn, Map<String, Profile> allProfiles) {
        this.ctn = ctn;
        this.allProfiles = allProfiles;
    }

    public Map<String, Profile> getAllProfiles() {
        return allProfiles;
    }

    /**
     * Self, matched and viewed profiles are not a subject for targeting, so remove them
     */
    public Map<String, Profile> newAdjustedModifiableProfiles() {
        final Map<String, Profile> map = new HashMap<>(allProfiles);

        map.keySet().remove(ctn);
        map.keySet().removeAll(opMembers);
        map.keySet().removeAll(allConnections.keySet());

        return map;
    }

}
