package org.example.pedicle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:/application.properties")
public class PedicleApplication {

    public static void main(String[] args) {
        SpringApplication.run(PedicleApplication.class, args);
    }

}
