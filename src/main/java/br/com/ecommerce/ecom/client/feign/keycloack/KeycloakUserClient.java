package br.com.ecommerce.ecom.client.feign.keycloack;


import br.com.ecommerce.ecom.model.keycloack.CredentialRepresentation;
import br.com.ecommerce.ecom.model.keycloack.RoleRepresentation;
import br.com.ecommerce.ecom.model.keycloack.UserRepresentation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "keycloak-user-client", url = "${keycloak.url}/admin/realms/${keycloak.realm}")
public interface KeycloakUserClient {

    @PostMapping("/users")
    ResponseEntity<Void> createUser(@RequestBody UserRepresentation user, @RequestHeader("Authorization") String authorization);

    @GetMapping("/users")
    List<UserRepresentation> getUsers(@RequestParam("username") String username, @RequestHeader("Authorization") String authorization);

    @PutMapping("/users/{id}/reset-password")
    void resetPassword(@PathVariable("id") String userId, @RequestBody CredentialRepresentation password, @RequestHeader("Authorization") String authorization);

    @PostMapping("/users/{id}/role-mappings/realm")
    void addRealmRole(@PathVariable("id") String userId, @RequestBody List<RoleRepresentation> roles, @RequestHeader("Authorization") String authorization);

    @GetMapping("/roles/{roleName}")
    RoleRepresentation getRoleByName(@PathVariable("roleName") String roleName, @RequestHeader("Authorization") String authorization);

    @PutMapping("/users/{id}")
    void updateUserEnabledStatus(@PathVariable("id") String userId,
                                 @RequestBody UserRepresentation user,
                                 @RequestHeader("Authorization") String authorization);
}