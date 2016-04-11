package com.platform.cubism.base.format;

import com.platform.cubism.base.CField;

public class FormatManager {
	public static FormatConvert[] getFormatConvert() {
		return null;
	}

	public static String convert(CField field) {
		FormatConvert[] converts = getFormatConvert();
		if (converts == null) {
			return null;
		}

		for (FormatConvert fc : converts) {
			if (fc.isFormat(field)) {
				return fc.format(field);
			}
		}

		return null;
	}
}
