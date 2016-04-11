package com.platform.cubism.processor.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.platform.cubism.base.Json;
import com.platform.cubism.processor.RptProcessor;

public abstract class AbstractRptProcessor implements RptProcessor{
	protected Logger logger = LoggerFactory.getLogger(getClass());
	protected int sourceRowIndex;//第X行开始插入
	private String tmplPathName;
	private String expFileName;
	private String fileSaveName;
	private String contentType;
	protected Workbook workbook;
	
	public AbstractRptProcessor(String tmplPathName, int startRow, String expFileName){
		this(tmplPathName, startRow, expFileName, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
	}
	
	public AbstractRptProcessor(String tmplPathName, int startRow, String expFileName, String contentType){
		this.tmplPathName = tmplPathName;
		this.sourceRowIndex = startRow;
		this.expFileName = expFileName;
		this.contentType = contentType;
	}
	
	public boolean start(Json in) {
		fileSaveName = in.getFieldValue("filesavename");
		
		File f = new File(tmplPathName);
		if(!f.exists()){
			return false;
		}
		InputStream inStream = null;
		try {
			inStream = new FileInputStream(f);
			workbook = WorkbookFactory.create(inStream);
		} catch (Throwable e) {
			workbook = null;
			return false;
		}
		finally{
			try {
				inStream.close();
			} catch (IOException e) {
				;
			}
		}
		
		return true;
	}

	public void end(HttpServletResponse response) {
		if(workbook == null){
			return ;
		}
		response.setHeader("Pragma", "No-cache");   
		response.setHeader("Cache-Control", "no-cache"); 
		response.setContentType(contentType);
		try {
			if(fileSaveName != null && fileSaveName.length() > 0){
				response.setHeader("Content-Disposition", "inline; filename=" +  new String(fileSaveName.getBytes("utf-8"), "ISO8859-1"));//界面传输过来的，不需要转码
			}
			else{
					//String fileName = java.net.URLEncoder.encode(expFileName,"ISO-8859-1");
					response.setHeader("Content-Disposition", "attachment; filename=" +new String(expFileName.getBytes("UTF-8"),"ISO-8859-1"));
			}
		} catch (Exception e) {
			logger.error("文件名称字符编码转换出错：" + e.getMessage());
			if(logger.isDebugEnabled()){
				e.printStackTrace();
			}
		}
		try {
			workbook.write(response.getOutputStream());
			response.flushBuffer();
		} catch (IOException e) {
			if(logger.isDebugEnabled()){
				logger.debug("返回导出文件出错：" + e.getMessage());
				e.printStackTrace();
			}
			else{
				logger.error("返回导出文件出错：" + e.getMessage());
			}
		}
	}

	public void end(Throwable e) {
		workbook = null;
	}
	
	public Cell getCell(Row row, int idx){
		Cell cell = row.getCell(idx);
		if(cell == null){
			cell = row.createCell(idx);
		}
		return cell;
	}
	public void setCellValue(Row row, int idx, String value){
		if(row == null){
			return;
		}
		Cell cell = getCell(row,idx);
		cell.setCellValue(value);
	}
	protected void insertRows(Sheet sheet, int rows, int cols) {
		sheet.shiftRows(sourceRowIndex, sheet.getLastRowNum(), rows, true, false);//sheet.shiftRows(a, b, c)将第 a 行到第 b 行向下移动 c 行

		Row sourceRow = sheet.getRow(sourceRowIndex+rows);//首行
		if (sourceRow == null) {
			sourceRow = sheet.createRow(sourceRowIndex+rows);
		}
		for (int i = 0; i < rows; i++) {
			Row targetRow = null;
			Cell sourceCell = null;
			Cell targetCell = null;
			
			targetRow = sheet.createRow(sourceRowIndex + i);
			targetRow.setHeight(sourceRow.getHeight());

			for (int m = 0; m < cols; m++) {
				sourceCell = sourceRow.getCell(m);
				targetCell = targetRow.createCell(m);
				
				if(sourceCell == null){
					continue;
				}

				// 风格一样
				targetCell.setCellStyle(sourceCell.getCellStyle());
				targetCell.setCellType(sourceCell.getCellType());
				if(sourceCell.getCellType() == Cell.CELL_TYPE_FORMULA)
				targetCell.setCellFormula(sourceCell.getCellFormula());
				//targetCell.setCellValue(2);
			}
		}
	}
}
