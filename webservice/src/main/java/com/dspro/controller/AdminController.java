package com.dspro.controller;

//import com.dspro.dao.iface.CommonDao;
//import com.dspro.dao.iface.ProfileDao;

import com.dspro.domain.Profile;
import com.dspro.service.ProfileService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by dmitry.zlykh
 */
@CrossOrigin(origins = "*")
@RestController
public class AdminController {
    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    ProfileService profileService;


    @RequestMapping(value = "/connect", method = RequestMethod.POST)
    public void connect(String from, String to) {
        profileService.addConnection(from, to, 0);
    }

    @RequestMapping(value = "/unlock", method = RequestMethod.POST)
    public void unlock(String from, String to, Long partition, String tag) {

    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResponseEntity signup(@RequestBody Profile request) {
        log.debug("request {}", request);

        ResponseEntity r500 = ResponseEntity.badRequest().build();
        if (request == null || StringUtils.isBlank(request.ctn)) {
            return r500;
        }

        return ResponseEntity.ok("");
    }

}
