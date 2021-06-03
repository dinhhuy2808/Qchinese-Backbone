package com.elearning.services;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.elearning.constant.ElearningConstant;
import com.elearning.constant.QuestionType;
import com.elearning.dao.BaseDao;
import com.elearning.dao.FriendsDao;
import com.elearning.dao.UserRankingDao;
import com.elearning.entity.Friends;
import com.elearning.entity.Rooms;
import com.elearning.entity.User;
import com.elearning.entity.UserResult;
import com.elearning.jwt.JWTUtil;
import com.elearning.model.APIResponse;
import com.elearning.model.ChangePasswordRequest;
import com.elearning.model.GetUserInfoResponse;
import com.elearning.model.LoginResponse;
import com.elearning.model.QuestionDescription;
import com.elearning.model.QuizResultResponse;
import com.elearning.model.ResultDetail;
import com.elearning.model.SubjectHolderForJWT;
import com.elearning.repository.FriendsRepository;
import com.elearning.repository.RoomsRepository;
import com.elearning.repository.UserRepository;
import com.elearning.repository.UserResultRepository;
import com.elearning.util.Util;
import com.google.gson.reflect.TypeToken;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private static final Type QUESTION_DESCRIPTION_LIST_TYPE = new TypeToken<ArrayList<QuestionDescription>>() {
	}.getType();
	private final Util util;
//	private final BaseDao baseDao;
	private final JWTUtil jwtUtil;
	private final QuizService quizService;
	private final UserRankingDao userRankingDao;
	private final FriendsDao friendsDao;
	private final UserRepository userRepository;
	private final FriendsRepository friendsRepository;
	private final UserResultRepository userResultRepository;
	private final RoomsRepository roomsRepository;

	public String checkUserByPhone(String phone) {
		User user = new User();
		user.setPhone(phone);
		if (StringUtils.isNotBlank(phone) && userRepository.findByPhone(phone).size() > 0) {
			return "true";
		} else {
			return "false";
		}
	}

	public String registerUser(User user, int method) {
		User tempUser = new User();
		tempUser.setPhone(user.getPhone());
		if (method == ElearningConstant.NORMAL_LOGIN_METHOD && userRepository.findByPhone(user.getPhone()).size() > 0) {
			return "201";
		} else {
			tempUser = new User();
			tempUser.setEmail(user.getEmail());
			if (userRepository.findByEmail(user.getEmail()).size() > 0) {
				return "202";
			}
		}
		String hashedPassword = util.getMd5(method == ElearningConstant.NORMAL_LOGIN_METHOD ? user.getPassword()
				: ElearningConstant.DEFAULT_PASSWORD_GOOGLE_LOGIN);
		user.setPassword(hashedPassword);
		user.setAccountType(2);
		boolean flag = userRepository.save(user).getUserId() > 0;
		return flag ? "200" : "203";

	}

	public String getUserInfo(Long userId) {
		User user = new User();
		user = userRepository.findById(userId).get();
		user.setAccountType(null);
		user.setPassword(null);
		user.setUserId(null);

		GetUserInfoResponse response = new GetUserInfoResponse();
		response.setUser(user);
		response.setQuizResultDetail(getHistoryBy(QuestionType.QUIZ, userId));
		response.setTestResultDetail(getHistoryBy(QuestionType.TEST, userId));
		response.setTotalResults(userRankingDao.getTotalResultBy(userId));
		response.setRanks(userRankingDao.getRankBoard());
		List<Friends> friendList = friendsRepository.findByUserIdAndIsAccepped(userId, true);
		if (!friendList.isEmpty()) {
			response.setFriendIds(friendList.stream().map(friend -> friend.getFriendId()).collect(Collectors.toList()));
		}
		return util.objectToJSON(response);
	}

	public List<QuizResultResponse> getHistoryBy(QuestionType quesionType, Long userId) {
		List<UserResult> userResults = new ArrayList<>();
		userResults =  userResultRepository.findByUserIdAndResultTypeOrderByIdDesc(userId, quesionType.name());
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
				? userRepository.findByPhoneAndPassword(user.getPhone(), hashedPassword)
				: userRepository.findByEmailAndPassword(user.getEmail(), hashedPassword);

		LoginResponse loginResponse = new LoginResponse();
		if (!users.isEmpty()) {
			user = users.get(0);
			String jwtId = Integer.toString(user.getAccountType());
			SubjectHolderForJWT jwtSubjectHolder = new SubjectHolderForJWT();
			jwtSubjectHolder.setLoginMethod(method);
			jwtSubjectHolder.setName(user.getName());
			jwtSubjectHolder.setPhone(user.getPhone());
			jwtSubjectHolder.setUserId(user.getUserId());
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
			user.setUserId(request.getUserId());
			List<User> users = userRepository.findByUserIdAndPassword(request.getUserId(), hashedPassword);
			if (users.isEmpty()) {
				response.setCode(202);
				response.setMessage("Password không đúng");
			} else {
				user = users.get(0);
				hashedPassword = util.getMd5(request.getNewPassword());
				user.setPassword(hashedPassword);
				user = userRepository.save(user);
				if (user.getUserId() > 0) {
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
			user.setUserId(request.getUserId());
			user = userRepository.findById(request.getUserId()).get();
			user.setPassword(hashedPassword);
			user = userRepository.save(user);
			if (user.getUserId() > 0) {
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
		user.setUserId(userId);
		user = userRepository.findById(userId).get();
		user.setAvatar(avatar);
		userRepository.save(user);
	}
	
	public void sendFriendRequest(Long userId, Long friendId) throws Exception {
		Friends friends = new Friends();
		friends.setUserId(userId);
		friends.setFriendId(friendId);
		if (friendsRepository.countByUserIdAndFriendId(userId, friendId) > 0) {
			throw new Exception("Already sent request?");
		}
		friendsDao.createFriendsRequest(userId, friendId, util.getKey());
	}
	
	public void acceptFriendRequest(Long userId, Long friendId) throws Exception {
		Friends friends = new Friends();
		friends.setUserId(userId);
		friends.setFriendId(friendId);
		friends.setIsAccepped(false);
		List<Friends> friendsList = friendsRepository.findByUserIdAndFriendIdAndIsAccepped(userId, friendId, false);
		if (friendsList.isEmpty()) {
			throw new Exception("Not have request.");
		}
		
		friends = friendsList.get(0);
		friends.setIsAccepped(true);
		friendsRepository.save(friends);
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
		if (roomsRepository.countByRoomKeyAndUserId(roomKey, userId) > 0) {
			throw new Exception("Already in group.");
		} else {
			rooms.setCreatedDate(LocalDateTime.now());
			rooms.setIsGroup(true);
			roomsRepository.save(rooms);
		}
	}
}
