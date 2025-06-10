package br.com.ecommerce.ecom.client.feign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "keycloak-admin-user-client", url = "${keycloak.auth-server-url}/admin/realms/${keycloak.realm}")
public interface KeycloakAdminUserClient {

    @PostMapping(value = "/users", consumes = "application/json")
    void createUser(
            @RequestHeader("Authorization") String authorization,
            @RequestBody Object userPayload
    );
}