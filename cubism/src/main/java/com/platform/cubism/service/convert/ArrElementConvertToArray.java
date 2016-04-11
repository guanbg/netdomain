package com.platform.cubism.service.convert;

import com.platform.cubism.base.CArray;
import com.platform.cubism.base.CStruc;
import com.platform.cubism.service.config.ArrElement;

public interface ArrElementConvertToArray {
	public boolean isConvert(ArrElement arrElem, CStruc data);

	public CArray[] convert(ArrElement arrElem, CStruc data);

}
