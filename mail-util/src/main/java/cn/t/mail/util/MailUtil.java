package cn.t.mail.util;

import java.util.Arrays;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import cn.t.mail.util.exceptions.MailUtilException;
import cn.t.mail.util.settings.Account;
import cn.t.mail.util.settings.Mail;
import cn.t.mail.util.settings.MultiContent;
import cn.t.mail.util.type.NodeType;

/**
 * 邮件发送工具包 已知bug:<br/>
 * 163邮箱出现554 DT:SPM时建议抄送一份发送给自己。原因是163误把要发送的邮件当成垃圾邮件。 163邮箱文本+附件接收正常
 * 163邮箱文本+文本->第二个文本会变成附件 -> 纯文本发送时尽量使用textContent设置 live邮箱同163一样的问题
 * TYPE类型为IMAGE尽量不要使用
 * 
 * @author tmq
 *
 */
public class MailUtil {
	public static void main(String[] args) throws AddressException {
		Account info = new Account("18662249830@163.com", "tmq7787",
				Arrays.asList(new String[] { "taominqia@live.com", "taominqi@live.com" }), "XVYJXFYFBSHDPMII");
		info.setCc(Arrays.asList(new String[] { "18662249830@163.com" }));
		Mail mail = new Mail();
		mail.setSentDate(new Date());
		mail.setSubject("--------------666666---------");
		// new MultiContent(NodeType.TEXT, "欲穷千里目"),
		// new MultiContent(NodeType.MIXED, "更上一层楼",
		// "C:/Users/taomi/Desktop/slide1.jpg").setNodeImgUrl("C:/Users/taomi/Desktop/slide1.jpg")
		mail.setMultiContent(new MultiContent(NodeType.TEXT, "欲穷千里目", "C:/Users/taomi/Desktop/slide1.jpg"));
		new MailUtil(info, mail, "smtp.163.com").send();
	}

	/**
	 * 文本类型
	 */
	private static final String CONTENT_TYPE = "text/html;charset=utf-8";

	/**
	 * 收发件人信息
	 */
	private Account account;

	/**
	 * 邮件信息
	 */
	private Mail mail;

	/**
	 * 协议等必要信息
	 */
	private Properties properties;

	/**
	 * 发件信息配置<br/>
	 * 发送邮件前需要先调用此方法配置各项信息
	 * @param account 收发件人信息
	 * @param mail 邮件对象
	 * @param mailHost 邮件服务器主机地址
	 * @return
	 */
	public MailUtil(Account account, Mail mail, String mailHost) {
		Properties props = new Properties();
		// 认证方式
		props.setProperty("mail.smtp.auth", "true");
		// 传输协议
		props.setProperty("mail.transport.protocol", "smtp");
		// 发件邮箱smtp服务地址
		props.setProperty("mail.smtp.host", mailHost);
		this.properties = props;
		this.account = account;
		this.mail = mail;
	}

	/**
	 * 发送邮件
	 */
	public void send() {
		if (this.properties == null) {
			throw new MailUtilException("必要信息未设置");
		}
		if (this.account == null) {
			throw new MailUtilException("邮箱信息未设置");
		}
		if (this.mail == null) {
			throw new MailUtilException("邮件信息未设置");
		}
		// 上下文Session对象
		Session session = Session.getInstance(this.properties);
		// 设置控制台显示调试信息
		session.setDebug(true);
		// 邮件实例对象
		Message mail = null;
		// 邮件传输对象Transport
		Transport transport = null;
		try {
			mail = this.createMail(session);
			transport = session.getTransport();
			// 连接发件服务器
			transport.connect(this.account.getFrom(), this.account.getPassword());
			// 发送邮件----发送到所有收件人地址，message.getAllRecipients() 获取到的是在创建邮件对象时添加的所有收件人, 抄送人, 密送人
			transport.sendMessage(mail, mail.getAllRecipients());
			// 关闭连接
			transport.close();
		} catch (Exception e) {
			throw new MailUtilException("邮件发送异常", e);
		} finally {
			if (transport != null) {
				try {
					transport.close();
				} catch (MessagingException e) {
					transport = null;
					throw new MailUtilException("连接关闭异常", e);
				}
			}
		}
	}

	/**
	 * 创建邮件
	 * 
	 * @param session 邮件会话
	 * @return 邮件实例
	 * @throws Exception 异常
	 */
	private MimeMessage createMail(Session session) throws Exception {
		// 邮件实例
		MimeMessage message = new MimeMessage(session);
		// 发件人地址
		message.setFrom(new InternetAddress(this.account.getFrom()));
		// 收件人
		// message.setRecipient(MimeMessage.RecipientType.TO, new
		// InternetAddress(this.account.getTo()));
		if (this.account.getTo() != null && !this.account.getTo().isEmpty()) {
			// 抄送人数组
			InternetAddress[] to = this.account.getTo().stream().map(c -> {
				try {
					return new InternetAddress(c);
				} catch (AddressException e) {
					e.printStackTrace();
				}
				return null;
			}).filter(c -> c != null).collect(Collectors.toList())
					// 这里转换的数组长度设置为0，实际运行中list和数组指定长度不匹配时会动态添加数组长度
					.toArray(new InternetAddress[0]);
			message.setRecipients(MimeMessage.RecipientType.TO, to);
		}
		// 抄送
		if (this.account.getCc() != null && !this.account.getCc().isEmpty()) {
			// 抄送人数组
			InternetAddress[] cc = this.account.getCc().stream().map(c -> {
				try {
					return new InternetAddress(c);
				} catch (AddressException e) {
					e.printStackTrace();
				}
				return null;
			}).filter(c -> c != null).collect(Collectors.toList())
					// 这里转换的数组长度设置为0，实际运行中list和数组指定长度不匹配时会动态添加数组长度
					.toArray(new InternetAddress[0]);
			message.setRecipients(MimeMessage.RecipientType.CC, cc);
		}
		// 密送
		if (this.account.getBcc() != null && !this.account.getBcc().isEmpty()) {
			// 密送人数组
			InternetAddress[] bcc = this.account.getBcc().stream().map(c -> {
				try {
					return new InternetAddress(c);
				} catch (AddressException e) {
					e.printStackTrace();
				}
				return null;
			}).filter(c -> c != null).collect(Collectors.toList())
					// 这里转换的数组长度设置为0，实际运行中list和数组指定长度不匹配时会动态添加数组长度
					.toArray(new InternetAddress[0]);
			message.setRecipients(MimeMessage.RecipientType.BCC, bcc);
			// 也可以使用addRecipient或者addRecipients添加
		}
		// 发件人
		if (this.account.getNickName() == null || this.account.getNickName().isBlank()) {
			message.setFrom(new InternetAddress(this.account.getFrom()));
		} else {
			message.setFrom(new InternetAddress(this.account.getFrom(), this.account.getNickName(), "UTF-8"));
		}
		// 邮件主题
		message.setSubject(this.mail.getSubject(), "UTF-8");
		// 纯文本邮件
		if (this.mail.getMultiContent() == null) {
			message.setContent(this.mail.getTextContent(), CONTENT_TYPE);
		} else {
			// 复杂邮件
			message.setContent(this.createBody());
		}
		// 发件时间,日期延后可以设置延时发送
		message.setSentDate(this.mail.getSentDate());
		// 保存设置
		message.saveChanges();
		return message;
	}

	/**
	 * 创建正文文本 参考
	 * 
	 * @return 复合文本内容
	 * @throws Exception
	 */
	private MimeMultipart createBody() throws Exception {
		// 混合正文类型 多个MimeBodyPart组装而成
		MimeMultipart mimeMultipart = new MimeMultipart();
		// 类型内容节点
		MimeBodyPart part = null;
		// 是否有附件或者图片
		MultiContent node = this.mail.getMultiContent();
		part = new MimeBodyPart();
		switch (node.getType()) {
		case TEXT:
			// MimeMultipart text = new MimeMultipart("related");
			// 文本节点
			// MimeBodyPart textNode = new MimeBodyPart();
			// textNode.setContent("<p>" + node.getBody().toString() + "</p>" +
			// wrap(node.isWrap()), CONTENT_TYPE);
			// text.addBodyPart(textNode);
			// part.setContent(text);

			// 这里封装了一层MimeMultipart是为了如果有文本+图片显示的邮件时，可以正常显示文本
			part.setContent("<p>" + node.getBody().toString() + "</p>", CONTENT_TYPE);
			break;
		case MIXED:
			// IMAGE类型作为单独一块内容(MimeMultipart)发送的时候没有问题,再加上另一块MimeMultipart会导致另一块变成附件
			// 文本与图片组合的部分
			MimeMultipart textAndImage = new MimeMultipart();
			// 文本节点
			MimeBodyPart textNodePart = new MimeBodyPart();
			// 图片节点
			MimeBodyPart imageNodePart = new MimeBodyPart();
			// 读取本地图片
			DataHandler dh = new DataHandler(new FileDataSource(node.getImgUrl()));
			// 将图片数据添加到节点
			imageNodePart.setDataHandler(dh);
			// 节点设置唯一编号（可以在文本节点将引用该ID）
			String contentId = UUID.randomUUID().toString();
			imageNodePart.setContentID("<" + contentId + ">");
			// 使用cid获取上面图片节点的id
			textNodePart.setContent("<p>" + node.getBody() + "</p><img src='cid:" + contentId + "'/>", CONTENT_TYPE);
			// part.attachFile(node.getBody().toString()); // 使用这种方法可以直接嵌入图片.但是这种形式是以附件方式发送的

			textAndImage.addBodyPart(textNodePart);
			textAndImage.addBodyPart(imageNodePart);
			// 关联关系---这样才能使用cid指向图片节点的资源
			textAndImage.setSubType("related");
			// 将文本图片的组合节点放入节点中
			// 如果要在邮件中显示图片则需要使用这种方式将文本和图片组合起来
			// 直接使用图片节点将会使得图片变成二进制格式的文件负载在附件中
			part.setContent(textAndImage);
			break;
		default:
			part.setContent("", CONTENT_TYPE);
			break;
		}

		// Node类型不为FILE并且有单独附件的情况
		if (node.getAttachments() != null && node.getAttachments().length > 0) {
			MimeBodyPart attachment = null;
			for (String e : Arrays.asList(node.getAttachments())) {
				attachment = new MimeBodyPart();
				attachment.attachFile(e);
				// 设置文件名,防止中文乱码
				attachment.setFileName(MimeUtility.encodeText(attachment.getFileName()));
				mimeMultipart.addBodyPart(attachment);
			}
		}
		mimeMultipart.addBodyPart(part);
		return mimeMultipart;
	}
}
