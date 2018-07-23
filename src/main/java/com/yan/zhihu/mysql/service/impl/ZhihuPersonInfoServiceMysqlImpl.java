package com.yan.zhihu.mysql.service.impl;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.yan.zhihu.mysql.dao.facade.ZhiHuCollectionDaoService;
import com.yan.zhihu.mysql.dao.impl.ZhiHuCollectionDaoServiceMybatisImpl;
import com.yan.zhihu.mysql.schema.ZhiHuCollection;
import com.yan.zhihu.service.facade.ZhihuPersonInfoService;

public class ZhihuPersonInfoServiceMysqlImpl implements ZhihuPersonInfoService{

	private static Logger logger = Logger.getLogger(ZhihuPersonInfoServiceMysqlImpl.class);
	
	@Override
	public void personalCollectionsIntoDB(WebDriver driver, String userId, int currentPageNo) {
		
		//可能页面还没有加载出来
		try {
			Thread.sleep(3 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//Profile-following
		WebElement profileFollowingElement = driver.findElement(By.id("Profile-collections"));
		
		//第一个div元素是表头
		//第二个div元素中是关注的话题列表
		WebElement followingTopicListBodyElement = profileFollowingElement.findElements(By.tagName("div")).get(1);
		
		List<WebElement> listItemElements = profileFollowingElement.findElements(By.className("List-item"));
		
		if(listItemElements != null && listItemElements.size() > 0) {
			ZhiHuCollectionDaoService zhiHuCollectionDaoService = new ZhiHuCollectionDaoServiceMybatisImpl();
			
			for(WebElement itemElement:listItemElements) {
				ZhiHuCollection zhiHuCollection = new ZhiHuCollection();

				//data-za-module="TopicItem"
				WebElement contentItemElement = itemElement.findElement(By.className("ContentItem"));
				
				//headDivElement是classname为“ContentItem-head”的div
				WebElement contentItemTitleElement = contentItemElement.findElement(By.className("ContentItem-title"));
				
				WebElement linkElement = contentItemTitleElement.findElement(By.tagName("a"));

				String collectionName = linkElement.getText();
				//     /people/wang-xi-65-12
				String collectionRelativeUrl = linkElement.getAttribute("href");
				int index = collectionRelativeUrl.lastIndexOf("/");
				String collectionId = collectionRelativeUrl.substring(index + 1);
				
				zhiHuCollection.setCollectionId(collectionId);
				zhiHuCollection.setCollectionName(collectionName);
				
				// 因为此处爬取的是用户自己创建的收藏夹，所以作者是自己
				zhiHuCollection.setAuthorId(userId);
				
				WebElement peopleItemMetaElement = contentItemElement.findElement(By.className("ContentItem-meta"));
				WebElement peopleItemStatusElement = peopleItemMetaElement.findElement(By.className("ContentItem-status"));
				List<WebElement> spanElements = peopleItemStatusElement.findElements(By.tagName("span"));
				
				
				String regEx = "\\d+";
				// 编译正则表达式
				Pattern pattern = Pattern.compile(regEx);
				
				if(spanElements != null && spanElements.size() > 0) {
					int i = 0;
					for(WebElement spanEle:spanElements) {
						//15个答案
						String linkText = spanEle.getText();
						
						if(i == 0) {
							//日期
							linkText = linkText.replaceAll("更新", "");
							zhiHuCollection.setContentLastModifyDay(linkText);
							
						}else {
							Matcher matcher = pattern.matcher(linkText);
							if(matcher.find()) {
								String answersInTopicCountStr = matcher.group();
								int count = Integer.parseInt(answersInTopicCountStr);
								//System.out.println(answersInTopicCount);
								if(i == 1) {
									//内容数
									zhiHuCollection.setFollowersCount(count);;
								}else if(i == 2) {
									//关注人数
									zhiHuCollection.setFollowersCount(count);;
								}
							}
						}
						i++;
					}
					
				}
		    	
		    	
		    	//TODO 先保存关注的这个专栏的基本信息
				ZhiHuCollection collection = zhiHuCollectionDaoService.queryZhiHuCollectionByCollectionId(collectionId);
		    	if(collection == null) {
		    		zhiHuCollection.setInsertTime(new Date());
		    		zhiHuCollection.setUpdateTime(new Date());
		    		zhiHuCollectionDaoService.insertZhiHuCollection(zhiHuCollection);
		    	}else {
		    		//TODO 更新操作先搁置
		    		
		    	}
		    	
			}
		}
		
		
		WebDriverWait wait = new WebDriverWait(driver,10);
		Boolean isPagination = wait.until(new ExpectedCondition<Boolean>(){
        	@Override
            public Boolean apply(WebDriver d) {
        		Boolean flag = false;
        		
        		try {
					driver.findElement(By.className("Pagination"));
					flag = true;
				} catch (Exception e) {
					logger.error(e.getLocalizedMessage());
				}
        		
                return flag;
        	}
        });
		
        if(isPagination) {
        	//分页信息
        	WebElement paginationElement = profileFollowingElement.findElement(By.className("Pagination"));
        	//理论上我只要点击最后一个可用的分页按钮就可以往下一页走，但是我们还要知道什么时候结束
        	//所以知道最大页还是有用的
        	//获取最大页数，通过在第一页的时候，在分页部分，找button中的text，找到符合数字的，比较出数字中的最大值就是最大页数
        	List<WebElement> buttonElements = paginationElement.findElements(By.tagName("button"));
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
        	//当前页，class中有如下内容
        	//PaginationButton--current
        	WebElement currentPageElement = paginationElement.findElement(By.className("PaginationButton--current"));
        	String currentPageNoStr = currentPageElement.getText();
        	
        	if(currentPageNo < maxPageNo) {
        		//有下一页，点击下一页按钮
        		System.out.println("处理下一页");
        		//By.cssSelector("[class='NumberBoard FollowshipCard-counts']")
        		paginationElement.findElement(By.cssSelector("[class='Button PaginationButton PaginationButton-next Button--plain']")).click();
        		personalCollectionsIntoDB(driver, userId, currentPageNo + 1);
        	}else {
        		//没有下一页了，不继续下面的处理
        	}
        }
	}

}
