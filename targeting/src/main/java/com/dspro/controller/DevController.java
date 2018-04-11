package com.dspro.controller;

import com.dspro.logic.MatcherProcess;
import com.dspro.service.ProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = "*")
@RestController
@Profile("default")
public class DevController {
    private static final Logger log = LoggerFactory.getLogger(DevController.class);

    private ProfileService profileService;

    private MatcherProcess matcherProcess;

    @Autowired
    public void setProfileService(ProfileService profileService) {
        this.profileService = profileService;
    }

    @Autowired
    public void setMatcherProcess(MatcherProcess matcherProcess) {
        this.matcherProcess = matcherProcess;
    }

    @RequestMapping(value = "/target/{ctn}", method = RequestMethod.GET)
    public ResponseEntity createUser(@PathVariable("ctn") String ctn) {
        log.debug("input ctn {}", ctn);
        if (ctn.equalsIgnoreCase("batch")) {
            matcherProcess.startBatch();
            return ResponseEntity.ok("batch ok");
        } else {
            matcherProcess.startSingle(ctn);
            return ResponseEntity.ok(profileService.getNextMatchedProfile(ctn));
        }

    }

}
