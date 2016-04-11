package com.platform.cubism.front.file;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.platform.cubism.SystemConfig;
import com.platform.cubism.util.SecurityHelper;
import com.platform.cubism.util.StringUtils;

@WebServlet(urlPatterns = { "/sys.download.files" })
public class Download extends HttpServlet {
	private static final long serialVersionUID = -831098806070486046L;
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private static final Map<String, String> contentType = new HashMap<String, String>();
	static {
		contentType.put("doc", "application/msword");
		contentType.put("docx", "application/msword");
		contentType.put("dot", "application/msword");

		contentType.put("xls", "application/vnd.ms-excel");
		contentType.put("xlsx", "application/vnd.ms-excel");

		contentType.put("ppt", "appication/powerpoint");
		contentType.put("pptx", "appication/powerpoint");
		contentType.put("pdf", "application/pdf");

		contentType.put("ico", "image/ico");
		contentType.put("png", "image/png");
		contentType.put("jpeg", "image/jpeg");
		contentType.put("jpg", "image/jpeg");
		contentType.put("jpe", "image/jpeg");
		contentType.put("gif", "image/gif");
		contentType.put("bmp", "image/bmp");
		contentType.put("tiff", "image/tiff");
		contentType.put("cgm", "image/cgm");
		contentType.put("emf", "application/x-emf");

		contentType.put("avi", "video/x-msvideo");
		contentType.put("wav", "audio/x-wav");
		contentType.put("ram", "audio/x-pn-realaudio");
		contentType.put("ra", "audio/x-pn-realaudio");
		contentType.put("midi", "audio/x-aiff");
		contentType.put("mid", "audio/x-aiff");
		contentType.put("rmf", "audio/x-aiff");
		contentType.put("mpeg", "audio/mpeg");
		contentType.put("mp2", "audio/mpeg");
		contentType.put("mp3", "audio/mpeg");
		contentType.put("au", "audio/basic");
		contentType.put("snd", "audio/basic");

		contentType.put("zip", "application/zip");
		contentType.put("rar", "application/zip");
		contentType.put("tar", "application/x-tar");
		contentType.put("gtar", "application/x-gtar");
		contentType.put("gzip", "application/x-gzip");
		contentType.put("z", "appication/x-compress");
		contentType.put("swf", "application/x-shockwave-flash");
		contentType.put("eps", "application/postscript");
		contentType.put("ps", "application/postscript");
		contentType.put("ai", "application/postscript");

		contentType.put("rtf", "appication/rtf");
		contentType.put("html", "text/html");
		contentType.put("htm", "text/html");
		contentType.put("txt", "text/plain");
		contentType.put("jsp", "text/plain");
		contentType.put("asp", "text/plain");
		contentType.put("xml", "text/xml");
		contentType.put("css", "text/css");
	}

	@Override
	public void init(ServletConfig config) {
		config.getServletContext();
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		String isAbsolutePath = request.getParameter("isabsolutepath");
		String fileSaveName = request.getParameter("filesavename");
		String filePathName = request.getParameter("filepathname");
		String fileType = request.getParameter("filetype");
		String isdecrypt = request.getParameter("isdecrypt");// 非必输，默认解密
		String isopen = request.getParameter("isopen");// 非必输，默认下载而不是在线打开
		if ((fileSaveName == null || "".equals(fileSaveName)) && (filePathName == null || "".equals(filePathName))) {
			return;
		}
		if (filePathName == null || "".equals(filePathName)) {
			filePathName = StringUtils.getFilePathName(SystemConfig.getDownloadPath(), fileSaveName);
		} else if (fileSaveName == null || "".equals(fileSaveName)) {
			fileSaveName = filePathName;
		}

		if (!"false".equalsIgnoreCase(isdecrypt)) {// 默认需要解密，除非明确制定为不解密，取值false
			filePathName = SecurityHelper.DesDecrypt(filePathName);
		}

		if (!"true".equalsIgnoreCase(isAbsolutePath)) {
			filePathName = StringUtils.getFilePathName(SystemConfig.getDownloadPath(), filePathName);
		}
		if (fileType != null && contentType.get(fileType) != null) {
			response.setContentType(contentType.get(fileType));
			try {
				String saveName = new String(fileSaveName.getBytes("utf-8"), "ISO8859-1");
				response.setHeader("Content-Disposition", "inline; filename=" + saveName);
			} catch (UnsupportedEncodingException e) {
				logger.info(fileSaveName + "文件名称编码转换错误:" + e.getMessage());
				response.setHeader("Content-Disposition", "inline; filename=" + fileSaveName);
			}
		} else if ("true".equalsIgnoreCase(isopen)) {
			String ftyp = fileSaveName.substring(fileSaveName.lastIndexOf('.') + 1, fileSaveName.length());
			if (ftyp != null && ftyp.length() > 0 && contentType.get(ftyp) != null) {
				response.setContentType(contentType.get(fileType));
			} else {
				response.setContentType("application/x-msdownload");
			}
			try {
				String saveName = new String(fileSaveName.getBytes("utf-8"), "ISO8859-1");
				response.setHeader("Content-Disposition", "inline; filename=" + saveName);
			} catch (UnsupportedEncodingException e) {
				logger.info(fileSaveName + "文件名称编码转换错误:" + e.getMessage());
				response.setHeader("Content-Disposition", "inline; filename=" + fileSaveName);
			}
		} else {
			response.setContentType("application/x-msdownload");
			try {
				String saveName = new String(fileSaveName.getBytes("utf-8"), "ISO8859-1");
				response.setHeader("Content-Disposition", "attachment; filename=" + saveName);
			} catch (UnsupportedEncodingException e) {
				logger.info(fileSaveName + "文件名称编码转换错误:" + e.getMessage());
				response.setHeader("Content-Disposition", "attachment; filename=" + fileSaveName);
			}
		}

		InputStream in = null;
		int readBytes = 0;
		if (logger.isDebugEnabled()) {
			logger.debug("需要下载的文件名称：" + fileSaveName);
			logger.debug("需要下载的文件路径：" + filePathName);
		}
		File f = new File(filePathName);
		if (!f.exists()) {
			try {
				String fp = new String(filePathName.getBytes("ISO8859-1"), "utf-8");
				logger.debug("需要下载的文件名称(转换后)：" + fp);
				f = new File(fp);
				if (!f.exists()) {
					logger.error("文件名称(转换后)：" + fp);
					return;
				}
			} catch (UnsupportedEncodingException e) {
				logger.error("文件名称转码错误：" + e.getMessage());
			}
		}
		try {
			int size = 1024 * 300;
			byte[] buffer = new byte[size];
			OutputStream out = response.getOutputStream();
			in = new BufferedInputStream(new FileInputStream(f));
			response.setHeader("Content_Length", String.valueOf(in.available()));
			while ((readBytes = in.read(buffer, 0, size)) > 0) {
				out.write(buffer, 0, readBytes);
			}
			response.flushBuffer();
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("文件名称：" + fileSaveName);
				logger.error("文件路径：" + filePathName);
				logger.error("文件下载失败，失败原因：" + e.getMessage());
			}
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		doPost(request, response);
	}
}
