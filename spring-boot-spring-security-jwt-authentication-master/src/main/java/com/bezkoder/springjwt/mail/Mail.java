package com.bezkoder.springjwt.mail;

import java.util.List;
import java.util.Map;

public class Mail {
	private String from;
	private String mailTo;
	private String subject;
	private String content;
	private List<Object> attachments;
	private Map<String, Object> props;

	public Mail() {
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getMailTo() {
		return mailTo;
	}

	public void setMailTo(String mailTo) {
		this.mailTo = mailTo;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public List<Object> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Object> attachments) {
		this.attachments = attachments;
	}

	public Map<String, Object> getProps() {
		return props;
	}

	public void setProps(Map<String, Object> props) {
		this.props = props;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
