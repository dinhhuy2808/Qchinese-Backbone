package com.elearning.configuration.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Getter;
import lombok.Setter;

@Configuration
@PropertySource("classpath:application-${spring.profiles.active}.properties")
@ConfigurationProperties(prefix = "audio")
@Getter
@Setter
public class AudioProperty {
	private String url;
	private String urlExercise;
	private String pathExercise;
	private String pathLesson;
}
