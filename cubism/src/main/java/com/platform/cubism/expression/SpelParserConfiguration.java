package com.platform.cubism.expression;

public class SpelParserConfiguration {
	private final boolean autoGrowNullReferences;
	private final boolean autoGrowCollections;

	public SpelParserConfiguration(boolean autoGrowNullReferences, boolean autoGrowCollections) {
		this.autoGrowNullReferences = autoGrowNullReferences;
		this.autoGrowCollections = autoGrowCollections;
	}

	public boolean isAutoGrowNullReferences() {
		return this.autoGrowNullReferences;
	}

	public boolean isAutoGrowCollections() {
		return this.autoGrowCollections;
	}
}