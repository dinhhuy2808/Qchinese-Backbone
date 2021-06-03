package com.elearning.resources;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.elearning.model.APIResponse;
import com.elearning.model.Answer;
import com.elearning.services.CourseService;
import com.elearning.util.Util;
import com.google.gson.reflect.TypeToken;

@RequestMapping("/course")
@RestController
public class CourseResource {
	@Autowired
	private Util util;

	@Autowired
	private CourseService courseService;
	private static final Type ANWSER_LIST_TYPE = new TypeToken<ArrayList<Answer>>() {
	}.getType();

	@PostMapping("v1/upload/{hsk}")
	public String uploadFile(@RequestParam("fileUpload") MultipartFile file, @PathVariable("hsk") int hsk) throws IOException {
		
		String uploadedFileLocation = file.getOriginalFilename();
		courseService.generateCourseHtmlDetail(file.getInputStream(), hsk);
		// save it
		// writeToFile(uploadedInputStream, uploadedFileLocation);

		String output = "File uploaded to : " + uploadedFileLocation;
		APIResponse response = new APIResponse();
		response.setCode(200);
		response.setMessage("Upload File bài học thành công !!!");
		return util.objectToJSON(response);

	}

	@PostMapping("v1/upload/word/{hsk}")
	public String uploadWordFile(@RequestParam("fileUpload") MultipartFile file, @PathVariable("hsk") int hsk) throws IOException {

		String uploadedFileLocation = file.getOriginalFilename();
		String text = "";

		courseService.generateCourseHtmlDetailInWordDocument(file.getInputStream(), hsk);

		String output = "File uploaded to : " + uploadedFileLocation;

		APIResponse response = new APIResponse();
		response.setCode(200);
		response.setMessage("Upload File bài học thành công !!!");
		return util.objectToJSON(response);

	}

	@PostMapping("{hsk}/lesson/{lesson}")
	public String getLesson(@PathVariable("lesson") String lesson, @PathVariable("hsk") int hsk) {
		return courseService.getLesson(hsk, lesson);
	}

	@PostMapping("getAllCourses")
	public String getAllCourses() {
		return courseService.getAllCourses();
	}

	@PostMapping("getCourseSumary/{hsk}")
	public String getCourseSumaryBy(@PathVariable("hsk") int hsk) {
		return courseService.getCourseSumaryBy(hsk);
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

	@PostMapping("v2/getQuiz/{hsk}/{lesson}")
	public String getQuizBy(@PathVariable("hsk") String hsk, @PathVariable("lesson") String lesson) {
		return courseService.getQuizBy(hsk, lesson);
	}

	@PostMapping("v2/getQuizInput/{hsk}/{lesson}")
	public String getQuizInputBy(@PathVariable("hsk") String hsk, @PathVariable("lesson") String lesson) {
		return courseService.getQuizInputBy(hsk, lesson);
	}
	
}
