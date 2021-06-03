package com.elearning.resources;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.elearning.dao.DictionaryDao;
import com.elearning.dto.UserDictionaryDTO;
import com.elearning.jwt.JWTUtil;
import com.elearning.model.Answer;
import com.elearning.model.DeleteWordsFromUserDicRequest;
import com.elearning.model.GetCourseDicRequest;
import com.elearning.model.GetDictionaryResponse;
import com.elearning.request.DatatableParamHolder;
import com.elearning.services.DictionaryService;
import com.elearning.util.Util;
import com.google.gson.reflect.TypeToken;

@RequestMapping("/dictionary")
@RestController
public class DictionaryResource {
	@Autowired
	private Util util;

	@Autowired
	private DictionaryService dictionaryService;

	@Autowired
	private DictionaryDao dictionaryDao;

	@Autowired
	private JWTUtil jwtUtil;

	@PostMapping("v1/syncFromUploadFile")
	public String syncFromUploadFile(@RequestParam("fileUpload") MultipartFile uploadedInputStream) throws IOException {
		return dictionaryService.addDataDictionary2(uploadedInputStream.getInputStream()) ? "true" : "false";
	}

	@PostMapping("v1/syncFullFromUploadFile")
	public String syncFullFromUploadFile(@RequestParam("fileUpload") MultipartFile uploadedInputStream) throws IOException {
		return dictionaryService.deleteAndAddFullDataDictionary(uploadedInputStream.getInputStream()) ? "true"
				: "false";
	}

	@PostMapping("v1/syncWordsWithLesson")
	public String syncWordsWithLesson(@RequestParam("fileUpload") MultipartFile uploadedInputStream) throws IOException {
		return dictionaryService.syncWordWithLesson2(uploadedInputStream.getInputStream());
	}

	@GetMapping("v2/translate/{word}")
	public String translateByWord(@PathVariable("word") String word) {
		return dictionaryService.translateByWord(word);
	}

	@PostMapping("v1/words/{page}")
	public String getWordsWith(@PathVariable("page") int page) {
		GetDictionaryResponse res = new GetDictionaryResponse();
		res.setCount(dictionaryService.countWords());
		res.setDictionaries(dictionaryService.getWordsWith(page));
		return util.objectToJSON(res);
	}

	@PostMapping("v2/addToUserDictionary/{id}/{tab}/{where}")
	public String addToUserDictionary(@PathVariable("id") int id, @PathVariable("tab") String tab,
			@PathVariable("where") String where, HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		Long user_id = jwtUtil.getUserId(token);
		return dictionaryService.addToMyDictionary(Arrays.asList(String.valueOf(id)), tab, user_id, where);
	}

	@PostMapping("v2/addWordsToUserDictionary/{tab}/{where}")
	public String addWordsToUserDictionary(@PathVariable("tab") String tab, @PathVariable("where") String where,
			@RequestBody List<String> ids, HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		Long user_id = jwtUtil.getUserId(token);
		return dictionaryService.addToMyDictionary(ids, tab, user_id, where);
	}

	@PostMapping("v2/addWordsToUserDictionaryFromDictionary/{tab}/{where}")
	public String addWordsToUserDictionaryFromDictionary(@PathVariable("tab") String tab,
			@PathVariable("where") String where, @RequestBody List<UserDictionaryDTO> words,
			HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		Long user_id = jwtUtil.getUserId(token);
		return dictionaryService.addToMyDictionaryFromDictionary(words, tab, user_id, where);
	}

	@PostMapping("v2/getUserDictionary/{tab}")
	public String getUserDictionary(@PathVariable("tab") String tab, @RequestBody DatatableParamHolder param,
			HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		Long user_id = jwtUtil.getUserId(token);
		return dictionaryService.getMyDictionary(tab, user_id, param);
	}

	@PostMapping("getNonUserDictionary/{tab}")
	public String getNonUserDictionary(@PathVariable("tab") String tab, String stringParam,
			@RequestBody DatatableParamHolder param ) {
		Long user_id = 0L;
		return dictionaryService.getMyDictionary(tab, user_id, param);
	}

	@PostMapping("v2/getUserTabs")
	public String getUserTabs(HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		Long user_id = jwtUtil.getUserId(token);

		return dictionaryService.getUserTabs(user_id);
	}

	@PostMapping("getNonUserTabs")
	public String getNonUserTabs() {
		Long user_id = 0L;

		return dictionaryService.getUserTabs(user_id);
	}

	@PostMapping("v2/getUserUserDictionaryCreateDate/{tab}")
	public String getUserUserDictionaryCreateDate(@PathVariable ("tab") String tab, HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		Long user_id = jwtUtil.getUserId(token);

		return dictionaryService.getUserUserDictionaryCreateDate(user_id, tab);
	}

	@GetMapping("v2/delete/{tab}")
	public String delete(@PathVariable("tab") String tab, HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		Long user_id = jwtUtil.getUserId(token);
		return dictionaryService.deleteTab(tab, user_id);
	}

	@PostMapping("v2/find-words-by-key/{keyWord}")
	public String findWordsByKeyWord(@PathVariable("keyWord") String keyWord) {
		GetDictionaryResponse res = new GetDictionaryResponse();
		res.setCount(dictionaryService.countWords());
		res.setDictionaries(dictionaryService.findWordsByKeyWord(keyWord));
		return util.objectToJSON(res);
	}

	@PostMapping("getCourseDic")
	public String getCourseDic(@RequestBody GetCourseDicRequest request) {
		return dictionaryService.getCourseDictionary(request);
	}

	@PostMapping("v2/save-practice-words")
	public String savePracticeWords(@RequestBody List<UserDictionaryDTO> wordsId, HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		Long user_id = jwtUtil.getUserId(token);
		return dictionaryService.savePracticeWordsTEST(wordsId, user_id);
	}

	@PostMapping("v2/user-dictionary/delete")
	public String deleteUserDictionary(@RequestBody DeleteWordsFromUserDicRequest deleteRequest,
			HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		Long user_id = jwtUtil.getUserId(token);
		return dictionaryDao.deleteWordsFromUserDic(deleteRequest.getTab(), deleteRequest.getWordIds(), user_id)
				? "true"
				: "false";
	}

	@PostMapping("split-word/{word}")
	public ResponseEntity<Object> splitWord(@PathVariable("word") String word) {
		return ResponseEntity.ok((Object) util.objectToJSON(dictionaryService.splitWordToList(word)));
	}

	@GetMapping("read/{hantu}")
	public ResponseEntity<Object> filter(@PathVariable("hantu") String hantu) throws IOException {
		return ResponseEntity.ok((Object) IOUtils.toByteArray(dictionaryService.readWord(hantu)));
	}
}
