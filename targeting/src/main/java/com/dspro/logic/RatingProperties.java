package com.dspro.logic;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RatingProperties {

    @Value("${rating.fraction.age}")
    private int ageFraction;

    @Value("${rating.fraction.tags}")
    private int tagsFraction;

    public int getAgeFraction() {
        return ageFraction;
    }

    public int getTagsFraction() {
        return tagsFraction;
    }

    @Override
    public String toString() {
        return "RatingProperties{" +
                "ageFraction=" + ageFraction +
                ", tagsFraction=" + tagsFraction +
                '}';
    }
}
