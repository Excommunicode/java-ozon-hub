package kz.ozon.javaozonhub;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class JavaOzonHubApplication implements CommandLineRunner {
    @Value("${server.port}")
    private String port;

    public static void main(String[] args) {
        SpringApplication.run(JavaOzonHubApplication.class, args);
    }

    @Override
    public void run(String... args) {
      log.info("App is running on the port {}", port);
    }
}
