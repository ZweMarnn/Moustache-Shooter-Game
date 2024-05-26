package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

public class FileTest {

	public static List<Integer> getScoreList(List<String> recordList) {

		List<Integer> levelList = new ArrayList<Integer>();
		List<Integer> scoreList = new ArrayList<Integer>();

		for (int i = 0; i < recordList.size(); i++) {
			// System.out.println(recordList.get(i));
			StringTokenizer st = new StringTokenizer(recordList.get(i), ",");

			if (st.hasMoreTokens()) {
				levelList.add(Integer.parseInt(st.nextToken()));
				scoreList.add(Integer.parseInt(st.nextToken()));
			}

		}

		return scoreList;

	}

	public static List<Integer> getLevelList(List<String> recordList) {

		List<Integer> levelList = new ArrayList<Integer>();
		List<Integer> scoreList = new ArrayList<Integer>();

		for (int i = 0; i < recordList.size(); i++) {
			// System.out.println(recordList.get(i));
			StringTokenizer st = new StringTokenizer(recordList.get(i), ",");

			if (st.hasMoreTokens()) {
				levelList.add(Integer.parseInt(st.nextToken()));
				scoreList.add(Integer.parseInt(st.nextToken()));
			}

		}

		return levelList;

	}

	public static void writeRecord(String level, String score) {
		File file = null;
		FileWriter fw = null;
		
		try {
			file = new File("record.txt");
			if (file.createNewFile()) {
				System.out.println("File1" + file.getAbsolutePath());
				fw = new FileWriter(file, true);
				fw.write(level + "," + score + "\n");
			}else {
				System.out.println("File1" + file.getAbsolutePath());
				fw = new FileWriter(file, true);
				fw.append(level + "," + score + "\n");
				
			}
		
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fw != null)
					fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Finish writing records.");
	}

	public static List<String> readRecords() {
		List<String> historyList = new ArrayList<String>();
		File file = null;
		FileReader fr = null;
		BufferedReader br = null;
		try {
			file = new File("record.txt");
			System.out.println("File: " + file.getAbsolutePath());
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			String line;
			while ((line = br.readLine()) != null) {
				// System.out.println(line);
				historyList.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
				if (fr != null)
					fr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Finish reading file.");
		return historyList;
	}

}
