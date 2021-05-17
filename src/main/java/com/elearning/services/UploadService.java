package com.elearning.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.elearning.jerseyguice.dao.BaseDao;
import com.elearning.util.Util;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import static com.github.reap.rest.guice.BindJerseyPropertiesModule.BIND_LESSON_QUIZ;;

@Singleton
public class UploadService {
	@Inject
	@Named(BIND_LESSON_QUIZ)
	private String lessonQuizPath;
	@Inject
	Util util;
	@Inject
	BaseDao baseDao;

	public boolean uploadLessonQuiz(String fileName) {
		String fileZip = lessonQuizPath + "\\"+fileName;
		
		ZipEntry zipEntry;
		try {
			ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip));
			zipEntry = zis.getNextEntry();
			File destDir = new File(lessonQuizPath);
			byte[] buffer = new byte[1024];
			while (zipEntry != null) {
				File newFile = newFile(destDir, zipEntry);
				FileOutputStream fos = new FileOutputStream(newFile);
				int len;
				while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}
				fos.close();
				zipEntry = zis.getNextEntry();
			}
			zis.closeEntry();
			zis.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	private File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
		File destFile = new File(destinationDir, zipEntry.getName());

		String destDirPath = destinationDir.getCanonicalPath();
		String destFilePath = destFile.getCanonicalPath();

		if (!destFilePath.startsWith(destDirPath + File.separator)) {
			throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
		}

		return destFile;
	}
}
