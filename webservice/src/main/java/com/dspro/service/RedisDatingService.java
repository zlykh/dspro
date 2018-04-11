package com.dspro.service;

//import com.dspro.dao.iface.ProfileDao;

import com.dspro.domain.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class RedisDatingService implements DatingService {
    private static final Logger log = LoggerFactory.getLogger(RedisDatingService.class);

    @Autowired
    ProfileService profileService;

    @Override
    public Profile getNextOffer(String ctn) {

        return profileService.getNextMatchedProfile(ctn);
    }

    @Override
    public boolean addConnection(String ctnSubject, String ctnObject) {
        return false;
    }

    @Override
    public Set<String> getConnections(String ctn) {
        return profileService.getConnections(ctn, false).keySet();
    }


}

