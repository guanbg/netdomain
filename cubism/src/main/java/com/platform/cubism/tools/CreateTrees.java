package com.platform.cubism.tools;

import com.platform.cubism.base.CArray;
import com.platform.cubism.base.CStruc;
import com.platform.cubism.base.Json;
import com.platform.cubism.base.JsonFactory;

public class CreateTrees {
	private final static String ID = "id";//上送数据中的节点编号
	private final static String PID = "pid";//上送数据中的父节点编号
	private final static String LVL = "lvl";//上送数据中的层级
	
	private final static String CHILDREN = "children";//返回数据中子节点的名称
	private final static String TREENAME = "tree";//返回数据中根节点名称
	
	public static Json adjacency(CArray data){
		return adjacency(data,TREENAME, ID, PID, LVL);
	}
	public static Json adjacency(CArray data,String treeName, String id, String pid, String lvl){
		if(treeName == null || treeName.length() <= 0){
			treeName = TREENAME;
		}
		if(id == null || id.length() <= 0){
			id = ID;
		}
		if(pid == null || pid.length() <= 0){
			pid = PID;
		}
		if(lvl == null || lvl.length() <= 0){
			lvl = LVL;
		}
		if(data == null || data.isEmpty()){
			return JsonFactory.create();
		}
		
		CArray children = null,tree = JsonFactory.createArray(treeName);
		String parentId,level;
		for(CStruc cs : data.getRecords()){//查找根
			parentId = cs.getFieldValue(pid);
			level = cs.getFieldValue(lvl);
			if(parentId == null || parentId.length() <= 0 || (level != null && "1".equals(level))){
				if(cs.getField("level") == null){
					cs.addField("level", "1");//顶级为1级
				}
				tree.add(cs);
				data.removeRow(cs);
				children = getChildren(data, cs.getFieldValue(id), id, pid,1);
				tree.add(children);
				
				if(children != null && !children.isEmpty()){
					if(cs.getField("isLeaf") == null && cs.getField("isleaf") == null){
						cs.addField("isLeaf", "false");//是否叶子
					}
				}
				else if(cs.getField("isLeaf") == null && cs.getField("isleaf") == null){
					cs.addField("isLeaf", "true");//是否叶子
				}
			}
		}
		
		return JsonFactory.create().addArray(tree);
	}

	public static Json toTree(CArray data){
		return toTree(data,TREENAME, ID, PID, LVL, CHILDREN);
	}
	public static Json toTree(CArray data, String children){
		return toTree(data,TREENAME, ID, PID, LVL, children);
	}
	private static Json toTree(CArray data, String treeName, String id, String pid, String lvl, String children){
		if(treeName == null || treeName.length() <= 0){
			treeName = TREENAME;
		}
		if(id == null || id.length() <= 0){
			id = ID;
		}
		if(pid == null || pid.length() <= 0){
			pid = PID;
		}
		if(lvl == null || lvl.length() <= 0){
			lvl = LVL;
		}
		if(children == null || children.length() <= 0){
			children = CHILDREN;
		}
		if(data == null || data.isEmpty()){
			return JsonFactory.create();
		}
		
		CArray tree = JsonFactory.createArray(treeName);
		String parentId,level;
		for(CStruc cs : data.getRecords()){//查找根
			parentId = cs.getFieldValue(pid);
			level = cs.getFieldValue(lvl);
			if(parentId == null || parentId.length() <= 0 || (level != null && "1".equals(level))){
				tree.add(cs);
				//data.removeRow(cs);
				continue;
			}
			
			boolean hasParent = false;
			for(CStruc c : data.getRecords()){
				if(parentId.equalsIgnoreCase(c.getFieldValue(id))){
					hasParent = true;
					break;
				}
			}
			if(!hasParent){
				tree.add(cs);
				//data.removeRow(cs);
			}
		}
		
		if(tree == null || tree.isEmpty()){
			return JsonFactory.create();
		}
		
		for(CStruc cs : tree.getRecords()){
			data.removeRow(cs);
		}
		
		do{//查找子
			for(CStruc cs : data.getRecords()){
				if(addChildren(tree, cs, id, pid, children)){
					data.removeRow(cs);
				}
			}
		}while(data != null && !data.isEmpty());
		
		return JsonFactory.create().addArray(tree);
	}
	
	private static boolean addChildren(CArray tree, CStruc node, String id, String pid, String children){
		if(node == null || node.isEmpty()){
			return false;
		}
		if(tree == null || tree.isEmpty()){
			return false;
		}
		if(id == null || id.length() <= 0){
			id = ID;
		}
		if(pid == null || pid.length() <= 0){
			pid = PID;
		}
		if(children == null || children.length() <= 0){
			children = CHILDREN;
		}
		
		for(CStruc cs : tree.getRecords()){
			if(node.getFieldValue(pid).equalsIgnoreCase(cs.getFieldValue(id))){
				CArray arr = cs.getArray(children);
				if(arr == null){
					cs.addArray(JsonFactory.createArray(children));
					arr = cs.getArray(children);
				}
				arr.add(node);
				return true;
			}
		}
		for(CStruc cs : tree.getRecords()){
			CArray arr = cs.getArray(children);
			if(arr == null || arr.isEmpty()){
				continue;
			}
			if(addChildren(arr, node, id, pid, children)){
				return true;
			}
		}
		return false;		
	}
	
	private static CArray getChildren(CArray node, String value, String id, String pid, int parentlvl){
		CArray children = null,arr = JsonFactory.createArray();
		if(node == null || node.isEmpty()){
			return arr;
		}
		if(value == null || value.length() <= 0){
			return arr;
		}
		if(id == null || id.length() <= 0){
			id = ID;
		}
		if(pid == null || pid.length() <= 0){
			pid = PID;
		}
		
		for(CStruc cs : node.getRecords()){
			if(value.equalsIgnoreCase(cs.getFieldValue(pid))){
				if(cs.getField("level") == null){
					cs.addField("level", String.valueOf(parentlvl+1));//顶级为1级
				}
				arr.add(cs);
				node.removeRow(cs);
				children = getChildren(node, cs.getFieldValue(id), id, pid, parentlvl+1);
				arr.add(children);
				
				if(children != null && !children.isEmpty()){
					if(cs.getField("isLeaf") == null && cs.getField("isleaf") == null){
						cs.addField("isLeaf", "false");//是否叶子
					}
				}
				else if(cs.getField("isLeaf") == null && cs.getField("isleaf") == null){
					cs.addField("isLeaf", "true");//是否叶子
				}
			}
		}
		
		return arr;
	}
}
