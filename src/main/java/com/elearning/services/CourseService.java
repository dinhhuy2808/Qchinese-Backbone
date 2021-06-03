package com.elearning.services;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.elearning.configuration.property.AudioProperty;
import com.elearning.configuration.property.CourseProperty;
import com.elearning.configuration.property.LessonProperty;
import com.elearning.configuration.property.PublicProperty;
import com.elearning.entity.Course;
import com.elearning.model.CourseHolder;
import com.elearning.model.GetCourseResponse;
import com.elearning.model.Lesson;
import com.elearning.model.LessonForInput;
import com.elearning.model.LessonPart;
import com.elearning.model.LessonPartForInput;
import com.elearning.repository.CourseRepository;
import com.elearning.util.Util;
import com.mysql.jdbc.StringUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseService {
	
	private static final String HSK = "{hsk}";
	private Integer idCount = 0;
	
	private final CourseProperty courseProperty;
	private final LessonProperty lessonProperty;
	private final AudioProperty audioProperty;
	private final PublicProperty publicProperty;
	private final Util util;
	private final CourseRepository courseRepository;
	
	public void generateCourseHtmlDetail(InputStream inputExcelStream, int hsk) {
		try {
			CourseHolder courseHolder = new CourseHolder();
			courseHolder.setHsk(hsk);
			List<Lesson> lessions = new ArrayList<>();
			XSSFWorkbook workbook = new XSSFWorkbook(inputExcelStream);
			Course course = new Course();
			courseRepository.deleteByHsk(hsk);
			for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
				XSSFSheet sheet = workbook.getSheetAt(i);
				// lessions.add(getLession(sheet));
				Lesson lesson = getLession(sheet);
				String json = util.objectToJSON(lesson);
				util.writeToFile(json, courseProperty.getPath() + "/" + "hsk-" + hsk + "-lesson-" + sheet.getSheetName() + ".json");
				course = new Course();
				course.setHsk(hsk);
				course.setLesson(new String(sheet.getSheetName().getBytes(StandardCharsets.UTF_8)));
				course.setTitle(lesson.getTitle());
				
				if (courseRepository.findAll().size() > 0) {
					course = courseRepository.findByHskAndLesson(hsk, course.getLesson());
					course.setTitle(lesson.getTitle());
					courseRepository.save(course);
				} else {
					courseRepository.save(course);
				}
				saveTypingHtml(lesson, String.valueOf(hsk), course.getLesson());
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void saveTypingHtml(Lesson lesson, String hsk, String lessonNo) {
		LessonForInput lessonForInput = new LessonForInput();
		lessonForInput.setTitle(lesson.getTitle());
		List<LessonPartForInput> lessonPartForInputs = new LinkedList<LessonPartForInput>();
		idCount = 0;
		int part = 0;
		for(LessonPart lessonPart: lesson.getLessionParts()) {
			LessonPartForInput lessonPartForInput = new LessonPartForInput();
			lessonPartForInput.setPart(lessonPart.getPart());
			Document doc = Jsoup.parse(lessonPart.getDescription());
			Elements elements = doc.getElementsByTag("rt");
			elements.remove();
			elements = doc.getElementsByTag("rp");
			elements.remove();
			lessonPartForInput.setContent(generateTypingHtml(doc.text(), hsk + "-" + lessonNo, idCount, part++));
			lessonPartForInputs.add(lessonPartForInput);
		}
		lessonForInput.setLessionParts(lessonPartForInputs);
		String json = util.objectToJSON(lessonForInput);
		util.writeToFile(json, courseProperty.getInputPath() + "/" + "hsk-" + hsk + "-lesson-" + lessonNo + ".json");
	}

	
	private String generateTypingHtml(String content, String number, Integer idCount, int part) {
		if (content.isEmpty()) {
			return content;
		}
		String template = "<div><div class=\"quote\" id=\"content\">%s</div>"
				+ "<textarea id=\"input%s-%d-%d\" class=\"input_area form-control hantu-only\" onPaste=\"return true\" placeholder=\"start typing here...\""
				+ " oninput=\"Window.courseLessonComponent.processCurrentText(event.target)\"></textarea>"
				+ "</div>";
		String result = "";
		Pattern p = Pattern.compile("[\n。？]");
		String[] contentSplit = p.split(content);
		for(String read: contentSplit) {
			String[] s = read.split("：");
			if (s.length > 1) {
				s[0]="";
				read = String.join(" ", s);
			}
			if (!read.matches("[a-zA-Z ]+")) {
				result += String.format(template, read, number, part, idCount++);			
			}
		}
		return result;
	}
	
	public void generateCourseHtmlDetailInWordDocument(InputStream inputExcelStream, int hsk) {
		try {
			Document doc = Jsoup.parse(inputExcelStream, "ISO-8859-1", "");
			Elements elements = doc.getElementsByTag("tr");
			int lesson = 1;
			for (Element e : elements) {
				int count = 1;
				Lesson lession = new Lesson();
				List<LessonPart> parts = new ArrayList<LessonPart>();
				Elements td = e.getElementsByTag("td");
				LessonPart part = new LessonPart();
				part.setPart(String.valueOf(count++));

				for (Element child : td.get(1).getElementsByTag("span")) {
					String style = child.attr("style");
					if (style.contains("font-size")) {
						String newStyle = "font-size: 30px !important;"
								+ String.join(";", style.substring(style.indexOf(";") + 1, style.length()));
						child.attr("style", newStyle);
					}
				}
				for (Element child : td.get(1).getElementsByTag("rt")) {
					String style = child.attr("style");
					if (style.contains("font-size")) {
						String newStyle = "font-size: 18px;"
								+ String.join(";", style.substring(style.indexOf(";") + 1, style.length()));
						child.attr("style", newStyle);
					}
				}
				try {
					for (Element child : td.get(1).getElementsByTag("p")) {
						if (child.text().trim().equals("")) {
							// child.remove();
							parts.add((LessonPart) part.clone());
							part = new LessonPart();
							part.setLocation("");
							part.setPart(String.valueOf(count++));
						} else {
							part.setDescription(
									(part.getDescription() == null ? "" : part.getDescription()) + child.outerHtml());
						}
					}
					parts.add((LessonPart) part.clone());
				} catch (CloneNotSupportedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				lession.setLessionParts(parts);
				lession.setTitle(td.get(0).html());
				util.writeToFile(util.objectToJSON(lession), courseProperty.getPath() + "/" + "hsk-" + hsk + "-lesson-"
						+ (lesson) + ".json");
				Course course = new Course();
				course.setHsk(hsk);
				course.setLesson(String.valueOf(lesson++));
				Elements elementsRt = td.get(0).getElementsByTag("rt");
				elementsRt.remove();
				Elements elementsRp = td.get(0).getElementsByTag("rp");
				elementsRp.remove();
				course.setTitle(td.get(0).text());
				if (courseRepository.countByHskAndLesson(hsk, course.getLesson()) > 0) {
					course = courseRepository.findByHskAndLesson(hsk, course.getLesson());
					course.setTitle(td.get(0).text());
					courseRepository.save(course);
				} else {
					courseRepository.save(course);
				}
				saveTypingHtml(lession, String.valueOf(hsk), course.getLesson());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Lesson getLession(XSSFSheet sheet) {
		Lesson lession = new Lesson();
		List<LessonPart> parts = new ArrayList<LessonPart>();
		Iterator<Row> rowIterator = sheet.iterator();
		int count = 0;
		try {
			while (rowIterator.hasNext()) {
				if (count == 0) {
					Row row = rowIterator.next();
					count++;
				} else {
					XSSFRow row = (XSSFRow) rowIterator.next();
					Cell cell0 = row.getCell(0);
					if (cell0!=null && !StringUtils.isNullOrEmpty(cell0.toString())) {
						lession.setTitle(row.getCell(0) == null ? "" : row.getCell(0).toString());
						LessonPart part = new LessonPart();
						String s = "";
						if (row.getCell(1) != null) {
							s = row.getCell(1).getCellType() == 0 ? row.getCell(1).toString().split("\\.")[0]
									: row.getCell(1).getStringCellValue();
						}
						part.setPart(s);
						part.setDescription(row.getCell(3) == null ? ""
								: getHtmlFormatedCellValueFromSheet(row.getCell(3).getRichStringCellValue().getString()));
						part.setLocation(row.getCell(2) == null ? "" : row.getCell(2).toString());
						parts.add(part);
						count++;
					} else {
						break;
					}
				}
			}
			lession.setLessionParts(parts);
		} catch (Exception e) {
			System.out.println(sheet.getSheetName());
			e.printStackTrace();
		}

		return lession;
	}

	private String getHtmlFormatedCellValueFromSheet(String cellText) {
		String html = "";
		List<String> textSplit = Arrays.asList(cellText.split("\n"));
		html = textSplit.stream().map(text -> {
			if (text.contains(":")) {
				String temp = text.substring(0, text.indexOf(":"));
				text = String.format("<strong>%s</strong>", temp) + text.substring(text.indexOf(":"), text.length());
			}
			if (text.contains("：")) {
				String temp = text.substring(0, text.indexOf("："));
				text = String.format("<strong>%s</strong>", temp) + text.substring(text.indexOf("："), text.length());
			}
			return text;
		}).collect(Collectors.joining("<br/>"));
		return html;
	}

	public String getLesson(int hsk, String lesson) {
		String json = util.getJsonStringFromFile(courseProperty.getPath() + "/" + "hsk-" + hsk + "-lesson-" + lesson + ".json");
		Lesson currentLesson = util.jsonToObject(json, Lesson.class);
		currentLesson = getLessonWithAudioForPart(currentLesson, hsk, lesson);
		List<Course> courses = courseRepository.findByHsk(hsk);
		List<String> lessons = courses.stream().map(item -> item.getLesson()).collect(Collectors.toList());
		int index = lessons.indexOf(lesson);
		GetCourseResponse getCourseResponse = new GetCourseResponse();
		getCourseResponse.setLesson(currentLesson);
		getCourseResponse.setNextLesson(index == lessons.size() - 1 ? "" : lessons.get(index + 1));
		getCourseResponse.setPreviousLesson(index == 0 ? "" : lessons.get(index - 1));
		String jsonInput = util
				.getJsonStringFromFile(courseProperty.getInputPath() + "/" + "hsk-" + hsk + "-lesson-" + lesson + ".json");
		getCourseResponse.setLessonForInput(util.jsonToObject(jsonInput, LessonForInput.class));
		return util.objectToJSON(getCourseResponse);
	}

	public String getAllCourses() {
		List<Course> courses = courseRepository.getAllCourses();
		return util.objectToJSON(courses);
	}

	public String getCourseSumaryBy(int hsk) {
		Course course = new Course();
		course.setHsk(hsk);
		List<Course> courses = courseRepository.findByHsk(hsk);
		return util.objectToJSON(courses);
	}

	public String getQuizBy(String hsk, String lesson) {
		return util.getJsonStringFromFile(lessonProperty.getQuizPath() + "/" + "hsk-" + hsk + "-" + lesson + ".json");
	}

	public String getQuizInputBy(String hsk, String lesson) {
		return util.getJsonStringFromFile(lessonProperty.getQuizInputPath() + "/" + "hsk-" + hsk + "-" + lesson + ".json");
	}
	private Lesson getLessonWithAudioForPart(Lesson lesson, int hsk, String less) {
		String lessonKey = String.format("%02d", Integer.parseInt(less));
		Lesson returnLesson = lesson;
		String url = audioProperty.getPathLesson();
		url = url.replace(HSK, String.valueOf(hsk));
		List<String> filesName = util.getAllFilesNameInFolder(publicProperty.getAudioPath()+"BH/"+hsk);
		for (LessonPart part : returnLesson.getLessionParts()) {
			
			for(String fileName:filesName) {
				if (fileName.startsWith(lessonKey+"-"+part.getPart())) {
					part.setAudio(url+fileName);
					break;
				}
			}
		}
		return returnLesson;
	}

	private SSLSocketFactory socketFactory() {
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[0];
			}

			public void checkClientTrusted(X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(X509Certificate[] certs, String authType) {
			}
		} };

		try {
			SSLContext sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
			SSLSocketFactory result = sslContext.getSocketFactory();

			return result;
		} catch (NoSuchAlgorithmException | KeyManagementException e) {
			throw new RuntimeException("Failed to create a SSL socket factory", e);
		}
	}
}
