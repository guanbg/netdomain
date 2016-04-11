package com.platform.cubism.front.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.platform.cubism.SystemConfig;
import com.platform.cubism.util.SecurityHelper;
import com.platform.cubism.util.StringUtils;

@WebServlet(name = "ShowImage", urlPatterns = { "/sys.image.files" })
public class ShowImage extends HttpServlet {
	private static final long serialVersionUID = 111111111L;
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		String isAbsolutePath = request.getParameter("isabsolutepath");
		String filePathName = request.getParameter("filepathname");
		String filetype = request.getParameter("filetype");
		String isdecrypt = request.getParameter("isdecrypt");// 非必输，默认解密
		
		if (filePathName == null || "".equals(filePathName)) {
			logger.info("图片文件名称不能为空");
			return;
		}
		if (!"false".equalsIgnoreCase(isdecrypt)) {// 默认需要解密，除非明确制定为不解密，取值false
			filePathName = SecurityHelper.DesDecrypt(filePathName);
		}
		if (!"true".equalsIgnoreCase(isAbsolutePath)) {
			filePathName = StringUtils.getFilePathName(SystemConfig.getDownloadPath(), filePathName);
		}
		
		byte imgdata[] = null;

		try {
			File f = new File(filePathName);
			if(!f.exists()){
				//response.getOutputStream().print("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAAkCAYAAABIdFAMAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAHhJREFUeNo8zjsOxCAMBFB/KEAUFFR0Cbng3nQPw68ArZdAlOZppPFIBhH5EAB8b+Tlt9MYQ6i1BuqFaq1CKSVcxZ2Acs6406KUgpt5/LCKuVgz5BDCSb13ZO99ZOdcZGvt4mJjzMVKqcha68iIePB86GAiOv8CDADlIUQBs7MD3wAAAABJRU5ErkJggg%3D%3D");
				//response.flushBuffer();
				logger.error("图片文件不存在，请重新选择：" + filePathName);
				return;
			}
			
			FileInputStream hFile = new FileInputStream(f);
			int i = hFile.available();
			imgdata = new byte[i];//图片文件一般不会太大，所以一次读入
			hFile.read(imgdata);
			hFile.close();
		} catch (Exception e){
			logger.error("图片文件打开错误：" + e.getMessage());
			return;
		}

		if (imgdata == null || imgdata.length <= 0) {
			logger.info("读取图片文件时数据错误");
			return;
		}
		response.setHeader("Cache-Control", "private");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Connection", "Keep-Alive");
		response.setHeader("Proxy-Connection", "Keep-Alive");
		response.setHeader("Content-Disposition", "inline; filename=" +System.nanoTime()+"."+ filetype);
		try {
			if(filetype != null && filetype.length() >= 0){
				response.setContentType("image/"+filetype);
			}
			else{
				response.setContentType("image/*");
			}
			
			OutputStream out = response.getOutputStream();
			out.write(imgdata);
			response.flushBuffer();
			//out.close();
		} catch (Exception e) {
			logger.error("图片文件写入返回流错误：" + e.getMessage());
		}
		imgdata = null;
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		doPost(request, response);
	}
}
