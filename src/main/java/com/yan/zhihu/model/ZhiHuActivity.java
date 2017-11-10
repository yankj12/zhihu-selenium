package com.yan.zhihu.model;

import java.util.Date;

import com.yan.zhihu.model.subvo.AnswerInfo;
import com.yan.zhihu.model.subvo.ArticleInfo;
import com.yan.zhihu.model.subvo.CollectionInfo;
import com.yan.zhihu.model.subvo.ColumnInfo;
import com.yan.zhihu.model.subvo.QuestionInfo;
import com.yan.zhihu.model.subvo.TopicInfo;

public class ZhiHuActivity {

	private String id;
	
	private String userId;
	
	/**
	 * 哪天的活动
	 * 如 2017-11-07
	 */
	private String activityDay;
	
	/**
	 * 包括但不限于如下类型
	 * 赞了文章，收藏了文章，赞同了回答，收藏了回答，关注了问题，回答了问题，关注了专栏，关注了收藏夹，关注了话题，发布了想法
	 */
	private String activityType;
	
	private String activityTypeFullName;
	
	/**
	 * question
	 * article
	 * answer
	 * column
	 * topic
	 */
	private String itemType;
	
	/**
	 * activity对象的名称
	 * 可以通过activityType和itemName就可以唯一确定一个activity
	 */
	private String itemName;
	
	private ArticleInfo article;
	
	private AnswerInfo answer;
	
	private QuestionInfo question;
	
	private ColumnInfo column;
	
	private TopicInfo topic;
	
	private CollectionInfo collection;
	
	private Date insertTime;
	
	private Date updateTime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getActivityDay() {
		return activityDay;
	}

	public void setActivityDay(String activityDay) {
		this.activityDay = activityDay;
	}

	public String getActivityType() {
		return activityType;
	}

	public void setActivityType(String activityType) {
		this.activityType = activityType;
	}

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	public ArticleInfo getArticle() {
		return article;
	}

	public void setArticle(ArticleInfo article) {
		this.article = article;
	}

	public AnswerInfo getAnswer() {
		return answer;
	}

	public void setAnswer(AnswerInfo answer) {
		this.answer = answer;
	}

	public QuestionInfo getQuestion() {
		return question;
	}

	public void setQuestion(QuestionInfo question) {
		this.question = question;
	}

	public ColumnInfo getColumn() {
		return column;
	}

	public void setColumn(ColumnInfo column) {
		this.column = column;
	}

	public TopicInfo getTopic() {
		return topic;
	}

	public void setTopic(TopicInfo topic) {
		this.topic = topic;
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

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getActivityTypeFullName() {
		return activityTypeFullName;
	}

	public void setActivityTypeFullName(String activityTypeFullName) {
		this.activityTypeFullName = activityTypeFullName;
	}

	public CollectionInfo getCollection() {
		return collection;
	}

	public void setCollection(CollectionInfo collection) {
		this.collection = collection;
	}
	
}