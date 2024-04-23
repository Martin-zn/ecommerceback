package com.martin.ecommerce.springecommerce.api.security;

import com.martin.ecommerce.springecommerce.entities.LocalUser;
import com.martin.ecommerce.springecommerce.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authorization.AuthorizationEventPublisher;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.SpringAuthorizationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.messaging.access.intercept.AuthorizationChannelInterceptor;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.Map;
import java.util.regex.Matcher;

//Configuration para dsedignar esta clase como una clase de configuracion
@Configuration
//Habilito el uso de websockets
@EnableWebSocket
//MessageBoker es un intermediario para el envio de mensajes con websockets
@EnableWebSocketMessageBroker
public class WebsocketConfiguration implements WebSocketMessageBrokerConfigurer {
    @Autowired
    private ApplicationContext context;

    @Autowired
    private JWTRequestFilter jwtRequestFilter;

    @Autowired
    private UserService userService;

    private static final AntPathMatcher MATCHER = new AntPathMatcher();

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

    private AuthorizationManager<Message<?>> makeMassageAuthorizationManager(){
        MessageMatcherDelegatingAuthorizationManager.Builder messages = new MessageMatcherDelegatingAuthorizationManager.Builder();
        messages.
                simpDestMatchers("/topic/user/**").authenticated()
                .anyMessage().permitAll();
        return messages.build();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        WebSocketMessageBrokerConfigurer.super.configureClientInboundChannel(registration);
        AuthorizationManager<Message<?>> authorizationManager = makeMassageAuthorizationManager();
        AuthorizationChannelInterceptor authInterceptor = new AuthorizationChannelInterceptor(authorizationManager);
        AuthorizationEventPublisher publisher = new SpringAuthorizationEventPublisher(context);
        authInterceptor.setAuthorizationEventPublisher(publisher);
        registration.interceptors(jwtRequestFilter, authInterceptor, new RejectClientMessagesOnChannelsChannelInterceptor(),
                new DestinationLevelAuthorizationChannelInterceptor());
    }

    //Esta es una clase interceptora, que anulara los mensajes de acuerdo a los canales o paths por lo que se envien.
    private class RejectClientMessagesOnChannelsChannelInterceptor implements ChannelInterceptor{

        private String[] paths = new String[] {
          "/topic/user/*/address"
        };

        @Override
        public Message<?> preSend(Message<?> message, MessageChannel channel) {
            if(message.getHeaders().get("simpMessageType").equals(SimpMessageType.MESSAGE)){
                String destination = (String) message.getHeaders().get("simpDestination");

                for (String path: paths){
                    if(MATCHER.match(path, destination)){
                        //Importante, aca no se arroja una excepcion ya que esto desconectaria el websocket
                        message = null;
                    }
                }
            }
            return message;
        }
    }

    private class DestinationLevelAuthorizationChannelInterceptor implements ChannelInterceptor{


        @Override
        public Message<?> preSend(Message<?> message, MessageChannel channel) {
            if(message.getHeaders().get("simpMessageType").equals(SimpMessageType.SUBSCRIBE)){
                String destination = (String) message.getHeaders().get("simpDestination");
                Map<String, String> params = MATCHER.extractUriTemplateVariables("/topic/user/{userId}/**", destination);
                try{
                    Long userId = Long.valueOf(params.get("userId"));
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                    if(authentication != null) {
                        LocalUser user = (LocalUser) authentication.getPrincipal();
                        if (!userService.userHasPermissionToUser(user, userId)) {
                            message = null;
                        }
                    }else {
                        message = null;
                    }
                }catch (NumberFormatException ex){
                    message = null;
                }
            }
        return message;
        }
    }





}
