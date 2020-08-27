package com.payment.sandbox;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@CrossOrigin(origins = "*", allowedHeaders="*")
public class PaymentIntiationSandboxAppApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(PaymentIntiationSandboxAppApplication.class, args);
	}

}
