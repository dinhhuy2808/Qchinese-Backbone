package com.elearning.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;

import com.elearning.models.ProcessShellScript;
import com.google.gson.Gson;

@Service
public class Util {

	public <T> T jsonToObject(String json, Class<T> destination) {
		Gson gson = new Gson();
		T result = gson.fromJson(json, destination);
		return result;
	}

	public <T> List<T> jsonToListObject(String json, Type destination) {
		Gson gson = new Gson();
		List<T> result = gson.fromJson(json, destination);
		return result;
	}

	@SuppressWarnings("unchecked")
	public <T> Map<String, T> jsonToMapObject(String json, Map<String, ?> destination) {
		Gson gson = new Gson();
		Map<String, T> result = gson.fromJson(json, destination.getClass());
		return result;
	}

	public <T> String objectToJSON(T t) {
		Gson gson = new Gson();
		String result = gson.toJson(t);
		return result;
	}

	public String getCurrentDate() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
		LocalDateTime now = LocalDateTime.now();
		return dtf.format(now);
	}

	public String getCurrentDateToUpdateDb() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		return ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).format(formatter);
	}

	public Properties getDbProperties() {
		Properties prop = new Properties();
		try (InputStream input = new FileInputStream(System.getProperties().get("user.dir") + "/"
				+ "src/main/java/com/lhc/jerseyguice/resource/db.properties")) {

			// load a properties file
			prop.load(input);

		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return prop;
	}

	public String getMd5(String input) {
		try {

			// Static getInstance method is called with hashing MD5
			MessageDigest md = MessageDigest.getInstance("MD5");

			// digest() method is called to calculate message digest
			// of an input digest() return array of byte
			byte[] messageDigest = md.digest(input.getBytes());

			// Convert byte array into signum representation
			BigInteger no = new BigInteger(1, messageDigest);

			// Convert message digest into hex value
			String hashtext = no.toString(16);
			while (hashtext.length() < 32) {
				hashtext = "0" + hashtext;
			}
			return hashtext;
		}

		// For specifying wrong message digest algorithms
		catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	public void writeToFile(String output, String fileDestination) {
		FileOutputStream outputStream;
		try {
			outputStream = new FileOutputStream(fileDestination);
			byte[] strToBytes = output.getBytes();
			outputStream.write(strToBytes);

			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void writeToFile(Workbook workbook, String fileDestination) {
		

		FileOutputStream outputStream;
		try {
			outputStream = new FileOutputStream(fileDestination);
			workbook.write(outputStream);

			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public String getJsonStringFromFile(String localtion) {
		String json = "";
		try {
			for (String line : Files.readAllLines(Paths.get(localtion),StandardCharsets.UTF_8)) {
				json += line;
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
	}

	public List<String> getAllFilesNameInFolder(String path) {
		File[] files = new File(path).listFiles();
		try {
			return Arrays.asList(files).stream().map(file -> file.getName()).collect(Collectors.toList());
		} catch (Exception e) {
			return Arrays.asList();
		}
		
	}

	public String getCompleteNameInFolder(String pathFolder, String endWith, String startWith) {
		List<String> allFilesName = getAllFilesNameInFolder(pathFolder);
		String completeName = "";
		try {
			completeName = allFilesName.stream()
					.filter(fileName -> fileName.startsWith(startWith) && fileName.endsWith(endWith))
					.collect(Collectors.toList()).get(0);
		} catch (NullPointerException | IndexOutOfBoundsException e) {
			return "";
		}
		
		return completeName;
	}

	public void runShellScript(String folder, String command) {
		ProcessShellScript pss = new ProcessShellScript(command);
		pss.start();
	}

    public String getKey(){
        UUID uniqueKey = UUID.randomUUID();
        return uniqueKey.toString().replace("-", "");
    }

    public Long getRandomNumberBy(long max) {
    	return ThreadLocalRandom.current().nextLong(max);
    }
    
    @SuppressWarnings("unchecked")
	public Long getRandomNumberAndExcept(long max, List<Integer> ...exceptNumbersArray) {
    	List<Integer> exceptNumbers = new LinkedList<Integer>();
    	for (List<Integer> read : exceptNumbersArray) {
    		exceptNumbers.addAll(read);
    	}
    	Long index = ThreadLocalRandom.current().nextLong(max);
    	while(exceptNumbers.contains(index.intValue())) {
    		index = ThreadLocalRandom.current().nextLong(max);
    	}
    	return index;
    }
    
    public List<Long> getDistinctRandomNumersBy(int min, int max, int size) {
    	List<Long> result = new ArrayList<Long>();
    	while (result.size() < size) {
    		result.addAll(ThreadLocalRandom.current().longs(size,min, max).distinct().boxed().collect(Collectors.toList()));
    		result = result.stream().distinct().collect(Collectors.toList());
    	}
    	return result;
    }
	public static void main(String[] args) {
		Util util = new Util();
		System.out.println(util.getRandomNumberAndExcept(15, Arrays.asList(1 ,4 ,5, 6,7,8,9), Arrays.asList(10,11,12,13)));
	}
}
