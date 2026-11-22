package com.twister.persistence;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@ImportAutoConfiguration(classes = PersistenceConfiguration.class)
@SpringBootApplication
public class TestApplication {}
