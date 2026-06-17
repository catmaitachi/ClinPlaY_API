package dev.clinplay.api.modules.auth.oauth2;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;

@Configuration
public class OAuth2Config {

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository(

        @Value("${oauth2.google.client-id}") String googleClientId,
        @Value("${oauth2.google.client-secret}") String googleClientSecret
        
    ) { return new InMemoryClientRegistrationRepository( googleRegistration(googleClientId, googleClientSecret) ); }

    private ClientRegistration googleRegistration( String clientId, String clientSecret ) {

        return CommonOAuth2Provider.GOOGLE
            .getBuilder("google")
            .clientId(clientId)
            .clientSecret(clientSecret)
            .scope("openid", "email", "profile")
            .build();

    }

}
