package com.yan.zhihu.model;

import java.util.Date;

/**
 * 知乎问题
 * @author Yan
 *
 */
public class ZhiHuQuestion {

	private String id;
	
	private String questionId;
	
	private String questionName;
	
	private Integer answerCount;
	
	private Integer followerCount;
	
	private String questionDay;
	
	private String questionRelativeUrl;

	private Date insertTime;
	
	private Date updateTime;
	
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

	public Integer getAnswerCount() {
		return answerCount;
	}

	public void setAnswerCount(Integer answerCount) {
		this.answerCount = answerCount;
	}

	public Integer getFollowerCount() {
		return followerCount;
	}

	public void setFollowerCount(Integer followerCount) {
		this.followerCount = followerCount;
	}

	public String getQuestionRelativeUrl() {
		return questionRelativeUrl;
	}

	public void setQuestionRelativeUrl(String questionRelativeUrl) {
		this.questionRelativeUrl = questionRelativeUrl;
	}

	public String getQuestionDay() {
		return questionDay;
	}

	public void setQuestionDay(String questionDay) {
		this.questionDay = questionDay;
	}

	public Date getInsertTime() {
		return insertTime;
	}

	public void setInsertTime(Date insertTime) {
		this.insertTime = insertTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
