package com.dspro.controller;

//import com.dspro.dao.iface.CommonDao;

import com.dspro.dao.iface.MatcherDao;
//import com.dspro.dao.iface.ProfileDao;
import com.dspro.domain.ChangeProfileRequest;
import com.dspro.domain.Profile;
import com.dspro.service.ProfileService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.dspro.domain.Profile.AIMS;
import static com.dspro.domain.Profile.HOBBY;
import static com.dspro.domain.Profile.JOB;

/**
 * Created by dmitry.zlykh
 */
@Controller
public class NewController {
    private static final Logger log = LoggerFactory.getLogger(NewController.class);

    @Autowired
    ProfileService profileService;

    @Autowired
    MatcherDao matcherDao;

    @RequestMapping(path = "/profile/{ctn}", method = RequestMethod.GET)
    public ResponseEntity getProfile(@PathVariable("ctn") String ctn) {
        log.debug("ctn {}", ctn);
        Profile p = profileService.getProfile(ctn);
        log.warn("profile {}", p);
        return p == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(p);
    }

    // переделать пост без цтн, по сессии
    @RequestMapping(path = "/profile/{ctn}", method = RequestMethod.POST)
    public void saveProfile(@PathVariable("ctn") String ctn, @RequestBody ChangeProfileRequest request) {

        log.info("ctn {}", ctn);
        log.warn("request {}", request);
        Profile p = profileService.getProfile(ctn);

        profileService.saveProfile(prepareProfileChange(p, request));
//        profileDao.saveTags(p, request.tags);

    }

    @RequestMapping(path = "/test", method = RequestMethod.GET)
    public String displayCustomerForm() {
        // relative to static/
        return "profile2.html";
    }

    @RequestMapping(path = "/chat", method = RequestMethod.GET)
    public String chatHtml() {
        return "chat.html";
    }

    @RequestMapping(path = "/admin/info/{ctn}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getAdmInfo(@PathVariable("ctn") String ctn) {
        log.debug("ctn {}", ctn);
        Map<String, Object> result = new HashMap<>();
        Profile p = profileService.getProfile(ctn);
        if (p == null) {
            return ResponseEntity.notFound().build();
        }

        result.put("allcons", profileService.getConnections(ctn, false));

        Map<String, Object> tags = new HashMap<>();
        tags.put(HOBBY, profileService.getCategoryTags(HOBBY));
        tags.put(AIMS, profileService.getCategoryTags(AIMS));
        tags.put(JOB, profileService.getCategoryTags(JOB));
        result.put("globtags", tags);

        Map<String, Object> ubu = new HashMap<>();
        Set<String> unlockCtns = profileService.getConnections(ctn, true).keySet();
        ubu.put(HOBBY, profileService.getUnlockedTagsUserbyUser(HOBBY, ctn, unlockCtns));
        ubu.put(AIMS, profileService.getUnlockedTagsUserbyUser(AIMS, ctn, unlockCtns));
        ubu.put(JOB, profileService.getUnlockedTagsUserbyUser(JOB, ctn, unlockCtns));
        result.put("ubu", ubu);

        Map<String, Object> similar = new HashMap<>();
        log.warn("p.tags {}", p.tags);
        similar.put(HOBBY, matcherDao.getSimilarOnTags(HOBBY, p.tags.get(HOBBY)));
        similar.put(AIMS, matcherDao.getSimilarOnTags(AIMS, p.tags.get(AIMS)));
        similar.put(JOB, matcherDao.getSimilarOnTags(JOB, p.tags.get(JOB)));
        result.put("similar", similar);

        log.warn("result {}", result.toString());

        return ResponseEntity.ok(result);
    }

    private Profile prepareProfileChange(Profile p, ChangeProfileRequest request) {
        if (StringUtils.isNotBlank(request.name)) {
            p.name = request.name;
        }

        if (StringUtils.isNotBlank(request.city)) {
            p.city = request.city;
        }

        if (request.age != null) {
            p.age = request.age;
        }

        return p;
    }

}
