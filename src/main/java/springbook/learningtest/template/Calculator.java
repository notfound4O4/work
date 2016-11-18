package springbook.learningtest.template;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Calculator {

	public Integer calcSum(String filePath) throws IOException {
		/*
		 * BufferedReaderCallback sumCallback = new BufferedReaderCallback() {
		 * 
		 * @Override public Integer doSomethingWithReader(BufferedReader br)
		 * throws IOException { Integer sum = 0; String line = null;
		 * 
		 * while ((line = br.readLine()) != null ){ sum +=
		 * Integer.valueOf(line); }
		 * 
		 * return sum; } };
		 * 
		 * return fileReadTemplate(filePath, sumCallback);
		 */

		LineCallback<Integer> sumCallback = new LineCallback<Integer>() {

			public Integer doSomethingWithLine(String line, Integer value) {
				return value + Integer.valueOf(line);
			}
		};

		return lineReadTemplate(filePath, sumCallback, 0);

	}

	public Integer calcMultiply(String filePath) throws IOException {
		/*
		 * BufferedReaderCallback sumCallback = new BufferedReaderCallback() {
		 * 
		 * @Override public Integer doSomethingWithReader(BufferedReader br)
		 * throws IOException { Integer multiply = 1; String line = null;
		 * 
		 * while ((line = br.readLine()) != null) { multiply *=
		 * Integer.valueOf(line); }
		 * 
		 * return multiply; } };
		 * 
		 * return fileReadTemplate(filePath, sumCallback);
		 */

		LineCallback<Integer> multiplyCallback = new LineCallback<Integer>() {

			public Integer doSomethingWithLine(String line, Integer value) {
				return value * Integer.valueOf(line);
			}
		};

		return lineReadTemplate(filePath, multiplyCallback, 1);

	}

	public String concatenate(String filePath) throws IOException {
		LineCallback<String> concatenateCallback = new LineCallback<String>() {

			public String doSomethingWithLine(String line, String value) {
				return value + line;
			}
		};
		return lineReadTemplate(filePath, concatenateCallback, "");
	}

	public Integer fileReadTemplate(String filePath, BufferedReaderCallback callback) throws IOException {
		BufferedReader br = null;
		int ret = 0;
		try {
			br = new BufferedReader(new FileReader(filePath));
			ret = callback.doSomethingWithReader(br);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			throw e;
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (Exception e2) {
					System.out.println(e2.getMessage());
				}
			}
		}

		return ret;
	}

	public <T> T lineReadTemplate(String filePath, LineCallback<T> callback, T initVal) throws IOException {
		BufferedReader br = null;
		T res = null;
		try {
			br = new BufferedReader(new FileReader(filePath));

			res = initVal;
			String line = null;
			while ((line = br.readLine()) != null) {
				res = callback.doSomethingWithLine(line, res);
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
			throw e;
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (Exception e2) {
					System.out.println(e2.getMessage());
				}
			}
		}

		return res;
	}
}
