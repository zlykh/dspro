package com.dspro.logic;

//import com.dspro.dao.iface.ProfileDao;

import com.dspro.domain.MatcherInstance;
import com.dspro.domain.Profile;
import com.dspro.logic.rule.RatingRule;
import com.dspro.service.ProfileService;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by dmitry.zlykh
 */
public class MatcherProcess {
    private static final Logger log = LoggerFactory.getLogger(MatcherProcess.class);

    private ProfileService profileService;
    private List<RatingRule> ratingRules;

    public void setProfileService(ProfileService profileService) {
        this.profileService = profileService;
    }

    public void setRatingRules(List<RatingRule> ratingRules) {
        this.ratingRules = ratingRules;
    }


    public void startBatch() {
        final List<Profile> profiles = profileService.getAllProfiles();
        ImmutableMap<String, Profile> allProfiles = profiles.stream().collect(ImmutableMap.toImmutableMap(p -> p.ctn, p -> p));
        log.debug("all profiles: {}", allProfiles.toString());


        allProfiles.keySet().forEach(
                ctn -> {
                    MatcherInstance request = new MatcherInstance(ctn, allProfiles);
                    request.allConnections = ImmutableMap.copyOf(profileService.getConnections(ctn, true));
                    request.opMembers = ImmutableSet.copyOf(profileService.getViewedProfiles(ctn, System.currentTimeMillis()));

                    start(request);
                }
        );
    }

    public void startSingle(String ctn) {
        final List<Profile> profiles = profileService.getAllProfiles();
        ImmutableMap<String, Profile> allProfiles = profiles.stream().collect(ImmutableMap.toImmutableMap(p -> p.ctn, p -> p));
        log.debug("all profiles: {}", allProfiles.toString());

        MatcherInstance request = new MatcherInstance(ctn, allProfiles);
        request.allConnections = ImmutableMap.copyOf(profileService.getConnections(ctn, true));
        request.opMembers = ImmutableSet.copyOf(profileService.getViewedProfiles(ctn, System.currentTimeMillis()));

        start(request);
    }

    private void start(MatcherInstance request) {
        final String ctn = request.ctn;
        final Profile myProfile = request.getAllProfiles().get(ctn);

        if (myProfile == null) {
            return;
        }

        if (request.allConnections.isEmpty()) {
            log.debug("use empty profile targeting flow, applying CategoriesMatchRule for {}", request.ctn);
        } else {
            for (RatingRule rule : ratingRules) {
                log.debug("use normal targeting flow, applying {} for {}", rule.getName(), request.ctn);
                rule.apply(request, myProfile);
            }
        }

        log.debug("TOTAL ratings for {} is {}", ctn, request.totalRatings);
        profileService.fillOfferQueue(ctn, request.totalRatings, request.totalRatings);

    }


}
