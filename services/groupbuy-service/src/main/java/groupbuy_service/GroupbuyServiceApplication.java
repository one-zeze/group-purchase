package groupbuy_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class GroupbuyServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GroupbuyServiceApplication.class, args);
	}

}
