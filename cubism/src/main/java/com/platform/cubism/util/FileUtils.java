package com.platform.cubism.util;

import static com.platform.cubism.util.CubismHelper.getUserPath;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.platform.cubism.SystemConfig;
import com.platform.cubism.base.CArray;
import com.platform.cubism.base.CStruc;
import com.platform.cubism.base.Json;
import com.platform.cubism.base.JsonFactory;

public class FileUtils {
	public static boolean zip(String srcPath, String zipPath, String zipFileName,boolean hasRoot) {
		if (srcPath == null || srcPath.length() <= 0 || zipPath == null || zipPath.length() <= 0 || zipFileName == null || zipFileName.length() <= 0) {
			return false;
		}
		
		ZipOutputStream zos = null;
		try {
			File srcFile = new File(srcPath);

			// 判断压缩文件保存的路径是否为源文件路径的子文件夹，如果是，则抛出异常（防止无限递归压缩的发生）
			if (srcFile.isDirectory() && zipPath.indexOf(srcPath) != -1) {
				return false;
			}

			// 判断压缩文件保存的路径是否存在，如果不存在，则创建目录
			File zipDir = new File(zipPath);
			if (!zipDir.exists() || !zipDir.isDirectory()) {
				zipDir.mkdirs();
			}

			// 创建压缩文件保存的文件对象
			String zipFilePath = zipPath + File.separator + zipFileName;
			File zipFile = new File(zipFilePath);
			if (zipFile.exists()) {
				zipFile.delete();
			}

			zos = new ZipOutputStream(new CheckedOutputStream(new FileOutputStream(zipFile), new CRC32()));
			if(hasRoot){
				zip("", srcFile, zos);// 调用递归压缩方法进行目录或文件压缩
			}
			else{
				zip(null, srcFile, zos);
			}
			zos.flush();
		} catch (Exception e) {
			return false;
		} finally {
			try {
				if (zos != null) {
					zos.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	private static void zip(String parentPath, File file, ZipOutputStream zos) throws Exception {
		if (file == null) {
			return;
		}

		if (file.isFile()) {// 如果是文件，则直接压缩该文件
			int count, bufferLen = 1024 * 1024;
			byte buffer[] = new byte[bufferLen];

			ZipEntry entry = new ZipEntry(parentPath + file.getName());
			zos.putNextEntry(entry);
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
			while ((count = bis.read(buffer, 0, bufferLen)) != -1) {
				zos.write(buffer, 0, count);
			}
			bis.close();
			zos.closeEntry();
		}
		else {// 如果是目录，则压缩整个目录
			File[] childFileList = file.listFiles();// 压缩目录中的文件或子目录
			if(parentPath == null){
				for (int n=0; n<childFileList.length; n++) {
					parentPath = "";
					zip(parentPath, childFileList[n], zos);
				}
			}
			else{
				for (int n=0; n<childFileList.length; n++) {
					parentPath += file.getName() + File.separator;
					zip(parentPath, childFileList[n], zos);
				}
			}
		}
	}

	public static long copyFile(String srcfile, String dscfile) {
		if (srcfile == null || srcfile.length() <= 0 || dscfile == null || dscfile.length() <= 0) {
			return 0l;
		}
		File src = new File(srcfile);
		File dsc = new File(dscfile);

		return copyFile(src, dsc);
	}

	public static long copyFile(File srcfile, File dscfile) {
		long bytesum = 0l;
		try {
			if (!srcfile.exists()) {
				return bytesum;
			}
			if (dscfile.exists()) {
				dscfile.delete();
			} else if (!dscfile.canWrite()) {
				File dir = new File(MultipartUtils.extractFilePath(dscfile.getAbsolutePath()));
				dir.mkdirs();
			}
			dscfile.createNewFile();

			InputStream inStream = new FileInputStream(srcfile);
			FileOutputStream fs = new FileOutputStream(dscfile);
			byte[] buffer = new byte[1024 * 1024 * 10];
			int byteread = 0;
			while ((byteread = inStream.read(buffer)) != -1) {
				bytesum += byteread;
				fs.write(buffer, 0, byteread);
			}
			inStream.close();
			fs.close();
		} catch (Exception e) {
			bytesum = 0l;
			e.printStackTrace();
		}
		return bytesum;
	}

	public static boolean moveFile(String srcPathName, String dscPathName) {
		if (srcPathName == null || srcPathName.length() <= 0 || dscPathName == null || dscPathName.length() <= 0) {
			return false;
		}
		File srcfile = new File(srcPathName);
		File dscfile = new File(dscPathName);
		srcfile.renameTo(dscfile);
		srcfile = null;
		dscfile = null;
		return true;
	}

	public static boolean delDir(File dir) {
		if (!dir.exists()) {
			return true;
		}
		if (dir.isDirectory()) {
			File children[] = dir.listFiles();
			boolean flag = true;
			for (int i = 0; i < children.length; i++) {
				if (!delDir(children[i])) {
					flag = false;
				}
			}
			if (!flag)
				return false;
		}
		try {
			return dir.delete();
		} catch (Throwable t) {
			return false;
		}
	}

	public static boolean delFile(String fullPathName) {
		if ((fullPathName == null) || fullPathName.length() <= 0) {
			return false;
		}
		return delDir(new File(fullPathName));
	}

	public static Json delFile(CArray data) {
		Json json = JsonFactory.create();
		boolean ret = true;

		if ((data == null) || data.isEmpty()) {
			json.addField("ret", String.valueOf(ret));
			return json;
		}

		String fullPathName;
		String rootPath = SystemConfig.getUploadPath();
		for (CStruc cs : data.getRecords()) {
			if (cs.getField("fileidentify").isEmpty()) {
				continue;
			}
			fullPathName = rootPath + cs.getFieldValue("filepath") + cs.getFieldValue("fileidentify");
			if (!delFile(fullPathName))
				ret = false;
		}

		json.addField("ret", String.valueOf(ret));
		return json;
	}

	public static void copyFile(CArray src, CStruc sysHead) {
		if (src == null || src.isEmpty()) {
			return;
		}
		File srcFile, dscFile;
		String dscfilePath = getUserPath(sysHead);
		String rootPath = SystemConfig.getUploadPath();
		for (CStruc cs : src.getRecords()) {
			if ("1".equals(cs.getFieldValue("isdir"))) {// 目录跳过
				continue;
			}
			srcFile = new File(rootPath + cs.getFieldValue("filepath") + cs.getFieldValue("fileidentify"));
			String dscFileIdentity = getCopyFileName(rootPath + dscfilePath, cs.getFieldValue("fileidentify"));
			dscFile = new File(rootPath + dscfilePath + dscFileIdentity);
			if (copyFile(srcFile, dscFile) <= 0l) {
				src.removeRow(cs);
			}
			cs.getField("filepath").setValue(dscfilePath);
			cs.getField("fileidentify").setValue(dscFileIdentity);
		}
	}

	private static String getCopyFileName(String filePath, String fileName) {
		if (fileName == null || fileName.length() <= 0) {
			fileName = String.valueOf(System.nanoTime());
		}
		File f;
		for (;;) {
			f = new File(filePath + fileName);
			if (!f.exists()) {
				return fileName;
			}
			fileName += "_1";
		}
	}
}
