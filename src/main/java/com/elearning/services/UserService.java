package com.elearning.services;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.StringUtil;

import com.elearning.jerseyguice.constant.ElearningConstant;
import com.elearning.jerseyguice.constant.QuestionType;
import com.elearning.jerseyguice.dao.BaseDao;
import com.elearning.jerseyguice.dao.FriendsDao;
import com.elearning.jerseyguice.dao.UserRankingDao;
import com.elearning.jerseyguice.jwt.JWTUtil;
import com.elearning.jerseyguice.model.APIResponse;
import com.elearning.jerseyguice.model.ChangePasswordRequest;
import com.elearning.jerseyguice.model.Friends;
import com.elearning.jerseyguice.model.FriendsResponse;
import com.elearning.jerseyguice.model.GetUserInfoResponse;
import com.elearning.jerseyguice.model.LoginResponse;
import com.elearning.jerseyguice.model.QuestionDescription;
import com.elearning.jerseyguice.model.QuizResultResponse;
import com.elearning.jerseyguice.model.ResultDetail;
import com.elearning.jerseyguice.model.Rooms;
import com.elearning.jerseyguice.model.SubjectHolderForJWT;
import com.elearning.jerseyguice.model.User;
import com.elearning.jerseyguice.model.UserRanking;
import com.elearning.jerseyguice.model.UserResult;
import com.elearning.util.Util;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.jsonwebtoken.lang.Strings;

@Singleton
public class UserService {
	private static final Type QUESTION_DESCRIPTION_LIST_TYPE = new TypeToken<ArrayList<QuestionDescription>>() {
	}.getType();
	@Inject
	Util util;
	@Inject
	BaseDao baseDao;
	@Inject
	JWTUtil jwtUtil;
	@Inject
	QuizService quizService;
	@Inject
	UserRankingDao userRankingDao;
	@Inject
	FriendsDao friendsDao;

	public String checkUserByPhone(String phone) {
		User user = new User();
		user.setPhone(phone);
		if (StringUtils.isNotBlank(phone) && baseDao.findByKey(user).size() > 0) {
			return "true";
		} else {
			return "false";
		}
	}

	public String registerUser(User user, int method) {
		User tempUser = new User();
		tempUser.setPhone(user.getPhone());
		if (method == ElearningConstant.NORMAL_LOGIN_METHOD && baseDao.findByKey(tempUser).size() > 0) {
			return "201";
		} else {
			tempUser = new User();
			tempUser.setEmail(user.getEmail());
			if (baseDao.findByKey(tempUser).size() > 0) {
				return "202";
			}
		}
		String hashedPassword = util.getMd5(method == ElearningConstant.NORMAL_LOGIN_METHOD ? user.getPassword()
				: ElearningConstant.DEFAULT_PASSWORD_GOOGLE_LOGIN);
		user.setPassword(hashedPassword);
		user.setAccountType(2);
		boolean flag = baseDao.add(user);
		return flag ? "200" : "203";

	}

	public String getUserInfo(Long userId) {
		User user = new User();
		user.setUser_id(userId);
		user = (User) baseDao.findByKey(user).get(0);
		user.setAccountType(null);
		user.setPassword(null);
		user.setUser_id(null);

		GetUserInfoResponse response = new GetUserInfoResponse();
		response.setUser(user);
		response.setQuizResultDetail(getHistoryBy(QuestionType.QUIZ, userId));
		response.setTestResultDetail(getHistoryBy(QuestionType.TEST, userId));
		response.setTotalResults(userRankingDao.getTotalResultBy(userId));
		response.setRanks(userRankingDao.getRankBoard());
		Friends friends = new Friends();
		friends.setUserId(userId);
		friends.setIsAccepped(true);
		List<Friends> friendList = baseDao.findByKey(friends);
		if (!friendList.isEmpty()) {
			response.setFriendIds(friendList.stream().map(friend -> friend.getFriendId()).collect(Collectors.toList()));
		}
		return util.objectToJSON(response);
	}

	public List<QuizResultResponse> getHistoryBy(QuestionType quesionType, Long userId) {
		List<UserResult> userResults = new ArrayList<>();
		UserResult userResult = new UserResult();
		userResult.setUserId(userId);
		userResult.setResultType(quesionType.name());
		userResults = baseDao.findByKeySortBy(userResult, " id desc ");
		List<QuizResultResponse> responses = new ArrayList<>();
		for (UserResult result : userResults) {
			List<QuestionDescription> questionDescriptions = util.jsonToListObject(
					quesionType.compareTo(QuestionType.TEST) == 0
							? quizService.getQuestionForTest(result.getHsk() + "-" + result.getTestLesson())
							: quizService.getQuestionForQuiz(result.getHsk() + "-" + result.getTestLesson()),
					QUESTION_DESCRIPTION_LIST_TYPE);
			QuizResultResponse response = new QuizResultResponse();
			response.setResultDetail(util.jsonToObject(result.getResultDetail(), ResultDetail.class));
			if (response.getResultDetail().getAnswers() != null) {
				response.getResultDetail().getAnswers().stream().forEach(answer -> {
					answer.setUserAnswer(answer.getUserAnswer().replace(",", "-"));
				});
			}
			response.setQuestionDescription(questionDescriptions);
			responses.add(response);
		}
		return responses;
	}

	public String login(User user, int method) {

		String hashedPassword = util.getMd5(method == ElearningConstant.NORMAL_LOGIN_METHOD ? user.getPassword()
				: ElearningConstant.DEFAULT_PASSWORD_GOOGLE_LOGIN);
		user.setPassword(hashedPassword);

		List<User> users = method == ElearningConstant.NORMAL_LOGIN_METHOD
				? baseDao.findByGivenKey(user, "phone,password")
				: baseDao.findByGivenKey(user, "email,password");

		LoginResponse loginResponse = new LoginResponse();
		if (!users.isEmpty()) {
			user = users.get(0);
			String jwtId = Integer.toString(user.getAccountType());
			SubjectHolderForJWT jwtSubjectHolder = new SubjectHolderForJWT();
			jwtSubjectHolder.setLoginMethod(method);
			jwtSubjectHolder.setName(user.getName());
			jwtSubjectHolder.setPhone(user.getPhone());
			jwtSubjectHolder.setUser_id(user.getUser_id());
			String jwtSubject = util.objectToJSON(jwtSubjectHolder);
			long jwtTimeToLive = Long.parseLong("2592000000");
			String jwt = jwtUtil.createJWT(jwtId, // claim = jti
					jwtSubject, // claim = sub
					jwtTimeToLive // used to calculate expiration (claim = exp)
			);

			loginResponse.setEmail(user.getEmail());
			loginResponse.setName(user.getName());
			loginResponse.setPhone(user.getPhone());
			loginResponse.setToken(jwt);

		} else {
			loginResponse.setErrorMessage("Login fail !!!");
			loginResponse.setErrorCode(400);
		}
		return util.objectToJSON(loginResponse);
	}

	public String changePassword(ChangePasswordRequest request) {
		APIResponse response = new APIResponse();
		if (!request.getNewPassword().equals(request.getVerify())) {
			response.setCode(201);
			response.setMessage("Confirmed Password không match.");
		} else {
			User user = new User();
			String hashedPassword = util.getMd5(request.getPassword());
			user.setPassword(hashedPassword);
			user.setUser_id(request.getUserId());
			List<User> users = baseDao.findByKey(user);
			if (users.isEmpty()) {
				response.setCode(202);
				response.setMessage("Password không đúng");
			} else {
				user = users.get(0);
				hashedPassword = util.getMd5(request.getNewPassword());
				user.setPassword(hashedPassword);
				int count = baseDao.updateByInputKey(user, Arrays.asList("user_id"));
				if (count > 0) {
					response.setCode(200);
					response.setMessage("Success");
				} else {
					response.setCode(203);
					response.setMessage("Fail");
				}
			}
		}
		return util.objectToJSON(response);
	}

	public String changePasswordByAdmin(ChangePasswordRequest request) {
		APIResponse response = new APIResponse();
		if (!request.getNewPassword().equals(request.getVerify())) {
			response.setCode(201);
			response.setMessage("Confirmed Password không match.");
		} else {
			User user = new User();
			String hashedPassword = util.getMd5(request.getNewPassword());
			user.setUser_id(request.getUserId());
			user = (User) baseDao.findByKey(user).get(0);
			user.setPassword(hashedPassword);
			int count = baseDao.updateByInputKey(user, Arrays.asList("user_id"));
			if (count > 0) {
				response.setCode(200);
				response.setMessage("Success");
			} else {
				response.setCode(203);
				response.setMessage("Fail");
			}
		}
		return util.objectToJSON(response);
	}
	
	public void updateAvatar(Long userId, String avatar) {
		User user = new User();
		user.setUser_id(userId);
		user = (User) baseDao.findByKey(user).get(0);
		user.setAvatar(avatar);
		baseDao.updateByInputKey(user, Arrays.asList("user_id"));
	}
	
	public void sendFriendRequest(Long userId, Long friendId) throws Exception {
		Friends friends = new Friends();
		friends.setUserId(userId);
		friends.setFriendId(friendId);
		if (baseDao.findByKey(friends).size() > 0) {
			throw new Exception("Already sent request?");
		}
		friends.setUserId(friendId);
		friends.setFriendId(userId);
		if (baseDao.findByKey(friends).size() > 0) {
			throw new Exception("Already sent request?");
		}

		friendsDao.createFriendsRequest(userId, friendId, util.getKey());
	}
	
	public void acceptFriendRequest(Long userId, Long friendId) throws Exception {
		Friends friends = new Friends();
		friends.setUserId(userId);
		friends.setFriendId(friendId);
		friends.setIsAccepped(false);
		List<Friends> friendsList = baseDao.findByKey(friends);
		if (friendsList.isEmpty()) {
			throw new Exception("Not have request.");
		}
		
		friends = friendsList.get(0);
		friends.setIsAccepped(true);
		baseDao.updateByInputKey(friends, Arrays.asList("id"));
	}
	
	public String getFriends(Long userId) {
		return util.objectToJSON(friendsDao.getFriendsDetail(userId));
	}
	public String getFriendsRequest(Long userId) {
		return util.objectToJSON(friendsDao.getFriendRequest(userId));
	}
	public void addGroupChat(String roomKey, Long userId, Long friendId) throws Exception {
		Rooms rooms = new Rooms();
		rooms.setRoomKey(roomKey);
		rooms.setUserId(userId);
		List<Rooms> friendsList = baseDao.findByKey(rooms);
		if (!friendsList.isEmpty()) {
			throw new Exception("Already in group.");
		} else {
			rooms.setCreatedDate(new Date(System.currentTimeMillis()));
			rooms.setIsGroup(true);
			baseDao.add(rooms);
			
			Rooms updateRooms = new Rooms();
			updateRooms.setRoomKey(roomKey);
			updateRooms.setIsGroup(true);
			baseDao.updateByInputKey(updateRooms, Arrays.asList("roomkey"));
		}
	}
}
