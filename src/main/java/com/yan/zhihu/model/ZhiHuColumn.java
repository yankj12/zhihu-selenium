package com.yan.zhihu.model;

import java.util.Date;
import java.util.List;

public class ZhiHuColumn {

	/**
	 * mongodb的id
	 */
	private String id;
	
	private String userId;
	
	private String authorMemberHashId;
	
	private String columnName;
	
	private String columnId;
	
	private String relativeUrl;
	
	private String imageSrc;
	
	private String imageSrcset;
	
	private Integer imageWidth;
	
	private Integer imageHeight;
	
	private Integer articleCount;
	/**
	 * 被关注
	 */
	private Integer followersCount;
	
	private String followersUrl;

	private List<String> followerIds;
	
	private List<String> articleIds;

	private Date insertTime;
	
	private Date updateTime;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getColumnId() {
		return columnId;
	}

	public void setColumnId(String columnId) {
		this.columnId = columnId;
	}

	public String getRelativeUrl() {
		return relativeUrl;
	}

	public void setRelativeUrl(String relativeUrl) {
		this.relativeUrl = relativeUrl;
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

	public Integer getArticleCount() {
		return articleCount;
	}

	public void setArticleCount(Integer articleCount) {
		this.articleCount = articleCount;
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

	public List<String> getFollowerIds() {
		return followerIds;
	}

	public void setFollowerIds(List<String> followerIds) {
		this.followerIds = followerIds;
	}

	public List<String> getArticleIds() {
		return articleIds;
	}

	public void setArticleIds(List<String> articleIds) {
		this.articleIds = articleIds;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getAuthorMemberHashId() {
		return authorMemberHashId;
	}

	public void setAuthorMemberHashId(String authorMemberHashId) {
		this.authorMemberHashId = authorMemberHashId;
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
