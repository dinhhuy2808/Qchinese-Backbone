package com.elearning.resources;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.elearning.jerseyguice.constant.QuestionType;
import com.elearning.jerseyguice.jwt.JWTUtil;
import com.elearning.jerseyguice.model.Answer;
import com.elearning.services.QuizService;
import com.elearning.util.Util;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

@Path("/quiz")
@Singleton
public class QuizResource {
	@Inject
	Util util;

	@Inject
	JWTUtil jwtUtil;
	
	@Inject
	QuizService quizService;
	private static final Type ANWSER_LIST_TYPE = new TypeToken<ArrayList<Answer>>(){}.getType();
	
	@Context
	HttpServletRequest request;
	
	@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFile(
		@FormDataParam("fileTest") InputStream uploadedInputStream,
		@FormDataParam("fileTest") FormDataContentDisposition fileDetail) {

		String uploadedFileLocation =  fileDetail.getFileName();

		// save it
		writeToFile(uploadedInputStream, uploadedFileLocation);

		String output = "File uploaded to : " + uploadedFileLocation;

		return Response.status(200).entity(output).build();

	}
	// save uploaded file to new location
		private void writeToFile(InputStream uploadedInputStream,
			String uploadedFileLocation) {

			try {
				OutputStream out = new FileOutputStream(new File(
						uploadedFileLocation));
				int read = 0;
				byte[] bytes = new byte[1024];

				out = new FileOutputStream(new File(uploadedFileLocation));
				while ((read = uploadedInputStream.read(bytes)) != -1) {
					out.write(bytes, 0, read);
				}
				out.flush();
				out.close();
			} catch (IOException e) {

				e.printStackTrace();
			}

		}
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("testUpload")
	public String echo(HttpServletRequest request) {
		try {
			InputStream inputStream = request.getPart("fileTest").getInputStream();
			  Scanner sc = new Scanner(inputStream);
		      //Reading line by line from scanner to StringBuffer
		      StringBuffer sb = new StringBuffer();
		      while(sc.hasNext()){
		         sb.append(sc.nextLine());
		      }
		      System.out.println(sb.toString());
		} catch (IOException | ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return "ok";
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("v2/getTest")
	public String test(@QueryParam("id") String test) {
		return quizService.getQuestionForTest(test);
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("v2/checkResult/{hsk}/{test}/{type}/{time}")
	public String checkResult(@PathParam("hsk") String hsk, @PathParam("test") String test,@PathParam("type") QuestionType type,@PathParam("time") int time, String json) {
		List<Answer> requestAnswer = new ArrayList<>();
		requestAnswer = util.jsonToListObject(json, ANWSER_LIST_TYPE);
		Map<String, String> userAnswers = requestAnswer.stream()
				.collect(Collectors.toMap(number -> String.valueOf(number.getNumber()), number -> number.getUserAnswer()));
		String token = request.getHeader("Authorization");
		Long user_id = jwtUtil.getUserId(token);
		return quizService.checkResult(Integer.parseInt(hsk), Integer.parseInt(test), userAnswers, type, user_id, time);
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("v1/update/{id}")
	public String update(@PathParam("id") String test) {
		return quizService.update(test);
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("tests-summary/{hsk}")
	public String getTestsSummary(@PathParam("hsk") String hsk) {
		return quizService.countTests(hsk);
	}
	
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("v2/quiz-history/{hsk}/{lesson}")
	public String getQuizSummary(@PathParam("hsk") String hsk, @PathParam("lesson") String lesson) {
		String token = request.getHeader("Authorization");
		Long user_id = jwtUtil.getUserId(token);
		return quizService.getHistoryBy(hsk, lesson, QuestionType.QUIZ, user_id);
	}
	
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Path("v1/testUploadQuiz")
	public String testUploadQuiz(
			@FormDataParam("fileUpload") InputStream uploadedInputStream,
			@FormDataParam("fileUpload") FormDataContentDisposition fileDetail) throws CloneNotSupportedException {
		return quizService.testUploadQuiz(uploadedInputStream)?"true":"false";
	}
	
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Path("v1/testUploadQuiz2/{hsk}")
	public String testUploadQuiz2(
			@FormDataParam("fileUpload") InputStream uploadedInputStream,
			@FormDataParam("fileUpload") FormDataContentDisposition fileDetail,
			@PathParam("hsk") String hsk) throws CloneNotSupportedException {
		quizService.uploadExerciseFile(uploadedInputStream, hsk);
		return "true";
	}

	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Path("v1/uploadTest/{hsk}")
	public String uploadTest(
			@FormDataParam("fileUpload") InputStream uploadedInputStream,
			@FormDataParam("fileUpload") FormDataContentDisposition fileDetail,
			@PathParam("hsk") String hsk) throws CloneNotSupportedException {
		quizService.uploadTest(uploadedInputStream, hsk);
		return "true";
	}

	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Path("v1/uploadInput/{hsk}")
	public String uploadInput(
			@FormDataParam("fileUpload") InputStream uploadedInputStream,
			@FormDataParam("fileUpload") FormDataContentDisposition fileDetail,
			@PathParam("hsk") String hsk) throws CloneNotSupportedException {
		return String.valueOf(quizService.uploadInputContent(uploadedInputStream, hsk));
	}
}
