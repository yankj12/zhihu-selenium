package com.yan.zhihu.model;

import java.util.Date;

/**
 * 知乎专栏
 * @author Yan
 *
 */
public class ZhiHuPeopleColumn {

	private String id;
	
	/**
	 * 话题的主键
	 * 类似于主键的字段
	 * 话题的token
	 */
	private String columnId;
	
	private String columnName;
	
	private String userId;
	
	/**
	 * 类型，当然是topic了
	 */
	private String type;
	
	
	private Date insertTime;
	
	private Date updateTime;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getColumnId() {
		return columnId;
	}

	public void setColumnId(String columnId) {
		this.columnId = columnId;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
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
