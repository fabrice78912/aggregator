package com.example.aggregator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
/*
@EnableAutoConfiguration(
		exclude = {org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration.class})
*/
public class AggreagtorApplication {

	public static void main(String[] args) {
		SpringApplication.run(AggreagtorApplication.class, args);
	}

}
