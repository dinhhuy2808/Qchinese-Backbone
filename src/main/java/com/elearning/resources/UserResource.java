package com.elearning.resources;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.elearning.jerseyguice.dao.BaseDao;
import com.elearning.jerseyguice.jwt.JWTUtil;
import com.elearning.jerseyguice.model.Answer;
import com.elearning.jerseyguice.model.ChangePasswordRequest;
import com.elearning.jerseyguice.model.User;
import com.elearning.services.QuizService;
import com.elearning.services.UserService;
import com.elearning.util.Util;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Path("/user")
@Singleton
public class UserResource {
	@Inject
	Util util;

	@Inject
	UserService userService;

	@Context
	HttpServletRequest request;
	
	@Inject
	JWTUtil jwtUtil;
	
	@Inject
	BaseDao baseDao;
	private static final Type ANWSER_LIST_TYPE = new TypeToken<ArrayList<Answer>>(){}.getType();  

	@GET
	@Path("/check/{phone}")
	public String user(@PathParam("phone") String phone) {
		return userService.checkUserByPhone(phone);
	}
	
	@GET
	@Path("/v2/user-info")
	public String getUser() {
		String token = request.getHeader("Authorization");
		Long user_id = jwtUtil.getUserId(token);
		return userService.getUserInfo(user_id);
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/{method}")
	public String register(String content,@PathParam("method") int method) {
		User user = new User();
		user = util.jsonToObject(content, User.class);
		return userService.registerUser(user, method);
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/login/{loginMethod}")
	public String login(String content,@PathParam("loginMethod") int loginMethod) {
		User user = util.jsonToObject(content, User.class);
		return userService.login(user, loginMethod);
	}
	
	@GET
	@Path("v1/isAdmin")
	public String isAdmin() {
		String token = request.getHeader("Authorization");
		return jwtUtil.isValidAdmin(token)?"true":"false";
	}
	
	@GET
	@Path("v1/getAllUsers")
	public String getAllUsers() {
		String token = request.getHeader("Authorization");
		return util.objectToJSON(baseDao.findByKey(new User()));
	}
	
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("v1/getUserInfo/{userId}")
	public String getUserInfo(@PathParam("userId") Long userId) {
		return userService.getUserInfo(userId);
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("v1/changePassword")
	public String changePasswordByAdmin(String content,@PathParam("loginMethod") int loginMethod) {
		return userService.changePasswordByAdmin(util.jsonToObject(content, ChangePasswordRequest.class));
	}
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("v2/changePassword")
	public String changePassword(String content,@PathParam("loginMethod") int loginMethod) {
		String token = request.getHeader("Authorization");
		Long user_id = jwtUtil.getUserId(token);
		ChangePasswordRequest request = util.jsonToObject(content, ChangePasswordRequest.class);
		request.setUserId(user_id);
		return userService.changePassword(request);
	}
	
	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("v2/update/avatar")
	public String updateAvatar(String content,@PathParam("loginMethod") int loginMethod) {
		String token = request.getHeader("Authorization");
		Long user_id = jwtUtil.getUserId(token);
		userService.updateAvatar(user_id, content);
		return "";
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("v2/send-friend-request/{friendId}")
	public String sendFriendRequest(String content,@PathParam("friendId") Long friendId) {
		String token = request.getHeader("Authorization");
		Long user_id = jwtUtil.getUserId(token);
		try {
			userService.sendFriendRequest(user_id, friendId);
		} catch (Exception e) {
			return "Error while sending.";
		}
		return "200";
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("v2/accept-friend-request/{friendId}")
	public String acceptFriendRequest(String content,@PathParam("friendId") Long friendId) {
		String token = request.getHeader("Authorization");
		Long user_id = jwtUtil.getUserId(token);
		try {
			userService.acceptFriendRequest(user_id, friendId);
		} catch (Exception e) {
			return "Error while accepting.";
		}
		return "200";
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("v2/get-friends")
	public String getFriends() {
		String token = request.getHeader("Authorization");
		Long user_id = jwtUtil.getUserId(token);
		try {
			return userService.getFriends(user_id);
		} catch (Exception e) {
			return "404";
		}
	}
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("v2/get-friends-request")
	public String getFriendsRequest() {
		String token = request.getHeader("Authorization");
		Long user_id = jwtUtil.getUserId(token);
		try {
			return userService.getFriendsRequest(user_id);
		} catch (Exception e) {
			return "404";
		}
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("v2/add-group/{friendId}/{roomKey}")
	public String addGroup(String content, @PathParam("friendId") Long friendId,
			@PathParam("roomKey") String roomKey) {
		String token = request.getHeader("Authorization");
		Long user_id = jwtUtil.getUserId(token);
		try {
			userService.addGroupChat(roomKey, user_id, friendId);
		} catch (Exception e) {
			return "Error while adding.";
		}
		return "200";
	}
}
