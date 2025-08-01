package chat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer{
@Override
public void configureMessageBroker(MessageBrokerRegistry config) {
	// TODO Auto-generated method stub
	config.enableSimpleBroker("/topic");
	config.setApplicationDestinationPrefixes("/app");
}
@Override
public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/chat")
        .setAllowedOrigins(
            "http://localhost:5173",
            "https://asliengineers.vercel.app",
            "https://asliengineers.com",
            "https://www.asliengineers.com"
        )
        .withSockJS();
}
}
