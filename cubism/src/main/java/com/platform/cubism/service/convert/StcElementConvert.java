package com.platform.cubism.service.convert;

import com.platform.cubism.base.CStruc;
import com.platform.cubism.service.config.StcElement;

public interface StcElementConvert {
	public boolean isConvert(StcElement stcElem, CStruc data);

	public CStruc convert(StcElement stcElem, CStruc data);

}
