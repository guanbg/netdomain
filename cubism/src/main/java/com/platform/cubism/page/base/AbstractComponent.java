package com.platform.cubism.page.base;

import com.platform.cubism.page.Component;

public abstract class AbstractComponent implements Component{
	private static long n=0l;
	private String id;

	public AbstractComponent(){
		id = "cubi"+(++n);
	}
	
	public String getId() {
		return id;
	}

}
