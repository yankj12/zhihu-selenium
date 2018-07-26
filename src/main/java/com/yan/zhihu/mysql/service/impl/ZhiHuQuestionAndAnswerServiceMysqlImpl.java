package com.yan.zhihu.mysql.service.impl;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.yan.zhihu.mysql.dao.facade.ZhiHuCollectionItemDaoService;
import com.yan.zhihu.mysql.dao.impl.ZhiHuCollectionItemDaoServiceMybatisImpl;
import com.yan.zhihu.mysql.schema.ZhiHuCollectionItem;
import com.yan.zhihu.mysql.utils.ZhiHuUtil;
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
		// https://www.zhihu.com/collection/60315883
		// https://www.zhihu.com/collection/60315883?page=2
		
		String url = "https://www.zhihu.com/collection/" + collectionId;
		if(currentPageNo > 1){
			url += "?page=" + currentPageNo;
		}
		driver.get(url);
		
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
		// 有属性
		// data-type="Answer" data-za-module="AnswerItem"
		// data-type="Post" data-za-module="PostItem"
		List<WebElement> listItemElements = collectionWrapElement.findElements(By.className("zm-item"));
		
		if(listItemElements != null && listItemElements.size() > 0) {
			// 创建数据库dao类
			ZhiHuCollectionItemDaoService zhiHuCollectionItemDaoService = new ZhiHuCollectionItemDaoServiceMybatisImpl();
			
			for(WebElement itemElement:listItemElements){
				ZhiHuCollectionItem zhiHuCollectionItem = new ZhiHuCollectionItem();
				
				zhiHuCollectionItem.setCollectionId(collectionId);
				
				String dataType = null;
				String dataModule = null;
				
				dataType = itemElement.getAttribute("data-type");
				dataModule = itemElement.getAttribute("data-za-module");
				
				zhiHuCollectionItem.setDataType(dataType);
				zhiHuCollectionItem.setDataModule(dataModule);
				
				// 收藏内容的标题
				WebElement titleElement = itemElement.findElement(By.tagName("h2"));
				String title = titleElement.getText();
				zhiHuCollectionItem.setTitle(title);
				
				logger.info(title);
				
				// 收藏的内容包装组件
				// <div class="zm-item-fav">
				WebElement zmItemFavDivElement = itemElement.findElement(By.tagName("div"));
				
				// 如果是回答，有两个div
				// <div class="zm-item-answer " ...>  收藏的内容简介及链接
				// <div class="zm-item-meta-extra">  取消收藏
				// 如果是专栏中的文章，只有一个div
				List<WebElement> listDivElements = zmItemFavDivElement.findElements(By.tagName("div"));
				// <div class="zm-item-answer " ...>
				// 如果想要条件并列，可以使用xpath
				
				WebElement zmItemAnswerDivElement = listDivElements.get(0);
				WebElement zmItemMetaExtraElement = null;
				
				// 收藏的内容是回答，才会有zmItemMetaExtraElement
				if("Answer".equals(dataType)) {
					zmItemMetaExtraElement = listDivElements.get(1);
				}
				
				// 可以从attribute中获取answerid和answerUrlToken，需注意这两个不是一个概念，也可以从div中包含的其他标签中获取
				WebElement urlLinkElement = zmItemAnswerDivElement.findElement(By.tagName("link"));
				// 答案的相对链接
				String answerUrl = urlLinkElement.getAttribute("href");
				zhiHuCollectionItem.setAnswerUrl(answerUrl);
				
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
				if("Answer".equals(dataType)) {
					WebElement answerHeadDivElement = zmItemAnswerDivElement.findElement(By.className("answer-head"));
					
					// findElement不仅可以查找直接子级中的元素，只要是在自己下级的都可以查到
					
					// div class="zm-item-answer-author-info"
					WebElement answerAuthorInfoDivElement = zmItemAnswerDivElement.findElement(By.className("zm-item-answer-author-info"));
					// span class="summary-wrapper"
					WebElement authorInfoSpanElement = answerAuthorInfoDivElement.findElement(By.tagName("span"));
					
					// 作者名称
					String authorName = null;
					// 作者主页相对链接
					String authorRelativeUrl = null;
					try {
						// 作者主页链接
						WebElement authorLinkSpanElement = answerAuthorInfoDivElement.findElement(By.className("author-link-line"));
						authorName = authorLinkSpanElement.getText();
						zhiHuCollectionItem.setAuthorName(authorName);
						
						WebElement authorLinkElement = authorLinkSpanElement.findElement(By.tagName("a"));
						authorRelativeUrl = authorLinkElement.getAttribute("href");
						zhiHuCollectionItem.setAuthorRelativeUrl(authorRelativeUrl);
						
						// 根据作者的url来截取作者的id
						String authorId = ZhiHuUtil.subUserIdFormUrl(authorRelativeUrl);
						zhiHuCollectionItem.setAuthorId(authorId);
						
					} catch (NoSuchElementException e1) {
						logger.info("作者[" + authorName + "][" + authorRelativeUrl + "]可能是个匿名用户或者被和谐了");
					}
					
					try {
						// 有些作者没有作者简介
						// 作者简介
						WebElement authorTitleSpanElement = answerAuthorInfoDivElement.findElement(By.className("bio"));
						
						// 作者简介
						String authorSummary = authorTitleSpanElement.getText();
						zhiHuCollectionItem.setAuthorSummary(authorSummary);
					} catch (NoSuchElementException e) {
						logger.info("作者[" + authorName + "][" + authorRelativeUrl + "]没有简介");
					}

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
					
				}else if("Post".equals(dataType)) {
					WebElement answerHeadDivElement = zmItemAnswerDivElement.findElement(By.className("post-head"));
					WebElement summaryWrapperElement = answerHeadDivElement.findElements(By.tagName("div")).get(0);
					
					
					// 作者名称
					String authorName = null;
					// 作者主页相对链接
					String authorRelativeUrl = null;
					try {
						// 作者主页链接
						WebElement authorLinkSpanElement = summaryWrapperElement.findElement(By.className("author-link-line"));
						authorName = authorLinkSpanElement.getText();
						zhiHuCollectionItem.setAuthorName(authorName);
						
						WebElement authorLinkElement = authorLinkSpanElement.findElement(By.tagName("a"));
						authorRelativeUrl = authorLinkElement.getAttribute("href");
						zhiHuCollectionItem.setAuthorRelativeUrl(authorRelativeUrl);
						
						// 根据作者的url来截取作者的id
						String authorId = ZhiHuUtil.subUserIdFormUrl(authorRelativeUrl);
						zhiHuCollectionItem.setAuthorId(authorId);
						
					} catch (NoSuchElementException e1) {
						logger.info("作者[" + authorName + "][" + authorRelativeUrl + "]可能是个匿名用户或者被和谐了");
					}
					
					try {
						// 有些作者没有作者简介
						// 作者简介
						WebElement authorTitleSpanElement = summaryWrapperElement.findElement(By.className("bio"));
						
						// 作者简介
						String authorSummary = authorTitleSpanElement.getText();
						zhiHuCollectionItem.setAuthorSummary(authorSummary);
					} catch (NoSuchElementException e) {
						logger.info("作者[" + authorName + "][" + authorRelativeUrl + "]没有简介");
					}
					
					WebElement entryBodyDivElement = zmItemAnswerDivElement.findElement(By.className("entry-body"));
					// 回答的富文本内容
					WebElement zmItemRichTextDivElement = entryBodyDivElement.findElement(By.cssSelector("[class='zm-item-rich-text js-collapse-body']"));
					WebElement postContentDivElement = zmItemRichTextDivElement.findElement(By.className("post-content"));
					// 文章的url地址
					String articleUrl = postContentDivElement.getAttribute("url");
					
					WebElement contentElement = postContentDivElement.findElement(By.tagName("textarea"));
					
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
					WebElement summaryInfoElement = postContentDivElement.findElements(By.tagName("div")).get(0);
					String summaryInfo = summaryInfoElement.getAttribute("innerHTML");
					zhiHuCollectionItem.setSummaryInfo(summaryInfo);
					
				}else {
					throw new RuntimeException("错误的dataType");
				}
				
				// TODO 修改的先不进行
				ZhiHuCollectionItem collectionItemTmp = zhiHuCollectionItemDaoService.queryZhiHuCollectionItemByAnswerId(answerId);
				
				if(collectionItemTmp != null) {
					// 存在对应记录，属于修改
					Integer id = collectionItemTmp.getId();
					zhiHuCollectionItem.setId(id);
					
					zhiHuCollectionItem.setUpdateTime(new Date());
					// TODO 进行修改
					
				}else {
					zhiHuCollectionItem.setInsertTime(new Date());
					zhiHuCollectionItem.setUpdateTime(new Date());
					zhiHuCollectionItemDaoService.insertZhiHuCollectionItem(zhiHuCollectionItem);
				}
				
			}

			
			
		}
		
		
		//分页信息
		WebElement paginationElement = driver.findElement(By.className("border-pager")).findElement(By.tagName("div"));
		//理论上我只要点击最后一个可用的分页按钮就可以往下一页走，但是我们还要知道什么时候结束
		//所以知道最大页还是有用的
		//获取最大页数，通过在第一页的时候，在分页部分，找button中的text，找到符合数字的，比较出数字中的最大值就是最大页数
		List<WebElement> buttonElements = paginationElement.findElements(By.tagName("span"));
		int maxPageNo = 1;
		if(buttonElements != null && buttonElements.size() > 0) {
			for(WebElement ele:buttonElements) {
				String text = ele.getText();
				//判断下是否为数字
				//数字、...、下一页、上一页，这4中情况
				String regEx = "^\\d+$";
			    // 编译正则表达式
			    Pattern pattern = Pattern.compile(regEx);
			    // 忽略大小写的写法
			    // Pattern pat = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
			    
		    	Matcher matcher = pattern.matcher(text);
		    	// 字符串是否与正则表达式相匹配
				if(matcher.matches()) {
					int num = Integer.parseInt(text);
					if(num > maxPageNo) {
						maxPageNo = num;
					}
				}
			}
		}
		
		if(currentPageNo < maxPageNo) {
			//有下一页，点击下一页按钮
			logger.info("处理下一页");
			this.answersCollectedIntoDB(driver, collectionId, currentPageNo + 1);
		}else {
			//没有下一页了，不继续下面的处理
		}
	}

}
