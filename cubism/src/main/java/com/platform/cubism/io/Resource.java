package com.platform.cubism.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public interface Resource {
	public static final String URL_PROTOCOL_FILE = "file";
	public static final String URL_PROTOCOL_JAR = "jar";
	public static final String URL_PROTOCOL_ZIP = "zip";
	public static final String URL_PROTOCOL_WSJAR = "wsjar";
	public static final String URL_PROTOCOL_CODE_SOURCE = "code-source";
	public static final String JAR_URL_SEPARATOR = "!/";

	URL getURL() throws IOException;

	File getFile() throws IOException;

	InputStream getInputStream() throws IOException;

	Resource createRelative(String relativePath) throws IOException;
}