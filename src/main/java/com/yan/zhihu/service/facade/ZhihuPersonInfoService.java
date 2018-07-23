package com.yan.zhihu.service.facade;

import org.openqa.selenium.WebDriver;

public interface ZhihuPersonInfoService {

	/**
	 * 将用户的收藏夹保存到数据库
	 * @param driver
	 * @param userId
	 * @param currentPageNo
	 */
	void personalCollectionsIntoDB(WebDriver driver, String userId, int currentPageNo);
	
}
