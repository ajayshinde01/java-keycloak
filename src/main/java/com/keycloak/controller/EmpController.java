package com.keycloak.controller;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.Configuration;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.keycloak.dto.UserDTO;

import jakarta.ws.rs.core.Response;

@RestController
@RequestMapping("/emp")
public class EmpController {

	@Value("${keycloak.auth-server-url}")
	private String authServerUrl;

	@Value("${keycloak.realm}")
	private String realm;

	@Value("${keycloak.resource}")
	private String clientId;

	private String role = "admin";

	@Value("${keycloak.credentials.secret}")
	private String clientSecret;

//	@GetMapping("/get")
//	public String index(@AuthenticationPrincipal Jwt jwt) {
//		return String.format("Emp : Hello, %s!", jwt.getClaimAsString("preferred_username"));
//	}
	
	@GetMapping("/get-all")
	public ResponseEntity<?> getAllUsers() throws IOException, InterruptedException, URISyntaxException {
		 Keycloak keycloak = KeycloakBuilder.builder().serverUrl(authServerUrl)
		            .grantType(OAuth2Constants.PASSWORD).realm("master")
		            .clientId("admin-cli").username("admin").password("1234")
		            .resteasyClient(new ResteasyClientBuilderImpl().connectionPoolSize(10).build())
		            .build();

		    AccessTokenResponse tokenResponse = keycloak.tokenManager().getAccessToken();
		    String accessToken = tokenResponse.getToken();
//		    keycloak.realm(realm).users().

		    HttpRequest request = HttpRequest.newBuilder()
		            .uri(new URI("http://localhost:8080/admin/realms/quickstart/users"))
		            .header("Authorization", "Bearer " + accessToken)
		            .build();

		    HttpClient httpClient = HttpClient.newHttpClient();
		    HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

		    if (response.statusCode() == 200) {
		        return ResponseEntity.ok(response.body());
		    } else {
		        return ResponseEntity.status(response.statusCode()).body("Request failed: " + response.body());
		    }
	}
	

	@PostMapping("/signin")
	public ResponseEntity<?> signin(@RequestBody UserDTO userDTO) {

		Map<String, Object> clientCredentials = new HashMap<>();
		clientCredentials.put("secret", clientSecret);
		clientCredentials.put("grant_type", "password");

		Configuration configuration = new Configuration(authServerUrl, realm, clientId, clientCredentials, null);
		AuthzClient authzClient = AuthzClient.create(configuration);

		AccessTokenResponse response = authzClient.obtainAccessToken(userDTO.getEmail(), userDTO.getPassword());

		return ResponseEntity.ok(response);
	}

	@PostMapping("/create")
	public ResponseEntity<?> createUser(@RequestBody  UserDTO userDTO) {

		Keycloak keycloak = KeycloakBuilder.builder().serverUrl(authServerUrl).grantType(OAuth2Constants.PASSWORD)
				.realm("master").clientId("admin-cli").username("admin").password("1234")
				.resteasyClient(new ResteasyClientBuilderImpl().connectionPoolSize(10).build()).build();

		keycloak.tokenManager().getAccessToken();
		
//		System.out.println(keycloak.tokenManager().getAccessToken());

		UserRepresentation user = new UserRepresentation();
		user.setEnabled(true);
		user.setUsername(userDTO.getEmail());
		user.setFirstName(userDTO.getFirstName());
		user.setLastName(userDTO.getLastName());
		user.setEmail(userDTO.getEmail());

		// Get realm
		RealmResource realmResource = keycloak.realm(realm);
		UsersResource usersRessource = realmResource.users();

		Response response = usersRessource.create(user);

		userDTO.setStatusCode(response.getStatus());
		userDTO.setStatus(response.getStatusInfo().toString());

		if (response.getStatus() == 201) {

			String userId = CreatedResponseUtil.getCreatedId(response);

			// create password credential
			CredentialRepresentation passwordCred = new CredentialRepresentation();
			passwordCred.setTemporary(false);
			passwordCred.setType(CredentialRepresentation.PASSWORD);
			passwordCred.setValue(userDTO.getPassword());

			UserResource userResource = usersRessource.get(userId);

			userResource.resetPassword(passwordCred);

			RoleRepresentation realmRoleUser = realmResource.roles().get(role).toRepresentation();

			userResource.roles().realmLevel().add(Arrays.asList(realmRoleUser));
		}
		return ResponseEntity.ok(userDTO);
	}

//	@PostMapping("/create")
//	public ResponseEntity<?> createUser(@RequestBody  UserDTO userDTO) throws URISyntaxException, IOException, InterruptedException {
//
//		Keycloak keycloak = KeycloakBuilder.builder().serverUrl(authServerUrl).grantType(OAuth2Constants.PASSWORD)
//				.realm("master").clientId("admin-cli").username("admin").password("1234")
//				.resteasyClient(new ResteasyClientBuilderImpl().connectionPoolSize(10).build()).build();
//
//		keycloak.tokenManager().getAccessToken();
//
//		UserRepresentation user = new UserRepresentation();
//		user.setEnabled(true);
//		user.setUsername(userDTO.getEmail());
//		user.setFirstName(userDTO.getFirstname());
//		user.setLastName(userDTO.getLastname());
//		user.setEmail(userDTO.getEmail());
//
//		HttpRequest request=HttpRequest.newBuilder().uri(new URI("http://localhost:8080/admin/realms/quickstart/users")).POST(BodyPublishers.ofString("{'email':'dummy3@gmail.com','password':'pass'}")).build();
//		HttpClient httpClinet=HttpClient.newHttpClient();
//		HttpResponse<?> response= httpClinet.send(request, BodyHandlers.ofString());
//		
//		return ResponseEntity.ok(response);
//	}
}