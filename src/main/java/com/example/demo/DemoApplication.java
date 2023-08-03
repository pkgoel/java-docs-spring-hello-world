package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;
import org.json.JSONObject;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import java.util.Collections;

@SpringBootApplication
@RestController
public class DemoApplication extends SpringBootServletInitializer {
	String CLIENT_ID = "347123406383-vpn5n3bkm8itcce6sbp3ambjvdq0rm21.apps.googleusercontent.com";
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@RequestMapping(value = "/", method = RequestMethod.GET,
                produces = MediaType.APPLICATION_JSON_VALUE)
	String healthCheck() {
		return JSONObject.quote("Health Check : Good");
	}

	@RequestMapping(value = "/account", method = RequestMethod.GET,
                produces = MediaType.APPLICATION_JSON_VALUE)
	String fetchAccountDetails() {
		return JSONObject.quote("accountkey: {account details response as json}");
	}

	@RequestMapping(value = "/getAlertMode", method = RequestMethod.GET,
                produces = MediaType.APPLICATION_JSON_VALUE)
	String getAlertMode(String userId) {
		return JSONObject.quote("accountkey: {account details response as json}");
	}

	@RequestMapping(value = "/googleauth", method = RequestMethod.GET,
                produces = MediaType.APPLICATION_JSON_VALUE)
	String googleAuth(String idTokenString) {		
		GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
		.setAudience(Collections.singletonList(CLIENT_ID))
		.build();
		try {GoogleIdToken idToken = verifier.verify(idTokenString);
			if (idToken != null) {
				Payload payload = idToken.getPayload();
	
				// Print user identifier
				String userId = payload.getSubject();
				System.out.println("User ID: " + userId);
	
				// Get profile information from payload
				String email = payload.getEmail();
				boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
				String name = (String) payload.get("name");
				String pictureUrl = (String) payload.get("picture");
				String locale = (String) payload.get("locale");
				String familyName = (String) payload.get("family_name");
				String givenName = (String) payload.get("given_name");
			}}
		catch(Exception e) {		return JSONObject.quote("accountkey: {invalid token "+idTokenString+" }");
	}
		
		return JSONObject.quote("accountkey: {account details response as json}");
	}
}
