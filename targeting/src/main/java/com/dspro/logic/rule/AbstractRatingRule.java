package com.dspro.logic.rule;

/**
 * Created by dzlykh on 16.02.2018.
 */
public abstract class AbstractRatingRule implements RatingRule {
    private int ratingFraction;

    public AbstractRatingRule(int ratingFraction) {
        this.ratingFraction = ratingFraction;
    }

    public int getRatingFraction() {
        return ratingFraction;
    }
}
