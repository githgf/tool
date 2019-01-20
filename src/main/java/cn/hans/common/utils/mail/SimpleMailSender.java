package cn.hans.common.utils.mail;

import cn.hans.common.constant.CommonConstant;
import cn.hans.common.utils.DateUtils;
import cn.hans.common.utils.FileUtil;
import cn.hans.common.utils.LocaleType;
import org.apache.commons.io.FileExistsException;
import org.apache.commons.lang3.StringUtils;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.internet.MimeMessage.RecipientType;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 *
 * @author          hans
 * 邮件发送
 */
public class SimpleMailSender {

	/**
	 * 发送邮件的props文件
	 */
	private final transient Properties props = System.getProperties();
	/**
	 * 邮件服务器登录验证
	 */
	private transient MailAuthenticator authenticator;

	/**
	 * 邮箱session
	 */
	private transient Session session;


	public final static SimpleMailSender WISREADY_SENDER =  new SimpleMailSender(
			"smtp.exmail.qq.com",
			"gaofan@windforceisready.com",
			"Com.123feng",
			"465",
			true
	);



	/**
	 * 初始化邮件发送器
	 * 
	 * @param smtpHostName
	 *            SMTP邮件服务器地址
	 * @param username
	 *            发送邮件的用户名(地址)
	 * @param password
	 *            发送邮件的密码
	 */
	public SimpleMailSender(final String smtpHostName, final String username, final String password, final String port,final boolean isSsl) {
		init(username, password, smtpHostName,port,isSsl);
	}

	/**
	 * 初始化邮件发送器
	 * 
	 * @param username
	 *            发送邮件的用户名(地址)，并以此解析SMTP服务器地址
	 * @param password
	 *            发送邮件的密码
	 */
	public SimpleMailSender(final String username, final String password) {
		// 通过邮箱地址解析出smtp服务器，对大多数邮箱都管用
		final String smtpHostName = "smtp." + username.split("@")[1];
		init(username, password, smtpHostName,null,false);

	}

	/**
	 * 初始化
	 * 
	 * @param username
	 *            发送邮件的用户名(地址)
	 * @param password
	 *            密码
	 * @param smtpHostName
	 *            SMTP主机地址
	 */
	private void init(String username, String password, String smtpHostName, String port,boolean isSsl) {
		// 初始化props
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.host", smtpHostName);
		if (port != null) {
			props.put("mail.smtp.port", port);
		}
		props.put("mail.smtp.ssl.enable",isSsl);
//		props.put("mail.transport.protocol", "smtp");
		if (!smtpHostName.contains("163")){
			props.put("mail.smtp.starttls.enable", "true");
		}
		// 验证
		authenticator = new MailAuthenticator(username, password);
		// 创建session
		session = Session.getInstance(props, authenticator);

		session.setDebug(true);
	}



	/**
	 * 带附件的邮件发送
	 * @param recipients				收件人
	 * @param subject					主题
	 * @param content					内容
	 * @throws AddressException			{@link AddressException}
	 * @throws MessagingException		{@link MessagingException}
	 */
	public void send(List<String> recipients, String subject, String content, List<String> filePathList) throws MessagingException, UnsupportedEncodingException {
		try {
			// 创建mime类型邮件
			final MimeMessage message = new MimeMessage(session);
			// 设置发信人
			message.setFrom(new InternetAddress(authenticator.getUsername()));
			// 设置收件人们
			final int num = recipients.size();
			InternetAddress[] addresses = new InternetAddress[num];
			for (int i = 0; i < num; i++) {
				addresses[i] = new InternetAddress(recipients.get(i));
			}
			message.setRecipients(RecipientType.TO, addresses);
			// 设置主题
			message.setSubject(subject);
			// 设置邮件内容
			Multipart multipart = new MimeMultipart();
			if (content != null) {
				BodyPart contentPart = new MimeBodyPart();
				contentPart.setContent(content, "text/html; charset=utf-8");
				multipart.addBodyPart(contentPart);
			}

			for (int i = 0; i < filePathList.size(); i++) {

				File file = new File(filePathList.get(i));
				long totalFileSize = file.length();
				//如果有多个文件或者文件大小超标
				if (!FileUtil.verifyFileSize(totalFileSize,getMaxFileSize())){

					String s = FileUtil.zipFiles(null, null, filePathList.toArray(new String[]{}));
					file = new File(s);
				}

				// 设置附件
				MimeBodyPart fileBody = new MimeBodyPart();
				DataSource source = new FileDataSource(file);
				fileBody.setDataHandler(new DataHandler(source));
				fileBody.setFileName(file.getName());
				multipart.addBodyPart(fileBody);
			}


			message.setContent(multipart);

			// 发送
			Transport.send(message);
		} catch (FileExistsException e) {
			e.printStackTrace();
		}
	}

	//根据不同的smtp服务器，返回不同的最大附件大小
	public int getMaxFileSize(){
		String hostName = props.get("mail.smtp.host").toString();

		int size = 8;

		switch (hostName){
			case "smtp.gomrwind.com" : size = 8;break;
			case "smtp.qq.com" : size = 15;break;
		}

		return size;
	}


	public static void main(String[] args){

		//经测试发送邮件附件的大小由邮件服务器决定
		try {
			SimpleMailSender.WISREADY_SENDER.send(Arrays.asList("hgf1641197217@163.com"),
					"运单2",
					"为系统时间，麻烦请您查收谢谢",
					Arrays.asList("C:\\Users\\Administrator\\Desktop\\hk_ryd_expressData_2018_09_13.xls")
			);
		} catch (MessagingException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}


	}
}
