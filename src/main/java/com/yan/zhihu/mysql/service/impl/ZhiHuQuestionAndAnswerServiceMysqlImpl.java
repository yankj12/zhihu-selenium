package com.yan.zhihu.mysql.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.yan.zhihu.mysql.schema.ZhiHuCollectionItem;
import com.yan.zhihu.service.facade.ZhiHuQuestionAndAnswerService;

public class ZhiHuQuestionAndAnswerServiceMysqlImpl implements ZhiHuQuestionAndAnswerService{

	private static Logger logger = Logger.getLogger(ZhiHuQuestionAndAnswerServiceMysqlImpl.class);
	
	/**
	 * 将收藏夹中的回答爬取到数据库中
	 * @param driver
	 * @param collectionId
	 * @param currentPageNo
	 */
	public void answersCollectedIntoDB(WebDriver driver, String collectionId, int currentPageNo) {
		logger.info("页码:" + currentPageNo);
		
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
				ZhiHuCollectionItem zhiHuCollectionItem = new ZhiHuCollectionItem();
				
				zhiHuCollectionItem.setCollectionId(collectionId);
				
				// 收藏内容的标题
				WebElement titleElement = itemElement.findElement(By.tagName("h2"));
				String title = titleElement.getText();
				zhiHuCollectionItem.setTitle(title);
				
				logger.info(title);
				
				// 收藏的内容包装组件
				// <div class="zm-item-fav">
				WebElement zmItemFavDivElement = itemElement.findElement(By.tagName("div"));
				
				// 有两个div
				// <div class="zm-item-answer " ...>  收藏的内容简介及链接
				// <div class="zm-item-meta-extra">  取消收藏
				List<WebElement> listDivElements = zmItemFavDivElement.findElements(By.tagName("div"));
				// <div class="zm-item-answer " ...>
				// 如果想要条件并列，可以使用xpath
				
				WebElement zmItemAnswerDivElement = listDivElements.get(0);
				WebElement zmItemMetaExtraElement = listDivElements.get(1);
				
				// 可以从attribute中获取answerid和answerUrlToken，需注意这两个不是一个概念，也可以从div中包含的其他标签中获取
				WebElement urlLinkElement = zmItemAnswerDivElement.findElement(By.tagName("link"));
				// 答案的相对链接
				String answerRelativeUrl = urlLinkElement.getAttribute("href");
				zhiHuCollectionItem.setAnswerRelativeUrl(answerRelativeUrl);
				
				List<WebElement> answerMetaElements = zmItemAnswerDivElement.findElements(By.tagName("meta"));
				
				// 回答的id
				String answerId = null;
				// 回答的urltoken，和id不是一个概念。我理解的是唯一标识符
				String answerUrlToken = null;
				for(WebElement metaElement : answerMetaElements){
					// itemprop="answer-id"
					// itemprop="answer-url-token"
					String itemprop = metaElement.getAttribute("itemprop");
					if("answer-id".equals(itemprop)){
						answerId = metaElement.getAttribute("content");
					}else if("answer-url-token".equals(itemprop)){
						answerUrlToken = metaElement.getAttribute("content");
					}
				}
				
				zhiHuCollectionItem.setAnswerId(answerId);
				zhiHuCollectionItem.setAnswerUrlToken(answerUrlToken);
				
				// div class="zm-item-vote"
				// 回答的赞同数
				WebElement zmItemVoteDivElement = zmItemAnswerDivElement.findElement(By.className("zm-item-vote"));
				String voteCount = zmItemVoteDivElement.getText();
				zhiHuCollectionItem.setVoteCount(voteCount);
				
				// div class="answer-head"
				// 作者及作者简介等标题
				WebElement answerHeadDivElement = zmItemAnswerDivElement.findElement(By.className("answer-head"));
				
				// div class="zm-item-answer-author-info"
				WebElement answerAuthorInfoDivElement = zmItemAnswerDivElement.findElement(By.className("zm-item-answer-author-info"));
				// span class="summary-wrapper"
				WebElement authorInfoSpanElement = answerAuthorInfoDivElement.findElement(By.tagName("span"));
				
				// 作者主页链接
				WebElement authorLinkSpanElement = answerAuthorInfoDivElement.findElement(By.className("author-link-line"));
				WebElement authorLinkElement = authorLinkSpanElement.findElement(By.tagName("a"));
				// 作者名称
				String authorName = authorLinkElement.getText();
				// 作者主页相对链接
				String authorRelativeUrl = authorLinkElement.getAttribute("href");
				
				zhiHuCollectionItem.setAuthorName(authorName);
				zhiHuCollectionItem.setAuthorRelativeUrl(authorRelativeUrl);
				
				// 作者简介
				WebElement authorTitleSpanElement = answerAuthorInfoDivElement.findElement(By.className("bio"));
				// 作者简介
				String authorSummary = authorTitleSpanElement.getText();
				zhiHuCollectionItem.setAuthorSummary(authorSummary);
				
				// div class="zm-item-rich-text expandable js-collapse-body"
				// 回答的富文本内容
				WebElement zmItemRichTextDivElement = zmItemAnswerDivElement.findElement(By.cssSelector("[class='zm-item-rich-text expandable js-collapse-body']"));
				WebElement contentElement = zmItemRichTextDivElement.findElement(By.tagName("textarea"));
				
				/**
				 * innerHTML 会返回元素的内部 HTML， 包含所有的HTML标签。
				 * - 例如，<div>Hello <p>World!</p></div>的innerHTML会得到Hello <p>World!</p>
				 * 
				 * textContent 和 innerText 只会得到文本内容，而不会包含 HTML 标签。
				 * - textContent 是 W3C 兼容的文字内容属性，但是 IE 不支持
				 * - innerText 不是 W3C DOM 的指定内容，FireFox不支持
				 * 
				 */
				String contentHTML = contentElement.getAttribute("innerHTML");
				zhiHuCollectionItem.setContentHTML(contentHTML);
				
				// div class="zh-summary summary clearfix"
				WebElement summaryInfoElement = zmItemRichTextDivElement.findElement(By.tagName("div"));
				String summaryInfo = summaryInfoElement.getAttribute("innerHTML");
				zhiHuCollectionItem.setSummaryInfo(summaryInfo);
				
			}

			
			
		}
	}

}
