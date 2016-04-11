package com.platform.cubism.util;

import static com.platform.cubism.SystemConfig.getValue;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SendEmail {
	private final Logger logger = LoggerFactory.getLogger(SendEmail.class);
	/**
	 * 配置信息 
	 * email.ssl.tls=ssl 
	 * email.name=guanbg@163.com 
	 * email.pswd=guanbg
	 * email.smtp.host=smtp.exmail.qq.com
	 * email.content.title=建设单位注册用户确认
	 * email.tmpl.content=<h4>尊敬的用户您好！</h4><p>你的用户名为：{username}</p><p>重新设置密码的地址为：{url}</p><a href='{url}' target='_blank'>重新设置密码</a>
	 */
	private final String emailName;
	private final String emailPswd;
	private final String emailSmtp;
	private final String emailSslTls;

	private SendEmail() {
		emailName = getValue("email.name");
		emailPswd = getValue("email.pswd");
		emailSmtp = getValue("email.smtp.host");
		emailSslTls = getValue("email.ssl.tls");
	}

	private static class LazyHolder {// 只有当调用的时候才进行初始化
		private static final SendEmail INSTANCE = new SendEmail();
	}

	public static final SendEmail getInstance() {
		return LazyHolder.INSTANCE;
	}
	
	public void sendMail(String receiver, String subject, String content, boolean isHTML) throws Throwable{
		if("tls".equalsIgnoreCase(emailSslTls)){
			sendMailTLS(receiver, subject, content, isHTML);
		}
		else{//ssl
			sendMailSSL(receiver, subject, content, isHTML);
		}
	}
	
	private void sendMailTLS(String receiver, String subject, String content, boolean isHTML) throws Throwable {
		Properties props = new Properties();
		props.put("mail.smtp.host", emailSmtp);
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		// props.put("mail.smtp.port", "587");
		if (logger.isDebugEnabled()) {
			logger.debug("emailName：" + emailName);
			logger.debug("emailPswd：" + emailPswd);
			logger.debug("emailSmtp：" + emailSmtp);
		}
		Session session = Session.getInstance(props, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(emailName, emailPswd);
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(emailName));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiver));
			message.setSubject(subject);
			if (isHTML) {
				message.setContent(content, "text/html; charset=utf-8");
			} else {
				message.setText(content);
			}

			Transport.send(message);
		} catch (Throwable t) {
			if (logger.isDebugEnabled()) {
				t.printStackTrace();
			}
			logger.error(t.getMessage());
			throw t;
		}
	}

	private void sendMailSSL(String receiver, String subject, String content, boolean isHTML) throws Throwable {
		Properties props = new Properties();
		props.put("mail.smtp.host", emailSmtp);
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		// props.put("mail.smtp.socketFactory.port", "465");
		// props.put("mail.smtp.port", "465");
		if (logger.isDebugEnabled()) {
			logger.debug("emailName：" + emailName);
			logger.debug("emailPswd：" + emailPswd);
			logger.debug("emailSmtp：" + emailSmtp);
		}
		Session session = Session.getDefaultInstance(props, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(emailName, emailPswd);
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(emailName));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiver));
			message.setSubject(subject);
			if (isHTML) {
				message.setContent(content, "text/html; charset=utf-8");
			} else {
				message.setText(content);
			}

			Transport.send(message);
		} catch (Throwable t) {
			if (logger.isDebugEnabled()) {
				t.printStackTrace();
			}
			logger.error(t.getMessage());
			throw t;
		}
	}
}