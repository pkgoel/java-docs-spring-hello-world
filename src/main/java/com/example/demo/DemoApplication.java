package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;
import org.JSONObject;

@SpringBootApplication
@RestController
public class DemoApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@RequestMapping("/", method = RequestMethod.GET,
                produces = MediaType.APPLICATION_JSON_VALUE)
	String sayHello() {
		return JSONObject.quote("Hello World");
	}

	@RequestMapping("/account", method = RequestMethod.GET,
                produces = MediaType.APPLICATION_JSON_VALUE)
	String sayHello() {
		return JSONObject.quote("account details response");
	}
}
