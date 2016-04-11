package com.platform.cubism.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.regex.Pattern;

import com.platform.cubism.SystemConfig;
import com.platform.cubism.base.CArray;
import com.platform.cubism.base.CField;
import com.platform.cubism.base.Json;
import com.platform.cubism.base.JsonFactory;

public class WebUtils {
	public static boolean hasIn(String[] src, String v){
		if(src != null && src.length > 0){
			for(String s : src){
				if((s == null || s.length() <= 0) && (v == null || v.length() <= 0)){
					return true;
				}
				else if(s != null && s.equalsIgnoreCase(v)){
					return true;
				}
			}
		}
		return false;
	}
	public static CArray strToArray(String name, String value) {
		return strToArray(name, value, ",");
	}
	public static CArray strToArray(String name, String value, String spliter) {
		if(name == null || name.length() <= 0 || value == null || value.length() <= 0){
			return null;
		}
		CArray ret = new CArray(name);
		if(spliter == null || spliter.length() <= 0){
			ret.add(new CField(name,value));
			return ret;
		}
		String[] values = value.split(spliter);
		for(String v : values){
			ret.add(new CField(name,v));
		}

		return ret;
	}
	
	public static Json zip(String distfilepath,String distfilename, String srcfilepath,String srcfilename) {
		Json ret = JsonFactory.create();
		Pattern pat = Pattern.compile(srcfilename);
		StringBuilder sb = new StringBuilder();
		try {
			
			File srcfile = new File(srcfilepath);
			for(File f : srcfile.listFiles()){
				if (f.isFile() && (srcfilename == null || srcfilename.length() <= 0 || pat.matcher(f.getName()).matches())) {//文件
					String s = null;
					BufferedReader br = new BufferedReader(new FileReader(f));
					while( (s = br.readLine()) != null){  
						sb.append(s).append("\r\n");
					}
					br.close();
				}
				f.delete();
			}
			
			if(sb.length()>0){
				BufferedWriter writer  = new BufferedWriter(new FileWriter(srcfilepath+distfilename.substring(0,distfilename.indexOf('.'))+".sql"));
				writer.write(sb.toString());
				writer.flush();
				writer.close();
				
				String zipFileName = SecurityHelper.bytes2HexString(distfilename.getBytes());
				FileUtils.zip(srcfilepath, distfilepath, zipFileName,false);
				FileUtils.delFile(srcfilepath);
				ret.addField("fileid", zipFileName);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return ret;
	}
	
	public static Json buildPath(String path) {
		return buildPath(path, true);
	}
	public static Json buildPath(String path,boolean addTime) {
		Json ret = JsonFactory.create();
		String fullpath = SystemConfig.getUploadPath() + path + "/";
		ret.addField("path", fullpath);
		if(addTime){
			fullpath += System.nanoTime() + "/";
			ret.addField("timepath", fullpath);
		}
		File f = new File(fullpath);
		if (!f.exists()) {
			f.mkdirs();
		}
		return ret;
	}
}
