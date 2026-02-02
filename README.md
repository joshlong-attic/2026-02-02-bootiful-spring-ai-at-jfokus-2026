# NOTES

 * change into uniform live
 * add `security` but make sure to comment it out
 * add `spring-boot-jackson-2`
 * add `devtools`
 * MCP party picture
 * discussion around generational infrastructure moment discussion
 * mcp-service should be on `8090`
 * make sure to setup the service as `STREAMABLE` and in the client use `...*streamable-http-connections.*`
 * make sure to suffix the url in the client with `/mcp`!!
 * copy `application.yml` from `mcpauth/oauth-server`
 
 ## auth server
 * add `org.springaicommunity` :  `mcp-authorization-server` : `0.1.1`
 * add in `auth-server` Spring Security `HttpSecurity` customizer
 ```java 
 http.with(mcpAuthorizationServer(), Customizer.withDefaults());
 ``` 
 

 ## mcp client 
 
 add: 
 
```properties
 spring.ai.mcp.client.type=SYNC
 spring.ai.mcp.client.initialized=false
 spring.security.oauth2.client.registration.authserver.client-id=default-client
 spring.security.oauth2.client.registration.authserver.client-secret=default-secret
 spring.security.oauth2.client.registration.authserver.authorization-grant-type=authorization_code
 spring.security.oauth2.client.registration.authserver.provider=authserver
 spring.security.oauth2.client.provider.authserver.issuer-uri=http://localhost:9000
 spring.ai.mcp.client.toolcallback.enabled=true
```

add: 

```java
    @Bean  
    SecurityFilterChain securityFilterChain(HttpSecurity http)  {
        return http
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .oauth2Client(Customizer.withDefaults())
                .build();
    }

    @Bean
    McpSyncHttpClientRequestCustomizer mcpSyncHttpClientRequestCustomizer(
          OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager) {
        return new OAuth2AuthorizationCodeSyncHttpRequestCustomizer(
            oAuth2AuthorizedClientManager, "authserver");
    }

    @Bean
    McpSyncClientCustomizer mcpSyncClientCustomizer() {
        return (name, syncSpec) -> syncSpec
             .transportContextProvider(new AuthenticationMcpTransportContextProvider());
    }
```

## scheduler 
* add `spring-security-resource-server` starter
* add `org.springaicommunity`: `mcp-server-security` : `0.1.1`
* it's a resource service; add `spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:9000`
* refactor the scheduler service to return a record with the currently authenticated user from `SecurityContextHolder`.
* add: 
```java

    @Bean
    Customizer<HttpSecurity> httpSecurityCustomizer(
            @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}") String issuer) {
        return http -> http.with(mcpServerOAuth2(),
                auth -> auth.authorizationServer(issuer));
    }
```
