package com.yan.zhihu.mysql.schema;

import java.io.Serializable;
import java.util.Date;

/**
 * 知乎收藏夹中内容
 * @author Yan
 *
 */
public class ZhiHuCollectionItem implements Serializable{

	private static final long serialVersionUID = 1L;

	// 自增主键
	private Integer id;
	
	// 收藏夹id
	private String collectionId;
	
	// 收藏内容的标题
	private String title;
	
	// 答案的相对链接
	private String answerRelativeUrl;
	
	// 回答的id
	private String answerId;
	
	// 回答的urltoken，和id不是一个概念。我理解的是唯一标识符
	private String answerUrlToken;
	
	// 回答的赞同数
	private String voteCount;
	
	// 作者名称
	private String authorName;
	
	// 作者主页相对链接
	private String authorRelativeUrl;
	
	// 作者简介
	private String authorSummary;
	
	// 回答的富文本内容
	private String contentHTML;
	
	// 回答内容的摘要
	private String summaryInfo;
	
	private Date insertTime;
	
	private Date updateTime;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCollectionId() {
		return collectionId;
	}

	public void setCollectionId(String collectionId) {
		this.collectionId = collectionId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAnswerRelativeUrl() {
		return answerRelativeUrl;
	}

	public void setAnswerRelativeUrl(String answerRelativeUrl) {
		this.answerRelativeUrl = answerRelativeUrl;
	}

	public String getAnswerId() {
		return answerId;
	}

	public void setAnswerId(String answerId) {
		this.answerId = answerId;
	}

	public String getAnswerUrlToken() {
		return answerUrlToken;
	}

	public void setAnswerUrlToken(String answerUrlToken) {
		this.answerUrlToken = answerUrlToken;
	}

	public String getVoteCount() {
		return voteCount;
	}

	public void setVoteCount(String voteCount) {
		this.voteCount = voteCount;
	}

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public String getAuthorRelativeUrl() {
		return authorRelativeUrl;
	}

	public void setAuthorRelativeUrl(String authorRelativeUrl) {
		this.authorRelativeUrl = authorRelativeUrl;
	}

	public String getAuthorSummary() {
		return authorSummary;
	}

	public void setAuthorSummary(String authorSummary) {
		this.authorSummary = authorSummary;
	}

	public String getContentHTML() {
		return contentHTML;
	}

	public void setContentHTML(String contentHTML) {
		this.contentHTML = contentHTML;
	}

	public String getSummaryInfo() {
		return summaryInfo;
	}

	public void setSummaryInfo(String summaryInfo) {
		this.summaryInfo = summaryInfo;
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
}
