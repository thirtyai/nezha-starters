package com.thirtyai.nezha.web.security.processor;

import com.thirtyai.nezha.web.security.tokens.LoginNamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Processor aware
 *
 * @author kyleju
 */
public interface ProcessorAware {

	/**
	 * login name password authentication aware
	 *
	 * @param authentication authentication
	 * @param userDetails    user details
	 */
	void loginNamePasswordAuthentication(LoginNamePasswordAuthenticationToken authentication, UserDetails userDetails);
}
