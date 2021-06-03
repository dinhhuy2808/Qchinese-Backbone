package com.elearning.resources;

import java.lang.reflect.Type;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.elearning.dao.BaseDao;
import com.elearning.entity.User;
import com.elearning.jwt.JWTUtil;
import com.elearning.model.Answer;
import com.elearning.model.ChangePasswordRequest;
import com.elearning.repository.UserRepository;
import com.elearning.services.UserService;
import com.elearning.util.Util;
import com.google.gson.reflect.TypeToken;

import lombok.RequiredArgsConstructor;

@RequestMapping("/user")
@RestController
@RequiredArgsConstructor
public class UserResource {
	private final Util util;
	private final UserService userService;
	private final UserRepository userRepository;
	private final JWTUtil jwtUtil;

	@GetMapping("/check/{phone}")
	public String user(@PathVariable("phone") String phone) {
		return userService.checkUserByPhone(phone);
	}
	
	@GetMapping("/v2/user-info")
	public String getUser(HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		Long user_id = jwtUtil.getUserId(token);
		return userService.getUserInfo(user_id);
	}
	
	@PostMapping("/{method}")
	public String register(@RequestBody User user,@PathVariable("method") int method) {
		return userService.registerUser(user, method);
	}
	
	@PostMapping("/login/{loginMethod}")
	public String login(@RequestBody User user,@PathVariable("loginMethod") int loginMethod) {
		return userService.login(user, loginMethod);
	}
	
	@GetMapping("v1/isAdmin")
	public String isAdmin(HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		return jwtUtil.isValidAdmin(token)?"true":"false";
	}
	
	@GetMapping("v1/getAllUsers")
	public String getAllUsers(HttpServletRequest request) {
		return util.objectToJSON(userRepository.findAll());
	}
	
	@GetMapping("v1/getUserInfo/{userId}")
	public String getUserInfo(@PathVariable("userId") Long userId) {
		return userService.getUserInfo(userId);
	}
	
	@PostMapping("v1/changePassword")
	public String changePasswordByAdmin(@RequestBody ChangePasswordRequest changePasswordRequest,
			@PathVariable("loginMethod") int loginMethod) {
		return userService.changePasswordByAdmin(changePasswordRequest);
	}
	
	@PostMapping("v2/changePassword")
	public String changePassword(@RequestBody ChangePasswordRequest changePasswordRequest,
			@PathVariable("loginMethod") int loginMethod,
			HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		Long user_id = jwtUtil.getUserId(token);
		changePasswordRequest.setUserId(user_id);
		return userService.changePassword(changePasswordRequest);
	}
	
	@PostMapping("v2/update/avatar")
	public String updateAvatar(String content, HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		Long user_id = jwtUtil.getUserId(token);
		userService.updateAvatar(user_id, content);
		return "";
	}

	@PostMapping("v2/send-friend-request/{friendId}")
	public String sendFriendRequest(String content,@PathVariable("friendId") Long friendId,
			HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		Long user_id = jwtUtil.getUserId(token);
		try {
			userService.sendFriendRequest(user_id, friendId);
		} catch (Exception e) {
			return "Error while sending.";
		}
		return "200";
	}

	@PostMapping("v2/accept-friend-request/{friendId}")
	public String acceptFriendRequest(@PathVariable("friendId") Long friendId,
			HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		Long user_id = jwtUtil.getUserId(token);
		try {
			userService.acceptFriendRequest(user_id, friendId);
		} catch (Exception e) {
			return "Error while accepting.";
		}
		return "200";
	}

	@PostMapping("v2/get-friends")
	public String getFriends(HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		Long user_id = jwtUtil.getUserId(token);
		try {
			return userService.getFriends(user_id);
		} catch (Exception e) {
			return "404";
		}
	}

	@PostMapping("v2/get-friends-request")
	public String getFriendsRequest(HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		Long user_id = jwtUtil.getUserId(token);
		try {
			return userService.getFriendsRequest(user_id);
		} catch (Exception e) {
			return "404";
		}
	}

	@PostMapping("v2/add-group/{friendId}/{roomKey}")
	public String addGroup(@PathVariable("friendId") Long friendId,
			@PathVariable("roomKey") String roomKey,
			HttpServletRequest request) {
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
