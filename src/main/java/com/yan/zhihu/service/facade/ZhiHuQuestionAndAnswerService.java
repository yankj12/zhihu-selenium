package com.yan.zhihu.service.facade;

import org.openqa.selenium.WebDriver;

public interface ZhiHuQuestionAndAnswerService {

	/**
	 * 将收藏夹中的回答爬取到数据库中
	 * @param driver
	 * @param collectionId
	 * @param currentPageNo
	 */
	void answersCollectedIntoDB(WebDriver driver, String collectionId, int currentPageNo);
}
