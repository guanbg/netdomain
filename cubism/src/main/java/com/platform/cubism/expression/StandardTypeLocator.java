package com.platform.cubism.expression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.platform.cubism.util.CubismHelper;

public class StandardTypeLocator implements TypeLocator {
	private ClassLoader loader;
	private final List<String> knownPackagePrefixes = new ArrayList<String>();

	public StandardTypeLocator() {
		this(CubismHelper.getDefaultClassLoader());
	}

	public StandardTypeLocator(ClassLoader loader) {
		this.loader = loader;
		registerImport("java.lang");
	}

	public Class<?> findType(String typename) throws EvaluationException {
		String nameToLookup = typename;
		try {
			return this.loader.loadClass(nameToLookup);
		} catch (ClassNotFoundException ey) {
			// try any registered prefixes before giving up
		}
		for (String prefix : this.knownPackagePrefixes) {
			try {
				nameToLookup = new StringBuilder().append(prefix).append(".").append(typename).toString();
				return this.loader.loadClass(nameToLookup);
			} catch (ClassNotFoundException ex) {
				// might be a different prefix
			}
		}
		throw new SpelEvaluationException(SpelMessage.TYPE_NOT_FOUND, typename);
	}

	public void registerImport(String prefix) {
		this.knownPackagePrefixes.add(prefix);
	}

	public List<String> getImportPrefixes() {
		return Collections.unmodifiableList(this.knownPackagePrefixes);
	}

	public void removeImport(String prefix) {
		this.knownPackagePrefixes.remove(prefix);
	}
}