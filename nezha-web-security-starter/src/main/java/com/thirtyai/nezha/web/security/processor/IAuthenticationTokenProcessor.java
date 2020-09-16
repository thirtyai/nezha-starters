package com.thirtyai.nezha.web.security.processor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

/**
 * token authentication processor
 *
 * @author kyleju
 */
public interface IAuthenticationTokenProcessor {

	/**
	 * through token processor distribute credential
	 *
	 * @param token token
	 * @return value
	 */
	default boolean distributeCredential(UsernamePasswordAuthenticationToken token) {
		return true;
	}

	/**
	 * authentication
	 * main check logic
	 *
	 * @param authentication authentication {@link org.springframework.security.core.Authentication}
	 * @param userDetails
	 */
	void authentication(UsernamePasswordAuthenticationToken authentication, UserDetails userDetails) throws AuthenticationException;


	/**
	 * processor support class
	 *
	 * @return {@link List<Class<?>>}
	 */
	List<Class<? extends UsernamePasswordAuthenticationToken>> supports();
}
