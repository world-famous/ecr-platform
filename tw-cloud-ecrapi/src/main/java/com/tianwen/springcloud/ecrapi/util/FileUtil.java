package com.tianwen.springcloud.ecrapi.util;

import org.apache.commons.lang.ArrayUtils;

import java.io.*;

public class FileUtil {

	public static String getUniqueFileName(String filePath, String extension) {
		int i = 0;

		while (true) {
			String fileName = DateTimeUtil.getUniqueTime() + i;

			if (!extension.equals("")) {
				fileName += "." + extension;
			}

			java.io.File file = new java.io.File(filePath + fileName);

			if (!file.exists()) {
				return fileName;
			}

			i++;
		}
	}

	public static String getFileNameOnly(String fileName) throws Exception {
		return fileName.substring(0, fileName.lastIndexOf("."));
	}

	public static long getFileSize(String path) throws Exception {
		java.io.File file = new java.io.File(path);

		return file.length();
	}

	public static String getFileExtension(String fileName) {
		String extension = "";

		if (fileName.lastIndexOf(".") > -1) {
			extension = fileName.substring(fileName.lastIndexOf(".") + 1);
		}

		return extension;
	}

	public static String getFileName(String fileName) {
		String extension = "";

		if (fileName.lastIndexOf(".") > -1) {
			extension = fileName.substring(0, fileName.lastIndexOf("."));
		}

		return extension;
	}

	public static String getFilePath(String fileName) {
		String path = "";
		if (fileName.lastIndexOf("/") > -1) {
			path = fileName.substring(0, fileName.lastIndexOf("/"));
		}
		return path;
	}

	public static String loadText(String path) throws Exception {

		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
		StringWriter stringWriter = new StringWriter();

		String line;

		while ((line = bufferedReader.readLine()) != null) {
			stringWriter.append(line + "\n");
		}

		bufferedReader.close();

		return stringWriter.toString();
	}

	public static void saveText(String path, String text) throws Exception {
		PrintWriter printWriter = new PrintWriter(
				new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), "UTF-8")), true);
		printWriter.print(text);
		printWriter.close();
	}

	public static boolean delete(String filePath) {
		java.io.File deleteFile = new java.io.File(filePath);

		if (deleteFile.exists()) {
			return deleteFile.delete();
		}
		return false;
	}

	public static boolean deleteDirectory(File path) {
		boolean res = false;
		if (path.exists()) {
			File[] files = path.listFiles();
			if (ArrayUtils.isNotEmpty(files)) {
				for (int i = 0; i < files.length; i++) {
					if (files[i].isDirectory()) {
						deleteDirectory(files[i]);
					} else {
						res = files[i].delete();
					}
				}
			}
		}
		res = path.delete();
		return res;
	}

	public static boolean copyPartitionFile(String sourceDirPath, int partitionCount, String destinFilePath) {
		File file = new File(destinFilePath);
		if (file.exists()) {
			boolean res = file.delete();
			if (!res) return res;
		}

		for (int i = 0; i < partitionCount; i++) {
			String sourcePath = sourceDirPath + "/" + i;
			String command = String.format("cat \"%s\" >> \"%s\"", sourcePath, destinFilePath);
			
			System.out.println("copyPartitionFile:" + command);
		}
		return true;
	}

}
