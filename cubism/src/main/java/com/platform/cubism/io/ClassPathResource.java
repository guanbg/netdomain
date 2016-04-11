package com.platform.cubism.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import com.platform.cubism.util.Assert;
import com.platform.cubism.util.CubismHelper;
import com.platform.cubism.util.StringUtils;

public class ClassPathResource implements Resource {
	private final String path;
	private ClassLoader classLoader;
	private Class<?> clazz;

	public ClassPathResource(String path) {
		this(path, (ClassLoader) null);
	}

	public ClassPathResource(String path, ClassLoader classLoader) {
		Assert.notNull(path, "Path must not be null");
		String pathToUse = StringUtils.cleanPath(path);
		if (pathToUse.startsWith("/")) {
			pathToUse = pathToUse.substring(1);
		}
		this.path = pathToUse;
		this.classLoader = (classLoader != null ? classLoader : CubismHelper.getDefaultClassLoader());
	}

	public ClassPathResource(String path, Class<?> clazz) {
		Assert.notNull(path, "Path must not be null");
		this.path = StringUtils.cleanPath(path);
		this.clazz = clazz;
	}

	protected ClassPathResource(String path, ClassLoader classLoader, Class<?> clazz) {
		this.path = StringUtils.cleanPath(path);
		this.classLoader = classLoader;
		this.clazz = clazz;
	}

	public final String getPath() {
		return this.path;
	}

	public final ClassLoader getClassLoader() {
		return (this.classLoader != null ? this.classLoader : clazz != null ? this.clazz.getClassLoader() : CubismHelper.getDefaultClassLoader());
	}

	public URL getURL() throws IOException {
		URL url;
		if (this.clazz != null) {
			url = this.clazz.getResource(this.path);
		} else {
			url = this.classLoader.getResource(this.path);
		}
		if (url == null) {
			throw new FileNotFoundException(path + " cannot be resolved to URL because it does not exist");
		}
		return url;
	}

	public URI getURI() throws URISyntaxException, IOException {
		return new URI(StringUtils.replace(getURL().toString(), " ", "%20"));
	}

	public File getFile() throws IOException {
		URL url = getURL();
		Assert.notNull(url, "Resource URL must not be null");
		if (!URL_PROTOCOL_FILE.equals(url.getProtocol())) {
			throw new FileNotFoundException(path + " cannot be resolved to absolute file path " + "because it does not reside in the file system: " + path);
		}
		try {
			return new File(getURI().getSchemeSpecificPart());
		} catch (URISyntaxException ex) {
			return new File(getURL().getFile());
		}
	}

	public InputStream getInputStream() throws IOException {
		InputStream is;
		if (this.clazz != null) {
			is = this.clazz.getResourceAsStream(this.path);
		} else {
			is = this.classLoader.getResourceAsStream(this.path);
		}
		if (is == null) {
			throw new FileNotFoundException(path + " cannot be opened because it does not exist");
		}
		return is;
	}

	public Resource createRelative(String relativePath) throws IOException {
		String pathToUse = StringUtils.applyRelativePath(this.path, relativePath);
		return new ClassPathResource(pathToUse, this.classLoader, this.clazz);
	}
}
