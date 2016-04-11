package com.platform.cubism.front.file;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.platform.cubism.SystemConfig;
import com.platform.cubism.util.SecurityHelper;

@WebServlet(urlPatterns = { "*.sys_diskfile_download" })
public class DownloadDisk  extends HttpServlet{
	private static final long serialVersionUID = 3580162447448465818L;
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private static final String rootPath = SystemConfig.getUploadPath();
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
	
	public void init(ServletConfig config) {
		config.getServletContext();
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		response.setHeader("pragma","no-cache");   
		response.setHeader("cache-control","no-cache");   
		response.setDateHeader("Expires",0);   
		response.setContentType("octets/stream");  
		response.addHeader("Content-Type", "text/html; charset=utf-8");
		
		try {
			request.setCharacterEncoding("UTF-8");//设置编码
		} catch (UnsupportedEncodingException e) {
			if(logger.isDebugEnabled()){
				logger.debug("设置request编码错误，忽略");
				e.printStackTrace();
			}
		}  
		
		String fileid = request.getParameter("fileid");
		String dirtype = request.getParameter("dirtype");//
		String downtype = request.getParameter("downtype");//image-图片展示，inline-在线打开，attachment-文件下载
		String dimension = request.getParameter("dimension");//图片显示尺寸：width*height
		String percentage = request.getParameter("percentage");//图片显示尺寸百分比
		
		if (fileid == null || fileid.length() <= 0) {
			return;
		}
		if(downtype == null || downtype.length() <= 0){
			downtype = "attachment";
		}
		
		fileid = fileid.toUpperCase();
		String dir = "",fileName,fileType,diskFilename,fileName2,filePath,agent;
		if(dirtype == null || dirtype.equals("A")){
			int idx = fileid.indexOf('A');
			if(idx <= 0){
				return;
			}
			String dt = fileid.substring(0, idx);
			fileName = new String(SecurityHelper.hexStringToBytes(fileid.substring(idx+1)));
			fileType = fileName.substring(fileName.lastIndexOf('.') + 1);
			
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(Long.parseLong(dt));
			int year = cal.get(Calendar.YEAR);//获取年份
	        int month = cal.get(Calendar.MONTH)+1;//获取月份 
	        int day = cal.get(Calendar.DATE);//获取日 
	        
	        dir = year+"/"+month+"/"+day+"/";
			filePath = idx < 0 ?fileid:dir+fileid;
			diskFilename = rootPath  + filePath;
			fileName2 = fileName;
			agent = request.getHeader("USER-AGENT"); 
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
		}
		else if(dirtype.equals("/")){
			int idx = fileid.indexOf('/');
			if(idx>0){
				dir = fileid.substring(0, idx+1);
				fileName = new String(SecurityHelper.hexStringToBytes(fileid.substring(idx+1)));
			}
			else{
				fileName = new String(SecurityHelper.hexStringToBytes(fileid));
			}
			fileType = fileName.substring(fileName.lastIndexOf('.') + 1);
			filePath = fileid;
			diskFilename = rootPath  + filePath;
			fileName2 = fileName;
			agent = request.getHeader("USER-AGENT"); 
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
		}
		else{
			return;
		}
		 
		if (logger.isDebugEnabled()) {
			logger.debug("下载文件信息：");
			logger.debug("fileid=" + fileid);
			logger.debug("downtype=" + downtype);
			
			logger.debug("fileType=" + fileType);
			logger.debug("fileName=" + fileName);
			logger.debug("filePath=" + filePath);
			logger.debug("diskFilename=" + diskFilename);
			logger.debug("agent=" + agent);
		}
		
		if("image".equalsIgnoreCase(downtype)){
			if(percentage != null && percentage.length() > 0){
				showPercentageImage(diskFilename, fileName2, response, Integer.parseInt(percentage));
			}
			else if(dimension != null && dimension.length() > 0){
				String[] widthHeight = dimension.split("\\D");
				int width = Integer.parseInt(widthHeight[0]);
				int height = Integer.parseInt(widthHeight[1]);
				showZoomImage(diskFilename, fileName2, response, width, height);
			}
			else{
				showImage(diskFilename, fileName2, response);
			}
		}
		else if("inline".equalsIgnoreCase(downtype)){
			if (fileType != null && contentType.get(fileType) != null) {
				response.setContentType(contentType.get(fileType));
			}
			response.addHeader("Content-Disposition", "inline; filename=" + fileName2);
			
			downFile(diskFilename, response);
		}
		else if("download".equalsIgnoreCase(downtype)){
			//response.setContentType("application/octet-stream;charset=GBK");  
			//response.setContentType("application/octet-stream;charset=UTF-8");
			//response.setContentType("application/x-msdownload;charset=UTF-8");
			response.addHeader("Content-Disposition", "attachment; filename=" + fileName2); 
			//response.setHeader("Content-Disposition", "attachment; filename=" + fileName2);
			
			downFile(diskFilename, response);
		}
		else{
			if (fileType != null && contentType.get(fileType) != null) {
				response.setContentType(contentType.get(fileType));
			}
			response.addHeader("Content-Disposition", "attachment; filename=" + fileName2); 
			
			downFile(diskFilename, response);
		}
	}
	
	private void showPercentageImage(String diskFilename, String fileName, HttpServletResponse response,int percentage){ 
		byte imgdata[] = null;
		String fileType = fileName.substring(fileName.lastIndexOf('.') + 1);
		
		try {
			File srcFile = new File(diskFilename);
			if(!srcFile.exists()){
				logger.error("图片文件不存在，请重新选择：" + diskFilename);
				return;
			}
			
			FileInputStream hSrcFile = new FileInputStream(srcFile);
			int size = hSrcFile.available();
			if(size / 1024 < 60){//60K以下不压缩
				imgdata = new byte[size];//图片文件一般不会太大，所以一次读入
				hSrcFile.read(imgdata);
				hSrcFile.close();
			}
			else{
				int idx = diskFilename.lastIndexOf('/');
				String destFileName =  (idx < 0 ?diskFilename:diskFilename.substring(idx+1)) + percentage + "%";
				String dest = diskFilename + percentage + "%";
				File destFile = new File(dest); 
				
				if(!destFile.exists()){
					destFile.createNewFile();
					
					BufferedImage bufImg = ImageIO.read(srcFile);
					/* 原始图像的宽度和高度 */  
		            int width = bufImg.getWidth();  
		            int height = bufImg.getHeight(); 
		           
		            /* 调整后的图片的宽度和高度 */  
		            int toWidth = (int)(width * percentage / 100);  
		            int toHeight = (int)(height * percentage / 100);  
		            if(toWidth < 20){
		            	toWidth = width;
		            }
		            if(toHeight < 20){
		            	toHeight = height;
		            }
		            
		            /* 新生成结果图片 */  
		            BufferedImage result = new BufferedImage(toWidth, toHeight,BufferedImage.TYPE_INT_RGB);	  
		            result.getGraphics().drawImage(bufImg.getScaledInstance(toWidth, toHeight,Image.SCALE_SMOOTH), 0, 0, null); 
		            
					try {  
			            ImageIO.write(result, fileType, destFile);  
			        } catch (Exception ex) {
			        	logger.error("图片文件缩放错误：" + destFileName);
			        	if(logger.isDebugEnabled()){
			        		ex.printStackTrace();
			        	}
			        }
				}
				
				FileInputStream hFile = new FileInputStream(destFile);
				int i = hFile.available();
				imgdata = new byte[i];//图片文件一般不会太大，所以一次读入
				hFile.read(imgdata);
				hFile.close();
			}
		} catch (Exception e){
			logger.error("图片文件打开错误：" + e.getMessage());
			if(logger.isDebugEnabled()){
        		e.printStackTrace();
        	}
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
		response.setHeader("Content-Disposition", "inline; filename="+fileName);
		try {
			if(fileType != null && fileType.length() >= 0){
				response.setContentType("image/"+fileType);
			}
			else{
				response.setContentType("image/*");
			}
			
			OutputStream out = response.getOutputStream();
			out.write(imgdata);
			response.flushBuffer();
		} catch (Exception e) {
			logger.error("图片文件写入返回流错误：" + e.getMessage());
			if(logger.isDebugEnabled()){
        		e.printStackTrace();
        	}
		}
		imgdata = null;
	}
	private void showZoomImage(String diskFilename, String fileName, HttpServletResponse response,int w,int h){  
		byte imgdata[] = null;
		String fileType = fileName.substring(fileName.lastIndexOf('.') + 1);
		
		try {
			File srcFile = new File(diskFilename);
			if(!srcFile.exists()){
				logger.error("图片文件不存在，请重新选择：" + diskFilename);
				return;
			}
			FileInputStream hSrcFile = new FileInputStream(srcFile);
			int size = hSrcFile.available();
			if(size / 1024 < 60){//60K以下不压缩
				imgdata = new byte[size];//图片文件一般不会太大，所以一次读入
				hSrcFile.read(imgdata);
				hSrcFile.close();
			}
			else{
				int idx = diskFilename.lastIndexOf('/');
				String destFileName =  (idx < 0 ?diskFilename:diskFilename.substring(idx+1)) + w + "X" + h;
				String dest = diskFilename + w + "X" + h;
				File destFile = new File(dest); 
				
				if(!destFile.exists()){
					destFile.createNewFile();
					
					BufferedImage bufImg = ImageIO.read(srcFile);  
					BufferedImage result = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB); 
					result.getGraphics().drawImage(bufImg.getScaledInstance(w, h, Image.SCALE_SMOOTH), 0, 0, null); 
					
					try {  
			            ImageIO.write(result, fileType, destFile);  
			        } catch (Exception ex) {
			        	logger.error("图片文件缩放错误：" + destFileName);
			        	if(logger.isDebugEnabled()){
			        		ex.printStackTrace();
			        	}
			        }
				}
				
				FileInputStream hFile = new FileInputStream(destFile);
				int i = hFile.available();
				imgdata = new byte[i];//图片文件一般不会太大，所以一次读入
				hFile.read(imgdata);
				hFile.close();
			}
		} catch (Exception e){
			logger.error("图片文件打开错误：" + e.getMessage());
			if(logger.isDebugEnabled()){
        		e.printStackTrace();
        	}
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
		response.setHeader("Content-Disposition", "inline; filename="+fileName);
		try {
			if(fileType != null && fileType.length() >= 0){
				response.setContentType("image/"+fileType);
			}
			else{
				response.setContentType("image/*");
			}
			
			OutputStream out = response.getOutputStream();
			out.write(imgdata);
			response.flushBuffer();
		} catch (Exception e) {
			logger.error("图片文件写入返回流错误：" + e.getMessage());
			if(logger.isDebugEnabled()){
        		e.printStackTrace();
        	}
		}
		imgdata = null;
    }  
	
	private void showImage(String diskFilename, String fileName, HttpServletResponse response){
		byte imgdata[] = null;
		try {
			File f = new File(diskFilename);
			if(!f.exists()){
				logger.error("图片文件不存在，请重新选择：" + diskFilename);
				return;
			}
			
			FileInputStream hFile = new FileInputStream(f);
			int i = hFile.available();
			imgdata = new byte[i];//图片文件一般不会太大，所以一次读入
			hFile.read(imgdata);
			hFile.close();
		} catch (Exception e){
			logger.error("图片文件打开错误：" + e.getMessage());
			if(logger.isDebugEnabled()){
        		e.printStackTrace();
        	}
			return;
		}
		
		if (imgdata == null || imgdata.length <= 0) {
			logger.info("读取图片文件时数据错误");
			return;
		}
		
		String fileType = fileName.substring(fileName.lastIndexOf('.') + 1);
		
		response.setHeader("Cache-Control", "private");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Connection", "Keep-Alive");
		response.setHeader("Proxy-Connection", "Keep-Alive");
		response.setHeader("Content-Disposition", "inline; filename="+fileName);
		try {
			if(fileType != null && fileType.length() >= 0){
				response.setContentType("image/"+fileType);
			}
			else{
				response.setContentType("image/*");
			}
			
			OutputStream out = response.getOutputStream();
			out.write(imgdata);
			response.flushBuffer();
		} catch (Exception e) {
			logger.error("图片文件写入返回流错误：" + e.getMessage());
			if(logger.isDebugEnabled()){
        		e.printStackTrace();
        	}
		}
		imgdata = null;
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
