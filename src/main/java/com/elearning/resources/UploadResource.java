package com.elearning.resources;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.elearning.configuration.property.LessonProperty;
import com.elearning.services.UploadService;;

@RequestMapping("/upload")
@RestController
public class UploadResource {
	@Autowired
	private LessonProperty lessonProperty;
	
	@Autowired
	private UploadService uploadService;
	
	@PostMapping("v1/upload-lesson-quiz/zip")
	public ResponseEntity<String> uploadImage(
		@RequestParam("fileUpload") MultipartFile uploadedInputStream,
		@PathVariable("hsk") int hsk) throws IOException {

		String uploadedFileLocation =  uploadedInputStream.getOriginalFilename();
		// save it
		writeToFile(uploadedInputStream.getInputStream(), lessonProperty.getQuizPath()+"\\"+uploadedFileLocation);
		uploadService.uploadLessonQuiz(uploadedFileLocation);
		String output = "File uploaded to : " + uploadedFileLocation;
		
		return ResponseEntity.ok().body(output);

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
