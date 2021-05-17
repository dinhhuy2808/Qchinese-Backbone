package com.elearning.resources;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.elearning.jerseyguice.jwt.JWTUtil;
import com.elearning.jerseyguice.model.Answer;
import com.elearning.jerseyguice.model.GetDictionaryResponse;
import com.elearning.jerseyguice.model.User;
import com.elearning.services.DictionaryService;
import com.elearning.services.QuizService;
import com.elearning.services.UserService;
import com.elearning.util.Util;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mysql.fabric.xmlrpc.base.Array;

@Singleton
public class Auth extends HttpServlet  {
	@Inject
	Util util;

	@Inject
	DictionaryService dictionaryService;

	@Inject
	JWTUtil jwtUtil;
	private static final Type ANWSER_LIST_TYPE = new TypeToken<ArrayList<Answer>>(){}.getType();  
	private static final Type IDS_TYPE = new TypeToken<ArrayList<String>>(){}.getType();
	@Context
	HttpServletRequest request;
	 
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String getUserDictionary() {
		String token = request.getHeader("Authorization");
		Long user_id = jwtUtil.getUserId(token);
		return "";
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doPost(req, resp);
	}
}
