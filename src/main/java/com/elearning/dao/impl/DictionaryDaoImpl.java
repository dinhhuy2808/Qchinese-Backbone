package com.elearning.dao.impl;

import static com.elearning.constant.DictionaryConstant.CHECKBOX_COLUMN;
import static com.elearning.constant.DictionaryConstant.HSK_COLUMN;
import static com.elearning.constant.DictionaryConstant.INPUT_COLUMN;
import static com.elearning.constant.DictionaryConstant.LESSON_COLUMN;
import static com.elearning.constant.DictionaryConstant.NOTED_DATE_COLUMN;
import static com.elearning.constant.ElearningConstant.POPULAR_DICTIONARY_TAB;
import static com.elearning.constant.ElearningConstant.STANDART_DICTIONARY_TAB;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.elearning.dao.DictionaryDao;
import com.elearning.dto.StringListDTO;
import com.elearning.dto.UserDictionaryDTO;
import com.elearning.dto.UserDictionaryDatatableResponse;
import com.elearning.entity.Dictionary;
import com.elearning.entity.UserDictionary;
import com.elearning.repository.DictionaryRepository;
import com.elearning.repository.UserDictionaryRepository;
import com.elearning.request.DatatableParam;
import com.elearning.request.DatatableParamHolder;
import com.elearning.request.Order;
import com.elearning.request.UserDictionarySearchParam;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class DictionaryDaoImpl implements DictionaryDao {
	private final DictionaryRepository dictionaryRepository;
	private final UserDictionaryRepository userDictionaryRepository;
	private final JdbcTemplate jdbcTemplate;
    @PersistenceContext
    private EntityManager entityManager;

	@Override
	public List<UserDictionary> getUserDictionaryList(String tab, Long userId) {
		List<UserDictionary> result = new ArrayList<>();
		if (userId == 0) {
			return result;
		}
		if (!tab.equals(STANDART_DICTIONARY_TAB) && !tab.equals(POPULAR_DICTIONARY_TAB)) {
			result = userDictionaryRepository.findByUserIdAndTab(userId, tab);
		}
		return result;
	}

	public int countLessonDictionary(int type, DatatableParamHolder param) {
		StringBuilder sql = new StringBuilder("");
		UserDictionarySearchParam searchParam = param.getUserDictionarySearchParam();
		int results = 0;
		sql = new StringBuilder("");
		if (type == 1) {
			sql.append(" select count(*) from lessondictionary where standart = 1 ");
		} else {
			sql.append(" select count(*) from lessondictionary where popular = 1 ");
		}

		// FILTER BASED ON SEARCH PARAM
		if (searchParam != null) {
			if (searchParam.getLesson().size() != 0) {
				StringBuilder tempString = new StringBuilder("");
				for (int i = 0; i < searchParam.getLesson().size(); i++) {
					tempString.append(searchParam.getLesson().get(i));
					if (i != searchParam.getLesson().size() - 1) {
						tempString.append(", ");
					}
				}

				sql.append("and lesson in (" + tempString + ") ");
			}

			if (searchParam.getHsk().size() != 0) {
				StringBuilder tempString = new StringBuilder("");
				for (int i = 0; i < searchParam.getHsk().size(); i++) {
					tempString.append(searchParam.getHsk().get(i));
					if (i != searchParam.getHsk().size() - 1) {
						tempString.append(", ");
					}
				}

				sql.append("and hsk in (" + tempString + ") ");
			}
		}
		results = (Integer) entityManager.createNativeQuery(sql.toString()).getResultList().get(0);
		return results;
	}

	@Override
	public UserDictionaryDatatableResponse getUserDictionaryResponse(List<UserDictionary> userDictionaries, String tab,
			DatatableParamHolder param) {
		UserDictionaryDatatableResponse result = new UserDictionaryDatatableResponse();
		DatatableParam datatableParam = param.getDatatableParam();
		UserDictionarySearchParam searchParam = param.getUserDictionarySearchParam();
			// SET DICTIONARIES
			List<UserDictionaryDTO> userDictionaryDTOs = new ArrayList<>();
			StringBuilder sql = new StringBuilder("");

			if (!tab.equals(STANDART_DICTIONARY_TAB) && !tab.equals(POPULAR_DICTIONARY_TAB)) {
				for (UserDictionary userDictionary : userDictionaries) {
					sql = new StringBuilder("");
					if (userDictionary.getPlace().equals(STANDART_DICTIONARY_TAB)
							|| userDictionary.getPlace().equals(POPULAR_DICTIONARY_TAB)) {
						sql.append(" select * from lessondictionary where id in (" + userDictionary.getWordId() + ")");
					} else {
						sql.append(" select * from dictionary where id in (" + userDictionary.getWordId() + ")");
					}

					userDictionaryDTOs = jdbcTemplate.query(sql.toString(), (rs, rowNum) -> {

						Dictionary d = setToInstane(rs, new Dictionary());

						UserDictionaryDTO temp = new UserDictionaryDTO();
						temp.setWordId(d.getId());
						temp.setHantu(d.getHantu());
						temp.setPinyin(d.getPinyin());
						temp.setNghia1(d.getNghia1());
						temp.setHanviet(d.getHanviet());
						if (userDictionary.getPlace().equals(STANDART_DICTIONARY_TAB)
								|| userDictionary.getPlace().equals(POPULAR_DICTIONARY_TAB)) {
							temp.setHsk(d.getHsk());
							temp.setLesson(d.getLesson());
						} else {
//							String place = userDictionary.getPlace();
//							Integer hsk = Integer.parseInt(
//									place.startsWith("hsk") ? place.split("-")[0].split(" ")[1] : place.split("-")[1]);
//							String lesson = place.startsWith("hsk") ? place.split("-")[1].substring(6)
//									: place.split("-")[2];
//							temp.setHsk(hsk);
//							temp.setLesson(lesson);
						}
						temp.setPart(d.getPart());
						temp.setStandart(d.getStandart());
						temp.setPopular(d.getPopular());
						temp.setCreateDate(userDictionary.getCreateDate());
						temp.setTab(userDictionary.getTab());
						temp.setPlace(userDictionary.getPlace());
						temp.setIsChecked(false);
						temp.setInput("");
						temp.setCorrectLevel(0);
						temp.setIsHantuHidden(false);
						temp.setIsPinyinHidden(false);
						temp.setIsNghiaHidden(false);
						temp.setIsAudioHidden(false);
						return temp;

					});

				}


				if (searchParam.getHantu().isEmpty() && searchParam.getNghia().isEmpty()) {
					// FILTER BASED ON SEARCH PARAM
					if (searchParam != null) {
						if (searchParam.getCreateDate().size() != 0) {
							List<UserDictionaryDTO> tempUserDictionaryDTOs = new ArrayList<>();
							for (Integer date : searchParam.getCreateDate()) {
								tempUserDictionaryDTOs.addAll(userDictionaryDTOs.stream()
										.filter(x -> x.getCreateDate().equals(date)).collect(Collectors.toList()));
							}
							userDictionaryDTOs = tempUserDictionaryDTOs;
						}
					}

					result.setRescordsTotal(userDictionaryDTOs.size());
					result.setRecordsFiltered(userDictionaryDTOs.size());

					// FILTER BASED ON DATATABLE PARAM
					if (datatableParam != null) {
						result.setDraw(datatableParam.getDraw());

						// sort
						if (datatableParam.getOrder() != null) {
							for (Order o : datatableParam.getOrder()) {
								if (o.getColumn() != CHECKBOX_COLUMN && o.getColumn() != INPUT_COLUMN) {
									switch (o.getColumn()) {
									case NOTED_DATE_COLUMN: {
										DateTimeFormatter inFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

										if (o != null && o.getDir().equals("asc")) {
											userDictionaryDTOs.sort(
													(x, y) -> LocalDate.parse(x.getCreateDate().toString(), inFormatter)
															.compareTo(LocalDate.parse(y.getCreateDate().toString(),
																	inFormatter)));
										} else {
											userDictionaryDTOs.sort(
													(x, y) -> LocalDate.parse(y.getCreateDate().toString(), inFormatter)
															.compareTo(LocalDate.parse(x.getCreateDate().toString(),
																	inFormatter)));
										}
									}
									}
								}
							}
						}

						// paging
						userDictionaryDTOs = userDictionaryDTOs.stream().skip(datatableParam.getStart())
								.limit(datatableParam.getLength()).collect(Collectors.toList());
					}
				} else {
					if (!searchParam.getHantu().isEmpty()) {
						userDictionaryDTOs = userDictionaryDTOs.stream()
								.filter(x -> x.getHantu().equals(searchParam.getHantu())).collect(Collectors.toList());
					}

					if (!searchParam.getNghia().isEmpty()) {
						if (searchParam.getNghia().contains(" ")) {
							userDictionaryDTOs = userDictionaryDTOs.stream()
									.filter(x -> x.getNghia1().contains(searchParam.getNghia()))
									.collect(Collectors.toList());
						} else {
							// userDictionaryDTOs = userDictionaryDTOs.stream().filter(x ->
							// x.getNghia1().contains(" " + searchParam.getNghia() + "
							// ")).collect(Collectors.toList());
							userDictionaryDTOs = userDictionaryDTOs.stream()
									.filter(x -> x.getNghia1().equals(searchParam.getNghia()))
									.collect(Collectors.toList());
						}
					}
					// userDictionaryDTOs = userDictionaryDTOs.stream().filter(x ->
					// x.getHantu().equals(searchParam.getHantu())).collect(Collectors.toList());
					result.setRescordsTotal(userDictionaryDTOs.size());
					result.setRecordsFiltered(userDictionaryDTOs.size());
				}
			} else {
				final String currentTabName = tab.equals(STANDART_DICTIONARY_TAB) ? STANDART_DICTIONARY_TAB
						: (tab.equals(POPULAR_DICTIONARY_TAB) ? POPULAR_DICTIONARY_TAB : "");
				if (tab.equals(STANDART_DICTIONARY_TAB)) {
					sql.append(" select * from lessondictionary where standart = 1 ");
				} else if (tab.equals(POPULAR_DICTIONARY_TAB)) {
					sql.append(" select * from lessondictionary where popular = 1 ");
				}

				// FILTER BASED ON SEARCH PARAM
				if (searchParam != null) {
					if (searchParam.getLesson().size() != 0) {
						StringBuilder tempString = new StringBuilder("");
						for (int i = 0; i < searchParam.getLesson().size(); i++) {
							tempString.append(searchParam.getLesson().get(i));
							if (i != searchParam.getLesson().size() - 1) {
								tempString.append(", ");
							}
						}

						sql.append("and lesson in (").append(tempString).append(") ");
					}

					if (searchParam.getHsk().size() != 0) {
						StringBuilder tempString = new StringBuilder("");
						for (int i = 0; i < searchParam.getHsk().size(); i++) {
							tempString.append(searchParam.getHsk().get(i));
							if (i != searchParam.getHsk().size() - 1) {
								tempString.append(", ");
							}
						}

						sql.append("and hsk in (").append(tempString).append(") ");
					}

					if (!searchParam.getHantu().isEmpty()) {
						sql.append("and hantu = '").append(searchParam.getHantu()).append("' ");
					}

					if (!searchParam.getNghia().isEmpty()) {
						if (searchParam.getNghia().contains(" ")) {
							sql.append("and nghia1 like '%").append(searchParam.getNghia()).append("%' ");
						} else {
							sql.append("and nghia1 = '").append(searchParam.getNghia()).append("' ");
						}
					}
				}

				// FILTER BASED ON DATATABLE PARAM
				if (datatableParam != null) {
					result.setDraw(datatableParam.getDraw());

					// sort
					if (datatableParam.getOrder() != null) {
						for (Order o : datatableParam.getOrder()) {
							if (o.getColumn() != CHECKBOX_COLUMN && o.getColumn() != INPUT_COLUMN) {
								switch (o.getColumn()) {
								case HSK_COLUMN: {
									if (o != null && o.getDir().equals("asc")) {
										sql.append("order by hsk asc ");
									} else {
										sql.append("order by hsk desc ");
									}
								}
									break;
								case LESSON_COLUMN: {
									if (o != null && o.getDir().equals("asc")) {
										sql.append("order by lesson asc ");
									} else {
										sql.append("order by lesson desc ");
									}
								}
								}
							}
						}
					}

					// paging
					sql.append("limit ").append(datatableParam.getLength()).append(" offset ")
							.append(datatableParam.getStart());
				}

				userDictionaryDTOs.addAll(jdbcTemplate.query(sql.toString(), (rs, rowNum) ->{
					Dictionary d = setToInstane(rs, new Dictionary());

					UserDictionaryDTO temp = new UserDictionaryDTO();
					temp.setWordId(d.getId());
					temp.setHantu(d.getHantu());
					temp.setPinyin(d.getPinyin());
					temp.setNghia1(d.getNghia1());
					temp.setHanviet(d.getHanviet());
					temp.setHsk(d.getHsk());
					temp.setLesson(d.getLesson());
					temp.setPart(d.getPart());
					temp.setStandart(d.getStandart());
					temp.setPopular(d.getPopular());
					temp.setCreateDate(null);
					temp.setTab(currentTabName);
					temp.setPlace("");
					temp.setIsChecked(false);
					temp.setInput("");
					temp.setCorrectLevel(0);
					temp.setIsHantuHidden(false);
					temp.setIsPinyinHidden(false);
					temp.setIsNghiaHidden(false);

					return temp;
				}));
				
				// get total count
				int total;
				if (!searchParam.getHantu().isEmpty()) {
					total = userDictionaryDTOs.size();
				} else if (!searchParam.getNghia().isEmpty()) {
					total = userDictionaryDTOs.size();
				} else {
					total = tab.equals(STANDART_DICTIONARY_TAB) ? countLessonDictionary(1, param)
							: countLessonDictionary(0, param);
				}

				result.setRescordsTotal(total);
				result.setRecordsFiltered(total);
			}

			result.setData(userDictionaryDTOs);
		return result;
	}

	@Override
	public StringListDTO getUserTabs(Long userId) {
		StringListDTO result = new StringListDTO();
		List<String> tabs = new ArrayList<>();
		tabs.add(STANDART_DICTIONARY_TAB);
		tabs.add(POPULAR_DICTIONARY_TAB);

		if (userId == 0) {
			result.setValues(tabs);
			return result;
		}

			String query = " select distinct tab from userdictionary where userid = ? order by field(tab, 'Từ vựng đã lưu') desc ";
			jdbcTemplate.queryForList(query, String.class, userId);
			result.setValues(tabs);
		return result;
	}

	public StringListDTO getUserUserDictionaryCreateDate(Long userId, String tab) {
		StringListDTO result = new StringListDTO();
		List<String> createDates = new ArrayList<>();
			String query = " select distinct createdate from userdictionary where userid = ? and tab = ? order by createdate desc";
			createDates = jdbcTemplate.queryForList(query, String.class, String.valueOf(userId), tab);
			result.setValues(createDates);
		return result;
	}

	@Override
	public List<Dictionary> findWords(String keyWord) {
		return dictionaryRepository.findByHantuLikeOrPinyinLikeOrderById(keyWord, keyWord);
	}

	@Override
	public boolean deleteWordsFromUserDic(String tab, String wordIds, Long userId) {
		UserDictionary userDic = new UserDictionary();
		userDic.setUserId(userId);
		userDic.setTab(tab);
		List<UserDictionary> listDics = userDictionaryRepository.findByUserIdAndTab(userId, tab);
		List<String> wordsArray = Arrays.asList(wordIds.split(","));
			for (UserDictionary item : listDics) {
				String words = Arrays.asList(item.getWordId().split(",")).stream()
						.filter(word -> !wordsArray.contains(word)).collect(Collectors.joining(","));
				StringBuilder sql = new StringBuilder(
						"update userdictionary set wordid = ? where userid = ? and tab = ? and place = ?");
				if (words.trim().equals("")) {
					sql = new StringBuilder("delete from userdictionary where userid = ? and tab = ? and place = ?");
					jdbcTemplate.update(sql.toString(), userId, tab, item.getPlace());
				} else {
					jdbcTemplate.update(sql.toString(), words, userId, tab, item.getPlace());
				}

			}
		return true;
	}


	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Dictionary setToInstane(ResultSet rs, Object t) {
		Dictionary newT = null;
		try {
			newT = (Dictionary) t.getClass().newInstance();
		} catch (InstantiationException | IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Class<? extends Dictionary> targetClass = newT.getClass();

		for (Field field : targetClass.getDeclaredFields()) {
			String methodName = "set" + String.valueOf(field.getName().charAt(0)).toUpperCase()
					+ field.getName().substring(1);
			try {

				if (field.getType().getSimpleName().equals("Integer")) {
					Method method = targetClass.getDeclaredMethod(methodName, Integer.class);
					method.invoke(newT, rs.getInt(field.getName()));
				} else if (field.getType().getSimpleName().equals("Double")) {
					Method method = targetClass.getDeclaredMethod(methodName, Double.class);
					method.invoke(newT, rs.getDouble(field.getName()));
				} else if (field.getType().getSimpleName().equals("Long")) {
					Method method = targetClass.getDeclaredMethod(methodName, Long.class);
					method.invoke(newT, rs.getLong(field.getName()));
				}else if (field.getType().getSimpleName().equals("String")){
					Method method = targetClass.getDeclaredMethod(methodName, String.class);
					method.invoke(newT, rs.getString(field.getName()));
				}else if (field.getType().getSimpleName().equals("Date")){
					Method method = targetClass.getDeclaredMethod(methodName, Date.class);
					method.invoke(newT, rs.getDate(field.getName()));
				} else if (field.getType().isEnum()) {
					Method method = targetClass.getDeclaredMethod(methodName, field.getType());
					method.invoke(newT, Enum.valueOf((Class<? extends Enum>)field.getType(), rs.getString(field.getName())));
				}
				
			} catch (NoSuchMethodException e) {

				e.printStackTrace();
			} catch (SecurityException e) {

				e.printStackTrace();
			} catch (IllegalAccessException e) {

				e.printStackTrace();
			} catch (IllegalArgumentException e) {

				e.printStackTrace();
			} catch (InvocationTargetException e) {

				e.printStackTrace();
			} catch (SQLException e) {

				e.printStackTrace();
			}

		}

		return newT;
	}
}
