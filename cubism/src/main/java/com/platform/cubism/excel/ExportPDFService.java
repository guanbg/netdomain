package com.platform.cubism.excel;

import java.io.File;
import java.io.FileOutputStream;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.platform.cubism.CubismException;
import com.platform.cubism.SystemConfig;
import com.platform.cubism.base.Json;
import com.platform.cubism.base.JsonFactory;
import com.platform.cubism.service.CustomService;
import com.platform.cubism.service.ServiceFactory;
import com.platform.cubism.struc.MsgLevel;
import com.platform.cubism.struc.RetStatus;
import com.platform.cubism.util.CubismHelper;
import com.platform.cubism.util.HeadHelper;
import com.platform.cubism.util.MultipartUtils;

public class ExportPDFService implements CustomService{
	private static final String rootPath = SystemConfig.getDownloadPath();
	public Json execute(Json in) throws CubismException {
		String serviceName= in.getField("servicename").getValue();//导出数据时调用的数据库查询服务
		
		logger.debug(in.toString());
		Json ret = ServiceFactory.executeService(serviceName, in);
		if (!HeadHelper.isSuccess(ret)) {
			return ret;
		}
		
		try {
			ret.addField("exportfilename", createPDF(ret));
		} catch (Throwable t) {
			logger.error("写入pdf文件时错误："+t.getMessage());
			if(logger.isDebugEnabled()){
				t.printStackTrace();
			}
			
			Json rt = JsonFactory.create();
			rt.addStruc(HeadHelper.createRetHead(serviceName, "10019", "写入pdf文件时错误："+t.getMessage(), MsgLevel.D, RetStatus.FAILED));
			return rt;
		}
		return ret;
	}
	
	private String createPDF(Json out) throws Throwable{
		String fileIdentify = String.valueOf(System.nanoTime());//
		String filepathname = SystemConfig.getUploadTempPath()+ fileIdentify;
		String diskFilename = rootPath + filepathname;
		String fontpath = this.getClass().getClassLoader().getResource("/").getPath(); // 返回WEB-INF/class 目录
		String logoimg = CubismHelper.getAppRootDir()+"/img/ciiclogo.png";
		logger.debug("fileIdentify="+fileIdentify);
		logger.debug("diskFilename="+diskFilename);
		logger.debug("fontpath="+fontpath);
		
		File file = new File(diskFilename);
		if (file.exists()) {
			file.delete();
		}
		else if(!file.canWrite()){
			File dir = new File(MultipartUtils.extractFilePath(diskFilename));
			dir.mkdirs();
		}
		
		/**
         * SIMLI.TTF 	隶书
         * SIMKAI.TTF	楷体
         * SIMFANG.TTF	仿宋
         * SIMHEI.TTF	黑体
         * */
        BaseFont baseFont = BaseFont.createFont(fontpath+"SIMHEI.TTF", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        Font font = new Font(baseFont); 
        Font font_title = new Font(baseFont, 18, Font.BOLD); //字体大小18，加粗，Font.UNDERLINE|Font.BOLD|Font.ITALIC
        //外层边框粗细 
        Rectangle b1 = new Rectangle(0f, 0f);  
        b1.setBorderWidthLeft(1f);  
        b1.setBorderWidthBottom(0f);  
        b1.setBorderWidthRight(1f);  
        b1.setBorderWidthTop(0f); 
        
        //内层边框粗细 
        Rectangle b2 = new Rectangle(0f, 0f);  
        b2.setBorderWidthLeft(0f);  
        b2.setBorderWidthBottom(1f);  
        b2.setBorderWidthRight(0f);  
        b2.setBorderWidthTop(0f);  
        
        PdfPTable table = new PdfPTable(1);//总列数
        table.setTotalWidth(410f);//总宽度
        table.getDefaultCell().setBorder(1);//设置边框
        
        PdfPCell cell; 
        Paragraph p;
        Image image;
        
        image = Image.getInstance(logoimg);
        //image.scaleAbsolute(65,300);//控制图片大小
        //image.setWidthPercentage(50);
        image.setAlignment(Element.ALIGN_CENTER);
        cell = new PdfPCell(image, true);//调整图片大小
        cell.setUseAscender(true); 
        cell.setHorizontalAlignment(Element.ALIGN_CENTER); //水平居中
        cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
        b1.setBorderWidthTop(1f);
        cell.cloneNonPositionParameters(b1);
        cell.setFixedHeight(30f);//固定高度
        cell.setPaddingLeft(30f);
        cell.setPaddingRight(30f);
        cell.setPaddingTop(10f);
        table.addCell(cell);
        b1.setBorderWidthTop(0f);
        
        p = new Paragraph(out.getFieldValue("student.examination_name"),font_title);//考试名称
        p.setAlignment(Element.ALIGN_CENTER);
        cell = new PdfPCell(p);
        cell.setUseAscender(true); 
        cell.setHorizontalAlignment(Element.ALIGN_CENTER); //水平居中
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.cloneNonPositionParameters(b1);
        cell.setNoWrap(true);//不折行
        cell.setFixedHeight(40f);//固定高度
        table.addCell(cell); 
        
        p = new Paragraph("准考证",font);
        p.setAlignment(Element.ALIGN_CENTER);
        cell = new PdfPCell(p);
        
        cell.setUseAscender(true); 
        cell.setHorizontalAlignment(Element.ALIGN_CENTER); //水平居中
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.cloneNonPositionParameters(b1);
        cell.setPaddingBottom(10f);
        table.addCell(cell);
        
        String fname = out.getFieldValue("student.photo");
        image = Image.getInstance(rootPath + fname.substring(0, fname.lastIndexOf(".")));
        image.scaleAbsolute(65,100);//控制图片大小
        image.setAlignment(Element.ALIGN_CENTER);
        cell = new PdfPCell(image, false);//调整图片大小
        cell.setUseAscender(true); 
        cell.setHorizontalAlignment(Element.ALIGN_CENTER); //水平居中
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.cloneNonPositionParameters(b1);
        table.addCell(cell);
        
        p = new Paragraph("准考证号：" + out.getFieldValue("student.admission_card_number"), font);
        p.setAlignment(Element.ALIGN_MIDDLE);
        cell = new PdfPCell(p);
        cell.setUseAscender(true); 
        cell.setHorizontalAlignment(Element.ALIGN_CENTER); //水平居中
        b1.setBorderWidthBottom(0f);
        cell.cloneNonPositionParameters(b1);
        cell.setFixedHeight(30f);//固定高度
        cell.setPaddingTop(10f);
        table.addCell(cell);
        
        PdfPTable inner = new PdfPTable(4);//总行数
        float[] widths = {60f, 135f, 65f, 135f};
        inner.setWidths(widths); 
        inner.getDefaultCell().setBorder(PdfPCell.NO_BORDER);//设置边框
        inner.getDefaultCell().setUseAscender(true); 
        inner.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER); //水平居中
        inner.getDefaultCell().setVerticalAlignment(Element.ALIGN_BOTTOM);
        
        cell = new PdfPCell(new Paragraph("姓名",font));
        cell.setBorder(0);
        inner.addCell(cell);
        cell =  new PdfPCell(new Paragraph(out.getFieldValue("student.student_name"),font));
        cell.cloneNonPositionParameters(b2); 
        inner.addCell(cell);
        
        inner.addCell(new Paragraph("性别",font));
        cell =  new PdfPCell(new Paragraph(out.getFieldValue("student.sex"),font));
        cell.cloneNonPositionParameters(b2); 
        inner.addCell(cell);
        
        cell = new PdfPCell(new Paragraph("身份证号",font));
        cell.setBorder(0);
        inner.addCell(cell);
        cell =  new PdfPCell(new Paragraph(out.getFieldValue("student.card_id"),font));
        cell.cloneNonPositionParameters(b2); 
        inner.addCell(cell);

        inner.addCell(new Paragraph("应聘岗位",font));
        cell =  new PdfPCell(new Paragraph(out.getFieldValue("student.position_applied"),font));
        cell.cloneNonPositionParameters(b2); 
        inner.addCell(cell);
        
        cell = new PdfPCell(new Paragraph("毕业学校",font));
        cell.setBorder(0);
        inner.addCell(cell);
        cell =  new PdfPCell(new Paragraph(out.getFieldValue("student.institutions"),font));
        cell.cloneNonPositionParameters(b2); 
        inner.addCell(cell);

        inner.addCell(new Paragraph("文化程度",font));
        cell =  new PdfPCell(new Paragraph(out.getFieldValue("student.education"),font));
        cell.cloneNonPositionParameters(b2); 
        inner.addCell(cell);
        
        cell = new PdfPCell(new Paragraph("考试时间",font));
        cell.setBorder(0);
        inner.addCell(cell);
        cell =  new PdfPCell(new Paragraph(out.getFieldValue("student.examination_time"),font));
        cell.cloneNonPositionParameters(b2); 
        inner.addCell(cell);
        
        inner.addCell(new Paragraph("考试地点",font));
        cell =  new PdfPCell(new Paragraph(out.getFieldValue("student.examination_address"),font));
        cell.cloneNonPositionParameters(b2); 
        inner.addCell(cell);

        cell = new PdfPCell(inner);
        cell.setPadding(15f);
        cell.cloneNonPositionParameters(b1);
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph("",font));
        b1.setBorderWidthBottom(1f);
        cell.cloneNonPositionParameters(b1);
        cell.setFixedHeight(10f);//固定高度
        table.addCell(cell);
        
        Document document = new Document();
		PdfWriter.getInstance(document, new FileOutputStream(diskFilename));
		
		document.open();
        document.add(table);
        document.close(); 
        
		return filepathname;
	}
}
