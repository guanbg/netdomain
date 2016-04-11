package com.platform.cubism.sqlite;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.platform.cubism.SystemConfig;
import com.platform.cubism.util.SecurityHelper;

@WebServlet(urlPatterns = { "*.sqlite_diskfile_download" })
public class DownloadSqlite   extends HttpServlet{
	private static final long serialVersionUID = -8838012574037646529L;
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private static final String rootPath = SystemConfig.getUploadPath();
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		response.setHeader("pragma","no-cache");   
		response.setHeader("cache-control","no-cache");   
		response.setDateHeader("Expires",0);   
		response.setContentType("octets/stream");  
		response.addHeader("Content-Type", "text/html; charset=utf-8");
		
		String fileid = request.getParameter("fileid");
		String fileName = new String(SecurityHelper.hexStringToBytes(fileid));
		String fileName2 = fileName;
		String diskFilename = rootPath  + "ND/"+fileid;
		String agent = request.getHeader("USER-AGENT"); 
		 try {
			 String charset = "UTF-8";
			 if(agent != null){
				 agent = agent.toLowerCase();
				 if(agent.indexOf("msie") > 0 || agent.indexOf("trident") > 0){
					 charset = "GBK";
				 }
			 }
			 fileName2 = new String(fileName.getBytes(charset),"ISO_8859_1");
		} catch (UnsupportedEncodingException e){
			if(logger.isDebugEnabled()){
				logger.debug("字符编码转换错误，忽略："+fileName);
				e.printStackTrace();
			}
		}
		response.addHeader("Content-Disposition", "attachment; filename=" + fileName2); 
		downFile(diskFilename, response);
	}
	
	private void downFile(String diskFilename, HttpServletResponse response){
		InputStream in = null;
		int readBytes = 0;
		if (logger.isDebugEnabled()) {
			logger.debug("需要下载的文件：" + diskFilename);
		}
		File f = new File(diskFilename);
		if (!f.exists()) {
			logger.error("需要下载的文件不存在");
			return;
		}
		try {
			int size = 1024 * 300;
			byte[] buffer = new byte[size];
			OutputStream out = response.getOutputStream();
			in = new BufferedInputStream(new FileInputStream(f));
			response.setHeader("Content_Length", String.valueOf(in.available()));
			response.setContentLength((int)in.available());
			while ((readBytes = in.read(buffer, 0, size)) > 0) {
				out.write(buffer, 0, readBytes);
			}
			response.flushBuffer();
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("文件路径：" + diskFilename);
				logger.error("文件下载失败，失败原因：" + e.getMessage());
				e.printStackTrace();
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
}
