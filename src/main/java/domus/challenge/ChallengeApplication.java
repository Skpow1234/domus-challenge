package domus.challenge;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
	info = @Info(
		title = "Domus Challenge API",
		version = "1.0.0",
		description = "REST API for retrieving directors based on movie count threshold",
		contact = @Contact(
			name = "Domus Challenge",
			email = "challenge@domus.com"
		),
		license = @License(
			name = "MIT License",
			url = "https://opensource.org/licenses/MIT"
		)
	)
)
public class ChallengeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChallengeApplication.class, args);
	}

}
