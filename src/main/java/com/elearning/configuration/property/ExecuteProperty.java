package com.elearning.configuration.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Getter;
import lombok.Setter;

@Configuration
@PropertySource("classpath:application-${spring.profiles.active}.properties")
@ConfigurationProperties(prefix = "execute")
@Getter @Setter
public class ExecuteProperty {
	private String uploadShellScript;
	private String dictionaryUploadShellScript;
	private String syncDictionaryUploadShellScript;
	private String testUploadShellScript;
}
