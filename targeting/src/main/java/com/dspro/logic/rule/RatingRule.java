package com.dspro.logic.rule;

import com.dspro.domain.Profile;
import com.dspro.domain.MatcherInstance;

public interface RatingRule {
    String getName();

    void apply(MatcherInstance request, Profile profile);
}
