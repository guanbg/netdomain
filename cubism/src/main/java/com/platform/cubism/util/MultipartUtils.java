package com.platform.cubism.util;

import static com.platform.cubism.util.StringUtils.hasText;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;

public class MultipartUtils {
	private static final String FILENAME_KEY = "filename=";

	public static boolean isMultipart(HttpServletRequest request) {
		Assert.notNull(request, "Request must not be null");

		if (!"post".equals(request.getMethod().toLowerCase())) {
			return false;
		}
		String contentType = request.getContentType();
		return (contentType != null && contentType.toLowerCase().startsWith("multipart/"));
	}

	public static String extractFileName(String pathname) {
		if (!hasText(pathname)) {
			return "";
		}
		if (pathname.lastIndexOf(File.separatorChar) != -1) {
			pathname = pathname.substring(pathname.lastIndexOf(File.separatorChar) + 1);
		}
		if (pathname.lastIndexOf('/') != -1) {
			pathname = pathname.substring(pathname.lastIndexOf('/') + 1);
		}
		return pathname;
	}
	public static String extractFilePath(String pathname) {
		if (!hasText(pathname)) {
			return "";
		}
		int idx = pathname.lastIndexOf('/');
		if(idx <= -1){
			idx = pathname.lastIndexOf('\\');
		}		
		if(idx <= -1){
			return "";
		}
		return pathname.substring(0,idx);
	}
	public static String extractFilePathName(String contentDisposition) {
		if (contentDisposition == null) {
			return null;
		}
		int startIndex = contentDisposition.indexOf(FILENAME_KEY);
		if (startIndex == -1) {
			return null;
		}
		String filename = contentDisposition.substring(startIndex + FILENAME_KEY.length());
		if (filename.startsWith("\"")) {
			int endIndex = filename.indexOf("\"", 1);
			if (endIndex != -1) {
				return filename.substring(1, endIndex);
			}
		} else {
			int endIndex = filename.indexOf(";");
			if (endIndex != -1) {
				return filename.substring(0, endIndex);
			}
		}
		return filename;
	}

	public static long saveStreamToFile(InputStream is, String filename, long maxSize) throws Exception {
		Assert.notNull(is, "InputStream must not be null");
		Assert.hasText(filename, "filename must not be null");

		File file = new File(filename);
		if (file.exists()) {
			file.delete();
		}
		else if(!file.canWrite()){
			File dir = new File(extractFilePath(filename));
			dir.mkdirs();
		}
		file.createNewFile();
		OutputStream outStream = new BufferedOutputStream(new FileOutputStream(file));
		byte[] buffer = new byte[1024 * 1024 * 10];
		long byteCount = 0l;
		int bytesRead = -1;
		try {
			while ((bytesRead = is.read(buffer)) != -1) {
				outStream.write(buffer, 0, bytesRead);
				byteCount += bytesRead;
				
				if(maxSize > 0 && maxSize <= byteCount){
					throw new Exception("限制文件最大为：" + (maxSize/1024) + "K");
				}
			}
			outStream.flush();
		} finally {
			try {
				outStream.close();
			} catch (IOException ex) {
			}
		}

		return byteCount;
	}
}