package com.dspro;

import com.dspro.dao.iface.ProfileDao;
import com.sun.security.auth.UserPrincipal;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.security.messaging.access.intercept.ChannelSecurityInterceptor;
import org.springframework.security.messaging.context.SecurityContextChannelInterceptor;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.server.HandshakeHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@Configuration
@ImportResource("classpath:spring/app.xml")
@EnableAutoConfiguration(exclude = {DataSource.class, DataSourceAutoConfiguration.class})
@EnableWebSocketMessageBroker
public class WsApp extends AbstractWebSocketMessageBrokerConfigurer {

    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProfileDao profileDao;

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(WsApp.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }


    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/gschat")
                .setAllowedOrigins("*")
                .setHandshakeHandler(createHandshakeHandler())
                //.addInterceptors(new HttpSessionHandshakeInterceptor())
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/for");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        ChannelSecurityInterceptor interceptor = new ChannelSecurityInterceptor();
        interceptor.setAuthenticationManager();
        registration.setInterceptors(createSubscribeInterceptor(), new SecurityContextChannelInterceptor(),
                new ChannelSecurityInterceptor() {

                });
    }

    private ChannelInterceptorAdapter createSubscribeInterceptor() {
        return new SubscribeInterceptor(profileDao);
    }

    private HandshakeHandler createHandshakeHandler() {
        return new DefaultHandshakeHandler() {
            @Override
            protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
                return new UserPrincipal("princ");
            }
        };
    }
}
