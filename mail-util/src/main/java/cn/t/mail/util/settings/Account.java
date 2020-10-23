package cn.t.mail.util.settings;

import java.io.Serializable;
import java.util.List;

/**
 * 邮件收发人信息
 * @author tmq
 *
 */
public class Account implements Serializable {

	private static final long serialVersionUID = -5708764119199223618L;

	/**
	 * 发件人邮件地址
	 */
	private String from;

	/**
	 * 发件人名称(昵称)
	 */
	private String nickName;

	/**
	 * 收件人邮件地址
	 */
	private List<String> to;

	/**
	 * 抄送人邮箱地址
	 */
	private List<String> cc;

	/**
	 * 密送人邮箱地址
	 */
	private List<String> bcc;

	/**
	 * 发件人邮箱密码
	 */
	private String password;

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public List<String> getTo() {
		return to;
	}

	public void setTo(List<String> to) {
		this.to = to;
	}

	public List<String> getCc() {
		return cc;
	}

	public void setCc(List<String> cc) {
		this.cc = cc;
	}

	public List<String> getBcc() {
		return bcc;
	}

	public void setBcc(List<String> bcc) {
		this.bcc = bcc;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public Account() {
		
	}

	public Account(String from, String nickName, List<String> to, String password) {
		super();
		this.from = from;
		this.nickName = nickName;
		this.to = to;
		this.password = password;
	}

	public Account(String from, String nickName, List<String> to, List<String> cc, List<String> bcc, String password) {
		super();
		this.from = from;
		this.nickName = nickName;
		this.to = to;
		this.cc = cc;
		this.bcc = bcc;
		this.password = password;
	}
}
