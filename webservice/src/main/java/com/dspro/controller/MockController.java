package com.dspro.controller;

import com.dspro.domain.Profile;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

public class MockController {
    @RequestMapping(path = "/mock/list", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public List<Profile> getlist() {
        List<Profile> list = new ArrayList<>();
        list.add(new Profile("123", RandomStringUtils.random(3, true, false), 25));
        list.add(new Profile("456", RandomStringUtils.random(3, true, false), 79));
        list.add(new Profile("789", RandomStringUtils.random(3, true, false), 14));
        return list;
    }

    @RequestMapping(path = "/mock/profile/{ctn}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Profile gectn(@PathVariable("ctn") String ctn) {
        return new Profile(ctn, RandomStringUtils.random(3, true, false), 25);

    }
}
