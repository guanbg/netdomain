package com.platform.cubism.processor.impl;

import static com.platform.cubism.util.CubismHelper.getAppRootDir;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.platform.cubism.base.CArray;
import com.platform.cubism.base.CStruc;
import com.platform.cubism.base.Json;

public class ExaminationRptProcessor extends AbstractRptProcessor {
	private final static int startRowIndex = 4;//第X行开始插入,行号从0开始
	
	public ExaminationRptProcessor(){
		super(getAppRootDir()+"WEB-INF/template/examination.xlsx", startRowIndex, "学生考试信息.xlsx");
	}
	
	public boolean process(Json in) throws Exception {
		if(in == null || in.isEmpty()){
			return false;
		}
		if(workbook == null){
			return false;
		}
		
		Row row = null;
		Sheet sheet = workbook.getSheetAt(0);
		
		if(sourceRowIndex <= startRowIndex){
			CStruc examination = in.getStruc("examination");
			if(examination == null || examination.isEmpty()){
				return false;
			}
			
			row = sheet.getRow(1);
			setCellValue(row,1,examination.getFieldValue("batch_number"));//考试批次
			setCellValue(row,3,examination.getFieldValue("examination_time"));//考试时间
			setCellValue(row,5,examination.getFieldValue("examination_name"));//考试名称
			setCellValue(row,9,examination.getFieldValue("examination_address"));//考试地点

			row = sheet.getRow(2);
			setCellValue(row,1,examination.getFieldValue("admission_card_print_start")+" 至 "+examination.getFieldValue("admission_card_print_end"));//准考证打印起止时间
			setCellValue(row,5,examination.getFieldValue("score_start")+" 至 "+examination.getFieldValue("score_end"));//笔试成绩查询起止时间
			setCellValue(row,9,examination.getFieldValue("practice_score_start")+" 至 "+examination.getFieldValue("practice_score_end"));//实践成绩查询起止时间
		}
		
		CArray students = in.getArray("students");
		if(students == null || students.isEmpty()){
			return false;
		}
		
		CStruc cs = null;
		int size = students.size();		
		insertRows(sheet, size, 15);
		
		for (int i = 0; i < size; i++) {
			cs = students.getRecord(i);
			row = sheet.getRow(sourceRowIndex+i);
			setCellValue(row,0,cs.getFieldValue("student_name"));//姓名
			setCellValue(row,1,cs.getFieldValue("sex"));//性别
			setCellValue(row,2,cs.getFieldValue("card_id"));//身份证号
			setCellValue(row,3,cs.getFieldValue("phone_no"));//手机号码
			setCellValue(row,4,cs.getFieldValue("education"));//学历
			setCellValue(row,5,cs.getFieldValue("profession"));//专业
			setCellValue(row,6,cs.getFieldValue("institutions"));//毕业院校
			setCellValue(row,7,cs.getFieldValue("position_applied"));//应聘岗位
			setCellValue(row,8,cs.getFieldValue("corporation"));//企业名称
			setCellValue(row,9,cs.getFieldValue("corporate_identity"));//企业识别号
			setCellValue(row,10,cs.getFieldValue("score"));//笔试成绩
			setCellValue(row,11,cs.getFieldValue("practice_score"));//实践成绩
			setCellValue(row,12,cs.getFieldValue("admission_card_number"));//准考证号
			setCellValue(row,13,cs.getFieldValue("print_number"));//打印次数
			setCellValue(row,14,cs.getFieldValue("export_number"));//导出次数
		}
		
		sourceRowIndex += size;
		return true;
	}

}
