package com.platform.cubism.service.convert;

import com.platform.cubism.base.CField;
import com.platform.cubism.base.CStruc;
import com.platform.cubism.service.config.FldElement;

public interface FldElementConvertToArray {
	public boolean isConvert(FldElement fldElem, CStruc data);

	public CField[] convert(FldElement fldElem, CStruc data);

}
