package com.platform.cubism.processor.impl;

import static com.platform.cubism.util.CubismHelper.getAppRootDir;

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

import com.platform.cubism.base.CArray;
import com.platform.cubism.base.CStruc;
import com.platform.cubism.base.Json;
import com.platform.cubism.processor.RptProcessor;

public class TableABReport implements RptProcessor{
	private int startRowIndex = 4;//第四行开始插入
	private int sourceRowIndex = startRowIndex;
	private Workbook workbook = null;
	
	public boolean start(Json in) {
		String pathname = getAppRootDir()+"WEB-INF/template/TABLE A+B.xls";
		File f = new File(pathname);
		if(!f.exists()){
			return false;
		}
		InputStream inStream = null;
		try {
			inStream = new FileInputStream(f);
			workbook = WorkbookFactory.create(inStream);
		} catch (Throwable e) {
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

	public boolean process(Json in) throws Exception {
		if(in == null || in.isEmpty()){
			return false;
		}
		if(workbook == null){
			return false;
		}
		CArray shipment = in.getArray("shipment");
		if(shipment == null || shipment.isEmpty()){
			return false;
		}
		int size = shipment.size();
		Row row = null;
		CStruc cs = null;
		
		CArray catblab = null;
		CArray catblaInsurance = null;
		CArray catblaCustoms = null;
		CArray catblaOthers = null;
		CStruc cstblab = null;
		CStruc cstblaInsurance = null;
		CStruc cstblaCustoms = null;
		CStruc cstblaOthers = null;
		
		/********************table a begin*************************/
		Sheet sheet = workbook.getSheet("A");
		insertRows(sheet, size, 35);
		for (int i = 0; i < size; i++) {
			cs = shipment.getRecord(i);
			row = sheet.getRow(sourceRowIndex+i);
			row.getCell(0).setCellValue(cs.getField("mth").getValue());
			row.getCell(1).setCellValue(cs.getField("shipmentno").getValue());
			row.getCell(2).setCellValue(cs.getField("shipnameen").getValue());
			row.getCell(3).setCellValue(cs.getField("shipname").getValue());
			row.getCell(4).setCellValue(cs.getField("voy").getValue());
			row.getCell(5).setCellValue(cs.getField("loadplantime").getDateValue("yyyy-mm-dd"));
			row.getCell(6).setCellValue(cs.getField("loadquantity").getValue());
			row.getCell(7).setCellValue(cs.getField("loadportname").getValue());
			row.getCell(8).setCellValue(cs.getField("unloadportname").getValue());
			row.getCell(9).setCellValue(cs.getField("arrivetime").getDateValue("yyyy-mm-dd hh:ii"));
			row.getCell(10).setCellValue(cs.getField("leavetime").getDateValue("yyyy-mm-dd hh:ii"));
			
			catblab = cs.getArray("detail");
			if(catblab != null && !catblab.isEmpty()){
				cstblab = catblab.getRecord(0);
				if(cstblab != null && !cstblab.isEmpty()){
					row.getCell(12).setCellValue(cstblab.getField("selling_a_seller").getValue());
					row.getCell(13).setCellValue(cstblab.getField("selling_a_priceterms").getValue());
					row.getCell(14).setCellValue(cstblab.getField("selling_a_buyer1").getValue());
					row.getCell(15).setCellValue(cstblab.getField("selling_a_priceterms2").getValue());
					row.getCell(16).setCellValue(cstblab.getField("selling_a_buyer2").getValue());
					row.getCell(17).setCellValue(cstblab.getField("selling_a_priceterms3").getValue());
					row.getCell(18).setCellValue(cstblab.getField("selling_a_buyer3").getValue());
					row.getCell(19).setCellValue(cstblab.getField("selling_a_salestermsex").getValue());
					
					row.getCell(21).setCellValue(cstblab.getField("freight_a_owner").getValue());
					row.getCell(22).setCellValue(cstblab.getField("freight_a_chrtr1").getValue());
					row.getCell(23).setCellValue(cstblab.getField("freight_a_chrtr2").getValue());
					row.getCell(24).setCellValue(cstblab.getField("freight_a_chrtr3").getValue());
					
					row.getCell(25).setCellValue(cstblab.getField("loading_a_loading").getValue());
					row.getCell(26).setCellValue(cstblab.getField("discharging_a_discharging").getValue());
				}
			}
			catblaInsurance = cs.getArray("detail0");
			if(catblaInsurance != null && !catblaInsurance.isEmpty()){
				cstblaInsurance = catblaInsurance.getRecord(0);
				if(cstblaInsurance != null && !cstblaInsurance.isEmpty()){
					row.getCell(27).setCellValue(cstblaInsurance.getField("insurancepayer").getValue());
					row.getCell(28).setCellValue(cstblaInsurance.getField("insurancecompany").getValue());
				}
			}
			catblaCustoms = cs.getArray("detail1");
			if(catblaCustoms != null && !catblaCustoms.isEmpty()){
				cstblaCustoms = catblaCustoms.getRecord(0);
				if(cstblaCustoms != null && !cstblaCustoms.isEmpty()){
					row.getCell(29).setCellValue(cstblaCustoms.getField("customname").getValue());
					row.getCell(30).setCellValue(cstblaCustoms.getField("customagencyname").getValue());
					row.getCell(31).setCellValue(cstblaCustoms.getField("ciagencyname").getValue());
				}
			}
			catblaOthers = cs.getArray("detail2");
			if(catblaOthers != null && !catblaOthers.isEmpty()){
				for(int j=0; j<catblaOthers.size(); j++){
					cstblaOthers = catblaOthers.getRecord(0);
					if(cstblaOthers != null && !cstblaOthers.isEmpty()){
						getCell(row,32+(j*3)).setCellValue(cstblaCustoms.getField("shipagencyname").getValue());
						getCell(row,33+(j*3)).setCellValue(cstblaCustoms.getField("payer").getValue());
						getCell(row,34+(j*3)).setCellValue(cstblaCustoms.getField("feedesc").getValue());
					}
				}
			}
		}
		/********************end table a*************************/
		
		/********************table b begin*************************/
		sheet = workbook.getSheet("B");
		insertRows(sheet, size, 23);
		for (int i = 0; i < size; i++) {
			cs = shipment.getRecord(i);
			row = sheet.getRow(sourceRowIndex+i);
			row.getCell(0).setCellValue(cs.getField("mth").getValue());
			row.getCell(1).setCellValue(cs.getField("shipmentno").getValue());
			catblab = cs.getArray("detail");
			if(catblab != null && !catblab.isEmpty()){
				cstblab = catblab.getRecord(0);
				if(cstblab != null && !cstblab.isEmpty()){
					row.getCell(2).setCellValue(cstblab.getField("selling_b_sellerzie").getDoubleValue());
					row.getCell(3).setCellValue(cstblab.getField("selling_b_ziedy").getDoubleValue());
					row.getCell(4).setCellValue(cstblab.getField("selling_b_zieendbuyer").getDoubleValue());
					row.getCell(5).setCellValue(cstblab.getField("selling_b_dyendbuyer").getDoubleValue());
					row.getCell(6).setCellValue(cstblab.getField("selling_b_dydh").getDoubleValue());
					
					row.getCell(7).setCellValue(cstblab.getField("freight_b_ownerwc").getDoubleValue());
					row.getCell(8).setCellValue(cstblab.getField("freight_b_wcdy").getDoubleValue());
					row.getCell(9).setCellValue(cstblab.getField("freight_b_wczsh").getDoubleValue());
					row.getCell(10).setCellValue(cstblab.getField("freight_b_zshzie").getDoubleValue());
					
					row.getCell(11).setCellValue(cstblab.getField("loading_b_zieseller").getDoubleValue());
					row.getCell(12).setCellValue(cstblab.getField("loading_b_ownerwc").getDoubleValue());
					
					row.getCell(13).setCellValue(cstblab.getField("discharging_b_ownerwc").getDoubleValue());
					row.getCell(14).setCellValue(cstblab.getField("discharging_b_dyreceiver").getDoubleValue());
					row.getCell(15).setCellValue(cstblab.getField("discharging_b_zieseller").getDoubleValue());
					row.getCell(16).setCellValue(cstblab.getField("discharging_b_ziereceiver").getDoubleValue());
					
					row.getCell(17).setCellValue(cstblab.getField("insurance_b_amount").getDoubleValue());
					
					row.getCell(18).setCellValue(cstblab.getField("cgotax_b_valueaddtax").getDoubleValue());
					row.getCell(19).setCellValue(cstblab.getField("cgotax_b_customtax").getDoubleValue());
					row.getCell(20).setCellValue(cstblab.getField("cgotax_b_cgoclearancefee").getDoubleValue());
					row.getCell(21).setCellValue(cstblab.getField("cgotax_b_cgoinspectionfee").getDoubleValue());
					
					row.getCell(22).setCellValue(cstblab.getField("others_b_amount").getValue());
				}
			}
		}
		/********************end table b*************************/
		
		sourceRowIndex += size;
		return true;
	}

	public void end(HttpServletResponse response) {
		if(workbook == null){
			return ;
		}
		response.setHeader("Pragma", "No-cache");   
		response.setHeader("Cache-Control", "no-cache"); 
		/*
		Response.SetContentType用于设置输出的文档MIME类型，默认为text/html，是HTML文档的类型。
		如果需要生成JPEG类型，就要设置成image/jpeg：Response.SetContentType = "image/jpeg"
		常用的MIME类型还有
		image/gif（GIF图片）、
		image/bitmap（BMP图象）、
		text/plain（文本文档TXT）、
		text/vnd.wap.wml（手机WAP页面）
		text/html HTML 
		text/plain          TXT 
		text/xml             XML
		text/json           json字符串

		序号	内容类型							文件扩展名		描述
		1	application/msword				doc				Microsoft Word
		2	appication/powerpoint			ppt				Microsoft Powerpoint
		3	application/octet-stream bin	dms lha lzh exe class		可执行程序
		4	application/pdf					pdf				Adobe Acrobat
		5	application/postscript			ai eps ps		PostScript
		6	appication/rtf					rtf				rtf 格式
		7	appication/x-compress			z				unix 压缩文件
		8	application/x-gzip				gz				gzip
		9	application/x-gtar				gtar				tar 文档 (gnu 格式 )
		10	application/x-shockwave-flash	swf				MacroMedia Flash
		11	application/x-tar				tar				tar(4.3BSD)
		12	application/zip					zip				winzip
		13	audio/basic						au snd			sun/next 声音文件
		14	audio/mpeg						mpeg mp2		Mpeg 声音文件
		15	audio/x-aiff					mid midi rmf	Midi 格式
		16	audio/x-pn-realaudio			ram ra			Real Audio 声音
		17	audio/x-pn-realaudio-plugin		rpm				Real Audio 插件
		18	audio/x-wav						wav				Microsoft Windows 声音
		19	image/cgm						cgm				计算机图形元文件
		20	image/gif						gif				COMPUSERVE GIF 图像
		21	image/jpeg						jpeg jpg jpe	JPEG 图像
		22	image/png						png				PNG 图像
		
		//response.setContentType("application/x-msdownload");
		//response.setContentType("application/octet-stream");
		**/
		
		response.setContentType("application/msexcel; charset=UTF-8");
		response.setHeader("Content-Disposition", "attachment; filename=TABLE_A+B.xls");
		try {
			/*Sheet sheet = workbook.getSheet("A");
			sheet.removeRow(sheet.getRow(startRowIndex-1));
			sheet = workbook.getSheet("B");
			sheet.removeRow(sheet.getRow(startRowIndex-1));*/
			workbook.write(response.getOutputStream());
			response.flushBuffer();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void end(Throwable e) {
		if(workbook == null){
			return ;
		}
		workbook = null;
	}
	private Cell getCell(Row row, int idx){
		Cell cell = row.getCell(idx);
		if(cell == null){
			cell = row.createCell(idx);
		}
		return cell;
	}
	private void insertRows(Sheet sheet, int rows, int cols) {
		sheet.shiftRows(sourceRowIndex, sheet.getLastRowNum(), rows, true, false);

		Row sourceRow = sheet.getRow(sourceRowIndex+rows);
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
