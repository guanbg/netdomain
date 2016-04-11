package com.platform.cubism.cvt;

import java.beans.PropertyEditorSupport;

import com.platform.cubism.util.Assert;

public class ConvertingPropertyEditorAdapter extends PropertyEditorSupport {
	private final ConversionService conversionService;
	private final TypeDescriptor targetDescriptor;
	private final boolean canConvertToString;

	public ConvertingPropertyEditorAdapter(ConversionService conversionService, TypeDescriptor targetDescriptor) {
		Assert.notNull(conversionService, "ConversionService must not be null");
		Assert.notNull(targetDescriptor, "TypeDescriptor must not be null");
		this.conversionService = conversionService;
		this.targetDescriptor = targetDescriptor;
		this.canConvertToString = conversionService.canConvert(this.targetDescriptor, TypeDescriptor.valueOf(String.class));
	}

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		setValue(this.conversionService.convert(text, TypeDescriptor.valueOf(String.class), this.targetDescriptor));
	}

	@Override
	public String getAsText() {
		if (this.canConvertToString) {
			return (String) this.conversionService.convert(getValue(), this.targetDescriptor, TypeDescriptor.valueOf(String.class));
		} else {
			return null;
		}
	}
}