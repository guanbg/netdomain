package com.platform.cubism.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.platform.cubism.CubismException;
import com.platform.cubism.base.CArray;
import com.platform.cubism.base.CStruc;
import com.platform.cubism.base.Json;
import com.platform.cubism.base.JsonFactory;
import com.platform.cubism.service.CustomService;
import com.platform.cubism.service.ServiceFactory;
import com.platform.cubism.struc.MsgLevel;
import com.platform.cubism.struc.RetStatus;
import com.platform.cubism.util.HeadHelper;

public class ImportExcelService implements CustomService {
	public Json execute(Json in) throws CubismException {
		logger.debug(in.toString());
		String batchNumber = String.valueOf(System.nanoTime());//本次导入 批次号
		Json ret = JsonFactory.create();		
		ret.addField("batch_number", batchNumber);
		logger.debug("batchNumber="+batchNumber);
		
		String serviceName= in.getField("servicename").getValue();//导入数据时调用的数据库写入服务名称
		logger.debug("serviceName="+serviceName);
		
		String examination_batch_number= in.getField("examination_batch_number").getValue();//考试批次号		
		logger.debug("examination_batch_number="+examination_batch_number);
		
		String fileName= in.getField("filename").getValue();//用户上传的文件名称
		logger.debug("fileName="+fileName);
		
		String import_type= in.getField("import_type").getValue();//用户上传的文件类别
		logger.debug("import_type="+import_type);
		
		String pth= in.getField("filepath").getValue();//导入文件的本地磁盘路径及文件名
		logger.debug("pth="+pth);
		
		if (pth == null || pth.length() <= 0) {
			ret.addStruc(HeadHelper.createRetHead("com.platform.cubism.excel.service.ImportExcelService", "90001", "上送文件路径错误，操作被终止，请检查后再试！", MsgLevel.D,RetStatus.FAILED));
			return ret;
		}
		if (serviceName == null || serviceName.length() <= 0) {
			ret.addStruc(HeadHelper.createRetHead("com.platform.cubism.excel.service.ImportExcelService", "90002", "上送服务名称错误，操作被终止，请检查后再试！", MsgLevel.D,RetStatus.FAILED));
			return ret;
		}
		if (examination_batch_number == null || examination_batch_number.length() <= 0) {
			ret.addStruc(HeadHelper.createRetHead("com.platform.cubism.excel.service.ImportExcelService", "90003", "上送考试批次号错误，操作被终止，请检查后再试！", MsgLevel.D,RetStatus.FAILED));
			return ret;
		}
		
		File f = new File(pth);
		if (!f.exists()) {
			ret.addStruc(HeadHelper.createRetHead("com.platform.cubism.excel.service.ImportExcelService", "90004", "上送文件名称及路径不存在，操作被终止，请检查后再试！", MsgLevel.D,RetStatus.FAILED));
			return ret;
		}
		try{
			new Thread(new ImportExcelThread(f, batchNumber, serviceName, examination_batch_number, import_type, fileName), batchNumber+"-"+fileName).start();
			ret.addStruc(HeadHelper.createRetHead("com.platform.cubism.excel.service.ImportExcelService", "00000", "正在读取需要导入的数据，请稍后...", MsgLevel.B,RetStatus.SUCCESS));
			return ret;
		}
		catch(Throwable t){
			ret.addStruc(HeadHelper.createRetHead("com.platform.cubism.excel.service.ImportExcelService", "90009", "未知错误["+t.getMessage()+"]，操作被终止，请检查后再试！", MsgLevel.D,RetStatus.FAILED));
			return ret;
		}
	}

	private static class ImportExcelThread implements Runnable {
		private static String success = "0000";
		private File impFile = null;
		private Workbook workbook = null;
		private String batchNumber = null;
		private String serviceName = null;
		private String examination_batch_number = null;
		private String import_type = null;
		private String fileName = null;
		
		private void updateBathStatus(String code, String desc){
			Json in = JsonFactory.create("in");
			in.addField("state_code", code);
			in.addField("state_desc", desc);
			in.addField("batch_number", batchNumber);
			in.addField("examination_batch_number", examination_batch_number);
			in.addField("import_type", import_type);
			in.addField("filename", fileName);
			
			logger.debug(in.toString());
			ServiceFactory.executeService("import.batchstatus.save", in);
			
			try {
				workbook = null;
				impFile.delete();
			} catch (Exception e) {
				impFile = null;
				logger.debug(e.getMessage());
			}
		}
		
		public ImportExcelThread(File f, String batchNumber, String serviceName, String examination_batch_number, String import_type, String fileName) {
			this.impFile = f;
			this.batchNumber = batchNumber;
			this.serviceName = serviceName;
			this.examination_batch_number = examination_batch_number;
			this.import_type = import_type;
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
				updateBathStatus("1001", "读取文件内容错误:"+t.getMessage());
			} finally {
				try {
					inStream.close();
				} catch (IOException e) {
					;
				}
			}
			
		}

		public void run() {
			if (workbook == null) {
				return;
			}
			String isInit = "1";//导入开始标记，以便导入服务进行初始化操作
			String isEnd = "1";//导入结束标记，以便导入服务进行清理操作
			
			String status = null;
			Json in = JsonFactory.create("in");
			in.addField("batch_number", batchNumber);
			in.addField("examination_batch_number", examination_batch_number);
			in.addField("filename", fileName);
			in.addField("isinit", isInit);//是否清除数据标记
			
			try{
				Json ret = ServiceFactory.executeService(serviceName, in);
				if (!HeadHelper.isSuccess(ret)) {
					status = "导入服务初始化错误：["+HeadHelper.getRetHeadMsg(ret)+"]，终止导入，本次操作已撤销，请检查数据后重新导入！";
				}
			}catch(Throwable t){
				status = "导入服务初始化错误：["+t.getMessage()+"]，终止导入，本次操作已撤销，请检查数据后重新导入！";
			}
			if(status != null){
				updateBathStatus("1002", status);
				return;//撤销本次导入
			}
			in.remove("isinit");
			
			CArray rowArr = JsonFactory.createArray("excel");
			in.addArray(rowArr);
			
			for (int i = 0, j = workbook.getNumberOfSheets(); i < j; i++) {
				Sheet sheet = workbook.getSheetAt(i);
				int firstRowNum = sheet.getFirstRowNum();
				int lastRowNum = sheet.getLastRowNum();
				int currentRowNum = firstRowNum+1;//从第二行开始导入数据，假定第一行为标题或字段名称
				
				int max = 100;
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
						updateBathStatus("1003", status);
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
					updateBathStatus("1004", status);
					return;//撤销本次导入
				}
			}
			
			in.addField("isend", isEnd);//导入完毕进行数据清理处理
			in.remove("excel");
			try{
				Json ret = ServiceFactory.executeService(serviceName, in);
				if (!HeadHelper.isSuccess(ret)) {
					status = "导入服务清理数据阶段出错：["+HeadHelper.getRetHeadMsg(ret)+"]，终止导入，本次操作已撤销，请检查数据后重新导入！";
				}
			}catch(Throwable t){
				status = "导入服务清理数据阶段出错：["+t.getMessage()+"]，终止导入，本次操作已撤销，请检查数据后重新导入！";
			}
			
			if(status != null){
				updateBathStatus("1005", status);
				return;//撤销本次导入
			}
			
			updateBathStatus(success, "数据导入成功！");
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
