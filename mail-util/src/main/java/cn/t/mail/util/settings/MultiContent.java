package cn.t.mail.util.settings;

import java.io.Serializable;

import cn.t.mail.util.exceptions.MailUtilException;
import cn.t.mail.util.type.NodeType;

/**
 * 复合邮件内容(纯文本|文本+附件|文本+图片)
 * @author tmq
 *
 */
public class MultiContent implements Serializable {

	private static final long serialVersionUID = 5833169429216362446L;

	/**
	 * 初始化<br/>
	 * 设置图片url需要调用setNodeImgUrl或者setImgUrl
	 * @param type NodeType.TEXT 文本类型 | NodeType.MIXED 混合类型(图片+文本)
	 * @param body 正文内容
	 * @param attachments 附件
	 */
	public MultiContent(NodeType type, String body, String ...attachments) {
		super();
		if (type == null) {
			throw new MailUtilException("节点类型必须为合法值");
		}
		this.type = type;
		this.body = body;
		this.attachments = attachments;
	}
	

	/**
	 * 节点类型
	 */
	private NodeType type;

	/**
	 * 节点内容（文件内容则使用url）
	 */
	private String body;
	
	/**
	 * 附件列表
	 */
	private String[] attachments;
	
	/**
	 * 邮件中直接显示的图片地址
	 */
	private String imgUrl;

	public NodeType getType() {
		return type;
	}

	public void setType(NodeType type) {
		this.type = type;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	/**
	 * @return the attachments
	 */
	public String[] getAttachments() {
		return attachments;
	}

	/**
	 * @param attachments the attachments to set
	 */
	public void setAttachments(String[] attachments) {
		this.attachments = attachments;
	}

	/**
	 * @return the imgUrl
	 */
	public String getImgUrl() {
		return imgUrl;
	}

	/**
	 * @param imgUrl the imgUrl to set
	 */
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	
	/**
	 * 方便设置Node用
	 * @param imgUrl
	 * @return
	 */
	public MultiContent setNodeImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
		return this;
	}

}
