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
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.HttpURLConnection;

@SpringBootApplication
@RestController
public class DemoApplication extends SpringBootServletInitializer {
	String CLIENT_ID = "172137445539-lcrlr1d3kef4lt789mj60ij6oqj9cd60.apps.googleusercontent.com";
	String DB_CONNECTION_STRING = "jdbc:sqlserver://beepmeupdbsqlserver.database.windows.net:1433;database=beepmeupdb;user=beepmeup@beepmeupdbsqlserver;password=Nishant1-db;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	String healthCheck() {
		return JSONObject.quote("Health Check : Good");
	}

	@RequestMapping(value = "/account", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	String fetchAccountDetails() {
		return JSONObject.quote("accountkey: {account details response as json}");
	}

	String getAlertStatus(String userId) {
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			Connection connection = DriverManager.getConnection(DB_CONNECTION_STRING);
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement
					.executeQuery("select isActive from dbo.alert_mode_status where userid = " + userId);
			String alertStatus = "Not set yet";
			while (resultSet.next()) {
				alertStatus = resultSet.getBoolean("isActive") ? "Active" : "Inactive";
			}
			connection.close();
			return alertStatus;
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	@RequestMapping(value = "/getAlertMode", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	String getAlertMode(String userId) {
		String alertStatus = getAlertStatus(userId);
		return JSONObject.quote("AlertStatus for user " + alertStatus);
	}

	@RequestMapping(value = "/setAlertMode", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	String setAlertMode(String userId, Boolean setActive) {
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			Connection conn = DriverManager.getConnection(DB_CONNECTION_STRING);
			Statement statement = conn.createStatement();
			String currentStatus = getAlertStatus(userId);
			if (currentStatus == "Not set yet") {
				String activeStatus = setActive ? ", 1 )" : ", 0 )";
				statement.executeUpdate(
						"insert into dbo.alert_mode_status (userid, isActive) values (" + userId + activeStatus);
			} else {
				String activeStatus = setActive ? " 1 " : " 0 ";
				statement.executeUpdate(
						"update dbo.alert_mode_status set isActive =" + activeStatus + "where userId = " + userId);
			}
			return JSONObject.quote("Alert status updated successfully");
		} catch (Exception e) {
			return JSONObject.quote("Alert status updation failed " + e.getMessage());
		}
	}

	@RequestMapping(value = "/googleauth", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	String googleAuth(String idTokenString) {
		GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
				.setAudience(Collections.singletonList(CLIENT_ID))
				.build();
		try {
			GoogleIdToken idToken = verifier.verify(idTokenString);
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
		} catch (Exception e) {
			return JSONObject.quote("accountkey: {invalid token " + idTokenString + " }" + e.getLocalizedMessage());
		}

		return JSONObject.quote("accountkey: {account details response as json}");
	}

	@RequestMapping(value = "/getotp", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	String sendSms(String number) {
		try {
			// Construct data
			String apiKey = "apikey=" + "NmY3ODU3NTQzOTQ3NTg1NzQyNzMzMzY4NTU3NTYzNzc=";
			String otp = "12345";
			String message = "&message=" + "Your otp to verify beepmeup account : " + otp;
			String sender = "&sender=" + "Beepmeup";
			String numbers = "&numbers=" + "91" + number;

			// Send data
			HttpURLConnection conn = (HttpURLConnection) new URL("https://api.txtlocal.com/send/?").openConnection();
			String data = apiKey + numbers + message + sender;
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Length", Integer.toString(data.length()));
			conn.getOutputStream().write(data.getBytes("UTF-8"));
			final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			final StringBuffer stringBuffer = new StringBuffer();
			String line;
			while ((line = rd.readLine()) != null) {
				stringBuffer.append(line);
			}
			rd.close();

			return JSONObject.quote("otp: 12345");
		} catch (Exception e) {
		return JSONObject.quote("Failed to trigger otp");
		}
	}
}
