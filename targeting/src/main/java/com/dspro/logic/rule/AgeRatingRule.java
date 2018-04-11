package com.dspro.logic.rule;

import com.dspro.domain.MatcherInstance;
import com.dspro.domain.Profile;
import com.dspro.service.ProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Calculates age difference as a rating score.
 * diff -> match percentage = (100 - diff * 10)/100
 * 0 = 100% match out of 10 (rating fraction), so 10 is summed up with total rating score.
 * 1 = 90%
 * 5 = 50%
 * 10 = 0%
 * 11 = 0% of 10, so 0 is summed up with total rating score.
 * Total rating is summed up across rules.
 */
public class AgeRatingRule extends AbstractRatingRule {
    private static final Logger log = LoggerFactory.getLogger(AgeRatingRule.class);

    public AgeRatingRule(int ratingFraction) {
        super(ratingFraction);
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public void apply(MatcherInstance request, Profile myProfile) {
        log.debug("\n");
        log.debug("AgeRatingRule: start");
        log.debug("my ({}) age: {}", myProfile.ctn, myProfile.age);
        final Map<String, Profile> allProfiles = request.newAdjustedModifiableProfiles();

        final Set<String> ctnsWithRating = request.totalRatings.keySet();
        allProfiles.keySet().retainAll(ctnsWithRating);

        for (Profile candidateProfile : allProfiles.values()) {
            final int diffYears = Math.abs(myProfile.age - candidateProfile.age);
            if (diffYears > 10) {
                continue;
            }
            final int ageMatchPercentage = 100 - diffYears * 10;
            final double ageRating = ageMatchPercentage / 100.0 * getRatingFraction();

            log.debug("  for ({}) age rating is {}%({}, diff {}) [my {}, his {}]", candidateProfile.ctn, ageMatchPercentage, ageRating, diffYears, myProfile.age, candidateProfile.age);

            request.ageRatings.put(candidateProfile.ctn, ageRating);
            request.totalRatings.merge(candidateProfile.ctn, ageRating, (c1, c2) -> c1 + c2);
        }

        log.debug("age ratings: {}", request.ageRatings);
        log.debug("total ratings: {}", request.totalRatings);
        log.debug("AgeRatingRule: end \n");

    }

}
