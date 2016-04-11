package com.platform.cubism.front.file;

import static com.platform.cubism.util.HeadHelper.checkSysHead;
import static com.platform.cubism.util.MultipartUtils.extractFilePathName;
import static com.platform.cubism.util.MultipartUtils.extractFileName;
import static com.platform.cubism.util.MultipartUtils.saveStreamToFile;
import static com.platform.cubism.util.StringUtils.hasText;
import static com.platform.cubism.util.FileUtils.delFile;
import static com.platform.cubism.util.CubismHelper.getUserPath;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.platform.cubism.SystemConfig;
import com.platform.cubism.base.CArray;
import com.platform.cubism.base.CStruc;
import com.platform.cubism.base.Json;
import com.platform.cubism.base.JsonFactory;
import com.platform.cubism.service.ServiceFactory;
import com.platform.cubism.struc.MsgLevel;
import com.platform.cubism.struc.RetStatus;
import com.platform.cubism.util.HeadHelper;

@MultipartConfig(// 文件上传配置项
// location = "c:\\temp", // JDK缓存路径  System.getProperty("java.io.tmpdir")
fileSizeThreshold = 10240 * 1024 * 10,//如果文件大小为10M以下，则一次性读入内存，否则存入磁盘临时文件
maxFileSize = 1024L * 1024L * 1024L, // 每一个文件的最大值
maxRequestSize = 1024L * 1024L * 2048L // 一次上传最大值，若每次只能上传一个文件，则设置maxRequestSize意义不大
)
@WebServlet(name = "FileUpload", urlPatterns = { "/sys.upload.files" })
public class Upload extends HttpServlet {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	private static final long serialVersionUID = 3121576465016784388L;
	private static final String CONTENT_DISPOSITION = "Content-Disposition";
	private static final String rootPath = SystemConfig.getUploadPath();
	
	public void init(ServletConfig config) {
		config.getServletContext();
		String location = SystemConfig.getJdkUploadCacheLocation();
		if (logger.isDebugEnabled()) {
			logger.debug("Jdk Upload Cache Location=" + location);
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		Collection<Part> parts = request.getParts();
		String parentid = request.getParameter("parentid");
		String maxfilesize = request.getParameter("maxfilesize");//文件最大上传大小
		String isoverwrite = request.getParameter("isoverwrite");//是否覆盖相同名称的文件
		String isonlydisk = request.getParameter("isonlydisk");//是否仅保存到磁盘，不写入数据库中
		String istempdir = request.getParameter("istempdir");//是否保存到临时目录中
		CArray ca = JsonFactory.createArray("files");
		CStruc cs = null;
		if(parts.isEmpty()){
			logger.error("文件上传错误：没有上传文件内容，请重新上传");
			writeResponse(HeadHelper.createRetHead("sys.upload.files", "10003", "保存文件时失败：没有上传文件内容", "D", "F"), response);
			return;
		}
		if (!parts.isEmpty()) {
			try {
				for (Part part : parts) {
					String filename = extractFileName(extractFilePathName(part.getHeader(CONTENT_DISPOSITION)));
					if (!hasText(filename)) {
						part.delete();
						continue;
					}
					String filePath = getUserPath(request);
					String filetype = filename.substring(filename.lastIndexOf('.') + 1);
					if(filetype != null && filetype.length() > 0){
						filetype = filetype.toLowerCase();
					}
					String fileIdentify = String.valueOf(System.nanoTime());
					if(!"1".equals(isonlydisk) && "1".equalsIgnoreCase(isoverwrite)){
						String id = overWrite(filename,filetype,filePath);
						if(id != null && id.length() > 0){
							fileIdentify = id;
						}
					}
					if("1".equalsIgnoreCase(istempdir)){
						filePath = SystemConfig.getUploadTempPath() + filePath;
					}
					long maxSize = -1;
					if(maxfilesize != null && maxfilesize.length() > 0){
						try{
							maxSize = Long.parseLong(maxfilesize);
						}catch(Throwable t){
							logger.info("maxfilesize 参数转化为 长整型 出错，忽略该参数："+maxfilesize);
							if(logger.isDebugEnabled()){
								t.printStackTrace();
							}
							maxSize = -1;
						}	
					}
					String diskFilename = rootPath + filePath + fileIdentify;
					InputStream is = part.getInputStream();
					long fileSize = saveStreamToFile(is, diskFilename, maxSize);
					part.delete();
					
					if(!"1".equalsIgnoreCase(isoverwrite)){
						cs = JsonFactory.createStruc("files");//和数组的名称一致，以便自动处理数据
						cs.addField("filename", filename);
						cs.addField("fileidentify", fileIdentify);
						cs.addField("filetype", filetype);
						cs.addField("filepath", filePath);
						cs.addField("diskfilename", diskFilename);
						cs.addField("filesize", String.valueOf(fileSize));
						if (logger.isDebugEnabled()) {
							logger.debug(cs.toString());
						}
						ca.add(cs);
					}
				}
			} catch (Throwable t) {
				logger.error("文件上传错误：", t);
				if(logger.isDebugEnabled()){
					t.printStackTrace();
				}
				if(!ca.isEmpty()){
					for(CStruc s : ca.getRecords()){
						String pathname = s.getFieldValue("filepath") + s.getFieldValue("fileidentify");
						delFile(rootPath + pathname);
						logger.debug("=====>>>保存文件时失败，清除文件："+rootPath + pathname);
					}
					ca.clear();
				}
				//response.sendError(501, "文件上传错误："+t.getMessage());
				response.setStatus(501);
				writeResponse(HeadHelper.createRetHead("sys.upload.files", "10009", "保存文件时失败："+t.getMessage(), "D", "F"), response);
			}
		}
		
		if(ca == null || ca.isEmpty()){
			logger.error("文件上传错误：没有上传文件内容，请重新上传");
			writeResponse(HeadHelper.createRetHead("sys.upload.files", "10003", "保存文件时失败：没有上传文件内容", "D", "F"), response);
			return;
		}
		if("1".equals(isonlydisk)){//仅保存到磁盘，不写 数据库
			Json ret =JsonFactory.create("in");
			ret.addArray(ca);
			ret.addStruc(HeadHelper.createRetHead("sys.upload.files", "00000", "文件上传成功，", MsgLevel.B, RetStatus.SUCCESS));
			ret.getObject().setName(null);// 返回报文的顶层直接为:{......}的形式
			writeResponse(ret.toJson(),response);
			return;
		}
		
		try{
			Json in =JsonFactory.create("in");
			in.addField("parentid",parentid);
			in.addArray(ca);				
			checkSysHead(in, request);
			Json ret = ServiceFactory.executeService("sys.file.upload.service", in);
			ret.getObject().setName(null);// 返回报文的顶层直接为:{......}的形式
			
			if (logger.isDebugEnabled()) {
				if(HeadHelper.isFailed(ret)){
					logger.debug("=====>>>上传失败，返回报文："+ret);
				}else{
					logger.debug("=====>>>上传成功，返回报文："+ret);
				}
			}
			if(HeadHelper.isFailed(ret)){
				for(CStruc s : ca.getRecords()){
					String pathname = s.getFieldValue("filepath") + s.getFieldValue("fileidentify");
					delFile(rootPath + pathname);
					logger.debug("=====>>>写数据库时失败，清除文件："+rootPath + pathname);
				}
				//response.sendError(502, "写数据库时失败："+HeadHelper.getRetHeadFirstMsg(ret));
				response.setStatus(502);
				writeResponse(ret.toJson(),response);
			}
			else{
				writeResponse(ret.toJson(),response);
			}
		}catch(Throwable t){
			logger.error("=====>>>上传失败："+t.getMessage());
			if(logger.isDebugEnabled()){
				t.printStackTrace();
			}
			for(CStruc s : ca.getRecords()){
				String pathname = s.getFieldValue("filepath") + s.getFieldValue("fileidentify");
				delFile(rootPath + pathname);
				logger.debug("=====>>>写数据库时失败，清除文件："+rootPath + pathname);
			}
			//response.sendError(503, "上传失败："+t.getMessage());
			response.setStatus(503);
			writeResponse(HeadHelper.createRetHead("sys.upload.files", "10009", "写数据库时失败，"+t.getMessage(), "D", "F"), response);
		}
	}
	private void writeResponse(String s, HttpServletResponse response){
		try {
			response.setHeader("Cache-Control", "no-cache");
			response.setHeader("Pragma", "on-cache");
			response.setContentType("HTTP/1.1 204 NO Content/n/n");
			response.setContentType("text/html; charset=UTF-8");
		
			response.getWriter().print(s);
			response.flushBuffer();
		}catch(Throwable t){
			logger.error("=====>>>写入返回数据失败："+t.getMessage());
			if(logger.isDebugEnabled()){
				t.printStackTrace();
			}
		}
	}
	private String overWrite(String filename, String filetype, String filepath){
		Json in =JsonFactory.create("in");
		in.addField("filename",filename);
		in.addField("filetype",filetype);
		in.addField("filepath",filepath);
		try{
			Json ret = ServiceFactory.executeService("sys.file.overwrite.query", in);
			if(HeadHelper.isFailed(ret)){
				return null;
			}
			String fileidentify = ret.getArray("overwrite").getRecord(0).getFieldValue("fileidentify");
			delFile(rootPath + filepath + fileidentify);
			logger.debug("=====>>>删除要覆盖的文件："+rootPath + filepath + fileidentify);
			return fileidentify;
		}catch(Throwable t){
			logger.error("=====>>>文件上传：查询文件信息失败："+t.getMessage());
			if(logger.isDebugEnabled()){
				t.printStackTrace();
			}
		}
		return null;
	}
}