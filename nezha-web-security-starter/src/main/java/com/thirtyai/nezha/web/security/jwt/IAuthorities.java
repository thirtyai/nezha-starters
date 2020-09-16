package com.thirtyai.nezha.web.security.jwt;

import cn.hutool.core.util.StrUtil;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * authorities
 *
 * @author kyleju
 */
public interface IAuthorities {
	default Collection<? extends GrantedAuthority> getAuthorities(String claimsAuthorityValue) {
		Collection<? extends GrantedAuthority> authorities = AuthorityUtils.NO_AUTHORITIES;
		if (StrUtil.isNotBlank(claimsAuthorityValue)) {
			List<String> strings = Arrays.asList(claimsAuthorityValue.split(","));
			authorities = strings.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
		}
		return authorities;
	}
}
