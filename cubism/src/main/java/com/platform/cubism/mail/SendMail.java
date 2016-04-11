package com.platform.cubism.mail;

import static com.platform.cubism.SystemConfig.getValue;
import static com.platform.cubism.util.CubismHelper.getAppRootDir;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.platform.cubism.CubismException;
import com.platform.cubism.base.CField;
import com.platform.cubism.base.CStruc;
import com.platform.cubism.base.Json;
import com.platform.cubism.base.JsonFactory;
import com.platform.cubism.service.CustomService;
import com.platform.cubism.struc.MsgLevel;
import com.platform.cubism.struc.RetStatus;
import com.platform.cubism.util.HeadHelper;
import com.platform.cubism.util.SecurityHelper;
import com.platform.cubism.util.SendEmail;

public class SendMail implements CustomService {
	private static String root = getAppRootDir(true) + "template/";

	@Override
	public Json execute(Json in) throws CubismException {
		Json ret = JsonFactory.create();

		try {
			String system_name = getValue("sys.config.systemname");
			String system_url = getValue("sys.config.sysabout");
			String tmplname = getValue(in.getFieldValue("tmplname"));

			String username = in.getFieldValue("company_name");
			String title = in.getFieldValue("title");
			String key = null;

			CStruc parameters = in.getStruc("parameters");
			if (parameters != null && !parameters.isEmpty()) {
				key = "systime=" + System.currentTimeMillis();
				for (CField p : parameters.getField().values()) {
					key += "&" + p.getName() + "=" + p.getValue();
				}
				key = SecurityHelper.bytes2HexString(key.getBytes());
			} else {
				key = SecurityHelper.bytes2HexString((System.currentTimeMillis() + "&" + in.getFieldValue("contractor_id") + "&" + in.getFieldValue("email_new")).getBytes());
			}
			String url = system_url + in.getFieldValue("url") + "?" + key;

			in.addField("system_name", system_name);
			in.addField("system_url", url);
			in.addField("username", username);
			in.addField("title", title);

			String subject = system_name + "_" + title + "_的通知";
			String receiver = in.getFieldValue("email");
			String content = getTmpl(tmplname, in);
			
			if(content == null || content.length() <= 0){
				content = title +"<br/><a href='"+url+"' target='_blank'>"+url+"</a>";
			}
			
			if (logger.isDebugEnabled()) {
				logger.debug("receiver=" + receiver);
				logger.debug("subject=" + subject);
				logger.debug("content=" + content);
			}
			SendEmail.getInstance().sendMail(receiver, subject, content, true);
			ret.addStruc(HeadHelper.createRetHead("SendMail", "00000", "发送邮件成功!", MsgLevel.B, RetStatus.SUCCESS));
		} catch (Throwable t) {
			if (logger.isDebugEnabled()) {
				t.printStackTrace();
			}
			logger.error(t.getMessage());

			ret.addStruc(HeadHelper.createRetHead("SendMail", "1093", "发送邮件错误，请检查电子邮箱是否正确!", MsgLevel.D));
		}
		return ret;
	}

	private String getTmpl(String tmplname, Json data) {
		String tmplPath = root + tmplname;
		
		if (logger.isDebugEnabled()) {
			logger.debug("tmplPath=" + tmplPath);
		}

		File f = new File(tmplPath);
		if (!f.exists()) {
			logger.info("邮件模板没有找到：" + tmplPath);
			return null;
		}

		StringBuilder sb = new StringBuilder();
		BufferedReader bf = null;
		try {
			bf = new BufferedReader(new InputStreamReader(new FileInputStream(f),"UTF-8"));
			String tmp = null;
			while((tmp = bf.readLine()) != null) {
				sb.append(tmp);
			}
		} catch (Exception e) {
			logger.error("邮件模板读取错误：" + e.getMessage());
			if (logger.isDebugEnabled()) {
				e.printStackTrace();
			}
			return null;
		} finally {
			try {
				if (bf != null) {
					bf.close();
				}
			} catch (Throwable t) {
				logger.info("邮件模板读取错误：" + t.getMessage());
			}
		}

		if (sb.length() <= 0) {
			logger.error("邮件模板读取错误");
			return null;
		}
		
		Pattern pattern = Pattern.compile("\\{([^{}]+?)\\}", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(sb.toString());
		StringBuffer buf = new StringBuffer();
		while (matcher.find()) {
			matcher.appendReplacement(buf, data.getFieldValue(matcher.group(1).toLowerCase()));
			if (logger.isDebugEnabled()) {
				logger.debug(matcher.group(1) + "=" + data.getFieldValue(matcher.group(1).toLowerCase()));
			}
		}
		matcher.appendTail(buf);

		return buf.toString();
	}
}
