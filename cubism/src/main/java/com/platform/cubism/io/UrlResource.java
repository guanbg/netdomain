package com.platform.cubism.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

import com.platform.cubism.util.Assert;
import com.platform.cubism.util.StringUtils;

public class UrlResource implements Resource {
	private final URL url;

	public UrlResource(URL url) {
		Assert.notNull(url, "URL must not be null");
		this.url = url;
	}

	public UrlResource(URI uri) throws MalformedURLException {
		Assert.notNull(uri, "URI must not be null");
		this.url = uri.toURL();
	}

	public UrlResource(String path) throws MalformedURLException {
		Assert.notNull(path, "Path must not be null");
		this.url = new URL(path);
	}

	public URL getURL() throws IOException {
		return url;
	}

	public URI getURI() throws URISyntaxException {
		return new URI(StringUtils.replace(url.toString(), " ", "%20"));
	}

	public File getFile() throws IOException {
		Assert.notNull(url, "Resource URL must not be null");
		if (!URL_PROTOCOL_FILE.equals(url.getProtocol())) {
			throw new FileNotFoundException(" cannot be resolved to absolute file path because it does not reside in the file system: " + url.toString());
		}
		try {
			return new File(getURI().getSchemeSpecificPart());
		} catch (URISyntaxException ex) {
			// Fallback for URLs that are not valid URIs (should hardly ever
			// happen).
			return new File(url.getFile());
		}
	}

	public InputStream getInputStream() throws IOException {
		URLConnection con = this.url.openConnection();
		con.setUseCaches(false);
		try {
			return con.getInputStream();
		} catch (IOException ex) {
			// Close the HTTP connection (if applicable).
			if (con instanceof HttpURLConnection) {
				((HttpURLConnection) con).disconnect();
			}
			throw ex;
		}
	}

	public Resource createRelative(String relativePath) throws IOException {
		if (relativePath.startsWith("/")) {
			relativePath = relativePath.substring(1);
		}
		return new UrlResource(new URL(this.url, relativePath));
	}
}