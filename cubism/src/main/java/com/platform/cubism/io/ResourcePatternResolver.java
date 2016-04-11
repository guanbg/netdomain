package com.platform.cubism.io;

import static com.platform.cubism.io.Resource.JAR_URL_SEPARATOR;
import static com.platform.cubism.io.Resource.URL_PROTOCOL_CODE_SOURCE;
import static com.platform.cubism.io.Resource.URL_PROTOCOL_JAR;
import static com.platform.cubism.io.Resource.URL_PROTOCOL_WSJAR;
import static com.platform.cubism.io.Resource.URL_PROTOCOL_ZIP;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.platform.cubism.util.Assert;
import com.platform.cubism.util.CubismHelper;
import com.platform.cubism.util.StringUtils;

public class ResourcePatternResolver {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private static final String CLASSPATH_ALL_URL_PREFIX = "classpath*:";
	private static final String CLASSPATH_URL_PREFIX = "classpath:";
	public static final String FILE_URL_PREFIX = "file:";
	private ClassLoader classLoader;
	private AntPathMatcher pathMatcher = new AntPathMatcher();

	public void setPathMatcher(AntPathMatcher pathMatcher) {
		Assert.notNull(pathMatcher, "PathMatcher must not be null");
		this.pathMatcher = pathMatcher;
	}

	public AntPathMatcher getPathMatcher() {
		return this.pathMatcher;
	}

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	public ClassLoader getClassLoader() {
		return (this.classLoader != null ? this.classLoader : CubismHelper.getDefaultClassLoader());
	}

	public Resource getResource(String location) {
		Assert.notNull(location, "Location must not be null");
		if (location.startsWith(CLASSPATH_URL_PREFIX)) {
			return new ClassPathResource(location.substring(CLASSPATH_URL_PREFIX.length()), getClassLoader());
		} else {
			try {
				// Try to parse the location as a URL...
				URL url = new URL(location);
				return new UrlResource(url);
			} catch (MalformedURLException ex) {
				// No URL -> resolve as resource path.
				Resource res = new ClassPathResource(location, getClassLoader());
				try {
					logger.info("URL ->" + res.getURL());
					return res;
				} catch (Exception e) {
					try {
						logger.info("FILE ->" + location);
						File file = new File(location);
						if (!file.exists()) {
							logger.debug("AppRootDir ->" + CubismHelper.getAppRootDir());
							file = new File(CubismHelper.getAppRootDir() + location);
						}
						if (!file.exists()) {
							logger.error("file not exists ->" + file.getAbsolutePath());
							return null;
						}
						logger.info("Absolute FILE ->" + file);
						return new FileSystemResource(file);
					} catch (Exception ec) {
						logger.error(ec.getMessage());
					}
				}
			}
		}
		return null;
	}

	public Resource[] getResources(String locationPattern) throws IOException {
		int prefixEnd = locationPattern.indexOf(":") + 1;
		if (getPathMatcher().isPattern(locationPattern.substring(prefixEnd))) {
			return findPathMatchingResources(locationPattern);
		} else if (locationPattern.startsWith(CLASSPATH_ALL_URL_PREFIX)) {
			return findAllClassPathResources(locationPattern.substring(CLASSPATH_ALL_URL_PREFIX.length()));
		} else {
			return new Resource[] { getResource(locationPattern) };
		}
	}

	private Resource[] findAllClassPathResources(String location) throws IOException {
		String path = location;
		if (path.startsWith("/")) {
			path = path.substring(1);
		}
		Enumeration<URL> resourceUrls = getClassLoader().getResources(path);
		Set<Resource> result = new LinkedHashSet<Resource>(16);
		while (resourceUrls.hasMoreElements()) {
			URL url = resourceUrls.nextElement();
			result.add(new UrlResource(url));
		}
		return result.toArray(new Resource[result.size()]);
	}

	private Resource[] findPathMatchingResources(String locationPattern) throws IOException {
		String rootDirPath = determineRootDir(locationPattern);
		String subPattern = locationPattern.substring(rootDirPath.length());
		Resource[] rootDirResources = getResources(rootDirPath);
		Set<Resource> result = new LinkedHashSet<Resource>(16);
		for (Resource rootDirResource : rootDirResources) {
			if (isJarResource(rootDirResource)) {
				result.addAll(doFindPathMatchingJarResources(rootDirResource, subPattern));
			} else {
				result.addAll(doFindPathMatchingFileResources(rootDirResource, subPattern));
			}
		}
		return result.toArray(new Resource[result.size()]);
	}

	private String determineRootDir(String location) {
		int prefixEnd = location.indexOf(":") + 1;
		int rootDirEnd = location.length();
		while (rootDirEnd > prefixEnd && getPathMatcher().isPattern(location.substring(prefixEnd, rootDirEnd))) {
			rootDirEnd = location.lastIndexOf('/', rootDirEnd - 2) + 1;
		}
		if (rootDirEnd == 0) {
			rootDirEnd = prefixEnd;
		}
		return location.substring(0, rootDirEnd);
	}

	private boolean isJarResource(Resource res) throws IOException {
		URL url = res.getURL();
		String protocol = url.getProtocol();
		return (URL_PROTOCOL_JAR.equals(protocol) || URL_PROTOCOL_ZIP.equals(protocol) || URL_PROTOCOL_WSJAR.equals(protocol) || (URL_PROTOCOL_CODE_SOURCE.equals(protocol) && url
				.getPath().contains(JAR_URL_SEPARATOR)));
	}

	private JarFile getJarFile(String jarFileUrl) throws IOException {
		if (jarFileUrl.startsWith(FILE_URL_PREFIX)) {
			try {
				return new JarFile(new URI(StringUtils.replace(jarFileUrl, " ", "%20")).getSchemeSpecificPart());
			} catch (URISyntaxException ex) {
				// Fallback for URLs that are not valid URIs (should hardly ever
				// happen).
				return new JarFile(jarFileUrl.substring(FILE_URL_PREFIX.length()));
			}
		} else {
			return new JarFile(jarFileUrl);
		}
	}

	private Set<Resource> doFindPathMatchingJarResources(Resource rootDirResource, String subPattern) throws IOException {
		URLConnection con = rootDirResource.getURL().openConnection();
		JarFile jarFile;
		String jarFileUrl;
		String rootEntryPath;
		boolean newJarFile = false;

		if (con instanceof JarURLConnection) {
			// Should usually be the case for traditional JAR files.
			JarURLConnection jarCon = (JarURLConnection) con;
			jarCon.setUseCaches(false);
			jarFile = jarCon.getJarFile();
			jarFileUrl = jarCon.getJarFileURL().toExternalForm();
			JarEntry jarEntry = jarCon.getJarEntry();
			rootEntryPath = (jarEntry != null ? jarEntry.getName() : "");
		} else {
			// No JarURLConnection -> need to resort to URL file parsing.
			// We'll assume URLs of the format "jar:path!/entry", with the
			// protocol
			// being arbitrary as long as following the entry format.
			// We'll also handle paths with and without leading "file:" prefix.
			String urlFile = rootDirResource.getURL().getFile();
			int separatorIndex = urlFile.indexOf(JAR_URL_SEPARATOR);
			if (separatorIndex != -1) {
				jarFileUrl = urlFile.substring(0, separatorIndex);
				rootEntryPath = urlFile.substring(separatorIndex + JAR_URL_SEPARATOR.length());
				jarFile = getJarFile(jarFileUrl);
			} else {
				jarFile = new JarFile(urlFile);
				jarFileUrl = urlFile;
				rootEntryPath = "";
			}
			newJarFile = true;
		}

		try {
			if (!"".equals(rootEntryPath) && !rootEntryPath.endsWith("/")) {
				// Root entry path must end with slash to allow for proper
				// matching.
				// The Sun JRE does not return a slash here, but BEA JRockit
				// does.
				rootEntryPath = rootEntryPath + "/";
			}
			Set<Resource> result = new LinkedHashSet<Resource>(8);
			for (Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements();) {
				JarEntry entry = entries.nextElement();
				String entryPath = entry.getName();
				if (entryPath.startsWith(rootEntryPath)) {
					String relativePath = entryPath.substring(rootEntryPath.length());
					if (getPathMatcher().match(subPattern, relativePath)) {
						result.add(rootDirResource.createRelative(relativePath));
					}
				}
			}
			return result;
		} finally {
			// Close jar file, but only if freshly obtained -
			// not from JarURLConnection, which might cache the file reference.
			if (newJarFile) {
				jarFile.close();
			}
		}
	}

	private Set<Resource> doFindPathMatchingFileResources(Resource rootDirResource, String subPattern) throws IOException {

		File rootDir;
		try {
			rootDir = rootDirResource.getFile().getAbsoluteFile();
		} catch (IOException ex) {
			return Collections.emptySet();
		}
		logger.info("rootDir ->" + rootDir);
		if (!rootDir.exists() || !rootDir.isDirectory() || !rootDir.canRead()) {
			return Collections.emptySet();
		}
		String fullPattern = StringUtils.replace(rootDir.getAbsolutePath(), File.separator, "/");
		if (!subPattern.startsWith("/")) {
			fullPattern += "/";
		}
		fullPattern = fullPattern + StringUtils.replace(subPattern, File.separator, "/");
		logger.debug("fullPattern ->" + fullPattern);
		Set<File> matchingFiles = new LinkedHashSet<File>(8);
		doRetrieveMatchingFiles(fullPattern, rootDir, matchingFiles);

		Set<Resource> result = new LinkedHashSet<Resource>(matchingFiles.size());
		for (File file : matchingFiles) {
			result.add(new FileSystemResource(file));
		}
		return result;
	}

	private void doRetrieveMatchingFiles(String fullPattern, File dir, Set<File> result) throws IOException {
		File[] dirContents = dir.listFiles();
		if (dirContents == null) {
			return;
		}
		for (File content : dirContents) {
			String currPath = StringUtils.replace(content.getAbsolutePath(), File.separator, "/");
			logger.debug("currPath ->" + currPath);
			if (content.isDirectory() && getPathMatcher().matchStart(fullPattern, currPath + "/")) {
				if (!content.canRead()) {
					;
				} else {
					doRetrieveMatchingFiles(fullPattern, content, result);
				}
			}
			if (getPathMatcher().match(fullPattern, currPath)) {
				logger.debug("result ->" + fullPattern+"<==>"+currPath);
				result.add(content);
			}
		}
	}
}