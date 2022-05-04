package com.thewhite.calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * Created on 4/15/2022
 *
 * @author Fedor Ishchenko
 */
@SpringBootApplication
public class App {
    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(App.class);
        Environment env = SpringApplication.run(App.class, args).getEnvironment();
        try {
            logger.info("\n----------------------------------------------------------\n" +
                                "\tApplication '{}' is running! \n" +
                                "\tLocal: \t\thttp://localhost:{}\n" +
                                "\tExternal: \thttp://{}:{}\n" +
                                "----------------------------------------------------------",
                        env.getProperty("spring.application.name"),
                        env.getProperty("server.port"),
                        InetAddress.getLocalHost().getHostAddress(),
                        env.getProperty("server.port"),
                        Arrays.toString(env.getActiveProfiles()));
        } catch (UnknownHostException e) {
            logger.error("Exception host-address", e);
        }
    }
}
