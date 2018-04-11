package com.dspro;

import com.dspro.dao.iface.ProfileDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;

import java.security.Principal;
import java.util.Set;

public class SubscribeInterceptor extends ChannelInterceptorAdapter {
    private static final Logger log = LoggerFactory.getLogger(SubscribeInterceptor.class);

    ProfileDao profileDao;

    public SubscribeInterceptor(ProfileDao profileDao) {
        this.profileDao = profileDao;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);
        if (StompCommand.SUBSCRIBE.equals(headerAccessor.getCommand())) {
            Principal user = headerAccessor.getUser();
            if (!canSubscribe(user, headerAccessor)) {
                throw new IllegalArgumentException("No permission for this topic");

            }
        }
        return super.preSend(message, channel);

    }

    private boolean canSubscribe(Principal user, StompHeaderAccessor headerAccessor) {
        String destination = headerAccessor.getDestination();
        log.warn("dest {}", destination);
        if (user == null) {
            // unauthenticated user
            return false;
        }


        Set<String> ctns = profileDao.getConnections(user.getName(), false).keySet();
        if (!ctns.contains(destination)) {
            return true;
        }

        return true;
    }
}
