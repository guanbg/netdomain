package com.platform.cubism.base.format;

import com.platform.cubism.base.CField;

public interface FormatConvert {
	public boolean isFormat(CField field);

	public String format(CField field);
}
