package com.martin.ecommerce.springecommerce.api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.authorization.AuthorizationEventPublisher;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.SpringAuthorizationEventPublisher;
import org.springframework.security.messaging.access.intercept.AuthorizationChannelInterceptor;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
//Configuration para dsedignar esta clase como una clase de configuracion
@Configuration
//Habilito el uso de websockets
@EnableWebSocket
//MessageBoker es un intermediario para el envio de mensajes con websockets
@EnableWebSocketMessageBroker
public class WebsocketConfiguration implements WebSocketMessageBrokerConfigurer {
    @Autowired
    private ApplicationContext context;

    //Agrego un endpoint para interactuar con el websocket
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        WebSocketMessageBrokerConfigurer.super.registerStompEndpoints(registry);
        registry.addEndpoint("/websocket").setAllowedOriginPatterns("**").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        WebSocketMessageBrokerConfigurer.super.configureMessageBroker(registry);
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        WebSocketMessageBrokerConfigurer.super.configureClientInboundChannel(registration);
        AuthorizationManager<Message<?>> authorizationManager = makeMassageAuthorizationManager();
        AuthorizationChannelInterceptor authInterceptor = new AuthorizationChannelInterceptor(authorizationManager);
        AuthorizationEventPublisher publisher = new SpringAuthorizationEventPublisher(context);
        authInterceptor.setAuthorizationEventPublisher(publisher);
        registration.interceptors(authInterceptor);


    }

    private AuthorizationManager<Message<?>> makeMassageAuthorizationManager(){
        MessageMatcherDelegatingAuthorizationManager.Builder messages = new MessageMatcherDelegatingAuthorizationManager.Builder();
        messages.
                simpDestMatchers("/topic/user/**").authenticated()
                .anyMessage().permitAll();
        return messages.build();
    }


}
