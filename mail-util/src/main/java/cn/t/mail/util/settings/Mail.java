package cn.t.mail.util.settings;

import java.io.Serializable;
import java.util.Date;

/**
 * 邮件对象
 * @author tmq
 *
 */
public class Mail implements Serializable {

	private static final long serialVersionUID = -6594985209300779470L;

	/**
	 * 主题/标题
	 */
	private String subject;

	/**
	 * 内容(纯文本)
	 */
	private String textContent;

	/**
	 * 内容(复合)
	 */
	private MultiContent multiContent;

	/**
	 * 发件时间
	 */
	private Date sentDate;

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Date getSentDate() {
		if (this.sentDate == null) {
			return new Date();
		}
		return sentDate;
	}

	public void setSentDate(Date sentDate) {
		this.sentDate = sentDate;
	}

	public String getTextContent() {
		return textContent;
	}

	public void setTextContent(String textContent) {
		this.textContent = textContent;
	}

	public MultiContent getMultiContent() {
		return multiContent;
	}

	/**
	 * 设置复合类型邮件内容
	 * @param multiContent
	 */
	public void setMultiContent(MultiContent multiContent) {
		this.multiContent = multiContent;
	}
}
