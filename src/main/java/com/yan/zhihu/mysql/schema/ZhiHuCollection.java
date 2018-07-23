package com.yan.zhihu.mysql.schema;

import java.io.Serializable;
import java.util.Date;

/**
 * 知乎收藏夹
 * @author Yan
 *
 */
public class ZhiHuCollection implements Serializable{

	private static final long serialVersionUID = 1L;

	private String collectionId;
	
	private String collectionName;
	
	private String authorId;
	
	private String relativeUrl;

	private Integer itemCount;

	private Integer followersCount;
	
	private String contentLastModifyDay;

	private Date insertTime;
	
	private Date updateTime;

	public String getCollectionId() {
		return collectionId;
	}

	public void setCollectionId(String collectionId) {
		this.collectionId = collectionId;
	}

	public String getCollectionName() {
		return collectionName;
	}

	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}

	public String getAuthorId() {
		return authorId;
	}

	public void setAuthorId(String authorId) {
		this.authorId = authorId;
	}

	public String getRelativeUrl() {
		return relativeUrl;
	}

	public void setRelativeUrl(String relativeUrl) {
		this.relativeUrl = relativeUrl;
	}

	public Integer getItemCount() {
		return itemCount;
	}

	public void setItemCount(Integer itemCount) {
		this.itemCount = itemCount;
	}

	public Integer getFollowersCount() {
		return followersCount;
	}

	public void setFollowersCount(Integer followersCount) {
		this.followersCount = followersCount;
	}

	public String getContentLastModifyDay() {
		return contentLastModifyDay;
	}

	public void setContentLastModifyDay(String contentLastModifyDay) {
		this.contentLastModifyDay = contentLastModifyDay;
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
