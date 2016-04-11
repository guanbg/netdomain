package com.platform.cubism.test;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

public class Pdf {

	public static void main(String[] args) throws MalformedURLException, IOException {
		Document document = new Document();

        try {
            PdfWriter.getInstance(document,
                new FileOutputStream("D:\\test\\aaahhaaaaaa.pdf"));
            /**
             * SIMLI.TTF 	隶书
             * SIMKAI.TTF	楷体
             * SIMFANG.TTF	仿宋
             * SIMHEI.TTF	黑体
             * */
            BaseFont baseFont = BaseFont.createFont("D:/test/simhei.ttf",BaseFont.IDENTITY_H,BaseFont.NOT_EMBEDDED);
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
            
            document.open();
            
            PdfPTable table = new PdfPTable(1);//总行数
            table.setTotalWidth(410f);//总宽度
            table.getDefaultCell().setBorder(1);//设置边框
            
            PdfPCell cell; 
            Paragraph p;
            p = new Paragraph("考试名称",font_title);
            p.setAlignment(Element.ALIGN_CENTER);
            cell = new PdfPCell(p);
            cell.setUseAscender(true); 
            cell.setHorizontalAlignment(Element.ALIGN_CENTER); //水平居中
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            b1.setBorderWidthTop(1f);
            cell.cloneNonPositionParameters(b1);
            cell.setNoWrap(true);//不折行
            cell.setFixedHeight(50f);//固定高度
            table.addCell(cell); 
            b1.setBorderWidthTop(0f);
            
            p = new Paragraph("准考证",font);
            p.setAlignment(Element.ALIGN_CENTER);
            cell = new PdfPCell(p);
            cell.setUseAscender(true); 
            cell.setHorizontalAlignment(Element.ALIGN_CENTER); //水平居中
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.cloneNonPositionParameters(b1);
            table.addCell(cell);
            
            Image image = Image.getInstance("D:/test/zjz.jpg");
            image.scaleAbsolute(65,100);//控制图片大小
            image.setAlignment(Element.ALIGN_CENTER);
            cell = new PdfPCell(image, false);//调整图片大小
            cell.setUseAscender(true); 
            cell.setHorizontalAlignment(Element.ALIGN_CENTER); //水平居中
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.cloneNonPositionParameters(b1);
            table.addCell(cell);
            
            p = new Paragraph("准考证号：akd2323434",font);
            p.setAlignment(Element.ALIGN_MIDDLE);
            cell = new PdfPCell(p);
            cell.setUseAscender(true); 
            cell.setHorizontalAlignment(Element.ALIGN_CENTER); //水平居中
            b1.setBorderWidthBottom(0f);
            cell.cloneNonPositionParameters(b1);
            cell.setFixedHeight(30f);//固定高度
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
            cell =  new PdfPCell(new Paragraph("官本刚",font));
            cell.cloneNonPositionParameters(b2); 
            inner.addCell(cell);
            
            inner.addCell(new Paragraph("性别",font));
            cell =  new PdfPCell(new Paragraph("男",font));
            cell.cloneNonPositionParameters(b2); 
            inner.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("身份证号",font));
            cell.setBorder(0);
            inner.addCell(cell);
            cell =  new PdfPCell(new Paragraph("422201790619181",font));
            cell.cloneNonPositionParameters(b2); 
            inner.addCell(cell);

            inner.addCell(new Paragraph("应聘岗位",font));
            cell =  new PdfPCell(new Paragraph("架构师",font));
            cell.cloneNonPositionParameters(b2); 
            inner.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("毕业学校",font));
            cell.setBorder(0);
            inner.addCell(cell);
            cell =  new PdfPCell(new Paragraph("陕西XXX大学",font));
            cell.cloneNonPositionParameters(b2); 
            inner.addCell(cell);

            inner.addCell(new Paragraph("文化程度",font));
            cell =  new PdfPCell(new Paragraph("本科",font));
            cell.cloneNonPositionParameters(b2); 
            inner.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("考试时间",font));
            cell.setBorder(0);
            inner.addCell(cell);
            cell =  new PdfPCell(new Paragraph("2014-05-01 10:00",font));
            cell.cloneNonPositionParameters(b2); 
            inner.addCell(cell);
            
            inner.addCell(new Paragraph("考试地点",font));
            cell =  new PdfPCell(new Paragraph("陕西宾馆",font));
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
            
            document.add(table);
            document.close(); // no need to close PDFwriter?
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
	}

}
