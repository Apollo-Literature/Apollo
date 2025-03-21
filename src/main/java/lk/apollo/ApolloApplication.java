package lk.apollo;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ApolloApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure().load();
		System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
		System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
		System.setProperty("SUPABASE_URL", dotenv.get("SUPABASE_URL"));
		System.setProperty("SUPABASE_KEY", dotenv.get("SUPABASE_KEY"));
		System.setProperty("SUPABASE_JWT_SECRET", dotenv.get("SUPABASE_JWT_SECRET"));
		SpringApplication.run(ApolloApplication.class, args);
	}
}