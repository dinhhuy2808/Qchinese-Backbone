package com.nhs3108.filtes;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.GenericFilterBean;

/**
 * Created by nhs3108 on 29/03/2017.
 */
public class JWTAuthenticationFilter extends GenericFilterBean {
	@Autowired
	private JWTUtil jwtUtil;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		/*
		 * Authentication authentication =
		 * TokenAuthenticationService.getAuthentication((HttpServletRequest)
		 * servletRequest);
		 * SecurityContextHolder.getContext().setAuthentication(authentication);
		 * filterChain.doFilter(servletRequest, servletResponse);
		 */
        
        HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		String authType = "";
		if (request.getServletPath().split("/").length > 2) {
			authType = request.getServletPath().split("/")[2];
		}
		String token = request.getHeader("Authorization");
		// v2: user, v1: admin
		if (authType.matches("v[1-2]")) {
			if (token == null) {
				if (request.getMethod().equals("OPTIONS")) {
					filterChain.doFilter(servletRequest, servletResponse);
				} else {
					response.setStatus(401);
				}
				
			} else {
				if (isValidToken(authType, token)) { // `v1|v2` have to after service name:
																		// `account`,`course`...
					filterChain.doFilter(servletRequest, servletResponse);
				} else {
					response.setStatus(401);
				}
			}
		} else {
			filterChain.doFilter(servletRequest, servletResponse);
		}
    }

	private boolean isValidToken(String authType, String token) {
		return ((authType.equals("v2") && jwtUtil.isValidUser(token))
				||  jwtUtil.isValidAdmin(token));
	}
}
