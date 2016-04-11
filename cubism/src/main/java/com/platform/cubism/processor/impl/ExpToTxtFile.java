package com.platform.cubism.processor.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import com.platform.cubism.SystemConfig;
import com.platform.cubism.base.CArray;
import com.platform.cubism.base.CStruc;
import com.platform.cubism.base.Json;
import com.platform.cubism.base.JsonFactory;
import com.platform.cubism.processor.ExpProcessor;
import com.platform.cubism.util.StringUtils;

public class ExpToTxtFile implements ExpProcessor{
	private String batchno;
	private String filename;
	private String filepath;
	private String filepathname;
	File file;
	private OutputStreamWriter osWriter;
	
	public boolean start(Json in) {
		batchno = in.getField("seq.seqno").getValue();
		filename = "dzt"+batchno+".call";
		filepath = SystemConfig.getDownloadPath();
		filepathname = StringUtils.getFilePathName(filepath,filename);
		
		return writeSummary(in);
	}

	public boolean process(CArray rs) throws Exception {
		if(rs == null || rs.isEmpty()){
			return false;
		}
		StringBuilder sb = new StringBuilder();
		try {
			for(CStruc stc : rs.getRecords()){
				sb.append(stc.getField("cardno").getValue()).append(",\t");
				sb.append(stc.getField("pswd").getValue()).append(",\t");
				sb.append(stc.getField("parvalue").getValue()).append(",\t");
				sb.append(stc.getField("effectivedata").getDateValue("y年m月d日")).append(",\t");
				sb.append(stc.getField("createdate").getDateValue("y年m月d日")).append("\n");
				osWriter.write(sb.toString());
				sb.delete(0, sb.length()-1);
			}
		} catch (Throwable e) {
			throw new Exception(e);
		}
		
		return true;
	}

	public void end() {
		try {
			osWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			try {
				osWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void end(Throwable e) {
		try {
			osWriter.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		file.deleteOnExit();
		file = null;
	}
	
	public Json getResult() {
		Json json = JsonFactory.create();
		json.addField("filename", filename);
		
		return json;
	}

	private boolean writeSummary(Json in){
		file = new File(filepathname);
		if(file.exists()){
			return false;
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append(batchno).append("|").append(in.getField("in.total").getValue()).append("|");
		CArray ca = in.getArray("in.card");
		for(CStruc stc : ca.getRecords()){
			sb.append(stc.getField("productid").getValue()).append(",");
			sb.append(stc.getField("parvalueno").getValue()).append(",");
			sb.append(stc.getField("startcardno").getValue()).append(",");
			sb.append(stc.getField("quantity").getValue()).append(",");
			sb.append(stc.getField("endcardno").getValue()).append(";");
		}
		sb.append("\n");
		try {
			osWriter = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
			osWriter.write(sb.toString());
		} catch (Throwable e) {
			//e.printStackTrace();
			try {
				osWriter.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			file.deleteOnExit();
			file = null;
			return false;
		}
		return true;
	}
}
