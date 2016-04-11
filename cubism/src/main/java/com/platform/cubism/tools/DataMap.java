package com.platform.cubism.tools;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.platform.cubism.base.CArray;
import com.platform.cubism.base.CField;
import com.platform.cubism.base.CStruc;
import com.platform.cubism.base.Json;
import com.platform.cubism.base.JsonFactory;
import com.platform.cubism.util.SecurityHelper;

public class DataMap {
	public static Json toBarChart(CArray data, String columnNames){
		return toBarChart(data, columnNames, "num");
	}
	public static Json toBarChart(CArray data, CField columnNames){
		return toBarChart(data, columnNames.getValue(), "num");
	}
	public static Json toBarChart(CArray data, String columnNames, String num){
		if(columnNames == null || columnNames.length() <= 0){
			return JsonFactory.create();
		}
		if(data == null || data.isEmpty()){
			return JsonFactory.create();
		}
		
		String[] colum = columnNames.split(",");
		if(colum == null || colum.length<2){
			return JsonFactory.create();
		}
		String c1 = colum[0];
		String c2 = colum[1];
		
		Set<String> xdata = new HashSet<String>();
		Map<String,CArray> ydata = new HashMap<String,CArray>();
		String v1,v2;
		for(CStruc cs : data.getRecords()){
			v1 = cs.getFieldValue(c1);
			v2 = cs.getFieldValue(c2);
			if(v2 == null || v2.length() <= 0){
				v2 = "空值";
			}
			xdata.add(v1);
			if(ydata.containsKey(v2)){
				ydata.get(v2).add(cs.getField(num));
			}
			else{
				CArray arr = JsonFactory.createArray("data");
				arr.add(cs.getField(num));
				ydata.put(v2, arr);
			}
		}
		CArray x = JsonFactory.createArray("xdata");
		for(String s : xdata){
			x.add(JsonFactory.createField(c1,s));
		}
		CArray y = JsonFactory.createArray("ydata");
		for (Map.Entry<String,CArray> entry : ydata.entrySet()){
			y.add(JsonFactory.createStruc().addField("name", entry.getKey()).addArray(entry.getValue()));
		}
    
		Json ret = JsonFactory.create();
		ret.addArray(x).addArray(y);
		return ret;
	}
	
	/*
	 * 数据结构：
	groupplans:[
		{
			department_name:'',
			department_id:'',
			department_plans:[
				{
					employee_name:'',
					employee_id:'',
					employee_plans:[
						{
							plan_priority:'',
							completion_status:'',
							plan_content:'',
							start_time:'',
							end_time:'',
							......
						},
						{...}
					]
				},
				{...}
			]
		},
		{...}		
	]		
	*/	
	public static Json groupPlans(CArray data){		
		Json ret = JsonFactory.create();
		CArray department_plans,employee_plans,groupplans = JsonFactory.createArray("groupplans");
		CStruc struc,stc,cs;
		String department_id,departId,department_name,employee_id,emplId,employee_name;
		
		if(data == null || data.isEmpty()){
			return ret;
		}
		while(data != null && !data.isEmpty()){
			stc = data.getRecord(0);
		//for(CStruc stc : data.getRecords()){//会导致removeRow不能立刻删除，因为存在引用
			department_id = stc.getFieldValue("department_id");
			department_name = stc.getFieldValue("department_name");
			
			department_plans = JsonFactory.createArray("department_plans");
			struc = JsonFactory.createStruc();
			struc.addField("department_name", department_name);
			struc.addField("department_id", department_id);
			struc.addArray(department_plans);
			groupplans.add(struc);
			
			int n = 0;
			while(data != null && !data.isEmpty() && n<data.size()){
				cs = data.getRecord(n);
				departId = cs.getFieldValue("department_id");
				if((department_id == null && departId == null) || (department_id != null && department_id.equals(departId))){
					employee_id = cs.getFieldValue("employee_id");
					employee_name = cs.getFieldValue("employee_name");
					
					employee_plans = JsonFactory.createArray("employee_plans");
					struc = JsonFactory.createStruc();
					struc.addField("employee_name", employee_name);
					struc.addField("employee_id", employee_id);
					struc.addArray(employee_plans);
					department_plans.add(struc);
					
					for(CStruc c : data.getRecords()){
						emplId = c.getFieldValue("employee_id");
						if(employee_id != null && employee_id.equals(emplId)){
							struc = JsonFactory.createStruc();
							struc.addField(c.getField("plan_id"));
							struc.addField(c.getField("plan_content"));
							struc.addField(c.getField("is_allday"));
							struc.addField(c.getField("plan_priority"));
							struc.addField(c.getField("complete_progress"));
							struc.addField(c.getField("completion_status"));
							struc.addField(c.getField("remind_time"));
							struc.addField(c.getField("start_time"));
							struc.addField(c.getField("end_time"));
							struc.addField(c.getField("classname"));
							employee_plans.add(struc);
							
							data.removeRow(c);
						}
					}
					n = 0;
				}
				else{
					n++;
				}
			}			
		}
		
		ret.addArray(groupplans);
		return ret;
	}
	
	public static void reversalComma(CArray data, String columnName, String join){
		reversalSplit(data,columnName,",",join);
	}
	public static void reversalComma(CArray data, String columnName){
		reversalSplit(data,columnName,",","-");
	}
	
	public static void reversalMiddleLine(CArray data, String columnName, String join){
		reversalSplit(data,columnName,"-",join);
	}
	public static void reversalMiddleLine(CArray data, String columnName){
		reversalSplit(data,columnName,"-","-");
	}
	/**
	 * 将带有分隔符的字符串反转后用指定的连接符重新进行连接
	 * */
	public static void reversalSplit(CArray data, String columnName, String spliter, String join){
		if(columnName == null || columnName.length() <= 0){
			return;
		}
		if(spliter == null || spliter.length() <= 0){
			return;
		}
		if(data == null || data.isEmpty()){
			return;
		}
		
		for(CStruc cs : data.getRecords()){
			reversalSplit(cs,columnName,spliter,join);
		}
	}
	
	public static void reversalComma(CStruc data, String columnName, String join){
		reversalSplit(data,columnName,",",join);
	}
	public static void reversalComma(CStruc data, String columnName){
		reversalSplit(data,columnName,",","-");
	}
	
	public static void reversalMiddleLine(CStruc data, String columnName, String join){
		reversalSplit(data,columnName,"-",join);
	}
	public static void reversalMiddleLine(CStruc data, String columnName){
		reversalSplit(data,columnName,"-","-");
	}	
	/**
	 * 将带有分隔符的字符串反转后用指定的连接符重新进行连接
	 * */
	public static void reversalSplit(CStruc data, String columnName, String spliter, String join){
		if(columnName == null || columnName.length() <= 0){
			return;
		}
		if(spliter == null || spliter.length() <= 0){
			return;
		}
		if(data == null || data.isEmpty()){
			return;
		}
		String columnValue = data.getFieldValue(columnName);
		if(columnValue != null && columnValue.indexOf(spliter)>0){
			List<String> columnValueList = Arrays.asList(columnValue.split(spliter));
			Collections.reverse(columnValueList);
			String sb = "";
			for(String s : columnValueList){
				if(s == null || s.length() <= 0){
					continue;
				}
				sb += join + s;
			}
			sb.replace(" ", spliter);//sql查询时将元字符替换为空格了，故需要转换回来
			data.getField(columnName).setValue(sb);
		}
	}
	
	public static void mergeTop(CArray data, String columnName){
		if(columnName == null || columnName.length() <= 0){
			return;
		}
		if(data == null || data.isEmpty()){
			return;
		}
		CArray parent;
		for(CStruc cs : data.getRecords()){
			parent = cs.getArray(columnName);
			cs.remove(columnName);
			if(parent == null || parent.isEmpty()){
				continue;
			}
			for(CStruc c : data.getRecords()){
				for(CStruc p : parent.getRecords()){
					if(p.getFieldValue("id").equals(c.getFieldValue("id"))){
						parent.removeRow(p);
					}
				}
			}
			if(!parent.isEmpty()){
				data.mergeOf(parent);
			}
		}
	}
	
	public static void parseFileName(CArray data, String columnNames){
		parseFileName(data, columnNames, ",");
	}
	public static void parseFileName(CStruc data, String columnNames){
		parseFileName(data, columnNames, ",");
	}
	public static void parseFileName(Json data, String columnNames){
		parseFileName(data, columnNames, ",");
	}
	public static void parseFileName(Json data, String columnNames, String separater){
		String fileName,fids;
		StringBuilder sb = null;
		
		if(columnNames == null || columnNames.length() <= 0){
			return;
		}
		
		for(String col : columnNames.split(",")){
			fids = data.getFieldValue(col);
			
			if(fids == null || fids.length() <= 0){
				data.addField(col+"_filename", "");
			}
			else{
				sb = new StringBuilder();
				for(String id : fids.split(separater)){
					id = id.toUpperCase();
					int a = id.indexOf('A');
					fileName = new String(SecurityHelper.hexStringToBytes(id.substring(a+1)));
					
					if(sb.length() > 0){
						sb.append(separater);
					}
					sb.append(fileName);
				}
				
				data.addField(col+"_filename", sb.toString());
			}
		}
	}
	public static void parseFileName(CStruc data, String columnNames, String separater){
		String fileName,fids;
		StringBuilder sb = null;
		
		if(columnNames == null || columnNames.length() <= 0){
			return;
		}
		
		for(String col : columnNames.split(",")){
			fids = data.getFieldValue(col);
			
			if(fids == null || fids.length() <= 0){
				data.addField(col+"_filename", "");
			}
			else{
				sb = new StringBuilder();
				for(String id : fids.split(separater)){
					id = id.toUpperCase();
					int a = id.indexOf('A');
					fileName = new String(SecurityHelper.hexStringToBytes(id.substring(a+1)));
					
					if(sb.length() > 0){
						sb.append(separater);
					}
					sb.append(fileName);
				}
				
				data.addField(col+"_filename", sb.toString());
			}
		}
	}
	public static void parseFileName(CArray data, String columnNames, String separater){
		String fileName,fids;
		StringBuilder sb = null;
		
		if(columnNames == null || columnNames.length() <= 0){
			return;
		}
		
		for(CStruc cs : data.getRecords()){
			for(String col : columnNames.split(",")){
				fids = cs.getFieldValue(col);
				
				if(fids == null || fids.length() <= 0){
					cs.addField(col+"_filename", "");
				}
				else{
					sb = new StringBuilder();
					for(String id : fids.split(separater)){
						id = id.toUpperCase();
						int a = id.indexOf('A');
						fileName = new String(SecurityHelper.hexStringToBytes(id.substring(a+1)));
						
						if(sb.length() > 0){
							sb.append(separater);
						}
						sb.append(fileName);
					}
					
					cs.addField(col+"_filename", sb.toString());
				}
			}
		}
	}
}
