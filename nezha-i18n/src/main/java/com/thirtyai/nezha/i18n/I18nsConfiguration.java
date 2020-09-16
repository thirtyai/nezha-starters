package com.thirtyai.nezha.i18n;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * I18nsConfiguration i18ns configuration
 *
 * @author nezha i18n
 */
@Configuration("com.thirtyai.nezha.i18n.I18nsConfiguration")
public class I18nsConfiguration {
	@Bean
	public Status initStatus() { return Status.self(); }

}
