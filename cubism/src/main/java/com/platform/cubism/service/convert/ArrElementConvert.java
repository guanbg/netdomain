package com.platform.cubism.service.convert;

import java.util.regex.Pattern;

import com.platform.cubism.base.CArray;
import com.platform.cubism.base.CStruc;
import com.platform.cubism.service.config.ArrElement;

public interface ArrElementConvert {
	public static Pattern classPattern = Pattern.compile("^[^\"']*.+\\..+\\(.*\\)[^\"']*$");
	public static Pattern fieldPattern = Pattern.compile("^in\\..*[^\\.].+");
	public static Pattern namePattern = Pattern.compile("^#\\{(.+)}$");
	public static Pattern constPattern = Pattern.compile("^\".*\"$|^'.*'$");
	
	public boolean isConvert(ArrElement arrElem, CStruc data);

	public CArray convert(ArrElement arrElem, CStruc data);
}
