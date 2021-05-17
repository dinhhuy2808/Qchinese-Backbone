package com.elearning.resources;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.elearning.jerseyguice.model.Answer;
import com.elearning.services.UploadService;
import com.elearning.util.Util;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import static com.github.reap.rest.guice.BindJerseyPropertiesModule.BIND_LESSON_QUIZ;;

@Path("/upload")
@Singleton
public class UploadResource {
	@Inject
	@Named(BIND_LESSON_QUIZ)
	private String lessonQuizPath;
	
	@Inject
	Util util;

	@Inject
	UploadService uploadService;
	private static final Type ANWSER_LIST_TYPE = new TypeToken<ArrayList<Answer>>(){}.getType();
	
	@POST
	@Path("v1/upload-lesson-quiz/zip")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadImage(
		@FormDataParam("fileUpload") InputStream uploadedInputStream,
		@FormDataParam("fileUpload") FormDataContentDisposition fileDetail,
		@PathParam("hsk") int hsk) {

		String uploadedFileLocation =  fileDetail.getFileName();
		// save it
		writeToFile(uploadedInputStream, lessonQuizPath+"\\"+uploadedFileLocation);
		uploadService.uploadLessonQuiz(uploadedFileLocation);
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

	 
}
