package com.platform.cubism.expression;

import com.platform.cubism.cvt.ConversionException;
import com.platform.cubism.cvt.ConversionService;
import com.platform.cubism.cvt.ConverterNotFoundException;
import com.platform.cubism.cvt.DefaultConversionService;
import com.platform.cubism.cvt.TypeDescriptor;
import com.platform.cubism.util.Assert;

public class StandardTypeConverter implements TypeConverter {
	private static ConversionService defaultConversionService;
	private final ConversionService conversionService;

	public StandardTypeConverter() {
		synchronized (this) {
			if (defaultConversionService == null) {
				defaultConversionService = new DefaultConversionService();
			}
		}
		this.conversionService = defaultConversionService;
	}

	public StandardTypeConverter(ConversionService conversionService) {
		Assert.notNull(conversionService, "ConversionService must not be null");
		this.conversionService = conversionService;
	}

	public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return this.conversionService.canConvert(sourceType, targetType);
	}

	public Object convertValue(Object value, TypeDescriptor sourceType, TypeDescriptor targetType) {
		try {
			return this.conversionService.convert(value, sourceType, targetType);
		} catch (ConverterNotFoundException cenfe) {
			throw new SpelEvaluationException(cenfe, SpelMessage.TYPE_CONVERSION_ERROR, sourceType.toString(), targetType.toString());
		} catch (ConversionException ce) {
			throw new SpelEvaluationException(ce, SpelMessage.TYPE_CONVERSION_ERROR, sourceType.toString(), targetType.toString());
		}
	}
}