package com.yan.zhihu.model;

import java.util.Date;

/**
 * 知乎话题
 * @author Yan
 *
 */
public class ZhiHuPeopleTopic {

	private String id;
	
	/**
	 * 话题的主键
	 * 类似于主键的字段
	 * 话题的token
	 */
	private String topicId;
	
	private String topicName;
	
	private String userId;
	
	/**
	 * 类型，当然是topic了
	 */
	private String type;
	
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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
	
}
