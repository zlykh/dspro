package com.dspro.controller;

import com.dspro.domain.NextOfferResponse;
import com.dspro.domain.Profile;
import com.dspro.service.DatingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 * Created by dmitry.zlykh
 */
@RestController
@RequestMapping("/matcher")
public class ServiceController {
    private static final Logger log = LoggerFactory.getLogger(ServiceController.class);

    @Autowired
    DatingService datingService;

    @RequestMapping(value = "/nextOffer", method = RequestMethod.GET)
    public NextOfferResponse getNextOffer(String ctn) {
        NextOfferResponse response = new NextOfferResponse();

        Profile p = datingService.getNextOffer(ctn);

        response.setQuote(p.quote);

        return response;
    }

    @RequestMapping(value = "/contact", method = RequestMethod.GET)
    public ResponseEntity contact(String ctn, String target) {

        final boolean firstContact = datingService.addConnection(ctn, target);
        if (firstContact) {

        }
        return ResponseEntity.ok(null);
    }

    @RequestMapping(value = "/contactList", method = RequestMethod.GET)
    public Set<String> getContactList(String ctn) {
        return datingService.getConnections(ctn);
    }
}
