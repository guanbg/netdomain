package com.platform.cubism.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.platform.cubism.util.Assert;
import com.platform.cubism.util.StringUtils;

public class FileSystemResource implements Resource {
	private final File file;

	public FileSystemResource(File file) {
		Assert.notNull(file, "File must not be null");
		this.file = file;
	}

	public FileSystemResource(String path) {
		Assert.notNull(path, "Path must not be null");
		this.file = new File(path);
	}

	public final String getPath() {
		return StringUtils.cleanPath(file.getPath());
	}

	public InputStream getInputStream() throws IOException {
		return new FileInputStream(this.file);
	}

	public URL getURL() throws IOException {
		return this.file.toURI().toURL();
	}

	public File getFile() throws IOException {
		return this.file;
	}

	public Resource createRelative(String relativePath) throws IOException {
		String pathToUse = StringUtils.applyRelativePath(getPath(), relativePath);
		return new FileSystemResource(pathToUse);
	}
}
