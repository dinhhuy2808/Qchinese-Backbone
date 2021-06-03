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
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.elearning.constant.QuestionType;
import com.elearning.jwt.JWTUtil;
import com.elearning.model.Answer;
import com.elearning.services.QuizService;
import com.elearning.util.Util;
import com.google.gson.reflect.TypeToken;

@RequestMapping("/quiz")
@RestController
public class QuizResource {
	@Autowired
	private Util util;

	@Autowired
	JWTUtil jwtUtil;

	@Autowired
	QuizService quizService;
	private static final Type ANWSER_LIST_TYPE = new TypeToken<ArrayList<Answer>>() {
	}.getType();

	@PostMapping("/upload")
	public ResponseEntity<String> uploadFile(@RequestParam("fileTest") MultipartFile uploadedInputStream) throws IOException {

		String uploadedFileLocation = uploadedInputStream.getOriginalFilename();

		// save it
		writeToFile(uploadedInputStream.getInputStream(), uploadedFileLocation);

		String output = "File uploaded to : " + uploadedFileLocation;

		return ResponseEntity.ok(output);

	}

	// save uploaded file to new location
	private void writeToFile(InputStream uploadedInputStream, String uploadedFileLocation) {

		try {
			OutputStream out = new FileOutputStream(new File(uploadedFileLocation));
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

	@PostMapping("testUpload")
	public String echo(HttpServletRequest request) {
		try {
			InputStream inputStream = request.getPart("fileTest").getInputStream();
			Scanner sc = new Scanner(inputStream);
			// Reading line by line from scanner to StringBuffer
			StringBuffer sb = new StringBuffer();
			while (sc.hasNext()) {
				sb.append(sc.nextLine());
			}
			System.out.println(sb.toString());
		} catch (IOException | ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "ok";
	}

	@GetMapping("v2/getTest")
	public String test(@RequestParam("id") String test) {
		return quizService.getQuestionForTest(test);
	}

	@PutMapping("v2/checkResult/{hsk}/{test}/{type}/{time}")
	public String checkResult(@PathVariable("hsk") String hsk, @PathVariable("test") String test,
			@PathVariable("type") QuestionType type, @PathVariable("time") int time,
			@RequestBody List<Answer> requestAnswer, HttpServletRequest request) {
		Map<String, String> userAnswers = requestAnswer.stream().collect(
				Collectors.toMap(number -> String.valueOf(number.getNumber()), number -> number.getUserAnswer()));
		String token = request.getHeader("Authorization");
		Long user_id = jwtUtil.getUserId(token);
		return quizService.checkResult(Integer.parseInt(hsk), Integer.parseInt(test), userAnswers, type, user_id, time);
	}

	@GetMapping("v1/update/{id}")
	public String update(@PathVariable("id") String test) {
		return quizService.update(test);
	}

	@GetMapping("tests-summary/{hsk}")
	public String getTestsSummary(@PathVariable("hsk") String hsk) {
		return quizService.countTests(hsk);
	}

	@GetMapping("v2/quiz-history/{hsk}/{lesson}")
	public String getQuizSummary(@PathVariable("hsk") String hsk, @PathVariable("lesson") String lesson,
			HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		Long user_id = jwtUtil.getUserId(token);
		return quizService.getHistoryBy(hsk, lesson, QuestionType.QUIZ, user_id);
	}

	@PostMapping("v1/testUploadQuiz")
	public String testUploadQuiz(@RequestParam("fileUpload") MultipartFile uploadedInputStream) throws CloneNotSupportedException, IOException {
		return quizService.testUploadQuiz(uploadedInputStream.getInputStream()) ? "true" : "false";
	}

	@PostMapping("v1/testUploadQuiz2/{hsk}")
	public String testUploadQuiz2(@RequestParam("fileUpload") MultipartFile uploadedInputStream
			, @PathVariable("hsk") String hsk)
			throws CloneNotSupportedException, IOException {
		quizService.uploadExerciseFile(uploadedInputStream.getInputStream(), hsk);
		return "true";
	}

	@PostMapping("v1/uploadTest/{hsk}")
	public String uploadTest(@RequestParam("fileUpload") MultipartFile uploadedInputStream,
			@PathVariable("hsk") String hsk)
			throws CloneNotSupportedException, IOException {
		quizService.uploadTest(uploadedInputStream.getInputStream(), hsk);
		return "true";
	}

	@PostMapping("v1/uploadInput/{hsk}")
	public String uploadInput(@RequestParam("fileUpload") MultipartFile uploadedInputStream,
			@PathVariable("hsk") String hsk)
			throws CloneNotSupportedException, IOException {
		return String.valueOf(quizService.uploadInputContent(uploadedInputStream.getInputStream(), hsk));
	}
}
