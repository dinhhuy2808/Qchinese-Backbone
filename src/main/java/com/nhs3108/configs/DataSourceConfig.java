package com.nhs3108.configs;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

/**
 * Created by nhs3108 on 07/11/2017.
 */
@Configuration
@PropertySource("classpath:application-${spring.profiles.active}.properties")
public class DataSourceConfig {

	@Value("${database.url}")
	private String dbUrl;
	@Value("${database.driver}")
	private String dbDriver;
	@Value("${database.username}")
	private String dbUsername;
	@Value("${database.password}")
	private String dbPassword;

	@Bean
	public DataSource dataSource() {
		DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
		dataSourceBuilder.url(dbUrl);
		dataSourceBuilder.driverClassName(dbDriver);
		dataSourceBuilder.username(dbUsername);
		dataSourceBuilder.password(dbPassword);
		return dataSourceBuilder.build();
	}

	@Bean
	public JdbcTemplate jdbcTemplate() {
		return new JdbcTemplate(dataSource());
	}

	@Bean
	public NamedParameterJdbcTemplate namedParameterJdbcTemplate() {
		return new NamedParameterJdbcTemplate(dataSource());
	}
}
