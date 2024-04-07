package com.dpo.depositoperation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class DepositoperationApplication {

	public static void main(String[] args) {
		SpringApplication.run(DepositoperationApplication.class, args);
	}

}
