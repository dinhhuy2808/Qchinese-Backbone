package com.elearning.resources;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.poi.util.IOUtils;

import com.elearning.jerseyguice.model.APIResponse;
import com.elearning.jerseyguice.model.Answer;
import com.elearning.services.CourseService;
import com.elearning.services.QuizService;
import com.elearning.util.Util;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

@Path("/course")
@Singleton
public class CourseResource {
	@Inject
	Util util;

	@Inject
	CourseService courseService;
	private static final Type ANWSER_LIST_TYPE = new TypeToken<ArrayList<Answer>>() {
	}.getType();

	@POST
	@Path("v1/upload-image")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadImage(@FormDataParam("fileUpload") InputStream uploadedInputStream,
			@FormDataParam("fileUpload") FormDataContentDisposition fileDetail, @PathParam("hsk") int hsk) {

		String uploadedFileLocation = fileDetail.getFileName();
		// save it
		writeToFile(uploadedInputStream, uploadedFileLocation);

		String output = "File uploaded to : " + uploadedFileLocation;

		return Response.status(200).entity(output).build();

	}

	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("v1/upload/{hsk}")
	public String uploadFile(@FormDataParam("fileUpload") InputStream uploadedInputStream,
			@FormDataParam("fileUpload") FormDataContentDisposition fileDetail, @PathParam("hsk") int hsk) {

		String uploadedFileLocation = fileDetail.getFileName();
		courseService.generateCourseHtmlDetail(uploadedInputStream, hsk);
		// save it
		// writeToFile(uploadedInputStream, uploadedFileLocation);

		String output = "File uploaded to : " + uploadedFileLocation;
		APIResponse response = new APIResponse();
		response.setCode(200);
		response.setMessage("Upload File bài học thành công !!!");
		return util.objectToJSON(response);

	}

	@POST
	@Path("v1/upload/word/{hsk}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.TEXT_PLAIN)
	public String uploadWordFile(@FormDataParam("fileUpload") InputStream uploadedInputStream,
			@FormDataParam("fileUpload") FormDataContentDisposition fileDetail, @PathParam("hsk") int hsk) {

		String uploadedFileLocation = fileDetail.getFileName();
		String text = "";

		courseService.generateCourseHtmlDetailInWordDocument(uploadedInputStream, hsk);

		String output = "File uploaded to : " + uploadedFileLocation;

		APIResponse response = new APIResponse();
		response.setCode(200);
		response.setMessage("Upload File bài học thành công !!!");
		return util.objectToJSON(response);

	}

	@POST
	@Path("{hsk}/lesson/{lesson}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String getLesson(@PathParam("lesson") String lesson, @PathParam("hsk") int hsk) {

		return courseService.getLesson(hsk, lesson);

	}

	@POST
	@Path("getAllCourses")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String getAllCourses() {
		return courseService.getAllCourses();
	}

	@POST
	@Path("getCourseSumary/{hsk}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String getCourseSumaryBy(@PathParam("hsk") int hsk) {
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

	@POST
	@Path("v2/getQuiz/{hsk}/{lesson}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String getQuizBy(@PathParam("hsk") String hsk, @PathParam("lesson") String lesson) {
		return courseService.getQuizBy(hsk, lesson);
	}

	@POST
	@Path("v2/getQuizInput/{hsk}/{lesson}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String getQuizInputBy(@PathParam("hsk") String hsk, @PathParam("lesson") String lesson) {
		return courseService.getQuizInputBy(hsk, lesson);
	}
	
}
