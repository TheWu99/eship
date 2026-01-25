package com.thewu.eship.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HomeController {

	@GetMapping("/hello")
	public String hello() {
		return "Welcome to E-Ship Application - Built with Spring Boot 3.3.1!";
	}

	@GetMapping("/version")
	public String version() {
		return "Spring Boot Version: 3.3.1";
	}
}
