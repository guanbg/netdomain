package com.platform.cubism.front.file;

import static com.platform.cubism.util.CubismHelper.getUserPath;
import static com.platform.cubism.base.JsonFactory.createStruc;
import static com.platform.cubism.base.JsonFactory.createField;
import static com.platform.cubism.util.MultipartUtils.extractFileName;
import static com.platform.cubism.util.MultipartUtils.extractFilePathName;
import static com.platform.cubism.util.MultipartUtils.saveStreamToFile;
import static com.platform.cubism.util.StringUtils.hasText;

import java.io.IOException;
import java.util.Calendar;
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
import com.platform.cubism.base.CField;
import com.platform.cubism.base.Json;
import com.platform.cubism.base.JsonFactory;
import com.platform.cubism.struc.MsgLevel;
import com.platform.cubism.struc.RetStatus;
import com.platform.cubism.util.HeadHelper;
import com.platform.cubism.util.SecurityHelper;

@MultipartConfig(// 文件上传配置项
	// location = "c:\\temp", // JDK缓存路径  System.getProperty("java.io.tmpdir")
	fileSizeThreshold = 10240 * 1024 * 10,//如果文件大小为10M以下，则一次性读入内存，否则存入磁盘临时文件
	maxFileSize = 1024L * 1024L * 1024L, // 每一个文件的最大值
	maxRequestSize = 1024L * 1024L * 2048L // 一次上传最大值，若每次只能上传一个文件，则设置maxRequestSize意义不大
)
@WebServlet(name = "FileUploadDisk", urlPatterns = { "*.sys_diskfile_upload" })
public class UploadDisk extends HttpServlet{
	private static final long serialVersionUID = 14669140059158737L;
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private static final String CONTENT_DISPOSITION = "Content-Disposition";
	private static final String rootPath = SystemConfig.getUploadPath();
	
	public void init(ServletConfig config) {
		config.getServletContext();
		String location = SystemConfig.getJdkUploadCacheLocation();
		if (logger.isDebugEnabled()) {
			logger.debug("UploadDisk: Jdk Upload Cache Location=" + location);
		}
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		Collection<Part> parts = request.getParts();
		String maxfilesize = request.getParameter("maxfilesize");//文件最大上传大小
		
		if(parts.isEmpty()){
			logger.error("文件上传错误：没有上传文件内容，请重新上传");
			writeResponse(HeadHelper.createRetHead("sys.upload.disk.files", "10001", "保存文件时失败：没有上传文件内容", "D", "F"), response);
			return;
		}
		else{
			CArray files = JsonFactory.createArray("files");
			try {
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
				String filePath = getUserPath(request,'/');//系统内部文件路径统一使用正斜杠
				
				for (Part part : parts) {
					String filename = extractFileName(extractFilePathName(part.getHeader(CONTENT_DISPOSITION)));
					if (!hasText(filename)) {
						part.delete();
						continue;
					}
					String filetype = filename.substring(filename.lastIndexOf('.') + 1);
					if(filetype != null && filetype.length() > 0){
						filetype = filetype.toLowerCase();
					}
					
					filename = filename.replaceAll("/", "\\\\");//特殊字符转换，防止路径错误
					
					Calendar cal = Calendar.getInstance();
					int year = cal.get(Calendar.YEAR);//获取年份
			        int month = cal.get(Calendar.MONTH)+1;//获取月份 
			        int day = cal.get(Calendar.DATE);//获取日 
			        
			        String dir = year+"/"+month+"/"+day+"/";
					String fileIdentify = String.valueOf(System.currentTimeMillis()) + "A" + SecurityHelper.bytes2HexString(filename.getBytes());//防止重名
					String diskFilename = rootPath + dir + fileIdentify;
					long fileSize = saveStreamToFile(part.getInputStream(), diskFilename, maxSize);
					part.delete();
					
					files.add(createStruc("files",new CField[]{
						createField("filename",filename),
						createField("fileidentify",fileIdentify),
						createField("filetype",filetype),
						createField("filepath",filePath),
						createField("filesize", String.valueOf(fileSize))
					}));
				}
				
				if (logger.isDebugEnabled()) {
					logger.debug(files.toString());
				}
			} catch (Throwable t) {
				logger.error("文件上传错误：", t);
				if(logger.isDebugEnabled()){
					t.printStackTrace();
				}
				response.setStatus(501);
				writeResponse(HeadHelper.createRetHead("sys.upload.disk.files", "10009", "保存文件时失败："+t.getMessage(), "D", "F"), response);
			}
			
			if(files == null || files.isEmpty()){
				logger.error("文件上传错误：没有上传文件内容，请重新上传");
				writeResponse(HeadHelper.createRetHead("sys.upload.disk.files", "10008", "保存文件时失败：没有上传文件内容", "D", "F"), response);
				return;
			}
			
			Json ret =JsonFactory.create("in");
			ret.addArray(files);
			ret.addStruc(HeadHelper.createRetHead("sys.upload.disk.files", "00000", "文件上传成功，", MsgLevel.B, RetStatus.SUCCESS));
			ret.getObject().setName(null);// 返回报文的顶层直接为:{......}的形式
			writeResponse(ret.toJson(),response);
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
}
