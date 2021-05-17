package com.elearning.resources;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.elearning.jerseyguice.dao.DictionaryDao;
import com.elearning.jerseyguice.jwt.JWTUtil;
import com.elearning.jerseyguice.model.Answer;
import com.elearning.jerseyguice.model.DeleteWordsFromUserDicRequest;
import com.elearning.jerseyguice.model.GetCourseDicRequest;
import com.elearning.jerseyguice.model.GetDictionaryResponse;
import com.elearning.jerseyguice.model.DTO.UserDictionaryDTO;
import com.elearning.jerseyguice.model.Request.DatatableParamHolder;
import com.elearning.services.DictionaryService;
import com.elearning.util.Util;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

import org.apache.poi.util.IOUtils;

@Path("/dictionary")
@Singleton
public class DictionaryResource {
	@Inject
	Util util;

	@Inject
	DictionaryService dictionaryService;

	@Inject
	DictionaryDao dictionaryDao;

	@Inject
	JWTUtil jwtUtil;

	private static final Type ANWSER_LIST_TYPE = new TypeToken<ArrayList<Answer>>() {
	}.getType();

	private static final Type IDS_TYPE = new TypeToken<ArrayList<String>>() {
	}.getType();

	private static final Type WORRDS_TYPE = new TypeToken<ArrayList<UserDictionaryDTO>>() {
	}.getType();

	@Context
	HttpServletRequest request;

	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("v1/syncFromUploadFile")
	public String syncFromUploadFile(@FormDataParam("fileUpload") InputStream uploadedInputStream,
			@FormDataParam("fileUpload") FormDataContentDisposition fileDetail) {
		return dictionaryService.addDataDictionary2(uploadedInputStream) ? "true" : "false";
	}

	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("v1/syncFullFromUploadFile")
	public String syncFullFromUploadFile(@FormDataParam("fileUpload") InputStream uploadedInputStream,
			@FormDataParam("fileUpload") FormDataContentDisposition fileDetail) {
		return dictionaryService.deleteAndAddFullDataDictionary(uploadedInputStream) ? "true" : "false";
	}

	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("v1/syncWordsWithLesson")
	public String syncWordsWithLesson(@FormDataParam("fileUpload") InputStream uploadedInputStream,
			@FormDataParam("fileUpload") FormDataContentDisposition fileDetail) {
		return dictionaryService.syncWordWithLesson2(uploadedInputStream);
	}

	@GET
//	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("v2/translate/{word}")
	public String translateByWord(@PathParam("word") String word) {
		return dictionaryService.translateByWord(word);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("v1/words/{page}")
	public String getWordsWith(@PathParam("page") int page) {
		GetDictionaryResponse res = new GetDictionaryResponse();
		res.setCount(dictionaryService.countWords());
		res.setDictionaries(dictionaryService.getWordsWith(page));
		return util.objectToJSON(res);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("v2/addToUserDictionary/{id}/{tab}/{where}")
	public String addToUserDictionary(@PathParam("id") int id, @PathParam("tab") String tab,
			@PathParam("where") String where) {
		String token = request.getHeader("Authorization");
		Long user_id = jwtUtil.getUserId(token);
		return dictionaryService.addToMyDictionary(Arrays.asList(String.valueOf(id)), tab, user_id, where);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("v2/addWordsToUserDictionary/{tab}/{where}")
	public String addWordsToUserDictionary(@PathParam("tab") String tab, @PathParam("where") String where,
			String content) {
		String token = request.getHeader("Authorization");
		Long user_id = jwtUtil.getUserId(token);
		List<String> ids = util.jsonToListObject(content, IDS_TYPE);
		return dictionaryService.addToMyDictionary(ids, tab, user_id, where);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("v2/addWordsToUserDictionaryFromDictionary/{tab}/{where}")
	public String addWordsToUserDictionaryFromDictionary(@PathParam("tab") String tab, @PathParam("where") String where,
			String content) {
		String token = request.getHeader("Authorization");
		Long user_id = jwtUtil.getUserId(token);
		List<UserDictionaryDTO> words = util.jsonToListObject(content, WORRDS_TYPE);
		return dictionaryService.addToMyDictionaryFromDictionary(words, tab, user_id, where);
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("v2/getUserDictionary/{tab}")
	public String getUserDictionary(@PathParam("tab") String tab, String stringParam) {
		String token = request.getHeader("Authorization");
		Long user_id = jwtUtil.getUserId(token);
		DatatableParamHolder param = util.jsonToObject(stringParam, DatatableParamHolder.class);
		return dictionaryService.getMyDictionary(tab, user_id, param);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("getNonUserDictionary/{tab}")
	public String getNonUserDictionary(@PathParam("tab") String tab, String stringParam) {
		DatatableParamHolder param = util.jsonToObject(stringParam, DatatableParamHolder.class);
		Long user_id = 0L;
		return dictionaryService.getMyDictionary(tab, user_id, param);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("v2/getUserTabs")
	public String getUserTabs() {
		String token = request.getHeader("Authorization");
		Long user_id = jwtUtil.getUserId(token);
		
		return dictionaryService.getUserTabs(user_id);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("getNonUserTabs")
	public String getNonUserTabs() {
		Long user_id = 0L;
		
		return dictionaryService.getUserTabs(user_id);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("v2/getUserUserDictionaryCreateDate/{tab}")
	public String getUserUserDictionaryCreateDate(@PathParam("tab") String tab) {
		String token = request.getHeader("Authorization");
		Long user_id = jwtUtil.getUserId(token);
		
		return dictionaryService.getUserUserDictionaryCreateDate(user_id, tab);
	}


	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("v2/delete/{tab}")
	public String delete(@PathParam("tab") String tab) {
		String token = request.getHeader("Authorization");
		Long user_id = jwtUtil.getUserId(token);
		return dictionaryService.deleteTab(tab, user_id);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("v2/find-words-by-key/{keyWord}")
	public String findWordsByKeyWord(@PathParam("keyWord") String keyWord) {
		GetDictionaryResponse res = new GetDictionaryResponse();
		res.setCount(dictionaryService.countWords());
		res.setDictionaries(dictionaryService.findWordsByKeyWord(keyWord));
		return util.objectToJSON(res);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("getCourseDic")
	public String getCourseDic(String content) {
		GetCourseDicRequest request = util.jsonToObject(content, GetCourseDicRequest.class);
		return dictionaryService.getCourseDictionary(request);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("v2/save-practice-words")
	public String savePracticeWords(String content) {
		List<UserDictionaryDTO> wordsId = util.jsonToListObject(content, WORRDS_TYPE);
		String token = request.getHeader("Authorization");
		Long user_id = jwtUtil.getUserId(token);
		return dictionaryService.savePracticeWordsTEST(wordsId, user_id);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("v2/user-dictionary/delete")
	public String deleteUserDictionary(String content) {
		DeleteWordsFromUserDicRequest deleteRequest = util.jsonToObject(content, DeleteWordsFromUserDicRequest.class);
		String token = request.getHeader("Authorization");
		Long user_id = jwtUtil.getUserId(token);
		return dictionaryDao.deleteWordsFromUserDic(deleteRequest.getTab(), deleteRequest.getWordIds(), user_id)
				? "true"
				: "false";
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("split-word/{word}")
	public Response splitWord(@PathParam("word") String word) {
		ResponseBuilder response = null;
		response = Response.ok((Object)util.objectToJSON(dictionaryService.splitWordToList(word)));
		response.header("Access-Control-Allow-Origin:", "*");
		return response.build();
	}

	@GET
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("read/{hantu}")
	public Response filter(@PathParam("hantu") String hantu) {
		ResponseBuilder response = null;
		try {
			response = Response.ok((Object)IOUtils.toByteArray(dictionaryService.readWord(hantu)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		response.header("Access-Control-Allow-Origin:", "*");
		return response.build();
	}
}
