package br.com.ecommerce.ecom.client.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "keycloak-token-client", url = "${keycloak.auth-server-url}/realms/${keycloak.realm}/protocol/openid-connect")
public interface KeycloakTokenClient {

    @PostMapping(value = "/token", consumes = "application/x-www-form-urlencoded")
    Map<String, Object> getToken(
            @RequestParam("grant_type") String grantType,
            @RequestParam("client_id") String clientId,
            @RequestParam("client_secret") String clientSecret
    );
}