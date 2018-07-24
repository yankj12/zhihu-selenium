package com.yan.zhihu.mysql.service.impl;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ByChained;

import com.yan.zhihu.service.facade.ZhiHuQuestionAndAnswerService;

public class ZhiHuQuestionAndAnswerServiceMysqlImpl implements ZhiHuQuestionAndAnswerService{

	/**
	 * 将收藏夹中的回答爬取到数据库中
	 * @param driver
	 * @param collectionId
	 * @param currentPageNo
	 */
	public void answersCollectedIntoDB(WebDriver driver, String collectionId, int currentPageNo) {
		// 可能页面还没有加载出来
		try {
			Thread.sleep(3 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// id="zh-list-collection-wrap"
		WebElement collectionWrapElement = driver.findElement(By.id("zh-list-collection-wrap"));
		
		// 收藏夹中问题及答案的列表
		// class="zm-item"
		List<WebElement> listItemElements = collectionWrapElement.findElements(By.className("zm-item"));
		
		if(listItemElements != null && listItemElements.size() > 0) {
			//TODO 创建数据库dao类
			
			for(WebElement itemElement:listItemElements){
				// 收藏内容的标题
				WebElement titleElement = itemElement.findElement(By.tagName("h2"));
				String title = titleElement.getText();
				
				// 收藏的内容包装组件
				// <div class="zm-item-fav">
				WebElement zmItemFavDivElement = itemElement.findElement(By.tagName("div"));
				
				// <div class="zm-item-answer " ...>
				// 如果想要条件并列，可以使用xpath
				WebElement zmItemAnswerDivElement = zmItemFavDivElement.findElement(By.tagName("div").className("zm-item-answer "));
				// 可以从attribute中获取answerid和answerUrlToken，需注意这两个不是一个概念，也可以从div中包含的其他标签中获取
				zmItemAnswerDivElement.getAttribute("");
				
				
				
			}

			
			
		}
	}

}
