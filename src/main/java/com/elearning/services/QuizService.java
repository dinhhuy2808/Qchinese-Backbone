package com.elearning.services;

import static com.github.reap.rest.guice.BindJerseyPropertiesModule.BIND_AUDIO_PATH_FOR_EXERCISE;
import static com.github.reap.rest.guice.BindJerseyPropertiesModule.BIND_AUDIO_URL_FOR_HSK;
import static com.github.reap.rest.guice.BindJerseyPropertiesModule.BIND_DOCUMENT_ROOT_PATH;
import static com.github.reap.rest.guice.BindJerseyPropertiesModule.BIND_IMAGE_PATH_FOR_EXERCISE;
import static com.github.reap.rest.guice.BindJerseyPropertiesModule.BIND_LESSON_QUIZ;
import static com.github.reap.rest.guice.BindJerseyPropertiesModule.BIND_LESSON_QUIZ_INPUT;
import static com.github.reap.rest.guice.BindJerseyPropertiesModule.BIND_PUBLIC_IMAGE_PATH;
import static com.github.reap.rest.guice.BindJerseyPropertiesModule.BIND_QUESTIONS_PATH;
import static com.github.reap.rest.guice.BindJerseyPropertiesModule.BIND_EXECUTE_UPLOAD_SHELL_SCRIPT;
import static com.github.reap.rest.guice.BindJerseyPropertiesModule.BIND_EXECUTE_TEST_UPLOAD_SHELL_SCRIPT;
import static com.github.reap.rest.guice.BindJerseyPropertiesModule.BIND_UPLOAD_EXERCISES_PATH;
import static com.github.reap.rest.guice.BindJerseyPropertiesModule.BIND_UPLOAD_TEST_PATH;
import static com.github.reap.rest.guice.BindJerseyPropertiesModule.BIND_BATCH_PATH;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.elearning.jerseyguice.constant.Category;
import com.elearning.jerseyguice.constant.QuestionType;
import com.elearning.jerseyguice.dao.BaseDao;
import com.elearning.jerseyguice.dao.QuizDao;
import com.elearning.jerseyguice.dao.UserRankingDao;
import com.elearning.jerseyguice.model.Answer;
import com.elearning.jerseyguice.model.GetTestSummaryResponse;
import com.elearning.jerseyguice.model.PromoteSetting;
import com.elearning.jerseyguice.model.QuestionBody;
import com.elearning.jerseyguice.model.QuestionDescription;
import com.elearning.jerseyguice.model.QuestionDescriptionForInput;
import com.elearning.jerseyguice.model.QuizResultResponse;
import com.elearning.jerseyguice.model.Result;
import com.elearning.jerseyguice.model.ResultDetail;
import com.elearning.jerseyguice.model.UserRanking;
import com.elearning.jerseyguice.model.UserResult;
import com.elearning.util.Util;
import com.google.common.collect.ImmutableMap;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton
public class QuizService {
	@Inject
	@Named(BIND_QUESTIONS_PATH)
	private String questionPath;

	@Inject
	@Named(BIND_LESSON_QUIZ)
	private String lessonQuizPath;

	@Inject
	@Named(BIND_LESSON_QUIZ_INPUT)
	private String lessonQuizInputPath;
	
	@Inject
	@Named(BIND_IMAGE_PATH_FOR_EXERCISE)
	private String imageExercisePath;

	@Inject
	@Named(BIND_AUDIO_URL_FOR_HSK)
	private String audioUrl;

	@Inject
	@Named(BIND_AUDIO_PATH_FOR_EXERCISE)
	private String audioExercisePath;

	@Inject
	@Named(BIND_DOCUMENT_ROOT_PATH)
	private String documentRootPath;

	@Inject
	@Named(BIND_PUBLIC_IMAGE_PATH)
	private String publicImagePath;

	@Inject
	@Named(BIND_UPLOAD_EXERCISES_PATH)
	private String uploadExercisesPath;
	
	@Inject
	@Named(BIND_UPLOAD_TEST_PATH)
	private String uploadTestPath;
	
	@Inject
	@Named(BIND_EXECUTE_UPLOAD_SHELL_SCRIPT)
	private String executeUploadShellScript;
	
	@Inject
	@Named(BIND_EXECUTE_TEST_UPLOAD_SHELL_SCRIPT)
	private String executeTestUploadShellScript;
	
	@Inject
	@Named(BIND_BATCH_PATH)
	private String batchPath;
	
	private static final Type QUESTION_DESCRIPTION_LIST_TYPE = new TypeToken<ArrayList<QuestionDescription>>() {
	}.getType();

	@Inject
	Util util;

	@Inject
	BaseDao baseDao;

	@Inject
	UserRankingDao userRankingDao;

	@Inject
	QuizDao quizDao;

	private static String IMAGE_SOURCE_TEMP = "!image_source!";
	private static String AUDIO_TEMP = "!audio_temp!";
	private static String QUESTION_DESCRIPTION_TEMP = "!question_description!";
	private static String TYPE_1_TEMPLATE = "<div class=\"qustion\"><div class=\"field-number\">" + "" + "</div> "
			+ "<div class=\"field-image\"> " + " <img src=\"!image_source!\" alt=\"\"> " + "</div> "
			+ "<div class=\"field-audio\"> " + " !audio_temp! " + "</div> "
			+ "<div class=\"text-justify field-subject\"> " + " <p class=\"pinyin\">!question_description!</p> "
			+ "</div></div>";
	private static String ANSWER_CHAR_TEMP = "!answer_char!";
	private static String ANSWER_DESCRIPTION_TEMP = "!answer_description!";
	private static String FIELD_OPTION_TEMPMLATE = "<div class=\"field-option\"> <span class=\"field-no\">!answer_char!</span>\r\n"
			+ "<p><span class=\"label-words\" data-wid=\"875\">!answer_description!</span></p>\r\n" + "</div>";

	public String getQuestionForTest(String test) {
		String path = questionPath + "/test-" + test;
		return util.getJsonStringFromFile(path + ".json");
	}

	public String getQuestionForQuiz(String quiz) {
		String path = lessonQuizPath + "/hsk-" + quiz;
		return util.getJsonStringFromFile(path + ".json");
	}

	private List<QuestionDescription> getQuestionDescriptions(String path) {
		List<QuestionDescription> questionDescriptions = new ArrayList<>();
		String text = "";
		try {
			text = new String(Files.readAllBytes(Paths.get(path + ".html")), StandardCharsets.UTF_8);
			Document doc = Jsoup.parse(text);
			Elements items = doc.getElementsByClass("item");
			for (Element item : items) {
				String type = item.attr("type");
				questionDescriptions.add(generateQuestionDesc(type, item));
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return questionDescriptions;
	}

	public String update(String test) {

		String text = null;
		List<QuestionDescription> questionDescriptions = new ArrayList<>();
		Result result = new Result();
		for (int i = 1; i <= 6; i++) {
			for (int j = 1; j <= 10; j++) {
				String path = "D:\\Tax\\eLearning-Backbone\\Tool\\ElearningProject2\\test-" + i + "-" + j;
				try {
					text = new String(Files.readAllBytes(Paths.get(path + ".html")), StandardCharsets.UTF_8);
					Document doc = Jsoup.parse(text);
					Elements items = doc.getElementsByClass("item");
					QuestionDescription questionDescription = null;
					for (Element item : items) {
						String type = item.attr("type");
						result = new Result();
						result.setHsk(i);
						result.setTest(j);
						if (type.equalsIgnoreCase("1") || type.equalsIgnoreCase("3") || type.equalsIgnoreCase("4")) {

							String answer = item.getElementsByClass("field-footer").get(0)
									.getElementsByClass("field-answer").get(0).getElementsByTag("span").get(0).text();

							String number = item.getElementsByClass("exr-progress").get(0).text().split("/")[0];

							result.setNumber(Integer.parseInt(number));
							result.setAnswer(answer);
							baseDao.add(result);
						} else if (type.equalsIgnoreCase("2") || type.equalsIgnoreCase("5")
								|| type.equalsIgnoreCase("6")) {
							Element bodyElement = item.getElementsByClass("field-body").get(0);
							Elements exerciseChilds = bodyElement.getElementsByClass("exercise-child");
							for (Element exerciseChild : exerciseChilds) {
								String numberChild = exerciseChild.getElementsByClass("field-number").get(0).text();
								String answer = exerciseChild.getElementsByClass("field-footer").get(0)
										.getElementsByClass("field-answer").get(0).getElementsByTag("span").get(0)
										.text().split("/")[0];
								result.setNumber(Integer.parseInt(numberChild));
								result.setAnswer(answer);
								baseDao.add(result);
							}
						}
					}

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return util.objectToJSON(questionDescriptions);
	}

	private QuestionDescription generateQuestionDesc(String type, Element item) {
		QuestionDescription questionDescription = new QuestionDescription();
		List<QuestionBody> bodies = new ArrayList<QuestionBody>();
		if (type.equalsIgnoreCase("1") || type.equalsIgnoreCase("3") || type.equalsIgnoreCase("4")) {
			String number = item.getElementsByClass("exr-progress").get(0).text();
			String header = type.equalsIgnoreCase("4") ? "" : item.getElementsByClass("field-heading").get(0).html();

			Element bodyElement = item.getElementsByClass("field-body").get(0);
			Elements optionsElement = bodyElement.getElementsByClass("field-option");
			Map<String, String> value = new HashMap<String, String>();
			optionsElement.stream().forEach(element -> {
				Elements children = element.children();
				if (children.size() > 1) {
					value.put(children.get(0).text(), children.get(1).text());
				} else if (children.size() == 1) {
					value.put(children.get(0).text(), "");
				} else {
					value.put(element.text(), "");
				}
			});
			QuestionBody questionBody = new QuestionBody();
			questionBody.setValue(value);
			questionBody.setNumber(number.split("/")[0]);
			bodies.add(questionBody);
			questionDescription.setType(type);
			questionDescription.setNumber(number.split("/")[0]);
			questionDescription.setHeader(header);
			questionDescription.setBody(bodies);
		} else if (type.equalsIgnoreCase("2")) {
			String number = item.getElementsByClass("field-number").get(0).text();
			String header = item.getElementsByClass("field-heading").get(0).html();

			Element bodyElement = item.getElementsByClass("field-body").get(0);
			Elements exerciseChilds = bodyElement.getElementsByClass("exercise-child");
			for (Element exerciseChild : exerciseChilds) {
				QuestionBody questionBody = new QuestionBody();

				String headingChild = exerciseChild.getElementsByClass("field-heading").get(0).html();
				String numberChild = exerciseChild.getElementsByClass("field-number").get(0).text();

				Element bodyChildElement = exerciseChild.getElementsByClass("field-body").get(0);
				Elements optionsElement = bodyChildElement.getElementsByClass("field-option");
				Map<String, String> value = new HashMap<String, String>();
				optionsElement.stream().forEach(element -> {
					Elements children = element.children();
					if (children.size() > 1) {
						value.put(children.get(0).text(), children.get(1).text());
					} else if (children.size() == 1) {
						value.put(children.get(0).text(), "");
					} else {
						value.put(element.text(), "");
					}
				});
				questionBody.setValue(value);
				questionBody.setNumber(numberChild.split("/")[0]);
				questionBody.setHeader(headingChild);
				bodies.add(questionBody);
			}
			questionDescription.setType(type);
			questionDescription.setNumber(number.split("/")[0]);
			questionDescription.setHeader(header);
			questionDescription.setBody(bodies);
		} else if (type.equalsIgnoreCase("5")) {
			String number = item.getElementsByClass("field-number").get(0).text();
			String header = item.getElementsByClass("field-heading").get(0).html();
			Elements headingOptionsElement = item.getElementsByClass("field-heading").get(0)
					.getElementsByClass("field-option");
			Map<String, String> headingOptions = new HashMap<String, String>();
			headingOptionsElement.stream().forEach(element -> {
				Elements children = element.children();
				if (children.size() > 1) {
					headingOptions.put(children.get(0).text(), children.get(1).text());
				} else if (children.size() == 1) {
					headingOptions.put(children.get(0).text(), "");
				} else {
					headingOptions.put(element.text(), "");
				}
			});

			Element bodyElement = item.getElementsByClass("field-body").get(0);
			Elements exerciseChilds = bodyElement.getElementsByClass("exercise-child");
			for (Element exerciseChild : exerciseChilds) {
				QuestionBody questionBody = new QuestionBody();
				Map<String, String> value = new HashMap<String, String>();
				String headingChild = exerciseChild.getElementsByClass("field-heading").get(0).html();
				String numberChild = exerciseChild.getElementsByClass("field-number").get(0).text();
				questionBody.setValue(value);
				questionBody.setNumber(numberChild.split("/")[0]);
				questionBody.setHeader(headingChild);
				bodies.add(questionBody);
			}

			questionDescription.setType(type);
			questionDescription.setNumber(number.split("/")[0]);
			questionDescription.setHeader(header);
			questionDescription.setHeadingOptions(headingOptions);
			questionDescription.setBody(bodies);
		} else if (type.equalsIgnoreCase("6")) {
			String number = item.getElementsByClass("exr-progress").get(0).text();
			String header = item.getElementsByClass("field-heading").get(0).html();

			Element bodyElement = item.getElementsByClass("field-body").get(0);
			Elements options = bodyElement.getElementsByClass("field-option");
			Map<String, String> value = new HashMap<String, String>();
			options.stream().forEach(element -> {
				Elements children = element.children();
				if (children.size() > 1) {
					value.put(children.get(0).text(), children.get(1).text());
				} else if (children.size() == 1) {
					value.put(children.get(0).text(), "");
				} else {
					value.put(element.text(), "");
				}
			});
			QuestionBody questionBody = new QuestionBody();
			questionBody.setValue(value);
			questionBody.setNumber(number.split("/")[0]);
			bodies.add(questionBody);

			questionDescription.setType(type);
			questionDescription.setNumber(number.split("/")[0]);
			questionDescription.setHeader(header);
			questionDescription.setBody(bodies);
		}
		return questionDescription;
	}

	public String generateMainHtml(String test) {
		return null;
	}

	private List<Result> getAnswerBy(int hsk, int test, QuestionType questionType) {
		Result result = new Result();
		result.setHsk(hsk);
		result.setTest(test);
		result.setType(questionType.name());
		return baseDao.findByKey(result);
	}

	public String checkResult(int hsk, int test, Map<String, String> userAnswers, QuestionType quesionType, Long userId,
			int time) {
		List<Result> results = getAnswerBy(hsk, test, quesionType);
		int countListenCorrectAnswer = 0;
		int countReadCorrectAnswer = 0;
		List<Answer> answers = new ArrayList<>();
		Answer answer = new Answer();
		List<QuestionDescription> questionDescriptions = util
				.jsonToListObject(quesionType.compareTo(QuestionType.TEST) == 0 ? getQuestionForTest(hsk + "-" + test)
						: getQuestionForQuiz(hsk + "-" + test), QUESTION_DESCRIPTION_LIST_TYPE);
		Map<Category, List<String>> categoryMap = new HashMap<>();
		categoryMap.put(Category.NGHE, parseCategoryMap(questionDescriptions, Category.NGHE));
		categoryMap.put(Category.DOC_HIEU, parseCategoryMap(questionDescriptions, Category.DOC_HIEU));
		for (Result result : results) {
			String correctAnswer = result.getAnswer().trim();
			String userAnswer = userAnswers.get(String.valueOf(result.getNumber()));
			answer = new Answer();
			answer.setCorrectAnswer(correctAnswer);
			answer.setNumber(result.getNumber());
			if (userAnswer != null && userAnswer.replace(",", "-").trim().equals(correctAnswer)) {
				answer.setUserAnswer(userAnswer);
				if (isEligibleQuestionBy(String.valueOf(result.getNumber()), categoryMap, Category.NGHE)) {
					countListenCorrectAnswer++;
				} else if (isEligibleQuestionBy(String.valueOf(result.getNumber()), categoryMap, Category.DOC_HIEU)) {
					countReadCorrectAnswer++;
				}
			} else {
				if (userAnswer != null) {
					answer.setUserAnswer(userAnswer);
				} else {
					answer.setUserAnswer("");
				}
			}
			answers.add(answer);
		}
		ResultDetail resultDetail = new ResultDetail();
		resultDetail.setHsk(hsk);
		resultDetail.setTest(test);
		double rate =Double.valueOf(countListenCorrectAnswer + countReadCorrectAnswer) / Double.valueOf(results.size()); 
		resultDetail.setRate(Double.isNaN(rate) ? 0L : rate);
		resultDetail.setAnswers(answers);
		resultDetail
				.setListenRate(countListenCorrectAnswer + "/" + countNumberOfQuestionBy(categoryMap, Category.NGHE));
		resultDetail
				.setReadRate(countReadCorrectAnswer + "/" + countNumberOfQuestionBy(categoryMap, Category.DOC_HIEU));
		int listenScore = (int)Math.round(Double.valueOf(Double.valueOf(countListenCorrectAnswer)/ Double.valueOf(countNumberOfQuestionBy(categoryMap, Category.NGHE))*100));
		int readingScore = (int)Math.round(Double.valueOf(Double.valueOf(countReadCorrectAnswer)/ Double.valueOf(countNumberOfQuestionBy(categoryMap, Category.DOC_HIEU))*100));
		resultDetail.setScore(listenScore + readingScore);
		String path = questionPath + "/test-" + hsk + "-" + test;

		QuizResultResponse response = new QuizResultResponse();
		response.setQuestionDescription(questionDescriptions);
		response.setResultDetail(resultDetail);
		saveResultAndRanking(resultDetail, userId, quesionType, readingScore, listenScore, time);
		return util.objectToJSON(response);
	}

	private void saveResultAndRanking(ResultDetail resultDetail, Long userId, QuestionType quesionType,
			Integer correctReading, Integer correctListening, int time) {
		PromoteSetting promoteSetting = new PromoteSetting();
		promoteSetting.setHsk(resultDetail.getHsk());
		promoteSetting = (PromoteSetting) baseDao.findByKey(promoteSetting).get(0);

		UserResult userResult = new UserResult();
		userResult.setUserId(userId);
		userResult.setHsk(resultDetail.getHsk());
		userResult.setTestLesson(resultDetail.getTest());
		userResult.setResultType(quesionType.name());
		List<UserResult> userResults = baseDao.findByKeySortBy(userResult, " id desc ");
		userResult.setTotalScore( resultDetail.getScore().longValue() * promoteSetting.getFactor());
		userResult.setTotalTime(new Long(time));
		userResult.setTotalListenScore( correctListening.longValue() * promoteSetting.getFactor());
		userResult.setTotalListenTime(new Long(0));
		userResult.setTotalReadingScore( correctReading.longValue() * promoteSetting.getFactor());
		userResult.setTotalReadingTime(new Long(0));
		userResult.setResultDetail(util.objectToJSON(resultDetail));
		if (userResults.isEmpty()) {
			userResult.setWordAmount(0);
			userResult.setWordDetail("[]");
			baseDao.add(userResult);
		} else {
			userResult.setWordAmount(userResults.get(0).getWordAmount());
			userResult.setWordDetail(userResults.get(0).getWordDetail());
			baseDao.add(userResult);
			/*baseDao.updateByInputKey(userResult, Arrays.asList("userid", "hsk", "testlesson", "resulttype"));*/
		}

		UserRanking userRanking = new UserRanking();
		userRanking.setUserId(userId);
		List<UserRanking> userRankings = baseDao.findByKey(userRanking);
		if (userRankings.isEmpty()) {
			userRanking.setTotalScore(Long.valueOf(userRankingDao.getTotalScoreBy(userId)));
			userRanking.setTitleId(1);
			baseDao.add(userRanking);
		} else {
			userRanking = userRankings.get(0);
			userRanking.setTotalScore(Long.valueOf(userRankingDao.getTotalScoreBy(userId)));
			userRanking.setTitleId(userRankingDao.getCurrentRankBy(userId));
			baseDao.updateByInputKey(userRanking, Arrays.asList("userid"));
		}
	}

	private int countNumberOfQuestionBy(Map<Category, List<String>> categoryMap, Category cat) {
		return categoryMap.get(cat).stream().map(question -> {
			if (question.contains("-")) {
				return Integer.parseInt(question.split("-")[1]) - Integer.parseInt(question.split("-")[0]) + 1;
			} else {
				return 1;
			}
		}).collect(Collectors.summingInt(Integer::intValue));
	}

	private boolean isEligibleQuestionBy(String number, Map<Category, List<String>> categoryMap, Category cat) {
		for (String question : categoryMap.get(cat)) {
			if (question.contains("-")) {
				if (Integer.parseInt(question.split("-")[0].trim()) <= Integer.parseInt(number.trim())
						&& Integer.parseInt(number) <= Integer.parseInt(question.split("-")[1])) {
					return true;
				}
			} else {
				if (number.trim().equals(question.trim())) {
					return true;
				}
			}
		}
		return false;
	}

	private List<String> parseCategoryMap(List<QuestionDescription> questionDescriptions, Category cat) {
		return questionDescriptions.stream().filter(question -> question.getCategory().equals(cat)).map(question -> {
			if (question.getCategory().equals(cat)) {
				return question.getNumber().replace(" ", "").replace(".", "");
			}
			return "";
		}).collect(Collectors.toList());

	}

	public boolean testUploadQuiz2(InputStream inputStream, String hsk) throws CloneNotSupportedException {
		XSSFWorkbook workbook = null;
		boolean inserted = false;
		String quizProgress = "";
		int type = 0;
		try {
			workbook = new XSSFWorkbook(inputStream);
			Result result = new Result();
			result.setHsk(Integer.parseInt(hsk));
			result.setType(QuestionType.QUIZ.name());
			baseDao.deleteByGivenValue(result);
			for (int index = 1; index < workbook.getNumberOfSheets(); index++) {
				XSSFSheet sheet = workbook.getSheetAt(index);
				Set<MergedRow> mergedRows = new HashSet<>();
				sheet.getMergedRegions();
				for (CellRangeAddress cell : sheet.getMergedRegions()) {
					if (cell.getFirstColumn() != 0 && cell.getLastRow() != 0) {
						MergedRow row = new MergedRow();
						row.setFirstRow(cell.getFirstRow() + 1);
						row.setLastRow(cell.getLastRow() + 1);
						row.setFirstCol(cell.getFirstColumn() + 1);
						row.setLastCol(cell.getLastColumn() + 1);
						mergedRows.add(row);
					}

				}
				List<MergedRow> sortedList = mergedRows
						.stream().sorted(Comparator.comparingInt(MergedRow::getFirstRow)
								.thenComparingInt(MergedRow::getLastRow).thenComparingInt(MergedRow::getFirstCol))
						.collect(Collectors.toList());
				List<MergedRow> quizByMergedRow = sortedList.stream().filter(mergedRow -> mergedRow.firstCol == 4)
						.collect(Collectors.toList());
				Map<MergedRow, List<MergedRow>> childQuizMapFromMergedRow = new HashMap<QuizService.MergedRow, List<MergedRow>>();
				for (MergedRow merge : quizByMergedRow) {
					childQuizMapFromMergedRow.put(merge,
							sortedList.stream()
									.filter(mergedRow -> mergedRow.getFirstRow() >= merge.getFirstRow()
											&& mergedRow.getLastRow() <= merge.getLastRow() && !mergedRow.equals(merge))
									.collect(Collectors.toList()));
				}
				// let's sort this map by keys first
				childQuizMapFromMergedRow = childQuizMapFromMergedRow.entrySet().stream()
						.sorted(Map.Entry.comparingByKey(Comparator.comparingInt(MergedRow::getFirstRow)))
						.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2,
								LinkedHashMap::new));
				List<QuestionDescription> questionDescriptions = new ArrayList<>();
				List<Result> results = new ArrayList<Result>();
				for (MergedRow key : childQuizMapFromMergedRow.keySet()) {
					System.out.println("key: " + key);
					System.out.println(childQuizMapFromMergedRow.get(key));
					System.out.println(sheet.getSheetName().trim());
					questionDescriptions.add(getQuiz(key, childQuizMapFromMergedRow.get(key), sheet, hsk, results));
					String number = sheet.getRow(key.getFirstRow() - 1).getCell(5).toString();
					if (!StringUtils.isEmpty(number) && !StringUtils.isAlphanumericSpace(number)) {
					}
				}
				baseDao.addList(results);
				questionDescriptions = questionDescriptions.stream().filter(desc -> desc != null)
						.collect(Collectors.toList());
				util.writeToFile(util.objectToJSON(questionDescriptions),
						lessonQuizPath + "/hsk-" + hsk + "-" + sheet.getSheetName().trim() + ".json");
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getStackTrace());
			return false;
		}
		return true;
	}

	private QuestionDescription getQuiz(MergedRow mergedRow, List<MergedRow> childQuizInMergedRow, XSSFSheet sheet,
			String hsk, List<Result> results) {
		QuestionDescription questionDescription = new QuestionDescription();
		List<QuestionBody> bodies = new ArrayList<QuestionBody>();
		String type = sheet.getRow(mergedRow.getFirstRow() - 1).getCell(mergedRow.getFirstCol() - 2).getRawValue();
		Pattern progressPattern = Pattern.compile("^(\\(?\\+?[0-9]*\\)?)-[0-9_\\- \\(\\)]*$");// 5-8
		Pattern nummberPattern = Pattern.compile("^(\\(?\\+?[0-9]*\\)?).[0-9_\\- \\(\\)]*$");// 5
		String progress = sheet.getRow(mergedRow.getFirstRow() - 1).getCell(mergedRow.getFirstCol() - 1).toString()
				.replace(" ", "").replace(".", "");
		if (progressPattern.matcher(progress).matches() || nummberPattern.matcher(progress).matches()) {
			if (type.equalsIgnoreCase("1")) {
				QuestionBody questionBody = new QuestionBody();
				questionBody.setNumber(sheet.getRow(mergedRow.getFirstRow() - 1).getCell(5).getRawValue());
				Map<String, String> values = new ImmutableMap.Builder<String, String>()
						.put("A", "<div><i class=\"fa fa-check\"></i></div>")
						.put("B", "<div><i class=\"fa fa-times\"></i></div>")
						.build();
				String questionType = sheet.getRow(mergedRow.getFirstRow() - 1).getCell(0).getStringCellValue();
				String header = TYPE_1_TEMPLATE;
//				if (questionType.equals("NGHE")) {
					String value = sheet.getRow(mergedRow.getFirstRow() - 1).getCell(4).toString();
					String imageHtml = generateImageHtmlBy(value, hsk,
							Integer.parseInt(sheet.getSheetName().trim()), "");
					if (questionType.equals("NGHE")) {
						header = header.replace(QUESTION_DESCRIPTION_TEMP,"");
						questionDescription.setListenContent(value);
					} else {
						header = header.replace(QUESTION_DESCRIPTION_TEMP,value.equals(imageHtml)?value:"");
					}
					header = header.replace(IMAGE_SOURCE_TEMP,value.equals(imageHtml)?"":imageHtml);
					
					String imageTemp = "<div class=\"field-image\"> " + " <img style=\"max-width: 100%;\" src=\"!image_source!\" alt=\"\"> "
							+ "</div> ";
					value = sheet.getRow(mergedRow.getFirstRow() - 1).getCell(6).toString();
					imageHtml = generateImageHtmlBy(value, hsk,
							Integer.parseInt(sheet.getSheetName().trim()), imageTemp);
					questionBody.setHeader(value.equals(imageHtml)?value:imageHtml);
//				} else if (questionType.equals("DOC")) {
//					header = header.replace(QUESTION_DESCRIPTION_TEMP,
//							"");
//					header = header.replace(IMAGE_SOURCE_TEMP,
//							generateImageHtmlBy(sheet.getRow(mergedRow.getFirstRow() - 1).getCell(4).toString(), hsk,
//									Integer.parseInt(sheet.getSheetName().trim()), ""));
//					questionBody.setHeader(sheet.getRow(mergedRow.getFirstRow() - 1).getCell(6).getStringCellValue() != null
//							? sheet.getRow(mergedRow.getFirstRow() - 1).getCell(6).getStringCellValue()
//							: "");
//				}
				
				
				header = header.replace(AUDIO_TEMP,
						generateAudioHtmlBy(sheet.getRow(mergedRow.getFirstRow() - 1).getCell(10), hsk,
								Integer.parseInt(sheet.getSheetName().trim())));
				questionBody.setValue(values);
				bodies.add(questionBody);
				questionDescription.setType(type);
				questionDescription.setNumber(sheet.getRow(mergedRow.getFirstRow() - 1).getCell(5).getRawValue());
				questionDescription.setHeader(header);
				questionDescription.setBody(bodies);
				if (questionDescription.getNumber() != null && StringUtils.isNumeric(questionDescription.getNumber())) {
					Result newResult = new Result();
					newResult.setHsk(Integer.parseInt(hsk));
					newResult.setType(QuestionType.QUIZ.name());
					newResult.setTest(Integer.parseInt(sheet.getSheetName().trim()));
					newResult.setNumber(Integer.parseInt(questionDescription.getNumber()));
					newResult.setAnswer(sheet.getRow(mergedRow.getFirstRow() - 1).getCell(9).toString());
					results.add(newResult);
				}
			} else if (type.equalsIgnoreCase("2")) {
				Map<Integer, List<MergedRow>> splitRowsByChildQuestion = childQuizInMergedRow.stream()
						.collect(Collectors.groupingBy(MergedRow::getFirstRow)).entrySet().stream()
						.sorted(Map.Entry.comparingByKey()).collect(Collectors.toMap(Map.Entry::getKey,
								Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
				String header = "";
				for (Integer startRow : splitRowsByChildQuestion.keySet()) {
					String questionType = sheet.getRow(startRow - 1).getCell(0).getStringCellValue();
					List<MergedRow> chilQuiz = splitRowsByChildQuestion.get(startRow);
					Map<String, String> values = new HashMap<String, String>();
					QuestionBody questionBody = new QuestionBody();
					questionBody.setNumber(sheet.getRow(startRow - 1).getCell(5).getRawValue());
					questionBody.setHeader(sheet.getRow(startRow - 1).getCell(6).toString()
							+ generateAudioHtmlBy(sheet.getRow(startRow - 1).getCell(10), hsk,
									Integer.parseInt(sheet.getSheetName().trim())));
					if (questionType.equals("NGHE")) {
						questionBody.setListenContent(sheet.getRow(startRow - 1).getCell(4).getStringCellValue());
					}
					for (int index = startRow - 1; index <= chilQuiz.get(0).getLastRow() - 1; index++) {
						values.put(sheet.getRow(index).getCell(7).toString(),
								sheet.getRow(index).getCell(8).toString());
					}
					questionBody.setValue(values);
					bodies.add(questionBody);
					if (questionBody.getNumber() != null && StringUtils.isNumeric(questionBody.getNumber())) {
						Result newResult = new Result();
						newResult.setHsk(Integer.parseInt(hsk));
						newResult.setType(QuestionType.QUIZ.name());
						newResult.setTest(Integer.parseInt(sheet.getSheetName().trim()));
						newResult.setNumber(Integer.parseInt(questionBody.getNumber()));
						newResult.setAnswer(sheet.getRow(startRow - 1).getCell(9).toString());
						results.add(newResult);
					}
				}

				questionDescription.setType(type);
				questionDescription.setNumber(sheet.getRow(mergedRow.getFirstRow() - 1).getCell(3).toString());
				String questionType = sheet.getRow(mergedRow.getFirstRow() - 1).getCell(0).getStringCellValue();
				if (questionType.equals("DOC")) {
					questionDescription.setHeader(sheet.getRow(mergedRow.getFirstRow() - 1).getCell(4).toString());
				}
				questionDescription.setBody(bodies);

			} else if (type.equalsIgnoreCase("3")) {
				QuestionBody questionBody = new QuestionBody();
				questionBody.setNumber(sheet.getRow(mergedRow.getFirstRow() - 1).getCell(5).getRawValue());
				Map<String, String> values = new HashMap<String, String>();
				for (int index = mergedRow.getFirstRow() - 1; index <= mergedRow.getLastRow() - 1; index++) {
					values.put(sheet.getRow(index).getCell(7).toString(), sheet.getRow(index).getCell(8).toString());
				}
				String header = TYPE_1_TEMPLATE;
				String questionType = sheet.getRow(mergedRow.getFirstRow() - 1).getCell(0).getStringCellValue();
				if (questionType.equals("NGHE")) {
					header = header
							.replace(QUESTION_DESCRIPTION_TEMP,
									sheet.getRow(mergedRow.getFirstRow() - 1).getCell(6).getStringCellValue() != null
											? sheet.getRow(mergedRow.getFirstRow() - 1).getCell(6).getStringCellValue()
											: "")
							.replace(AUDIO_TEMP, generateAudioHtmlBy(sheet.getRow(mergedRow.getFirstRow() - 1).getCell(10),
									hsk, Integer.parseInt(sheet.getSheetName().trim())));
					questionDescription.setListenContent(sheet.getRow(mergedRow.getFirstRow() - 1).getCell(4).getStringCellValue() != null
											? sheet.getRow(mergedRow.getFirstRow() - 1).getCell(4).getStringCellValue()
											: "");
				} else if (questionType.equals("DOC")) {
					header = header
							.replace(QUESTION_DESCRIPTION_TEMP,
									sheet.getRow(mergedRow.getFirstRow() - 1).getCell(4).getStringCellValue() != null
											? sheet.getRow(mergedRow.getFirstRow() - 1).getCell(4).getStringCellValue()
											: "")
							.replace(AUDIO_TEMP, generateAudioHtmlBy(sheet.getRow(mergedRow.getFirstRow() - 1).getCell(10),
									hsk, Integer.parseInt(sheet.getSheetName().trim())));
					questionBody.setHeader(sheet.getRow(mergedRow.getFirstRow() - 1).getCell(6).getStringCellValue() != null
											? sheet.getRow(mergedRow.getFirstRow() - 1).getCell(6).getStringCellValue()
											: "");
				}
				if (header.contains(IMAGE_SOURCE_TEMP)) {
					Document doc = Jsoup.parse(header);
					Elements eleImages = doc.select("img");
					for(Element eleImage : eleImages) {
						if(eleImage.attr("src").equals(IMAGE_SOURCE_TEMP)) {
							eleImage.parent().remove();
						}
					}
					header = doc.body().html();
				}
				questionBody.setValue(values);
				bodies.add(questionBody);
				questionDescription.setType(type);
				questionDescription.setNumber(sheet.getRow(mergedRow.getFirstRow() - 1).getCell(5).getRawValue());
				questionDescription.setHeader(header);
				questionDescription.setBody(bodies);
				if (questionDescription.getNumber() != null && StringUtils.isNumeric(questionDescription.getNumber())) {
					Result newResult = new Result();
					newResult.setHsk(Integer.parseInt(hsk));
					newResult.setType(QuestionType.QUIZ.name());
					newResult.setTest(Integer.parseInt(sheet.getSheetName().trim()));
					newResult.setNumber(Integer.parseInt(questionDescription.getNumber()));
					newResult.setAnswer(sheet.getRow(mergedRow.getFirstRow() - 1).getCell(9).toString());
					results.add(newResult);
				}
			} else if (type.equalsIgnoreCase("4")) {
				QuestionBody questionBody = new QuestionBody();
				questionBody.setNumber(sheet.getRow(mergedRow.getFirstRow() - 1).getCell(5).getRawValue());
				Map<String, String> values = new HashMap<String, String>();
				for (int index = mergedRow.getFirstRow() - 1; index <= mergedRow.getLastRow() - 1; index++) {
					values.put(sheet.getRow(index).getCell(7).toString(), sheet.getRow(index).getCell(8).toString());
				}
				String header = "";
				questionBody.setValue(values);

				bodies.add(questionBody);
				questionDescription.setType(type);
				questionDescription.setNumber(sheet.getRow(mergedRow.getFirstRow() - 1).getCell(5).getRawValue());
				questionDescription.setHeader(header);
				questionDescription.setBody(bodies);
				if (questionDescription.getNumber() != null && StringUtils.isNumeric(questionDescription.getNumber())) {
					Result newResult = new Result();
					newResult.setHsk(Integer.parseInt(hsk));
					newResult.setType(QuestionType.QUIZ.name());
					newResult.setTest(Integer.parseInt(sheet.getSheetName().trim()));
					newResult.setNumber(Integer.parseInt(questionDescription.getNumber()));
					char[] answer = sheet.getRow(mergedRow.getFirstRow() - 1).getCell(9).toString().trim()
							.toCharArray();
					newResult.setAnswer(answer[0] + "-" + answer[1] + "-" + answer[2]);
					results.add(newResult);
				}
			} else if (type.equalsIgnoreCase("5")) {
				Map<String, String> values = new HashMap<String, String>();
				String header = "";
				String[] answerNames = { "A", "B", "C", "D", "E", "F", "G", "H", "I" };
				int countAnswer = 0;
				for (int index = mergedRow.getFirstRow() - 1; index <= mergedRow.getLastRow() - 1; index++) {
					QuestionBody questionBody = new QuestionBody();
					questionBody.setNumber(sheet.getRow(index).getCell(5).getRawValue());
					String subjectTemplate = "<div class=\"field-subject\"><p>!question_description!</p></div><div class=\"field-select\"></div>";
					bodies.add(questionBody);
					String questionType = sheet.getRow(mergedRow.getFirstRow() - 1).getCell(0).getStringCellValue();
					
					String key = sheet.getRow(index).getCell(7).toString();
					if (!StringUtils.isEmpty(key)) {
						
						String imageTemp = "<div class=\"field-image\"> " + " <img style=\"max-width: 100%;\" src=\"!image_source!\" alt=\"\"> "
								+ "</div> ";
						String imageHtml = generateImageHtmlBy(sheet.getRow(index).getCell(8).toString(), hsk,
								Integer.parseInt(sheet.getSheetName().trim()), imageTemp);
						String audiotemplate = generateAudioHtmlBy(sheet.getRow(index).getCell(10), hsk,
								Integer.parseInt(sheet.getSheetName().trim()));
						if (questionType.equals("NGHE")) {
							questionBody.setHeader(audiotemplate);
							values.put(answerNames[countAnswer], imageHtml);
							questionBody.setListenContent(sheet.getRow(index).getCell(4).getStringCellValue());
						} else {
							questionBody.setHeader(subjectTemplate.replace("!question_description!",
									sheet.getRow(index).getCell(4).toString()));
							values.put(answerNames[countAnswer], imageHtml + audiotemplate);
						}
						
						
						countAnswer++;
					} else {
						values.put(sheet.getRow(index).getCell(7).toString(),
								sheet.getRow(index).getCell(8).toString());
					}

					String headerTemp = FIELD_OPTION_TEMPMLATE;
					header += headerTemp.replace(ANSWER_CHAR_TEMP, sheet.getRow(index).getCell(7).toString())
							.replace(ANSWER_DESCRIPTION_TEMP, sheet.getRow(index).getCell(8).toString());

					if (questionBody.getNumber() != null && StringUtils.isNumeric(questionBody.getNumber())) {
						Result newResult = new Result();
						newResult.setHsk(Integer.parseInt(hsk));
						newResult.setType(QuestionType.QUIZ.name());
						newResult.setTest(Integer.parseInt(sheet.getSheetName().trim()));
						newResult.setNumber(Integer.parseInt(questionBody.getNumber()));
						newResult.setAnswer(sheet.getRow(index).getCell(9).getStringCellValue());
						results.add(newResult);
					}
				}

				questionDescription.setBody(bodies);
				questionDescription.setHeader(header);
				questionDescription.setHeadingOptions(values);
				questionDescription.setNumber(progress);
				questionDescription.setType(type);
			} else if (type.equalsIgnoreCase("6")) {
				Map<String, String> values = new HashMap<String, String>();
				QuestionBody questionBody = new QuestionBody();
				questionBody.setNumber(progress);
				for (int index = mergedRow.getFirstRow() - 1; index <= mergedRow.getLastRow() - 1; index++) {
					values.put(sheet.getRow(index).getCell(7).toString(), sheet.getRow(index).getCell(8).toString());
					Double number = sheet.getRow(index).getCell(5).getNumericCellValue();
					if (number != null) {
						Result newResult = new Result();
						newResult.setHsk(Integer.parseInt(hsk));
						newResult.setType(QuestionType.QUIZ.name());
						newResult.setTest(Integer.parseInt(sheet.getSheetName().trim()));
						newResult.setNumber(number.intValue());
						newResult.setAnswer(sheet.getRow(index).getCell(9).toString());
						results.add(newResult);
					}
				}
				questionBody.setValue(values);
				bodies.add(questionBody);
				questionDescription.setBody(bodies);

				String header = sheet.getRow(mergedRow.getFirstRow() - 1).getCell(4).toString();
				String regex = "________";
				header = header.replace(regex, "%s");
				String element = "<span class=\"child-number\" data-child=\"31604\""
						+ " id=\"%s\" onclick=\"chooseQuestion(%s)\""
						+ " style=\"cursor: pointer;border-bottom: 1px solid #39a0ff;color:red\" selected=\"false\">%s"
						+ "<span class=\"child-select rev-hide\">" + "</span></span>";
				List<String> listQuestionsElement = new ArrayList<>();
				for (int index = Integer.parseInt(progress.split("-")[0]); index <= Integer
						.parseInt(progress.split("-")[1]); index++) {
					listQuestionsElement
							.add(String.format(element, String.valueOf(index), String.valueOf(index), "___"));
				}
				header = String.format(header, listQuestionsElement.toArray());
				questionDescription.setHeader(header);
				questionDescription.setNumber(progress);
				questionDescription.setType(type);

			} else {
				System.out.println("---------------------------------" + type);
			}
		} else {
			return null;
		}
		String cat = sheet.getRow(mergedRow.getFirstRow() - 1).getCell(0).toString();
		questionDescription.setCategory(cat.equals("NGHE") ? Category.NGHE
				: cat.equals("DOC") ? Category.DOC_HIEU : cat.equals("VIET") ? Category.VIET_VAN : Category.OTHERS);
		return questionDescription;
	}

	public boolean uploadInputContent(InputStream inputStream, String hsk) throws CloneNotSupportedException {
		XSSFWorkbook workbook = null;
		boolean inserted = false;
		String quizProgress = "";
		int type = 0;
		try {
			workbook = new XSSFWorkbook(inputStream);
			for (int index = 1; index < workbook.getNumberOfSheets(); index++) {
				XSSFSheet sheet = workbook.getSheetAt(index);
				Set<MergedRow> mergedRows = new HashSet<>();
				sheet.getMergedRegions();
				for (CellRangeAddress cell : sheet.getMergedRegions()) {
					if (cell.getFirstColumn() != 0 && cell.getLastRow() != 0) {
						MergedRow row = new MergedRow();
						row.setFirstRow(cell.getFirstRow() + 1);
						row.setLastRow(cell.getLastRow() + 1);
						row.setFirstCol(cell.getFirstColumn() + 1);
						row.setLastCol(cell.getLastColumn() + 1);
						mergedRows.add(row);
					}

				}
				List<MergedRow> sortedList = mergedRows
						.stream().sorted(Comparator.comparingInt(MergedRow::getFirstRow)
								.thenComparingInt(MergedRow::getLastRow).thenComparingInt(MergedRow::getFirstCol))
						.collect(Collectors.toList());
				List<MergedRow> quizByMergedRow = sortedList.stream().filter(mergedRow -> mergedRow.firstCol == 4)
						.collect(Collectors.toList());
				Map<MergedRow, List<MergedRow>> childQuizMapFromMergedRow = new HashMap<QuizService.MergedRow, List<MergedRow>>();
				for (MergedRow merge : quizByMergedRow) {
					childQuizMapFromMergedRow.put(merge,
							sortedList.stream()
									.filter(mergedRow -> mergedRow.getFirstRow() >= merge.getFirstRow()
											&& mergedRow.getLastRow() <= merge.getLastRow() && !mergedRow.equals(merge))
									.collect(Collectors.toList()));
				}
				// let's sort this map by keys first
				childQuizMapFromMergedRow = childQuizMapFromMergedRow.entrySet().stream()
						.sorted(Map.Entry.comparingByKey(Comparator.comparingInt(MergedRow::getFirstRow)))
						.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2,
								LinkedHashMap::new));
				List<QuestionDescriptionForInput> questionDescriptions = new ArrayList<>();
				List<Result> results = new ArrayList<Result>();
				for (MergedRow key : childQuizMapFromMergedRow.keySet()) {
					System.out.println("key: " + key);
					System.out.println(childQuizMapFromMergedRow.get(key));
					System.out.println(sheet.getSheetName().trim());
					questionDescriptions.add(getInputContentFromQuiz(key, childQuizMapFromMergedRow.get(key), sheet, hsk, results));
					String number = sheet.getRow(key.getFirstRow() - 1).getCell(5).toString();
					if (!StringUtils.isEmpty(number) && !StringUtils.isAlphanumericSpace(number)) {
					}
				}
				questionDescriptions = questionDescriptions.stream().filter(desc -> desc != null)
						.collect(Collectors.toList());
				util.writeToFile(util.objectToJSON(questionDescriptions),
						lessonQuizInputPath + "/hsk-" + hsk + "-" + sheet.getSheetName().trim() + ".json");
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getStackTrace());
			return false;
		}
		return true;
	}

	private QuestionDescriptionForInput getInputContentFromQuiz(MergedRow mergedRow, List<MergedRow> childQuizInMergedRow, XSSFSheet sheet,
			String hsk, List<Result> results) {
		QuestionDescriptionForInput questionDescription = new QuestionDescriptionForInput();
		List<QuestionBody> bodies = new ArrayList<QuestionBody>();
		String type = sheet.getRow(mergedRow.getFirstRow() - 1).getCell(mergedRow.getFirstCol() - 2).getRawValue();
		Pattern progressPattern = Pattern.compile("^(\\(?\\+?[0-9]*\\)?)-[0-9_\\- \\(\\)]*$");// 5-8
		Pattern nummberPattern = Pattern.compile("^(\\(?\\+?[0-9]*\\)?).[0-9_\\- \\(\\)]*$");// 5
		String progress = sheet.getRow(mergedRow.getFirstRow() - 1).getCell(mergedRow.getFirstCol() - 1).toString()
				.replace(" ", "").replace(".", "");
		List<String> fileNames = util.getAllFilesNameInFolder(publicImagePath + "BT/" + hsk);
		fileNames = fileNames.stream().map(file -> file.substring(0, file.lastIndexOf("."))).collect(Collectors.toList());
		List<String> contents = new LinkedList<String>();
		if (progressPattern.matcher(progress).matches() || nummberPattern.matcher(progress).matches()) {
			Integer idCount = 0;
			String number = sheet.getRow(mergedRow.getFirstRow() - 1).getCell(5).getRawValue();
			questionDescription.setType(type);
			questionDescription.setNumber(sheet.getRow(mergedRow.getFirstRow() - 1).getCell(5).getRawValue());
			for (int index = mergedRow.getFirstRow() - 1; index <= mergedRow.getLastRow() - 1; index++) {
				String value = "";
				if (sheet.getRow(index).getCell(4) != null) {
					value = sheet.getRow(index).getCell(4).toString();
					if (!isImageName(value, hsk, fileNames) && !value.trim().equals("DUNG") && !value.equals("SAI")) {
						contents.add(generateTypingHtml(value, number, idCount));
						idCount+=value.split("[\n。]").length;
					}
				}
				if (sheet.getRow(index).getCell(6) != null) {
					value = sheet.getRow(index).getCell(6).toString();
					if (!isImageName(value, hsk, fileNames) && !value.trim().equals("DUNG") && !value.equals("SAI")) {
						contents.add(generateTypingHtml(value, number, idCount));
						idCount+=value.split("[\n。]").length;
					}
				}
				if (sheet.getRow(index).getCell(8) != null) {
					value = sheet.getRow(index).getCell(8).toString();
					if (!isImageName(value, hsk, fileNames) && !value.trim().equals("DUNG") && !value.equals("SAI")) {
						contents.add(generateTypingHtml(value, number, idCount));
						idCount+=value.split("[\n。]").length;
					}
				}
			}
		} else {
			return null;
		}
		String cat = sheet.getRow(mergedRow.getFirstRow() - 1).getCell(0).toString();
		questionDescription.setContents(contents);
		questionDescription.setCategory(cat.equals("NGHE") ? Category.NGHE
				: cat.equals("DOC") ? Category.DOC_HIEU : cat.equals("VIET") ? Category.VIET_VAN : Category.OTHERS);
		return questionDescription;
	}

	private String generateTypingHtml(String content, String number, Integer idCount) {
		if (content.isEmpty()) {
			return content;
		}
		String template = "<div><div class=\"quote\" id=\"content\">%s</div>"
				+ "<textarea id=\"input%s-%d\" class=\"input_area form-control hantu-only\" onPaste=\"return true\" placeholder=\"start typing here...\""
				+ " oninput=\"Window.practiceTypingComponent.processCurrentText(event.target)\"></textarea>"
				+ "</div>";
		String result = "";
		String[] contentSplit = content.split("\n");
		for(String read: contentSplit) {
			for(String readSplitDot : read.split("。")) {
				result += String.format(template, readSplitDot, number, idCount++);
			}
			
		}
		return result;
	}
	private boolean isImageName(String content, String hsk, List<String> fileNames) {
		String result = content;
		if (fileNames.contains(result)) {
			return true;
		}
		return false;
	}
	private String generateAudioHtmlBy(XSSFCell cellAudioName, String hsk, int lesson) {
		
		String audioTemp = "<audio controls=\"\" controlsList=\"nodownload\"> \r\n" + 
				"  <source src=\"!audio_source!\" type=\"audio/mpeg\"> Your browser does not support the audio element. \r\n" + 
				"</audio>\r\n" + 
				"<div>\r\n" + 
				"   <button class=\"btn\" ><img src=\"assets/images/numbers/undo.svg\" onclick=\"previous(this)\" alt=\"\"  width=\"30px\" height=\"30px\"></button>\r\n" + 
				"   <button class=\"btn mr-4\" ><img src=\"assets/images/numbers/redo.svg\" onclick=\"next(this)\" alt=\"\" width=\"30px\" height=\"30px\"></button>\r\n" + 
				"</div>";
		return cellAudioName != null && StringUtils.trimToNull(cellAudioName.toString()) != null
				? audioTemp.replace("!audio_source!",
						audioExercisePath.replace("{hsk}", hsk)
								+ util.getCompleteNameInFolder(documentRootPath + "public/audios/BT/" + hsk,
										cellAudioName.toString() + ".mp3", String.format("%02d", lesson)))
				: "";
	}

	private String generateImageHtmlBy(String imageName, String hsk, int lesson, String template) {
		String result = imageName;
		try {
			List<String> fileNames = util.getAllFilesNameInFolder(publicImagePath + "BT/" + hsk);
			for (String fileName : fileNames) {
				if (fileName.substring(0, fileName.lastIndexOf(".")).trim().equalsIgnoreCase(imageName.trim())) {
					if (template.trim().equals("")) {
						result = imageExercisePath.replace("{hsk}", hsk) + fileName;
					} else {
						result = template.replace(IMAGE_SOURCE_TEMP, imageExercisePath.replace("{hsk}", hsk) + fileName);
					}
					break;
				}
			}
		} catch (Exception e) {
			return result;
		}
		
		if (result.contains(IMAGE_SOURCE_TEMP)) {
			Document doc = Jsoup.parse(result);
			Elements eleImages = doc.select("img");
			for(Element eleImage : eleImages) {
				if(eleImage.attr("src").equals(IMAGE_SOURCE_TEMP)) {
					eleImage.parent().remove();
				}
			}
		}
		return result;
	}

	public boolean testUploadQuiz(InputStream inputStream) throws CloneNotSupportedException {
		XSSFWorkbook workbook = null;
		boolean inserted = false;
		String quizProgress = "";
		int type = 0;
		try {
			workbook = new XSSFWorkbook(inputStream);

			for (int index = 0; index < workbook.getNumberOfSheets(); index++) {
				XSSFSheet sheet = workbook.getSheetAt(index);
				Iterator<Row> rowIterator = sheet.iterator();
				List<QuestionDescription> questionDescriptions = new ArrayList<>();
				QuestionDescription questionDescription = new QuestionDescription();
				QuestionBody questionBody = new QuestionBody();
				List<QuestionBody> questionBodies = new ArrayList<QuestionBody>();
				while (rowIterator.hasNext()) {
					Row row = rowIterator.next();
					System.out.println("-----------------------------------------");
					Iterator<Cell> cellIterator = row.cellIterator();
					int cellIndex = 0;
					String answerKey = "";
					outer: while (cellIterator.hasNext()) {
						Cell cell = cellIterator.next();
						cellIndex++;
						for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
							CellRangeAddress region = sheet.getMergedRegion(i);

							int colIndex = region.getFirstColumn();
							int rowNum = region.getFirstRow();
							if (rowNum == cell.getRowIndex() && colIndex == cell.getColumnIndex()) {
								String cellValue = sheet.getRow(rowNum).getCell(colIndex).toString();
								System.out.println("merged cell-" + cellIndex + ": "
										+ sheet.getRow(rowNum).getCell(colIndex).toString());
								if (cellIndex == 3) {

									type = Integer.parseInt(cellValue.split("\\.")[0]);
								} else if (cellIndex == 4) {
									if (!StringUtils.isEmpty(quizProgress)) {
										QuestionBody tempQuestionBody = (QuestionBody) questionBody.clone();
										questionBodies.add(tempQuestionBody);
										questionBody = new QuestionBody();
										questionDescription.setBody(questionBodies);
										QuestionDescription tempQuestionDescription = (QuestionDescription) questionDescription
												.clone();
										questionDescriptions.add(tempQuestionDescription);
									}

									questionDescription = new QuestionDescription();
									questionBodies = new ArrayList<QuestionBody>();
									questionDescription.setType(String.valueOf(type));
									quizProgress = sheet.getRow(rowNum).getCell(colIndex).toString();
									questionDescription.setNumber(quizProgress);
								} else if (cellIndex == 5) {

									questionDescription.setHeader(cellValue);

								} else if (cellIndex == 6) {
									if (type == 2) {
										if (questionBody.getNumber() != null) {
											QuestionBody tempQuestionBody = (QuestionBody) questionBody.clone();
											questionBodies.add(tempQuestionBody);
											questionBody = new QuestionBody();
										}
									}
									if (!cellValue.trim().equals("")) {
										questionDescription.setNumber(cellValue.split("\\.")[0]);
										questionBody.setNumber(cellValue.split("\\.")[0]);
									}
								} else if (cellIndex == 7) {

									if (type == 2) {
										questionBody.setHeader(cell.toString());
									}

								}
								continue outer;
							}
						}
						if (cell.getCellType() == Cell.CELL_TYPE_BLANK || cell == null) {
							continue;
						}
						System.out.println("cell-" + cellIndex + ": " + cell.toString());
						if (cellIndex == 8) {
							Map<String, String> tempMap = questionBody.getValue();
							tempMap.put(cell.toString(), "");
							questionBody.setValue(tempMap);
							answerKey = cell.toString();
						} else if (cellIndex == 9) {
							Map<String, String> tempMap = questionBody.getValue();
							tempMap.put(answerKey, cell.toString());
						}
					}
				}
				QuestionBody tempQuestionBody = (QuestionBody) questionBody.clone();
				questionBodies.add(tempQuestionBody);
				questionDescription.setBody(questionBodies);
				QuestionDescription tempQuestionDescription = (QuestionDescription) questionDescription.clone();
				questionDescriptions.add(tempQuestionDescription);
				util.writeToFile(util.objectToJSON(questionDescriptions),
						lessonQuizPath + "/test-" + sheet.getSheetName().trim() + ".json");
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return inserted;
	}

	private class MergedRow {
		private int firstRow;
		private int lastRow;
		private int firstCol;
		private int lastCol;

		public int getFirstRow() {
			return firstRow;
		}

		public void setFirstRow(int firstRow) {
			this.firstRow = firstRow;
		}

		public int getLastRow() {
			return lastRow;
		}

		public void setLastRow(int lastRow) {
			this.lastRow = lastRow;
		}

		public int getFirstCol() {
			return firstCol;
		}

		public void setFirstCol(int firstCol) {
			this.firstCol = firstCol;
		}

		public int getLastCol() {
			return lastCol;
		}

		public void setLastCol(int lastCol) {
			this.lastCol = lastCol;
		}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return "firstRow: " + firstRow + " - " + "lastRow: " + lastRow + " - " + "firstCol: " + firstCol + " - "
					+ "lastCol: " + lastCol;
		}

		@Override
		public boolean equals(Object obj) {
			MergedRow newMergedRow = (MergedRow) obj;
			return this.firstCol == newMergedRow.getFirstCol() && this.lastCol == newMergedRow.getLastCol()
					&& this.firstRow == newMergedRow.getFirstRow() && this.lastRow == newMergedRow.getLastRow();

		}
	}
	
	public String countTests(String hsk) {
		GetTestSummaryResponse response = new GetTestSummaryResponse();
		response.setCountTests(quizDao.countTests());
		PromoteSetting promoteSetting = new PromoteSetting();
		promoteSetting.setHsk(Integer.valueOf(hsk));
		List<PromoteSetting> results = baseDao.findByKey(promoteSetting);
		if (!results.isEmpty()) {
			response.setTotalTestByHsk(results.get(0).getTests());
		}
		
		for (int i = 1; i <= response.getCountTests(); i++) {
//			if (userRankingDao.isEligibleForPromoteTesting(userId, i)) {
//				response.setCurrentEligibleTest(i);
//			} else {
//				break;
//			}
			response.setCurrentEligibleTest(i);
		}
		return util.objectToJSON(response);
	}
	
	public String getHistoryBy(String hsk, String lesson, QuestionType quesionType, Long userId) {
		List<QuestionDescription> questionDescriptions = util
				.jsonToListObject(quesionType.compareTo(QuestionType.TEST) == 0 ? getQuestionForTest(hsk + "-" + lesson)
						: getQuestionForQuiz(hsk + "-" + lesson), QUESTION_DESCRIPTION_LIST_TYPE);
		List<UserResult> userResults = new ArrayList<>();
		UserResult userResult = new UserResult();
		userResult.setUserId(userId);
		userResult.setHsk(Integer.parseInt(hsk));
		userResult.setTestLesson(Integer.parseInt(lesson));
		userResult.setResultType(quesionType.name());
		userResults = baseDao.findByKeySortBy(userResult, " id asc ");
		List<QuizResultResponse> responses = new ArrayList<>();
		for (UserResult result : userResults) {
			QuizResultResponse response = new QuizResultResponse();
			response.setResultDetail(util.jsonToObject(result.getResultDetail(), ResultDetail.class));
			responses.add(response);
		}
		if(!userResults.isEmpty()) {
			responses.get(0).setQuestionDescription(questionDescriptions);
		}
		return util.objectToJSON(responses);
	}
	
	public void uploadExerciseFile(InputStream inputStream, String hsk) {
		try {
			util.writeToFile(new XSSFWorkbook(inputStream), uploadExercisesPath + hsk +".xlsx");
		} catch (IOException e) {
			e.printStackTrace();
		}
		util.runShellScript(batchPath, executeUploadShellScript);
	}
	
	public void uploadTest(InputStream inputStream, String hsk) {
		try {
			util.writeToFile(new XSSFWorkbook(inputStream), uploadTestPath + hsk +".xlsx");
		} catch (IOException e) {
			e.printStackTrace();
		}
		util.runShellScript(batchPath, executeTestUploadShellScript);
	}
}
