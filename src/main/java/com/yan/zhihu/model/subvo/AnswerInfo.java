package com.yan.zhihu.model.subvo;

public class AnswerInfo {
	private String answerId;
	private String answerRelativeUrl;
	
	private String questionId;
	private String questionName;
	
	private String authorName;
	private String authorId;
	public String getAnswerId() {
		return answerId;
	}
	public void setAnswerId(String answerId) {
		this.answerId = answerId;
	}
	public String getQuestionId() {
		return questionId;
	}
	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}
	public String getQuestionName() {
		return questionName;
	}
	public void setQuestionName(String questionName) {
		this.questionName = questionName;
	}
	public String getAuthorName() {
		return authorName;
	}
	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}
	public String getAuthorId() {
		return authorId;
	}
	public void setAuthorId(String authorId) {
		this.authorId = authorId;
	}
	public String getAnswerRelativeUrl() {
		return answerRelativeUrl;
	}
	public void setAnswerRelativeUrl(String answerRelativeUrl) {
		this.answerRelativeUrl = answerRelativeUrl;
	}
}
