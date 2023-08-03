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
import java.sql.*;
import java.util.*;
import java.io.StringWriter;
import java.io.PrintWriter;

@SpringBootApplication
@RestController
public class DemoApplication extends SpringBootServletInitializer {
	String CLIENT_ID = "347123406383-vpn5n3bkm8itcce6sbp3ambjvdq0rm21.apps.googleusercontent.com";
	String DB_CONNECTION_STRING =
	"jdbc:sqlserver://beepmeupdbsqlserver.database.windows.net:1433;"
			+ "database=beepmeupdb;"
			+ "user=beepmeup@beepmeupdbsqlserver;"
			+ "password=Nishant1-db;"
			+ "encrypt=true;"
			+ "trustServerCertificate=false;"
			+ "loginTimeout=30;";
				String url = "jdbc:sqlserver://beepmeupdbsqlserver.database.windows.net:1433;database=beepmeupdb;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";
	String username = "beepmeup@beepmeupdbsqlserver";
	String password = "Nishant1-db";

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
		
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");


			System.out.print("Connecting to SQL Server ... ");
			try (Connection connection = DriverManager.getConnection(DB_CONNECTION_STRING))        {
				return JSONObject.quote("sql querying successful");
			}
		} catch (Exception e) {
			System.out.println();
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			String sStackTrace = sw.toString();
			return JSONObject.quote("sql querying failed" + sStackTrace);
			
		}
		/*
		try {
			Connection conn = DriverManager.getConnection(url, username, password);
			Statement statement = conn.createStatement();
			ResultSet resultSet = statement.executeQuery("select isActive from dbo.alert_mode_status where userid = " + userId);
			return JSONObject.quote("alertStatus: " + resultSet.toString());
		} 
		catch(Exception e) {return JSONObject.quote("sql querying failed" + e.getMessage());
		}
		*/
	}

	@RequestMapping(value = "/setAlertMode", method = RequestMethod.GET,
                produces = MediaType.APPLICATION_JSON_VALUE)
	String setAlertMode(String userId, Boolean setActive) {
		try {
			String activeStatus = setActive ? ", 1 )" : ", 0 )";
			Connection conn = DriverManager.getConnection(DB_CONNECTION_STRING);
			Statement statement = conn.createStatement();
			ResultSet resultSet = statement.executeQuery("insert into dbo.alert_mode_status (userid, isActive) values (" + userId + activeStatus);
			return JSONObject.quote("Edit status: " + resultSet.toString());
		} 
		catch(Exception e) {return JSONObject.quote("sql querying failed" + e.getMessage());
		}
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
			}
		}
		catch(Exception e) {		return JSONObject.quote("accountkey: {invalid token "+idTokenString+" }" + e.getLocalizedMessage());
	}
		
		return JSONObject.quote("accountkey: {account details response as json}");
	}
}
