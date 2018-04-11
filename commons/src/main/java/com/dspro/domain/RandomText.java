package com.dspro.domain;

import org.apache.commons.lang3.RandomStringUtils;

public interface RandomText {

    default String randText(int len) {
        return RandomStringUtils.randomAlphabetic(len);
    }
}
