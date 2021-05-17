package com.elearning.services;

import static com.elearning.jerseyguice.constant.ElearningConstant.POPULAR_DICTIONARY_TAB;
import static com.elearning.jerseyguice.constant.ElearningConstant.STANDART_DICTIONARY_TAB;
import static com.elearning.jerseyguice.constant.QuestionType.QUIZ;
import static com.github.reap.rest.guice.BindJerseyPropertiesModule.BIND_DICTIONARY_PATH;
import static com.github.reap.rest.guice.BindJerseyPropertiesModule.BIND_EXECUTE_UPLOAD_SHELL_SCRIPT;
import static com.github.reap.rest.guice.BindJerseyPropertiesModule.BIND_UPLOAD_DICTIONARY_PATH;
import static com.github.reap.rest.guice.BindJerseyPropertiesModule.BIND_EXECUTE_DICTIONARY_UPLOAD_SHELL_SCRIPT;
import static com.github.reap.rest.guice.BindJerseyPropertiesModule.BIND_EXECUTE_SYNC_DICTIONARY_UPLOAD_SHELL_SCRIPT;
import static com.github.reap.rest.guice.BindJerseyPropertiesModule.BIND_BATCH_PATH;
import static com.github.reap.rest.guice.BindJerseyPropertiesModule.BIND_DICTIONARY_AUDIO_PATH;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.lang.Character.UnicodeBlock;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.elearning.jerseyguice.dao.BaseDao;
import com.elearning.jerseyguice.dao.DictionaryDao;
import com.elearning.jerseyguice.model.APIResponse;
import com.elearning.jerseyguice.model.Dictionary;
import com.elearning.jerseyguice.model.GetCourseDicRequest;
import com.elearning.jerseyguice.model.GetUserDictionaryResponse;
import com.elearning.jerseyguice.model.LessonDictionary;
import com.elearning.jerseyguice.model.UserDictionary;
import com.elearning.jerseyguice.model.UserResult;
import com.elearning.jerseyguice.model.DTO.StringListDTO;
import com.elearning.jerseyguice.model.DTO.UserDictionaryDTO;
import com.elearning.jerseyguice.model.DTO.UserDictionaryDatatableResponse;
import com.elearning.jerseyguice.model.Request.DatatableParamHolder;
import com.elearning.util.Util;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.opencsv.CSVReader;

import io.jsonwebtoken.lang.Strings;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class DictionaryService {
	@Inject
	@Named(BIND_DICTIONARY_PATH)
	private String dictionaryPath;

	@Inject
	@Named(BIND_DICTIONARY_AUDIO_PATH)
	private String dictionaryAudioPath;
	
	@Inject
	@Named(BIND_EXECUTE_DICTIONARY_UPLOAD_SHELL_SCRIPT)
	private String executeDictionaryUploadShellScript;
	
	@Inject
	@Named(BIND_EXECUTE_SYNC_DICTIONARY_UPLOAD_SHELL_SCRIPT)
	private String executeSyncDictionaryUploadShellScript;
	
	@Inject
	@Named(BIND_UPLOAD_DICTIONARY_PATH)
	private String uploadDictionaryPath;
	
	@Inject
	@Named(BIND_BATCH_PATH)
	private String batchPath;
	
	@Inject
	Util util;
	
	@Inject
	BaseDao baseDao;
	
	@Inject
	DictionaryDao dictionaryDao;

	private static final Type WORRDS_TYPE = new TypeToken<ArrayList<Integer>>() {
	}.getType();

	public boolean addDataDictionary2(InputStream inputExcelStream) {
		try {
			util.writeToFile(new XSSFWorkbook(inputExcelStream), uploadDictionaryPath +"dictionary.xlsx");
		} catch (IOException e) {
			e.printStackTrace();
		}
		util.runShellScript(batchPath, executeDictionaryUploadShellScript);
		return true;
	}

	public boolean deleteAndAddFullDataDictionary(InputStream inputExcelStream) {
		XSSFWorkbook workbook = null;
		boolean inserted = false;
		Row row = null;
		try {
			workbook = new XSSFWorkbook(inputExcelStream);
			XSSFSheet sheet = workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();
			int count = 0;
			dictionaryDao.deleteAllWords();
			List<Dictionary> dics = new ArrayList<Dictionary>();
			int lastRow = sheet.getLastRowNum();
			for (int i = 1; i <= lastRow; i++) {
				row = sheet.getRow(i);
				if (row != null && row.getCell(1) != null && !row.getCell(1).getStringCellValue().trim().equals("")) {
					Dictionary dic = new Dictionary();
					dic.setHantu(
							row.getCell(1) == null ? "" : row.getCell(1).getStringCellValue().replace("\'", "\\'"));
					dic.setPinyin(
							row.getCell(2) == null ? "" : row.getCell(2).getStringCellValue().replace("\'", "\\'"));
					dic.setNghia1(
							row.getCell(3) == null ? "" : row.getCell(3).getStringCellValue().replace("\'", "\\'"));
					dic.setHanviet(row.getCell(4) == null ? ""
							: row.getCell(4).getStringCellValue().replace("...", "").replace("\'", "\\'").replace("…",
									""));

					dics.add(dic);
				}

			}
			if (!dics.isEmpty()) {
				inserted = baseDao.addList(dics);
			}
		} catch (IOException | NullPointerException e) {
			// TODO Auto-generated catch block
			System.out.println(row.getRowNum());
			e.printStackTrace();
		}

		return inserted;
	}

	public String syncWordWithLesson2(InputStream inputExcelStream) {
		APIResponse response = new APIResponse();
		try {
			util.writeToFile(new XSSFWorkbook(inputExcelStream), uploadDictionaryPath +"dictionary.xlsx");
		} catch (IOException e) {
			e.printStackTrace();
		}
		util.runShellScript(batchPath, executeSyncDictionaryUploadShellScript);

		response.setCode(200);
		response.setMessage("Upload done !!!");
		return util.objectToJSON(response);
	}

	private List<LessonDictionary> getDictionariesFrom(XSSFSheet sheet, boolean isStandart) throws Exception {
		int count = 0;
		List<LessonDictionary> dics = new ArrayList<LessonDictionary>();
		Iterator<Row> rowIterator = sheet.iterator();
		while (rowIterator.hasNext()) {

			if (count == 0) {
				Row row = rowIterator.next();
				count++;
			} else {
				Row row = rowIterator.next();
				try {
					if (row.getCell(1) != null) {
						LessonDictionary dic = new LessonDictionary();
						if (isStandart) {
							dic.setHantu(row.getCell(5).getStringCellValue().replace("\'", "\\'"));
							dic.setHsk(new Double(row.getCell(1).getNumericCellValue()).intValue());
							dic.setLesson(String.valueOf(new Double(row.getCell(2).getNumericCellValue()).intValue()));
							dic.setPart(String.valueOf(new Double(row.getCell(3).getNumericCellValue()).intValue()));
							dic.setStandart(1);
							dic.setPinyin(row.getCell(6).getStringCellValue());
							dic.setNghia1(row.getCell(7).getStringCellValue());
							dic.setOrder(new Double(row.getCell(4).getNumericCellValue()).intValue());
						} else {
							dic.setHantu(row.getCell(1).getStringCellValue().replace("\'", "\\'"));
							dic.setHsk(new Double(row.getCell(0).getNumericCellValue()).intValue());
							dic.setPopular(1);
							dic.setPinyin(row.getCell(2).getStringCellValue());
							dic.setNghia1(row.getCell(3).getStringCellValue());
						}
						dics.add(dic);
					}
				} catch (Exception e) {
					System.out.println(Stream.of(e.getStackTrace()).map(item -> item.toString())
							.collect(Collectors.joining("\n")));
					if (isStandart) {
						throw new Exception(String.format("Có lỗi trên file upload ở từ %s. Pinyin %s",
								row.getCell(5).getStringCellValue(), row.getCell(6).getStringCellValue()));
					} else {
						throw new Exception(String.format("Có lỗi trên file upload ở từ %s. Pinyin %s",
								row.getCell(1).getStringCellValue(), row.getCell(2).getStringCellValue()));
					}

				}
			}

		}
		return dics;
	}

	public List<Dictionary> getWordsWith(int page) {

		return dictionaryDao.getWordsWithPaging(page * 30);
	}

	public List<Dictionary> findWordsByKeyWord(String keyWord) {

		return dictionaryDao.findWords(keyWord);
	}

	public int countWords() {

		return dictionaryDao.countDictionary();
	}

	public String translateByWord(String word) {
		Dictionary dic = new Dictionary();
		dic.setHantu(word);
		List<Dictionary> dics = baseDao.findByKey(dic);
		return dics.isEmpty() ? "" : util.objectToJSON(dics.get(0));
	}

	public String addToMyDictionary(List<String> ids, String tab, Long user_id, String place) {
		UserDictionary userDictionary = new UserDictionary();
		userDictionary.setTab(tab);
		userDictionary.setUserId(user_id);
		userDictionary.setPlace(place);
		List<UserDictionary> userDictionaries = baseDao.findByKey(userDictionary);
		if (userDictionaries.isEmpty()) {
			userDictionary.setCreateDate(Integer.parseInt(util.getCurrentDate()));

			userDictionary.setWordId(String.valueOf(String.join(",", ids)));
			userDictionary.setPlace(place);
			baseDao.add(userDictionary);
		} else {
			List<String> currentWordIds = new ArrayList<>();
			userDictionaries.stream().forEach(userDic -> {
				List<String> words = Arrays.asList(userDic.getWordId().split(","));
				currentWordIds.addAll(words);
			});
			ids = ids.stream().filter(id -> {
				id = id.trim();
				return !currentWordIds.contains(id);
			}

			).collect(Collectors.toList());
			userDictionary = userDictionaries.get(0);
			userDictionary.setWordId(userDictionary.getWordId() + (ids.isEmpty() ? "" : ("," + String.join(",", ids))));
			userDictionary.setPlace(place);
			userDictionary.setCreateDate(Integer.parseInt(util.getCurrentDate()));
			baseDao.updateByInputKey(userDictionary, Arrays.asList("userid", "tab", "place"));
		}
		return "true";
	}

	public String addToMyDictionaryFromDictionary(List<UserDictionaryDTO> ws, String tab, Long user_id, String place) {
		UserDictionary userDictionary = new UserDictionary();

		//Group words by place
		Map<String, List<UserDictionaryDTO>> wordMaps = ws.stream().collect(Collectors.groupingBy(w -> w.getPlace()));

		for (Map.Entry<String, List<UserDictionaryDTO>> pair : wordMaps.entrySet()) {
			userDictionary = new UserDictionary();
			List<String> ids = pair.getValue().stream().map(UserDictionaryDTO::getWordIdAsString).collect(Collectors.toList());
			userDictionary.setTab(tab);
			userDictionary.setUserId(user_id);
			userDictionary.setPlace(pair.getKey().isEmpty()? place: pair.getKey());
			List<UserDictionary> userDictionaries = baseDao.findByKey(userDictionary);

			if (userDictionaries.isEmpty()) {
				userDictionary.setCreateDate(Integer.parseInt(util.getCurrentDate()));

				userDictionary.setWordId(String.valueOf(String.join(",", ids)));
				baseDao.add(userDictionary);
			} else {
				List<String> currentWordIds = new ArrayList<>();
				userDictionaries.stream().forEach(userDic -> {
					List<String> words = Arrays.asList(userDic.getWordId().split(","));
					currentWordIds.addAll(words);
				});
				ids = ids.stream().filter(id -> {
					id = id.trim();
					return !currentWordIds.contains(id);
				}

				).collect(Collectors.toList());
				userDictionary = userDictionaries.get(0);
				userDictionary.setWordId(userDictionary.getWordId() + (ids.isEmpty() ? "" : ("," + String.join(",", ids))));
				userDictionary.setCreateDate(Integer.parseInt(util.getCurrentDate()));
				baseDao.updateByInputKey(userDictionary, Arrays.asList("userid", "tab", "place"));
			}
		}
		
		return "true";
	}
	
	public String getMyDictionary(String tab, Long user_id, DatatableParamHolder param) {
		UserDictionaryDatatableResponse result = new UserDictionaryDatatableResponse();
		List<UserDictionary> userDictionaries = dictionaryDao.getUserDictionaryList(tab, user_id);
		result = dictionaryDao.getUserDictionaryResponse(userDictionaries, tab, param);
		 
		return util.objectToJSON(result);
	}
	
	public String getUserTabs(Long user_id){
		StringListDTO result = dictionaryDao.getUserTabs(user_id);
		
		return util.objectToJSON(result);
	}

	public String getUserUserDictionaryCreateDate(Long userId, String tab){
		StringListDTO result = dictionaryDao.getUserUserDictionaryCreateDate(userId, tab);
		
		return util.objectToJSON(result);
	}

	// private GetUserDictionaryResponse getStandartDic(List<String> tabs) {
	// 	tabs.add(STANDART_DICTIONARY_TAB);
	// 	GetUserDictionaryResponse userDictionaryResponse = new GetUserDictionaryResponse();
	// 	LessonDictionary lessonDictionary = new LessonDictionary();
	// 	lessonDictionary.setStandart(1);
	// 	userDictionaryResponse.setTabs(tabs);
	// 	List<LessonDictionary> lessDics = (List<LessonDictionary>) baseDao.findByKeySortBy(lessonDictionary,
	// 			" hsk asc, lesson asc, `order` asc ");
	// 	userDictionaryResponse.setDictionaries(lessDics.stream().map(lesDic -> {
	// 		Dictionary dic = new Dictionary();
	// 		dic.setHantu(lesDic.getHantu());
	// 		dic.setHanviet(lesDic.getHanviet());
	// 		dic.setHsk(lesDic.getHsk());
	// 		dic.setId(lesDic.getRefid());
	// 		dic.setLesson(lesDic.getLesson());
	// 		dic.setNghia1(lesDic.getNghia1());
	// 		dic.setPart(lesDic.getPart());
	// 		dic.setPinyin(lesDic.getPinyin());
	// 		return dic;
	// 	}).collect(Collectors.toList()));
	// 	return userDictionaryResponse;
	// }

	// private GetUserDictionaryResponse getPopularDic(List<String> tabs) {
	// 	tabs.add(POPULAR_DICTIONARY_TAB);
	// 	GetUserDictionaryResponse userDictionaryResponse = new GetUserDictionaryResponse();
	// 	LessonDictionary lessonDictionary = new LessonDictionary();
	// 	lessonDictionary.setPopular(1);
	// 	userDictionaryResponse.setTabs(tabs);
	// 	List<LessonDictionary> lessDics = (List<LessonDictionary>) baseDao.findByKeySortBy(lessonDictionary,
	// 			" hsk asc, `order` asc ");
	// 	userDictionaryResponse.setDictionaries(lessDics.stream().map(lesDic -> {
	// 		Dictionary dic = new Dictionary();
	// 		dic.setHantu(lesDic.getHantu());
	// 		dic.setHanviet(lesDic.getHanviet());
	// 		dic.setHsk(lesDic.getHsk());
	// 		dic.setId(lesDic.getRefid());
	// 		dic.setLesson(lesDic.getLesson());
	// 		dic.setNghia1(lesDic.getNghia1());
	// 		dic.setPart(lesDic.getPart());
	// 		dic.setPinyin(lesDic.getPinyin());
	// 		return dic;
	// 	}).collect(Collectors.toList()));
	// 	return userDictionaryResponse;
	// }

	public String getCourseDictionary(GetCourseDicRequest request) {
		LessonDictionary lessonDictionary = new LessonDictionary();
		lessonDictionary.setHsk(request.getHsk());
		lessonDictionary.setLesson(request.getLesson());
		lessonDictionary.setPart(request.getPart());
		lessonDictionary.setStandart(1);
		List<LessonDictionary> dics = baseDao.findByKeySortBy(lessonDictionary, " `order` asc ");
		return util.objectToJSON(dics.stream().map(lesDic -> {
			Dictionary dic = new Dictionary();
			dic.setHantu(lesDic.getHantu());
			dic.setHanviet(lesDic.getHanviet());
			dic.setHsk(lesDic.getHsk());
			dic.setId(lesDic.getRefid());
			dic.setLesson(lesDic.getLesson());
			dic.setNghia1(lesDic.getNghia1());
			dic.setPart(lesDic.getPart());
			dic.setPinyin(lesDic.getPinyin());
			return dic;
		}).collect(Collectors.toList()));
	}

	public String deleteTab(String tab, Long user_id) {
		UserDictionary userDictionary = new UserDictionary();
		userDictionary.setUserId(user_id);
		userDictionary.setTab(tab);
		return util.objectToJSON(baseDao.deleteByGivenValue(userDictionary));
	}

	public String savePracticeWords(List<UserDictionaryDTO> wordsId, Long userId) {
		Map<String, UserDictionaryDTO> map = wordsId.stream()
				.collect(Collectors.toMap(UserDictionaryDTO::getKey, dictionary -> dictionary));
		
		for (String key : map.keySet()) {
			UserResult userResult = new UserResult();
			userResult.setUserId(userId);
			userResult.setHsk(Integer.parseInt(key.split("-")[0]));
			userResult.setTestLesson(Integer.parseInt(key.split("-")[1]));
			userResult.setResultType(QUIZ.name());
			List<UserResult> userResults = baseDao.findByKey(userResult);
			if (userResults.isEmpty()) {
				userResult.setWordAmount(wordsId.size());
				userResult.setWordDetail(
						util.objectToJSON(wordsId.stream().map(dic -> dic.getWordId()).collect(Collectors.toList())));
				baseDao.add(userResult);
			} else {
				List<Integer> currentWords = util.jsonToListObject(userResults.get(0).getWordDetail(), WORRDS_TYPE);
				Set<Integer> newWords = Stream
						.of(currentWords, wordsId.stream().map(dic -> dic.getWordId()).collect(Collectors.toList()))
						.flatMap(item -> item.stream()).collect(Collectors.toSet());
				userResult.setWordAmount(newWords.size());
				userResult.setWordDetail(util.objectToJSON(newWords));
				baseDao.updateByInputKey(userResult, Arrays.asList("userid", "hsk", "testlesson", "resulttype"));
			}
		}
		return "200";
	}

	public String savePracticeWordsTEST(List<UserDictionaryDTO> wordsId, Long userId) {;
		Map<String, List<UserDictionaryDTO>> map = wordsId.stream().collect(Collectors.groupingBy(w -> w.getKey()));

		for (Map.Entry<String, List<UserDictionaryDTO>> pair : map.entrySet()){
			UserResult userResult = new UserResult();
			userResult.setUserId(userId);
			userResult.setHsk(Integer.parseInt(pair.getKey().split("-")[0]));
			userResult.setTestLesson(Integer.parseInt(pair.getKey().split("-")[1]));
			userResult.setResultType(QUIZ.name());
			List<UserResult> userResults = baseDao.findByKey(userResult);
			if (userResults.isEmpty()) {
				userResult.setWordAmount(pair.getValue().size());
				userResult.setWordDetail(
						util.objectToJSON(pair.getValue().stream().map(dic -> dic.getWordId()).collect(Collectors.toList())));
				userResult.setResultDetail("{}");
				baseDao.add(userResult);
			} else {
				List<Integer> currentWords = util.jsonToListObject(userResults.get(0).getWordDetail(), WORRDS_TYPE);
				Set<Integer> newWords = Stream
						.of(currentWords, pair.getValue().stream().map(dic -> dic.getWordId()).collect(Collectors.toList()))
						.flatMap(item -> item.stream()).collect(Collectors.toSet());
				userResult.setWordAmount(newWords.size());
				userResult.setWordDetail(util.objectToJSON(newWords));
				baseDao.updateByInputKey(userResult, Arrays.asList("userid", "hsk", "testlesson", "resulttype"));
			}
		}
		return "200";
	}

	public InputStream readWord(String word) {
		List<String> hantus = splitWordToList(word);
		checkExistAndDownloadByWord(word, hantus);
		Vector v = new Vector();
		int c;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(new File(dictionaryAudioPath + word + ".mp3"));
			v.add(fis);
		} catch (IOException e) {
			for(String hantu : hantus) {
				try {
					v.add(new FileInputStream(new File(dictionaryAudioPath+hantu+".mp3")));
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		SequenceInputStream bin = new SequenceInputStream(v.elements());

		return bin;
	}
	
	public List<String> splitWordToList(String words) {
		Set<UnicodeBlock> chineseUnicodeBlocks = new HashSet<UnicodeBlock>() {{
		    add(UnicodeBlock.CJK_COMPATIBILITY);
		    add(UnicodeBlock.CJK_COMPATIBILITY_FORMS);
		    add(UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS);
		    add(UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT);
		    add(UnicodeBlock.CJK_RADICALS_SUPPLEMENT);
		    add(UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION);
		    add(UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS);
		    add(UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A);
		    add(UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B);
		    add(UnicodeBlock.KANGXI_RADICALS);
		    add(UnicodeBlock.IDEOGRAPHIC_DESCRIPTION_CHARACTERS);
		}};

		List<String> singles = new LinkedList<>();
		for (char c : words.toCharArray()) {
		    if (!String.valueOf(c).trim().isEmpty() && chineseUnicodeBlocks.contains(UnicodeBlock.of(c))) {
		    	singles.add(String.valueOf(c));
		    } else {
		    }
		}
		return singles;
	}
	
	private void checkExistAndDownloadByWord(String word, List<String> hantus) {
		try {
			FileInputStream file = new FileInputStream(new File(dictionaryAudioPath+word+".mp3"));
		} catch (FileNotFoundException e) {
			try {
				URL url = new URL("https://vtudien.com/doc/trung/" + word + ".mp3");
				InputStream inputStream = url.openStream();
				FileOutputStream fileOutputStream = new FileOutputStream(new File(dictionaryAudioPath+word+".mp3"));

				int c;

				while ((c = inputStream.read()) != -1) {
					fileOutputStream.write(c);
					c++;
				}
				fileOutputStream.close();
				inputStream.close();
			} catch (IOException | NullPointerException e1) {
				for(String hantu : hantus) {
					checkExistAndDownloadByChar(hantu.trim());
				}
			}
		}
	}
	private void checkExistAndDownloadByChar(String hantu) {
		try {
			FileInputStream file = new FileInputStream(new File(dictionaryAudioPath+hantu+".mp3"));
		} catch (FileNotFoundException e) {
			try {
				URL url = new URL("https://vtudien.com/doc/trung/" + hantu + ".mp3");
				InputStream inputStream = url.openStream();
				FileOutputStream fileOutputStream = new FileOutputStream(new File(dictionaryAudioPath+hantu+".mp3"));

				int c;

				while ((c = inputStream.read()) != -1) {
					fileOutputStream.write(c);
					c++;
				}
				fileOutputStream.close();
				inputStream.close();
			} catch (IOException | NullPointerException e1) {
				// TODO Auto-generated catch block
				System.out.println(e1.getMessage());
			}
		}
	}
	public static void main(String[] args) {

		String csvFile = "C:\\TaxProject\\upload\\dictionary\\dictionary.csv";

		CSVReader reader = null;
		try {
			reader = new CSVReader(new FileReader(csvFile));
			String[] line;
			while ((line = reader.readNext()) != null) {
				System.out.println(line[0] + " - " + line[1] + " - " + line[2] + " - " + line[3] + " - " + line[4]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
