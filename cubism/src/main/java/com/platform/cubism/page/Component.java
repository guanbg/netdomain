package com.platform.cubism.page;

import com.platform.cubism.base.Json;

public interface Component {
	public static final String SHOW="show";
	public static final String HIDE="hide";
	public static final String DISABLED="disabled";
	
	public String toHTML();
	public Json getComponentStruc();//{id:'',type:'',event:{},child:[]}
}
