package com.communication.email;

import java.util.List;


public class EmailVO {

	public EmailVO() {
		
	}
	
	private String userName;
	private String password;
	private EmailAddess fromAddress;
	private List<EmailAddess> toAddress;
	private List<EmailAddess> ccAddress;
	private List<EmailAddess> bccAddress;
	private String subject;
	private String htmlContent;
	private String base64Attachment;
	private String attachmentName;
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public EmailAddess getFromAddress() {
		return fromAddress;
	}
	public void setFromAddress(EmailAddess fromAddress) {
		this.fromAddress = fromAddress;
	}
	public List<EmailAddess> getToAddress() {
		return toAddress;
	}
	public void setToAddress(List<EmailAddess> toAddress) {
		this.toAddress = toAddress;
	}
	public List<EmailAddess> getCcAddress() {
		return ccAddress;
	}
	public void setCcAddress(List<EmailAddess> ccAddress) {
		this.ccAddress = ccAddress;
	}
	public List<EmailAddess> getBccAddress() {
		return bccAddress;
	}
	public void setBccAddress(List<EmailAddess> bccAddress) {
		this.bccAddress = bccAddress;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	public String getBase64Attachment() {
		return base64Attachment;
	}
	public void setBase64Attachment(String base64Attachment) {
		this.base64Attachment = base64Attachment;
	}
	public String getHtmlContent() {
		return htmlContent;
	}
	public void setHtmlContent(String htmlContent) {
		this.htmlContent = htmlContent;
	}
	
	
	public String getAttachmentName() {
		return attachmentName;
	}
	public void setAttachmentName(String attachmentName) {
		this.attachmentName = attachmentName;
	}
}
