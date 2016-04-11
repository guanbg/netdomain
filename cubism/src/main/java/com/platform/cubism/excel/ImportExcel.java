package com.platform.cubism.excel;

import static com.platform.cubism.util.StringUtils.getUUID;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.platform.cubism.CubismException;
import com.platform.cubism.SystemConfig;
import com.platform.cubism.base.CArray;
import com.platform.cubism.base.CStruc;
import com.platform.cubism.base.Json;
import com.platform.cubism.base.JsonFactory;
import com.platform.cubism.service.CustomService;
import com.platform.cubism.service.ServiceFactory;
import com.platform.cubism.struc.MsgLevel;
import com.platform.cubism.struc.RetStatus;
import com.platform.cubism.util.HeadHelper;
import com.platform.cubism.util.SecurityHelper;

public class ImportExcel implements CustomService{
	private static final String rootPath = SystemConfig.getUploadPath();
	
	@Override
	public Json execute(Json in) throws CubismException {
		Json ret = JsonFactory.create();		
		String batchNumber = String.valueOf(System.nanoTime());//本次导入 批次号，系统自动生成
		ret.addField("batch_number", batchNumber);
		
		String serviceName= in.getFieldValue("servicename");//导入数据时调用的数据库写入服务名称
		String fileId= in.getFieldValue("fileid");//文件ID
		
		if (serviceName == null || serviceName.length() <= 0) {
			ret.addStruc(HeadHelper.createRetHead("com.platform.cubism.excel.service.ImportExcel", "90001", "数据写入服务名称上送错误，操作被终止，请检查后再试！", MsgLevel.D,RetStatus.FAILED));
			return ret;
		}
		
		if (fileId == null || fileId.length() <= 0) {
			ret.addStruc(HeadHelper.createRetHead("com.platform.cubism.excel.service.ImportExcel", "90002", "文件ID上送错误，操作被终止，请检查后再试！", MsgLevel.D,RetStatus.FAILED));
			return ret;
		}
		
		fileId = fileId.toUpperCase();
		int a = fileId.indexOf('A');
		String dt = fileId.substring(0, a);
		String fileName = new String(SecurityHelper.hexStringToBytes(fileId.substring(a+1)));
		String fileType = fileName.substring(fileName.lastIndexOf('.') + 1);
		
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(Long.parseLong(dt));
		int year = cal.get(Calendar.YEAR);//获取年份
        int month = cal.get(Calendar.MONTH)+1;//获取月份 
        int day = cal.get(Calendar.DATE);//获取日 
        
        String dir = year+"/"+month+"/"+day+"/";
		String filePath = a < 0 ?fileId:dir+fileId;
		String diskFilename = rootPath  + filePath;
		
		
		if (logger.isDebugEnabled()) {
			logger.debug("下载文件信息：");
			logger.debug("batch_number=" + batchNumber);
			
			logger.debug("serviceName=" + serviceName);
			logger.debug("fileid=" + fileId);
			
			logger.debug("fileType=" + fileType);
			logger.debug("fileName=" + fileName);
			logger.debug("filePath=" + filePath);
			logger.debug("diskFilename=" + diskFilename);
		}
		
		File f = new File(diskFilename);
		if (!f.exists()) {
			ret.addStruc(HeadHelper.createRetHead("com.platform.cubism.excel.service.ImportExcel", "90003", "导入文件名称及路径不存在，操作被终止，请检查后再试！", MsgLevel.D,RetStatus.FAILED));
			return ret;
		}
		
		try{
			new Thread(new ImportExcelThread(f, batchNumber, serviceName, in, fileId,fileName), batchNumber+"-"+fileName).start();
			ret.addStruc(HeadHelper.createRetHead("com.platform.cubism.excel.service.ImportExcel", "00000", "正在读取EXCEL文件数据，请稍后...", MsgLevel.B,RetStatus.SUCCESS));
			return ret;
		}
		catch(Throwable t){
			ret.addStruc(HeadHelper.createRetHead("com.platform.cubism.excel.service.ImportExcel", "90009", "未知错误["+t.getMessage()+"]，操作被终止，请检查后再试！", MsgLevel.D,RetStatus.FAILED));
			return ret;
		}
	}
	
	private static class ImportExcelThread implements Runnable {
		private static String success = "00000";
		private File impFile = null;
		private Workbook workbook = null;
		private String batchNumber = null;
		private String serviceName = null;
		private Json in = null;
		private String fileId = null;
		private String fileName = null;
		
		public ImportExcelThread(File f, String batchNumber, String serviceName, Json in, String fileId, String fileName) {
			this.impFile = f;
			this.batchNumber = batchNumber;
			this.serviceName = serviceName;
			this.in = in;
			this.fileId = fileId;
			this.fileName = fileName;
			InputStream inStream = null;
			
			try {
				inStream = new FileInputStream(f);
				workbook = WorkbookFactory.create(inStream);
			} catch (Throwable t) {
				workbook = null;
				try {
					inStream.close();
				} catch (IOException e) {
					logger.debug(e.getMessage());
				}
				try {
					f.delete();
				} catch (Exception e) {
					logger.debug(e.getMessage());
				}
				updateBathStatus("10001", "读取文件内容错误:"+t.getMessage());
			} finally {
				try {
					inStream.close();
				} catch (IOException e) {
					;
				}
			}
			
		}
		
		@Override
		public void run() {
			if (workbook == null) {
				return;
			}
			String isInit = "1";//导入开始标记，以便导入服务进行初始化操作
			String isEnd = "1";//导入结束标记，以便导入服务进行清理操作
			
			String status = null;
			in.addField("batch_number", batchNumber);
			in.addField("filename", fileName);
			in.addField("isinit", isInit);//是否清除数据标记
			
			try{
				Json ret = ServiceFactory.executeService(serviceName, in);
				if (!HeadHelper.isSuccess(ret)) {
					status = "导入服务初始化错误：["+HeadHelper.getRetHeadMsg(ret)+"]，终止导入，本次操作已撤销，请检查数据后重新导入！";
				}
			}catch(Throwable t){
				status = "导入服务初始化未知错误：["+t.getMessage()+"]，终止导入，本次操作已撤销，请检查数据后重新导入！";
			}
			if(status != null){
				updateBathStatus("10002", status);
				return;//撤销本次导入
			}
			in.remove("isinit");
			
			CArray rowArr = JsonFactory.createArray("excel");
			in.addArray(rowArr);
			
			for (int i = 0, j = workbook.getNumberOfSheets(); i < j; i++) {
				Sheet sheet = workbook.getSheetAt(i);
				in.addField("sheetname", sheet.getSheetName());
				in.addField("sheetid", getUUID());
				
				int firstRowNum = sheet.getFirstRowNum();
				int lastRowNum = sheet.getLastRowNum();
				int currentRowNum = firstRowNum+1;//从第二行开始导入数据，假定第一行为标题或字段名称
				
				int max = 300;
				while (currentRowNum <= lastRowNum){
					status = null;
					Row row = sheet.getRow(currentRowNum++);
					if(row == null || row.cellIterator() == null){
						continue;
					}
					CStruc cs = toStruc(row);
					if(cs == null || cs.isEmpty()){
						continue;
					}
					cs.addField("rowid", getUUID());
					cs.addField("rownum", String.valueOf(currentRowNum));
					rowArr.add(cs);
					if(rowArr.size() >= max){
						try{
							Json ret = ServiceFactory.executeService(serviceName, in);
							if (!HeadHelper.isSuccess(ret)) {
								status = "从"+(currentRowNum-max)+"行到"+currentRowNum+"行中写入数据错误：["+HeadHelper.getRetHeadMsg(ret)+"]，终止导入，本次操作已撤销，请检查数据后重新导入！";
							}
						}catch(Throwable t){
							status = "从"+(currentRowNum-max)+"行到"+currentRowNum+"行中写入数据错误：["+t.getMessage()+"]，终止导入，本次操作已撤销，请检查数据后重新导入！";
						}
						rowArr.clear();
					}
					
					if(status != null){
						updateBathStatus("10003", status);
						return;//撤销本次导入
					}
				}
				if(!rowArr.isEmpty()){
					try{
						Json ret = ServiceFactory.executeService(serviceName, in);
						if (!HeadHelper.isSuccess(ret)) {
							status = "从"+(currentRowNum-rowArr.size())+"行到"+currentRowNum+"行中写入数据错误：["+HeadHelper.getRetHeadMsg(ret)+"]，终止导入，本次操作已撤销，请检查数据后重新导入！";
						}
					}catch(Throwable t){
						status = "从"+(currentRowNum-rowArr.size())+"行到"+currentRowNum+"行中写入数据错误：["+t.getMessage()+"]，终止导入，本次操作已撤销，请检查数据后重新导入！";
					}
					rowArr.clear();
				}
				if(status != null){
					updateBathStatus("10004", status);
					return;//撤销本次导入
				}
			}
			
			in.addField("isend", isEnd);//导入完毕进行数据清理处理
			in.remove("excel");
			in.remove("sheetname");
			
			try{
				Json ret = ServiceFactory.executeService(serviceName, in);
				if (!HeadHelper.isSuccess(ret)) {
					status = "导入服务清理数据阶段出错：["+HeadHelper.getRetHeadMsg(ret)+"]，终止导入，本次操作已撤销，请检查数据后重新导入！";
				}
			}catch(Throwable t){
				status = "导入服务清理数据阶段出错：["+t.getMessage()+"]，终止导入，本次操作已撤销，请检查数据后重新导入！";
			}
			
			if(status != null){
				updateBathStatus("10005", status);
				return;//撤销本次导入
			}
			
			updateBathStatus(success, "数据导入成功！");
			
		}
		
		private void updateBathStatus(String code, String desc){
			Json status = JsonFactory.create("in");
			status.addField("state_code", code);
			status.addField("state_desc", desc);
			status.addField("batch_number", batchNumber);
			status.addField("filename", fileName);
			status.addField("fileid", fileId);
			status.addField("user_id", in.getFieldValue("syshead.userid"));
			
			logger.debug(status.toString());
			ServiceFactory.executeService("sys.import.status.save", status);
			
			try {
				workbook = null;
				impFile.delete();
			} catch (Exception e) {
				impFile = null;
				logger.debug(e.getMessage());
			}
		}
		
		private CStruc toStruc(Row row) {
			if(row == null || row.cellIterator() == null){
				return null;
			}
			CStruc cs = JsonFactory.createStruc();
			Iterator<Cell> cells = row.cellIterator();
			Cell cell;
			String cellValue;
			StringBuilder sb = new StringBuilder();
			while (cells.hasNext()) {
				cell = cells.next();
				cellValue = getCellValue(cell);
				cs.addField(getCellIndexName(cell.getColumnIndex()), cellValue);
				sb.append(cellValue.trim());
			}
			if(sb.toString().length() <= 0){
				return null;
			}
			return cs;
		}
		
		private String getCellValue(Cell cell){
			String value = "";
			int cellType = cell.getCellType();
			try{
				switch(cellType){
				case Cell.CELL_TYPE_NUMERIC:value = String.valueOf(cell.getNumericCellValue());break;//单元格为数字型
				case Cell.CELL_TYPE_STRING:value = cell.getStringCellValue();break;//单元格为字符型
				case Cell.CELL_TYPE_FORMULA:value = cell.getCellFormula();break;//单元格为计算公式型
				case Cell.CELL_TYPE_ERROR:value = String.valueOf(cell.getErrorCellValue());break;//单元格为错误型
				case Cell.CELL_TYPE_BLANK:value = "";break;//单元格为空
				default:
					if(HSSFDateUtil.isCellDateFormatted(cell)){//单元格为日期型
						value = cell.getDateCellValue().toString();
					}
					else{
						value = cell.getRichStringCellValue().getString();
					}
				}
			}catch(Throwable t){
				value = "";
			}
			return value.trim();
		}

		private String getCellIndexName(int columnIdx) {//columnIdx从0开始计算
			int count = 26;
			int mod = columnIdx % count;
			int div = columnIdx / count;

			char A = 'A';
			if (div == 0) {
				return String.valueOf((char)(A + mod));
			} else {
				String perfix = "";
				for (int i = 0, j = div / count - 1; i < j; i++) {
					perfix += String.valueOf((char)(A + i));
				}
				perfix += String.valueOf((char)(A + ((div-1) % count)));
				
				return perfix + getCellIndexName(mod);
			}
		}
	}
}
