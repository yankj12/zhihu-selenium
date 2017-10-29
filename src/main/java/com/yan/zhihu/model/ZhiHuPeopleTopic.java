package com.yan.zhihu.model;

import java.util.Date;

/**
 * 知乎话题
 * @author Yan
 *
 */
public class ZhiHuPeopleTopic {

	private String id;
	
	private String topicId;
	
	private String topicName;
	
	private String userId;
	
	/**
	 * 话题的主键
	 * 类似于主键的字段
	 */
	private String token;
	
	/**
	 * 类型，当然是topic了
	 */
	private String type;
	
	private String relativeUrl;
	
	private String imageSrc;
	
	private String imageSrcset;
	
	private Integer imageWidth;
	
	private Integer imageHeight;
	
	/**
	 * 这个知乎用户在这个话题下的答案的连接
	 */
	private String answersInTopicUrl;
	
	/**
	 * 这个知乎用户在这个话题下回答的问题数
	 */
	private Integer answersInTopicCount;

	private Date insertTime;
	
	private Date updateTime;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTopicId() {
		return topicId;
	}

	public void setTopicId(String topicId) {
		this.topicId = topicId;
	}

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRelativeUrl() {
		return relativeUrl;
	}

	public void setRelativeUrl(String relativeUrl) {
		this.relativeUrl = relativeUrl;
	}

	public String getAnswersInTopicUrl() {
		return answersInTopicUrl;
	}

	public void setAnswersInTopicUrl(String answersInTopicUrl) {
		this.answersInTopicUrl = answersInTopicUrl;
	}

	public Integer getAnswersInTopicCount() {
		return answersInTopicCount;
	}

	public void setAnswersInTopicCount(Integer answersInTopicCount) {
		this.answersInTopicCount = answersInTopicCount;
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

	public String getImageSrc() {
		return imageSrc;
	}

	public void setImageSrc(String imageSrc) {
		this.imageSrc = imageSrc;
	}

	public String getImageSrcset() {
		return imageSrcset;
	}

	public void setImageSrcset(String imageSrcset) {
		this.imageSrcset = imageSrcset;
	}

	public Integer getImageWidth() {
		return imageWidth;
	}

	public void setImageWidth(Integer imageWidth) {
		this.imageWidth = imageWidth;
	}

	public Integer getImageHeight() {
		return imageHeight;
	}

	public void setImageHeight(Integer imageHeight) {
		this.imageHeight = imageHeight;
	}
	
}
