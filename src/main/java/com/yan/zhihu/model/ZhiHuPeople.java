package com.yan.zhihu.model;

import java.util.Date;
import java.util.List;

/**
 * 知乎用户
 * @author Yan
 *
 */
public class ZhiHuPeople {

	/**
	 * mongodb的id
	 */
	private String id;
	
	private String userName;
	
	private String userId;
	
	private String relativeUrl;
	
	private String imageSrc;
	
	private String imageSrcset;
	
	private Integer imageWidth;
	
	private Integer imageHeight;
	
	private Integer answerCount;
	
	private Integer articleCount;
	
	/**
	 * 被赞同数
	 */
	private Integer beAgreedCount;
	
	/**
	 * 被感谢数
	 */
	private Integer beThankedCount;
	
	/**
	 * 被收藏数
	 */
	private Integer beCollectedCount;
	
	/**
	 * 关注了
	 */
	private Integer followingCount;
	
	/**
	 * 关注了的链接
	 */
	private String followingUrl;
	
	private List<String> followingIds;
	
	/**
	 * 被关注
	 */
	private Integer followersCount;
	
	/**
	 * 关注者的链接
	 */
	private String followersUrl;
	
	private String followingTopicsUrl;
	
	private List<String> followerIds;
	
	/**
	 * 关注的话题
	 */
//	private List<ZhiHuTopic> followingTopics;
	
	private String followingColumnsUrl;
		
	/**
	 * 关注的专栏
	 */
//	private List<ZhiHuColumn> followingColumns;
	
	private String followingQuestionsUrl;
	
	/**
	 * 关注的问题
	 */
//	private List<ZhiHuQuestion> followingQuestions;
	
	private String followingCollectionsUrl;
	
	/**
	 * 关注的收藏夹
	 */
//	private List<ZhiHuCollection> followingCollections;

	private Date insertTime;
	
	private Date updateTime;
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Integer getBeAgreedCount() {
		return beAgreedCount;
	}

	public void setBeAgreedCount(Integer beAgreedCount) {
		this.beAgreedCount = beAgreedCount;
	}

	public Integer getBeThankedCount() {
		return beThankedCount;
	}

	public void setBeThankedCount(Integer beThankedCount) {
		this.beThankedCount = beThankedCount;
	}

	public Integer getBeCollectedCount() {
		return beCollectedCount;
	}

	public void setBeCollectedCount(Integer beCollectedCount) {
		this.beCollectedCount = beCollectedCount;
	}

	public Integer getFollowingCount() {
		return followingCount;
	}

	public void setFollowingCount(Integer followingCount) {
		this.followingCount = followingCount;
	}

	public String getFollowingUrl() {
		return followingUrl;
	}

	public void setFollowingUrl(String followingUrl) {
		this.followingUrl = followingUrl;
	}

	public Integer getFollowersCount() {
		return followersCount;
	}

	public void setFollowersCount(Integer followersCount) {
		this.followersCount = followersCount;
	}

	public String getFollowersUrl() {
		return followersUrl;
	}

	public void setFollowersUrl(String followersUrl) {
		this.followersUrl = followersUrl;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFollowingTopicsUrl() {
		return followingTopicsUrl;
	}

	public void setFollowingTopicsUrl(String followingTopicsUrl) {
		this.followingTopicsUrl = followingTopicsUrl;
	}

	public String getFollowingColumnsUrl() {
		return followingColumnsUrl;
	}

	public void setFollowingColumnsUrl(String followingColumnsUrl) {
		this.followingColumnsUrl = followingColumnsUrl;
	}

	public String getFollowingQuestionsUrl() {
		return followingQuestionsUrl;
	}

	public void setFollowingQuestionsUrl(String followingQuestionsUrl) {
		this.followingQuestionsUrl = followingQuestionsUrl;
	}

	public String getFollowingCollectionsUrl() {
		return followingCollectionsUrl;
	}

	public void setFollowingCollectionsUrl(String followingCollectionsUrl) {
		this.followingCollectionsUrl = followingCollectionsUrl;
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

	public List<String> getFollowingIds() {
		return followingIds;
	}

	public void setFollowingIds(List<String> followingIds) {
		this.followingIds = followingIds;
	}

	public List<String> getFollowerIds() {
		return followerIds;
	}

	public void setFollowerIds(List<String> followerIds) {
		this.followerIds = followerIds;
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

	public String getRelativeUrl() {
		return relativeUrl;
	}

	public void setRelativeUrl(String relativeUrl) {
		this.relativeUrl = relativeUrl;
	}

	public Integer getAnswerCount() {
		return answerCount;
	}

	public void setAnswerCount(Integer answerCount) {
		this.answerCount = answerCount;
	}

	public Integer getArticleCount() {
		return articleCount;
	}

	public void setArticleCount(Integer articleCount) {
		this.articleCount = articleCount;
	}
	
}
