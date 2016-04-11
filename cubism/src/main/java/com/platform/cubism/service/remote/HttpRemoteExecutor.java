package com.platform.cubism.service.remote;

import static com.platform.cubism.util.StringUtils.hasText;
import static com.platform.cubism.service.convert.ConvertManager.IN;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.slf4j.LoggerFactory;

import com.platform.cubism.CubismException;
import com.platform.cubism.SystemConfig;
import com.platform.cubism.base.CStruc;
import com.platform.cubism.base.Json;
import com.platform.cubism.util.CubismHelper;

public class HttpRemoteExecutor implements RemoteExecutor {
	private final org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());
	private static final String CHARSET = "UTF-8";
	private String url;
	private int timeout = 1000 * 10;
	private int status; // 0-空闲 1-使用 2-连接无效3-发送数据错误4-接收数据错误
	private URLConnection conn;

	public HttpRemoteExecutor() {
		this(SystemConfig.getValue("call.SoftPlatformUrl"),SystemConfig.getValue("call.SoftPlatformTimeout"));
	}

	public HttpRemoteExecutor(String url, String timeout) {
		if(hasText(timeout)){
			this.timeout = Integer.parseInt(timeout);
		}
		this.url = url;
		status = 1;
		if (!hasText(this.url)) {
			if (logger.isDebugEnabled()) {
				logger.debug("url为空，无法 建立 连接");
			}
			status = 2;
			throw new CubismException("读取的URL为空，不能建立 连接");
		}
		try {
			URL remote = new URL(this.url);
			conn = remote.openConnection();

			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
			conn.setRequestProperty("contentType", "text/x-json; charset="+CHARSET);
			conn.setDoOutput(true); 
			conn.setDoInput(true);

			conn.setConnectTimeout(this.timeout);
			if (logger.isDebugEnabled()) {
				logger.debug("建立 连接成功：" + url);
			}
			status = 0;
		} catch (MalformedURLException e) {
			status = 2;
			conn = null;
			logger.error("open the " + url + " error:" + e.getMessage());
			throw new CubismException("url不正确，建立连接错误：" + e.getMessage());
		} catch (IOException e) {
			status = 2;
			conn = null;
			logger.error("open the " + url + " error:" + e.getMessage());
			throw new CubismException("打开url错误，建立连接失败：" + e.getMessage());
		}
	}

	public Json execute(Json in) {
		status = 1;
		CStruc cs = in.getStruc(IN);
		cs.setName(null);// 返回报文的顶层直接为:{......}的形式
		if (logger.isInfoEnabled()) {
			logger.info("=====>>>开始连接" + url + "，发送报文为：" + cs.toString());
		}
		String ret = sendAndRead(cs.toJson());
		if (logger.isInfoEnabled()) {
			logger.info("=====>>>连接" + url + "，返回的数据为：" + ret);
		}
		Json out = CubismHelper.parseJson(ret);
		if (logger.isInfoEnabled()) {
			logger.info("=====>>>连接" + url + "，转换后的数据为：" + out);
		}
		status = 0;
		return out;
	}

	public void reset() {
		status = 0;
	}

	public boolean isInvalidate() {
		return status != 0 && status != 1;
	}

	public boolean iFFreeThenBussy() {
		if (status == 0) {
			status = 1;
			return true;
		}
		return false;
	}

	private String sendAndRead(String data) {
		if (conn == null) {
			logger.error("当前连接不可用，无法发送数据，系统返回");
			throw new CubismException("当前连接不可用，无法发送数据，系统返回");
		}
		StringBuffer sb = new StringBuffer();

		BufferedReader in = null;
		PrintWriter out = null;
		try {
			out = new PrintWriter(conn.getOutputStream());
			out.print(data);
			out.flush();
		} catch (IOException e) {
			status = 3;
			conn = null;
			logger.error("发送数据错误：" + e.getMessage());
			throw new CubismException("发送数据错误：" + e.getMessage());
		}

		try {
			in = new BufferedReader(new InputStreamReader(conn.getInputStream(),CHARSET));
			String line = null;
			while ((line = in.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			status = 4;
			conn = null;
			logger.error("接收数据错误：" + e.getMessage());
			throw new CubismException("接收数据错误：" + e.getMessage());
		} finally {
			if (out != null) {
				out.close();
				out = null;
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					in = null;
					logger.error("接收数据完毕，关闭输入流错误：" + e.getMessage());
				}
				in = null;
			}
		}

		return sb.toString();
	}
}