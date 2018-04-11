package com.dspro.service;

import com.dspro.domain.Profile;

import java.util.Set;

public interface DatingService {
    Profile getNextOffer(String ctn);

    boolean addConnection(String ctnSubject, String ctnObject);

    Set<String> getConnections(String ctn);
}
