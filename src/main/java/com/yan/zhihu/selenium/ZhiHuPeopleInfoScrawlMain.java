package com.yan.zhihu.selenium;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yan.common.PropertiesIOUtil;
import com.yan.zhihu.dao.ZhiHuActivityMongoDaoUtil;
import com.yan.zhihu.dao.ZhiHuColumnMongoDaoUtil;
import com.yan.zhihu.dao.ZhiHuPeopleColumnMongoDaoUtil;
import com.yan.zhihu.dao.ZhiHuPeopleMongoDaoUtil;
import com.yan.zhihu.dao.ZhiHuPeopleQuestionMongoDaoUtil;
import com.yan.zhihu.dao.ZhiHuPeopleTopicMongoDaoUtil;
import com.yan.zhihu.dao.ZhiHuQuestionMongoDaoUtil;
import com.yan.zhihu.dao.ZhiHuTopicMongoDaoUtil;
import com.yan.zhihu.model.ZhiHuActivity;
import com.yan.zhihu.model.ZhiHuColumn;
import com.yan.zhihu.model.ZhiHuPeople;
import com.yan.zhihu.model.ZhiHuPeopleColumn;
import com.yan.zhihu.model.ZhiHuPeopleQuestion;
import com.yan.zhihu.model.ZhiHuPeopleTopic;
import com.yan.zhihu.model.ZhiHuQuestion;
import com.yan.zhihu.model.ZhiHuTopic;
import com.yan.zhihu.model.subvo.AnswerInfo;
import com.yan.zhihu.model.subvo.ArticleInfo;
import com.yan.zhihu.model.subvo.CollectionInfo;
import com.yan.zhihu.model.subvo.ColumnInfo;
import com.yan.zhihu.model.subvo.QuestionInfo;
import com.yan.zhihu.model.subvo.TopicInfo;

public class ZhiHuPeopleInfoScrawlMain {
	
	private static String userId;
	
	private static String webdriverFirefoxBin;
	
	private static String webdriverGeckoDriver;
	
	static {
		Properties properties = PropertiesIOUtil.loadProperties("/config.properties");
		userId = properties.getProperty("userId");
		webdriverFirefoxBin = properties.getProperty("webdriver.firefox.bin");
		webdriverGeckoDriver = properties.getProperty("webdriver.gecko.driver");
	}
	
	private static Logger logger = Logger.getLogger(ZhiHuPeopleInfoScrawlMain.class);
	
	public static final String ZHI_HU_ROOT_URL = "https://www.zhihu.com";
	
	public static void main(String[] args) {
		WebDriver driver = getWebDriver();
		
		//用户动态，或者叫做用户活动
//		personalMainPage(driver, userId, "activities");
		
		//关注了哪些话题
//		personalMainPage(driver, userId, "followingTopics");
		
//		personalMainPage(driver, userId, "followingColumns");
		
		personalMainPage(driver, userId, "followingQuestions");
		
		//关注了哪些人
//		personalMainPage(driver, userId, "followings");
//		//那些人关注了我
//		personalMainPage(driver, userId, "followers");
		
	}
	
	/**
	 * 获取webdriver
	 * @return
	 */
	public static WebDriver getWebDriver() {
		System.setProperty("webdriver.firefox.bin", webdriverFirefoxBin); 
		System.setProperty("webdriver.gecko.driver", webdriverGeckoDriver);
		WebDriver driver = new FirefoxDriver();
		
		//将浏览器最大化
		driver.manage().window().maximize();
		
		return driver;
	}
	
	/**
	 * 根据userId进入知乎用户的主页，获取主页中知乎用户的一些信息以及他关注的信息
	 * 在这个方法中只获取这个用户关注的其他用户的简要信息，如id等，不获取其他用户关注的信息
	 * 其他用户关注的信息后续通过单独的调度队列完成
	 * 
	 * @param driver
	 * @param userId
	 * @param step followingTopics, followings, followers
	 * @return
	 */
	public static ZhiHuPeople personalMainPage(WebDriver driver, String userId, String step) {
		ZhiHuPeople zhiHuPeople = new ZhiHuPeople();
		zhiHuPeople.setUserId(userId);
		
		driver.get("https://www.zhihu.com/people/" + userId + "/activities");
		
		//获取页面源码
//		String pageSource = driver.getPageSource();
//		System.out.println(pageSource);
		
		//右侧的整个区域
		WebElement profileSideColumnElement = driver.findElement(By.className("Profile-sideColumn"));
		
		//个人成就
		WebElement profileSideColumnItemsElement = profileSideColumnElement.findElement(By.className("Profile-sideColumnItems"));
		List<WebElement> profileSideColumnItemElements = profileSideColumnItemsElement.findElements(By.className("Profile-sideColumnItem"));
		
		String regEx = "\\d+";
		Pattern pattern = Pattern.compile(regEx);
		
		//被赞同的次数
		//获得 241 次赞同
		String agreeStr = profileSideColumnItemElements.get(0).findElement(By.className("IconGraf")).getText();
		
		Matcher m = pattern.matcher(agreeStr);
    	int i = 0;
    	while(m.find()) {
			//System.out.println(matcher.group());
			if(i == 0) {
				//被赞同的次数
				zhiHuPeople.setBeAgreedCount(Integer.parseInt(m.group()));
			}
			i++;
    	}
		
		String thankAndMarkStr = profileSideColumnItemElements.get(0).findElement(By.className("Profile-sideColumnItemValue")).getText();
		
		//获得 66 次感谢，92 次收藏
		
    	Matcher matcher = pattern.matcher(thankAndMarkStr);
    	int index = 0;
    	while(matcher.find()) {
			//System.out.println(matcher.group());
			if(index == 0) {
				//获得的感谢次数
				zhiHuPeople.setBeThankedCount(Integer.parseInt(matcher.group()));
			}else if (index == 1) {
				//获得的收藏次数
				zhiHuPeople.setBeCollectedCount(Integer.parseInt(matcher.group()));
			}
			index++;
    	}
    	
		//关注了 关注者 这个整体的区域
		//http://blog.csdn.net/cyjs1988/article/details/75006167
		//class中有空格的时候不能直接by.classname，需要使用css选择器
		//class属性中间的空格并不是空字符串，那是间隔符号，表示的是一个元素有多个class的属性名称
		//(class属性是比较特殊的一个，除了这个有多个属性外，其它的像name,id是没多个属性的)
		//.findElement(By.cssSelector("[class='Card FollowshipCard']"))
		//既然知道class属性有空格是多个属性了，那定位的时候取其中的一个就行（并且要唯一），也就是说class="j-inputtext dlemail"，
		//取j-inputtext 和dlemail都是可以的，这样这个class属性在页面上唯一就行
		WebElement followshipCardCountsElement = profileSideColumnElement.findElement(By.cssSelector("[class='NumberBoard FollowshipCard-counts']"));
		
		//关注了 的数量
		List<WebElement> alinkElements = followshipCardCountsElement.findElements(By.tagName("a"));
		
		//关注了
		WebElement followingElement = alinkElements.get(0);
		//href
		String followingUrl = followingElement.getAttribute("href");
		//NumberBoard-name, NumberBoard-value
		String followingCount = followingElement.findElement(By.className("NumberBoard-value")).getText();
		
		zhiHuPeople.setFollowingUrl(followingUrl);
		zhiHuPeople.setFollowingCount(Integer.parseInt(followingCount));
		
		//关注者
		WebElement followersElement = alinkElements.get(1);
		String followersUrl = followingElement.getAttribute("href");
		String followersCount = followingElement.findElement(By.className("NumberBoard-value")).getText();
		
		zhiHuPeople.setFollowersUrl(followersUrl);
		zhiHuPeople.setFollowersCount(Integer.parseInt(followersCount));
		
		//关注的话题、专栏、问题、收藏夹
		List<WebElement> profileLightItemElements = profileSideColumnElement.findElements(By.className("Profile-lightItem"));
		
		//关注的话题
		WebElement topicsFollowingElement = profileLightItemElements.get(0);
		String topicsFollowingUrl = topicsFollowingElement.getAttribute("href");
		//Profile-lightItemName
		//Profile-lightItemValue
		String topicsFollowingCount = topicsFollowingElement.findElement(By.className("Profile-lightItemValue")).getText();
		zhiHuPeople.setFollowingTopicsUrl(topicsFollowingUrl);
		
		//关注的专栏
		WebElement columnsFollowingElement = profileLightItemElements.get(1);
		String columnsFollowingUrl = columnsFollowingElement.getAttribute("href");
		//Profile-lightItemName
		//Profile-lightItemValue
		String columnsFollowingCount = columnsFollowingElement.findElement(By.className("Profile-lightItemValue")).getText();
		zhiHuPeople.setFollowingColumnsUrl(columnsFollowingUrl);
		
		//关注的问题
		WebElement questionsFollowingElement = profileLightItemElements.get(2);
		String questionsFollowingUrl = questionsFollowingElement.getAttribute("href");
		//Profile-lightItemName
		//Profile-lightItemValue
		String questionsFollowingCount = questionsFollowingElement.findElement(By.className("Profile-lightItemValue")).getText();
		zhiHuPeople.setFollowingQuestionsUrl(questionsFollowingUrl);
		
		//收藏夹
		WebElement collectionsFollowingElement = profileLightItemElements.get(3);
		String collectionsFollowingUrl = collectionsFollowingElement.getAttribute("href");
		//Profile-lightItemName
		//Profile-lightItemValue
		String collectionsFollowingCount = collectionsFollowingElement.findElement(By.className("Profile-lightItemValue")).getText();
		zhiHuPeople.setFollowingCollectionsUrl(collectionsFollowingUrl);
		
		//个人主页被浏览了多少次
		//WebElement footerOperationsElement = driver.findElement(By.className("Profile-footerOperations"));
		//String personalMaimPageViewedCountStr = footerOperationsElement.getText();
		//System.out.println(personalMaimPageViewedCountStr);
		
		
		//TODO 在此处将知乎用户的基本信息保存或者更新到数据库
		//先使用userId查询下数据是否存在
		//不存在添加
		//如果存在进行更新
		ZhiHuPeopleMongoDaoUtil zhiHuPeopleMongoDaoUtil = new ZhiHuPeopleMongoDaoUtil();
		ZhiHuPeople people = zhiHuPeopleMongoDaoUtil.findZhiHuPeopleByUserId(userId);
		if(people != null) {
			logger.info("ZhiHuPeople exists with userId:" +userId + ". update ZhiHuPeople");
		}else {
			logger.info("ZhiHuPeople not exists. Create ZhiHuPeople.");
			zhiHuPeople.setInsertTime(new Date());
			zhiHuPeople.setUpdateTime(new Date());
			String id = zhiHuPeopleMongoDaoUtil.insertZhiHuPeople(zhiHuPeople);
			logger.info("ZhiHuPeople created. mongo id is : " + id);
		}
		
		//点击关注的话题连接，进入关注的话题页面
		//如果是采用这种方式进入新的页面，那么需要考虑的是打开新的页面处理结束之后，还需要回到原来的页面进行之前的流程
		if(step != null) {
			if("activities".equals(step.trim())){
				personalActivities(driver, userId);
			}else if("followingTopics".equals(step.trim())) {
				topicsFollowingElement.click();
				followingTopics(driver, userId, 1);
				System.out.println("关注的话题处理结束");
			}else if("followingColumns".equals(step.trim())) {
				columnsFollowingElement.click();
				followingColumns(driver, userId, 1);
			}else if("followingQuestions".equals(step.trim())) {
				questionsFollowingElement.click();
				followingQuestions(driver, userId, 100, "2");
			}else if("followings".equals(step.trim())) {
				//页面刷新之后，需要重新获取元素
				followingElement.click();
				// 处理关注的人
				followingPeoples(driver, userId, 1);
			}else if("followers".equals(step.trim())) {
				followersElement.click();
				followerPeoples(driver, userId, 1);
			}
		}
		
		
		return null;
	}
	
	
	public static void personalActivities(WebDriver driver, String userId) {
		try {
			JavascriptExecutor jse = (JavascriptExecutor) driver;
			
			//String getScrollHeight = "document.documentElement.scrollTop";
			
			
			ZhiHuActivityMongoDaoUtil zhiHuActivityMongoDaoUtil = new ZhiHuActivityMongoDaoUtil();
			
			long currentHeight = 500L;
			
			//只查看最近一段时间内的活动
			boolean currentActiviesNotFinish = true;
			//滚动滚动条的次数
			int i = 0;
			//是否还有新的活动
			boolean hasNewActivies = true;
			
			Set<String> activitySet = new HashSet<>();
			
			//活动的日期和itemindex的映射
			Map<String, Integer> activityDayToItemIndexMap = new HashMap<>();
			//这个list用来存储活动的日期，这样就可以知道倒数X天是那一天了
			List<String> activityDayList = new ArrayList<>();
			
			Map<String, Integer> activityDayToScrollAddHeightMap = new HashMap<>();
			
			//循环终止的条件
			//1、获取最近一段时间的内容
			//2、最多循环100次
			//3、有可能内容不够，即使再刷新内容也无法更新了
			while( currentActiviesNotFinish && hasNewActivies && i < 100){
				
				//用来判断每次向下滚动滚动条，是否有还有新的活动
				long newActivityCount = 0L;
				
				//long currentHeight = (Long)jse.executeScript(getScrollHeight);
				
				// 获取用户的活动
				WebElement profileActivitiesElement = driver.findElement(By.id("Profile-activities"));
				List<WebElement> listItemElements = profileActivitiesElement.findElements(By.className("List-item"));
				
				int itemIndex = 0;
				//倒数前1天的itemIndex
				if(activityDayList.size() >= 1) {
					String day = activityDayList.get(activityDayList.size()-1);
					if(activityDayToItemIndexMap.containsKey(day) && activityDayToItemIndexMap.get(day) != null) {
						itemIndex = activityDayToItemIndexMap.get(day);
					}
				}
				
				if(listItemElements != null && listItemElements.size() > 0) {
					for(;itemIndex<listItemElements.size();itemIndex++) {
						
						WebElement element = listItemElements.get(itemIndex);
						
						ZhiHuActivity zhiHuActivity = new ZhiHuActivity();
						
						zhiHuActivity.setUserId(userId);
						
						WebElement listItemMetaElement = element.findElement(By.className("List-itemMeta"));
						List<WebElement> spanElements = listItemMetaElement.findElements(By.tagName("span"));
						String activityTypeFullName = spanElements.get(0).getText().trim();
						String timeText = spanElements.get(1).getText();
						
						logger.info(timeText);
						
						Calendar calendar = Calendar.getInstance();
						int timeCount = 0;
						
						String regEx = "\\d+";
					    Pattern pattern = Pattern.compile(regEx);
				    	Matcher matcher = pattern.matcher(timeText);
				    	if(matcher.find()) {
				    		String timeCountStr = matcher.group();
				    		timeCount = Integer.parseInt(timeCountStr);
				    	}
				    	
						//X分钟前，X小时前，X天前，X月前
						String timeUnit = timeText.replaceAll("\\d+", "");
						if("分钟前".equals(timeUnit.trim())){
							timeUnit = "分钟";
							calendar.add(Calendar.MINUTE, -1 * timeCount);
						}else if("小时前".equals(timeUnit.trim())){
							timeUnit = "小时";
							calendar.add(Calendar.HOUR, -1 * timeCount);
						}else if("天前".equals(timeUnit.trim())){
							timeUnit = "天";
							calendar.add(Calendar.DATE, -1 * timeCount);
						}else if("月前".equals(timeUnit.trim())){
							timeUnit = "月";
							calendar.add(Calendar.MONTH, -1 * timeCount);
						}
						
						//只获取最近两天内的活动
						if(timeCount >= 10 && "天".equals(timeUnit)) {
							currentActiviesNotFinish = false;
							logger.info("loop break currentActiviesNotFinish");
							break;
						}
						
						Date activityDate = calendar.getTime();
				    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				    	String activityDay = sdf.format(activityDate);
						zhiHuActivity.setActivityDay(activityDay);
						
						activityDayToItemIndexMap.put(activityDay, itemIndex);
						if(!activityDayList.contains(activityDay)) {
							activityDayList.add(activityDay);
						}
						
						
						//赞了文章，收藏了文章，赞同了回答，收藏了回答，关注了问题，回答了问题，关注了专栏，关注了收藏夹，关注了话题，发布了想法
						String activityType = null;
						String itemName = null;
						
						if(activityTypeFullName.endsWith("赞了文章")){
							activityType = "赞了文章";
							
							ArticleInfo articleInfo = new ArticleInfo();
							WebElement contentItemElement = element.findElement(By.cssSelector("[class='ContentItem ArticleItem']"));
							WebElement contentItemTitleElement = contentItemElement.findElement(By.className("ContentItem-title"));
							
							//文章id和文章标题
							WebElement aElement = contentItemTitleElement.findElement(By.tagName("a"));
							String articleRelativeUrl = aElement.getAttribute("href");
							String articleName = aElement.getText();
							itemName = articleName;
							
							int index1 = articleRelativeUrl.lastIndexOf("/");
							String articleId = articleRelativeUrl.substring(index1 + 1);
							articleInfo.setArticleId(articleId);
							articleInfo.setArticleName(articleName);
							
							//回答的作者信息
							WebElement contentItemMetaElement = contentItemElement.findElement(By.className("ContentItem-meta"));
							WebElement authorInfoElement = contentItemMetaElement.findElement(By.cssSelector("[class='AuthorInfo ArticleItem-authorInfo']"));
							
							List<WebElement> authorInfoMetaElements = authorInfoElement.findElements(By.tagName("meta"));
							if(authorInfoMetaElements != null && authorInfoMetaElements.size() > 0){
								for(WebElement metaElement:authorInfoMetaElements){
									//url,name
									String itemprop = metaElement.getAttribute("itemprop");
									String content = metaElement.getAttribute("content");
									if("url".equals(itemprop.trim())){
										String authorUrl = content;
										
										int index = authorUrl.lastIndexOf("/");
										String authorId = authorUrl.substring(index + 1);
										articleInfo.setAuthorId(authorId);
									}else if("name".equals(itemprop.trim())){
										String authorName = content;
										articleInfo.setAuthorName(authorName);
									}
								}
							}
							
							zhiHuActivity.setArticle(articleInfo);
							
						}else if(activityTypeFullName.endsWith("收藏了文章")){
							activityType = "收藏了文章";
							
							ArticleInfo articleInfo = new ArticleInfo();
							WebElement contentItemElement = element.findElement(By.cssSelector("[class='ContentItem ArticleItem']"));
							WebElement contentItemTitleElement = contentItemElement.findElement(By.className("ContentItem-title"));
							
							//文章id和文章标题
							WebElement aElement = contentItemTitleElement.findElement(By.tagName("a"));
							String articleRelativeUrl = aElement.getAttribute("href");
							String articleName = aElement.getText();
							itemName = articleName;
							
							int index1 = articleRelativeUrl.lastIndexOf("/");
							String articleId = articleRelativeUrl.substring(index1 + 1);
							articleInfo.setArticleId(articleId);
							articleInfo.setArticleName(articleName);
							
							//回答的作者信息
							WebElement contentItemMetaElement = contentItemElement.findElement(By.className("ContentItem-meta"));
							WebElement authorInfoElement = contentItemMetaElement.findElement(By.cssSelector("[class='AuthorInfo ArticleItem-authorInfo']"));
							
							List<WebElement> authorInfoMetaElements = authorInfoElement.findElements(By.tagName("meta"));
							if(authorInfoMetaElements != null && authorInfoMetaElements.size() > 0){
								for(WebElement metaElement:authorInfoMetaElements){
									//url,name
									String itemprop = metaElement.getAttribute("itemprop");
									String content = metaElement.getAttribute("content");
									if("url".equals(itemprop.trim())){
										String authorUrl = content;
										
										int index = authorUrl.lastIndexOf("/");
										String authorId = authorUrl.substring(index + 1);
										articleInfo.setAuthorId(authorId);
									}else if("name".equals(itemprop.trim())){
										String authorName = content;
										articleInfo.setAuthorName(authorName);
									}
								}
							}
							
							zhiHuActivity.setArticle(articleInfo);
							
						}else if(activityTypeFullName.endsWith("赞同了回答")){
							activityType = "赞同了回答";
							
							AnswerInfo answerInfo = new AnswerInfo();
							WebElement contentItemElement = element.findElement(By.cssSelector("[class='ContentItem AnswerItem']"));
							WebElement contentItemTitleElement = contentItemElement.findElement(By.className("ContentItem-title"));
							
							//问题的id和问题的全链接
							List<WebElement> metaElements = contentItemTitleElement.findElements(By.tagName("meta"));
							if(metaElements != null && metaElements.size() > 0){
								for(WebElement metaElement:metaElements){
									//url,name
									String itemprop = metaElement.getAttribute("itemprop");
									String content = metaElement.getAttribute("content");
									if("url".equals(itemprop.trim())){
										String questionUrl = content;
										
										int index = questionUrl.lastIndexOf("/");
										String questionId = questionUrl.substring(index + 1);
										answerInfo.setQuestionId(questionId);
									}else if("name".equals(itemprop.trim())){
										String questionName = content;
										answerInfo.setQuestionName(questionName);
									}
								}
							}
							
							//答案的相对链接和答案的id
							WebElement aElement = contentItemTitleElement.findElement(By.tagName("a"));
							String answerRelativeUrl = aElement.getAttribute("href");
							int index1 = answerRelativeUrl.lastIndexOf("/");
							String answerId = answerRelativeUrl.substring(index1 + 1);
							answerInfo.setAnswerId(answerId);
							answerInfo.setAnswerRelativeUrl(answerRelativeUrl);
							itemName = answerId;
							
							//回答的作者信息
							WebElement contentItemMetaElement = contentItemElement.findElement(By.className("ContentItem-meta"));
							WebElement authorInfoElement = contentItemMetaElement.findElement(By.className("AuthorInfo"));
							
							List<WebElement> authorInfoMetaElements = authorInfoElement.findElements(By.tagName("meta"));
							if(authorInfoMetaElements != null && authorInfoMetaElements.size() > 0){
								for(WebElement metaElement:authorInfoMetaElements){
									//url,name
									String itemprop = metaElement.getAttribute("itemprop");
									String content = metaElement.getAttribute("content");
									if("url".equals(itemprop.trim())){
										String authorUrl = content;
										
										int index = authorUrl.lastIndexOf("/");
										String authorId = authorUrl.substring(index + 1);
										answerInfo.setAuthorId(authorId);
									}else if("name".equals(itemprop.trim())){
										String authorName = content;
										answerInfo.setAuthorName(authorName);
									}
								}
							}
							
							zhiHuActivity.setAnswer(answerInfo);
							
						}else if(activityTypeFullName.endsWith("收藏了回答")){
							activityType = "收藏了回答";
							
							AnswerInfo answerInfo = new AnswerInfo();
							WebElement contentItemElement = element.findElement(By.cssSelector("[class='ContentItem AnswerItem']"));
							WebElement contentItemTitleElement = contentItemElement.findElement(By.className("ContentItem-title"));
							
							//问题的id和问题的全链接
							List<WebElement> metaElements = contentItemTitleElement.findElements(By.tagName("meta"));
							if(metaElements != null && metaElements.size() > 0){
								for(WebElement metaElement:metaElements){
									//url,name
									String itemprop = metaElement.getAttribute("itemprop");
									String content = metaElement.getAttribute("content");
									if("url".equals(itemprop.trim())){
										String questionUrl = content;
										
										int index = questionUrl.lastIndexOf("/");
										String questionId = questionUrl.substring(index + 1);
										answerInfo.setQuestionId(questionId);
									}else if("name".equals(itemprop.trim())){
										String questionName = content;
										answerInfo.setQuestionName(questionName);
									}
								}
							}
							
							//答案的相对链接和答案的id
							WebElement aElement = contentItemTitleElement.findElement(By.tagName("a"));
							String answerRelativeUrl = aElement.getAttribute("href");
							int index1 = answerRelativeUrl.lastIndexOf("/");
							String answerId = answerRelativeUrl.substring(index1 + 1);
							answerInfo.setAnswerId(answerId);
							answerInfo.setAnswerRelativeUrl(answerRelativeUrl);
							itemName = answerId;
							
							//回答的作者信息
							WebElement contentItemMetaElement = contentItemElement.findElement(By.className("ContentItem-meta"));
							WebElement authorInfoElement = contentItemMetaElement.findElement(By.className("AuthorInfo"));
							
							List<WebElement> authorInfoMetaElements = authorInfoElement.findElements(By.tagName("meta"));
							if(authorInfoMetaElements != null && authorInfoMetaElements.size() > 0){
								for(WebElement metaElement:authorInfoMetaElements){
									//url,name
									String itemprop = metaElement.getAttribute("itemprop");
									String content = metaElement.getAttribute("content");
									if("url".equals(itemprop.trim())){
										String authorUrl = content;
										
										int index = authorUrl.lastIndexOf("/");
										String authorId = authorUrl.substring(index + 1);
										answerInfo.setAuthorId(authorId);
									}else if("name".equals(itemprop.trim())){
										String authorName = content;
										answerInfo.setAuthorName(authorName);
									}
								}
							}
							
							zhiHuActivity.setAnswer(answerInfo);
							
						}else if(activityTypeFullName.endsWith("关注了问题")){
							activityType = "关注了问题";
							
							QuestionInfo questionInfo = new QuestionInfo();
							WebElement contentItemElement = element.findElement(By.cssSelector("[class='ContentItem']"));
							WebElement contentItemTitleElement = contentItemElement.findElement(By.className("ContentItem-title"));
							
							//答案的相对链接和答案的id
							WebElement aElement = contentItemTitleElement.findElement(By.tagName("a"));
							String questionRelativeUrl = aElement.getAttribute("href");
							String questionName = aElement.getText();
							itemName = questionName;
							
							int index1 = questionRelativeUrl.lastIndexOf("/");
							String questionId = questionRelativeUrl.substring(index1 + 1);
							questionInfo.setQuestionId(questionId);
							questionInfo.setQuestionName(questionName);
							
							zhiHuActivity.setQuestion(questionInfo);
							
						}else if(activityTypeFullName.endsWith("回答了问题")){
							activityType = "回答了问题";
							
							AnswerInfo answerInfo = new AnswerInfo();
							WebElement contentItemElement = element.findElement(By.cssSelector("[class='ContentItem AnswerItem']"));
							WebElement contentItemTitleElement = contentItemElement.findElement(By.className("ContentItem-title"));
							
							//问题的id和问题的全链接
							List<WebElement> metaElements = contentItemTitleElement.findElements(By.tagName("meta"));
							if(metaElements != null && metaElements.size() > 0){
								for(WebElement metaElement:metaElements){
									//url,name
									String itemprop = metaElement.getAttribute("itemprop");
									String content = metaElement.getAttribute("content");
									if("url".equals(itemprop.trim())){
										String questionUrl = content;
										
										int index = questionUrl.lastIndexOf("/");
										String questionId = questionUrl.substring(index + 1);
										answerInfo.setQuestionId(questionId);
									}else if("name".equals(itemprop.trim())){
										String questionName = content;
										answerInfo.setQuestionName(questionName);
									}
								}
							}
							
							//答案的相对链接和答案的id
							WebElement aElement = contentItemTitleElement.findElement(By.tagName("a"));
							String answerRelativeUrl = aElement.getAttribute("href");
							int index1 = answerRelativeUrl.lastIndexOf("/");
							String answerId = answerRelativeUrl.substring(index1 + 1);
							answerInfo.setAnswerId(answerId);
							answerInfo.setAnswerRelativeUrl(answerRelativeUrl);
							itemName = answerId;
							
							//回答的作者信息
							WebElement contentItemMetaElement = contentItemElement.findElement(By.className("ContentItem-meta"));
							WebElement authorInfoElement = contentItemMetaElement.findElement(By.className("AuthorInfo"));
							
							List<WebElement> authorInfoMetaElements = authorInfoElement.findElements(By.tagName("meta"));
							if(authorInfoMetaElements != null && authorInfoMetaElements.size() > 0){
								for(WebElement metaElement:authorInfoMetaElements){
									//url,name
									String itemprop = metaElement.getAttribute("itemprop");
									String content = metaElement.getAttribute("content");
									if("url".equals(itemprop.trim())){
										String authorUrl = content;
										
										int index = authorUrl.lastIndexOf("/");
										String authorId = authorUrl.substring(index + 1);
										answerInfo.setAuthorId(authorId);
									}else if("name".equals(itemprop.trim())){
										String authorName = content;
										answerInfo.setAuthorName(authorName);
									}
								}
							}
							
							zhiHuActivity.setAnswer(answerInfo);
							
						}else if(activityTypeFullName.endsWith("关注了专栏")){
							activityType = "关注了专栏";
							
							ColumnInfo columnInfo = new ColumnInfo();
							WebElement contentItemElement = element.findElement(By.cssSelector("[class='ContentItem']"));
							WebElement contentItemTitleElement = contentItemElement.findElement(By.className("ContentItem-title"));
							
							//答案的相对链接和答案的id
							WebElement aElement = contentItemTitleElement.findElement(By.tagName("a"));
							String columnRelativeUrl = aElement.getAttribute("href");
							String columnName = aElement.getText();
							int index1 = columnRelativeUrl.lastIndexOf("/");
							String columnId = columnRelativeUrl.substring(index1 + 1);
							columnInfo.setColumnId(columnId);
							columnInfo.setColumnName(columnName);
							itemName = columnName;
							
							zhiHuActivity.setColumn(columnInfo);
							
						}else if(activityTypeFullName.endsWith("关注了收藏夹")){
							activityType = "关注了收藏夹";
							
							CollectionInfo collectionInfo = new CollectionInfo();
							WebElement contentItemElement = element.findElement(By.cssSelector("[class='ContentItem']"));
							WebElement contentItemTitleElement = contentItemElement.findElement(By.className("ContentItem-title"));
							
							//答案的相对链接和答案的id
							WebElement aElement = contentItemTitleElement.findElement(By.tagName("a"));
							String collectionRelativeUrl = aElement.getAttribute("href");
							String collectionName = aElement.getText();
							int index1 = collectionRelativeUrl.lastIndexOf("/");
							String collectionId = collectionRelativeUrl.substring(index1 + 1);
							
							collectionInfo.setCollectionId(collectionId);
							collectionInfo.setCollectionName(collectionName);
							itemName = collectionName;
							
							zhiHuActivity.setCollection(collectionInfo);
							
						}else if(activityTypeFullName.endsWith("关注了话题")){
							activityType = "关注了话题";
							
							TopicInfo topicInfo = new TopicInfo();
							WebElement contentItemElement = element.findElement(By.cssSelector("[class='ContentItem']"));
							WebElement contentItemTitleElement = contentItemElement.findElement(By.className("ContentItem-title"));
							
							//答案的相对链接和答案的id
							WebElement aElement = contentItemTitleElement.findElement(By.tagName("a"));
							String topicRelativeUrl = aElement.getAttribute("href");
							String topicName = aElement.getText();
							int index1 = topicRelativeUrl.lastIndexOf("/");
							String topicId = topicRelativeUrl.substring(index1 + 1);
							topicInfo.setTopicId(topicId);
							topicInfo.setTopicName(topicName);
							itemName = topicName;
							
							zhiHuActivity.setTopic(topicInfo);
							
						}else if(activityTypeFullName.endsWith("发布了想法")){
							activityType = "发布了想法";
							
							WebElement richContentInnerElement = element.findElement(By.cssSelector("[class='RichContent-inner']"));
							String text = richContentInnerElement.getText();
							itemName = text;
							
						}else{
							activityType = activityTypeFullName;
							//特殊情况记录进日志
						}
						
						zhiHuActivity.setItemName(itemName);
						zhiHuActivity.setActivityType(activityType);
						zhiHuActivity.setActivityTypeFullName(activityTypeFullName);
						
						String key = activityTypeFullName + "," + itemName;
						if(activitySet.contains(key)) {
							//不做处理
						}else {
							ZhiHuActivity activity = zhiHuActivityMongoDaoUtil.findZhiHuActivityByActivityTypeFullNameAndItemName(activityTypeFullName, itemName);
							if(activity == null) {
								zhiHuActivity.setInsertTime(new Date());
								zhiHuActivity.setUpdateTime(new Date());
								zhiHuActivityMongoDaoUtil.insertZhiHuActivity(zhiHuActivity);
								newActivityCount++;
							}else {
								String id = activity.getId();
								zhiHuActivity.setId(id);
								zhiHuActivity.setUpdateTime(new Date());
								zhiHuActivityMongoDaoUtil.updateZhiHuActivity(zhiHuActivity);
							}
							activitySet.add(key);
						}
					}
					
				}
				
//				//如果每次向下滚动滚动条，都没有新的活动要保存，那么说明没有新的活动可以加载出来了
//				if(newActivityCount == 0L) {
//					hasNewActivies = false;
//					logger.info("loop break hasNewActivies");
//					break;
//				}
				
				//用户的活动类型：赞了文章，收藏了文章，赞同了回答，收藏了回答，关注了问题，回答了问题，关注了专栏，关注了收藏夹，关注了话题，发布了想法
				//用户的活动时间	1分钟前，1小时前，1天前，1个月前
				
				
				//判断下当前用户活动的活动时间据今天时间，只读取最近几天的活动
				
				//判断用户的活动是否存在
				
				//如果用户的活动已经阅读过了，那么我们
				
				//每次滚动50%
				long addHeight = currentHeight/2;
				currentHeight = currentHeight + addHeight;
				String setScrollHeight = "document.documentElement.scrollTop=" + currentHeight;  
				jse.executeScript(setScrollHeight);
				
				logger.info("滚动条高度 : " + currentHeight + "，高度增加" + addHeight);
				logger.info("向下滚动滚动条第 " + (i+1) + "次");
				i++;
				
				//休息5秒，避免加载不出来，也避免访问过快
				Thread.sleep(5 * 1000);
			}
		} catch (Exception e) {
            logger.error("Fail to set the scroll." + e.getLocalizedMessage());
        }
		
	}
	
	/**
	 * 
	 * @param driver
	 * @param relaticeUrl	/people/yankj12/following/topics
	 */
	public static void followingTopics(WebDriver driver, String userId, int currentPageNo) {
		
		//Profile-following
		WebElement profileFollowingElement = driver.findElement(By.id("Profile-following"));
		
		//第一个div元素是表头
		//第二个div元素中是关注的话题列表
		WebElement followingTopicListBodyElement = profileFollowingElement.findElements(By.tagName("div")).get(1);
		
		List<WebElement> listItemElements = profileFollowingElement.findElements(By.className("List-item"));
		
		if(listItemElements != null && listItemElements.size() > 0) {
			ZhiHuTopicMongoDaoUtil zhiHuTopicMongoDaoUtil = new ZhiHuTopicMongoDaoUtil();
	    	ZhiHuPeopleTopicMongoDaoUtil zhiHuPeopleTopicMongoDaoUtil = new ZhiHuPeopleTopicMongoDaoUtil();
	    	
			for(WebElement element:listItemElements) {
				ZhiHuTopic zhiHuTopic = new ZhiHuTopic();
				ZhiHuPeopleTopic zhiHuPeopleTopic = new ZhiHuPeopleTopic();
				zhiHuPeopleTopic.setUserId(userId);
				
				//data-za-module="TopicItem"
				String dataZaModule = element.getAttribute("data-za-module");
				
				//data-za-module-info='{"card":{"content":{"type":"Topic","token":"19551424"}}}'
				//这个是topic的一些数据
				String dataZaModuleInfo = element.getAttribute("data-za-module-info");
				
				JSONObject jsonObj = JSON.parseObject(dataZaModuleInfo);
				JSONObject card = (JSONObject)jsonObj.get("card");
				JSONObject content = (JSONObject)card.get("content");
				String type = (String)content.get("type");
				String token = (String)content.get("token");
				
				zhiHuTopic.setType(type);
				zhiHuTopic.setToken(token);
				zhiHuPeopleTopic.setType(type);
				zhiHuPeopleTopic.setTopicId(token);
				
				//classname为“ContentItem”的div，这个div中包含着一个classname为“ContentItem-main”的div，所以直接获取后者会方便些
				WebElement contentItemMainElement = element.findElement(By.className("ContentItem-main"));
				
				WebElement imageDivElement = contentItemMainElement.findElement(By.className("ContentItem-image"));
				WebElement headDivElement = contentItemMainElement.findElement(By.className("ContentItem-head"));
				
				//imageDivElement是classname为“ContentItem-image”的div
				//获取图片的src，srcset，width，height
				WebElement imgElement = imageDivElement.findElement(By.tagName("img"));
				String imageSrc = imgElement.getAttribute("src");
				//srcset比src的图片大，大约是后者的2倍
				String imageSrcset = imgElement.getAttribute("srcset");
				
				String imageWidth = imgElement.getAttribute("width");
				String imageHeight = imgElement.getAttribute("height");
				
				zhiHuTopic.setImageSrc(imageSrc);
				zhiHuTopic.setImageSrcset(imageSrcset);
				zhiHuTopic.setImageWidth(Integer.parseInt(imageWidth));
				zhiHuTopic.setImageHeight(Integer.parseInt(imageHeight));
								
				//headDivElement是classname为“ContentItem-head”的div
				WebElement linkElement = headDivElement.findElement(By.className("ContentItem-title"));
				WebElement aElement = linkElement.findElement(By.tagName("a"));
				String topicName = aElement.getText();
				String topicUrl = aElement.getAttribute("href");
				
				zhiHuTopic.setRelativeUrl(topicUrl);
				zhiHuTopic.setTopicName(topicName);
				
				zhiHuPeopleTopic.setTopicName(topicName);
				
				WebElement answersInTopicElement = headDivElement.findElement(By.className("ContentItem-meta"));
				WebElement answersInTopicLinkElement = answersInTopicElement.findElement(By.tagName("a"));
				
				String answersInTopicUrl = answersInTopicLinkElement.getAttribute("href");
				zhiHuPeopleTopic.setAnswersInTopicUrl(answersInTopicUrl);
				
				//15个答案
				String linkText = answersInTopicLinkElement.getText();
				String regEx = "\\d+";
			    // 编译正则表达式
			    Pattern pattern = Pattern.compile(regEx);
			    // 忽略大小写的写法
			    // Pattern pat = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
			    
		    	Matcher matcher = pattern.matcher(linkText);
		    	if(matcher.find()) {
		    		String answersInTopicCountStr = matcher.group();
		    		int answersInTopicCount = Integer.parseInt(answersInTopicCountStr);
		    		//System.out.println(answersInTopicCount);
		    		zhiHuPeopleTopic.setAnswersInTopicCount(answersInTopicCount);
		    	}
		    	
		    	
		    	//TODO 先保存ZhiHuTopic
		    	ZhiHuTopic tp = zhiHuTopicMongoDaoUtil.findZhiHuTopicByToken(token);
		    	if(tp == null) {
		    		zhiHuTopic.setInsertTime(new Date());
		    		zhiHuTopic.setUpdateTime(new Date());
		    		zhiHuTopicMongoDaoUtil.insertZhiHuTopic(zhiHuTopic);
		    	}
		    	//TODO 再保存ZhiHuPeopleTopic
		    	ZhiHuPeopleTopic ptp = zhiHuPeopleTopicMongoDaoUtil.findZhiHuPeopleTopicByUserIdAndTopicId(userId, token);
		    	if(ptp == null) {
		    		zhiHuPeopleTopic.setInsertTime(new Date());
		    		zhiHuPeopleTopic.setUpdateTime(new Date());
		    		zhiHuPeopleTopicMongoDaoUtil.insertZhiHuPeopleTopic(zhiHuPeopleTopic);
		    	}else {
		    		String id = ptp.getId();
		    		zhiHuPeopleTopic.setId(id);
		    		zhiHuPeopleTopic.setUpdateTime(new Date());
		    		zhiHuPeopleTopicMongoDaoUtil.updateZhiHuPeopleTopic(zhiHuPeopleTopic);
		    	}
			}
		}
		
		
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
		System.out.println(currentPageNoStr);
		
//		Boolean hasNextPage = new WebDriverWait(driver, 10).until(new ExpectedCondition<Boolean>() {
//
//			@Override
//			public Boolean apply(WebDriver arg0) {
//				boolean hasNext = false;
//				WebElement nextPage = paginationElement.findElement(By.linkText("下一页"));
//				if(nextPage != null) {
//					hasNext = true;
//				}
//				return hasNext;
//			}
//			
//		});
//		
//		if(hasNextPage) {
//			//有下一页，点击下一页按钮
//			System.out.println("处理下一页");
//			paginationElement.findElement(By.linkText("下一页")).click();
//			followingTopics(driver, currentPageNo + 1);
//		}else {
//			//没有下一页了，不继续下面的处理
//		}
		
		
		if(currentPageNo < maxPageNo) {
			//有下一页，点击下一页按钮
			System.out.println("处理下一页");
			//By.cssSelector("[class='NumberBoard FollowshipCard-counts']")
			paginationElement.findElement(By.cssSelector("[class='Button PaginationButton PaginationButton-next Button--plain']")).click();
			followingTopics(driver, userId, currentPageNo + 1);
		}else {
			//没有下一页了，不继续下面的处理
		}
	}
	
	
	public static void followerPeoples(WebDriver driver, String userId, int currentPageNo) {
		
		//可能页面还没有加载出来
		try {
			Thread.sleep(3 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//Profile-following
		WebElement profileFollowingElement = driver.findElement(By.id("Profile-following"));
		
		//第一个div元素是表头
		//第二个div元素中是关注的话题列表
		WebElement followingTopicListBodyElement = profileFollowingElement.findElements(By.tagName("div")).get(1);
		
		List<WebElement> listItemElements = profileFollowingElement.findElements(By.className("List-item"));
		
		if(listItemElements != null && listItemElements.size() > 0) {
			for(WebElement itemElement:listItemElements) {
				ZhiHuPeople zhiHuPeople = new ZhiHuPeople();
				
				//data-za-module="TopicItem"
				WebElement contentItemElement = itemElement.findElement(By.className("ContentItem"));
				String dataZaModuleInfo = contentItemElement.getAttribute("data-za-module-info");
				
				//data-za-module-info='{"card":{"content":{"type":"User","member_hash_id":"121b9970625a84d7ad8d35bd4e97de71","follower_num":5676}}}'
				
				JSONObject jsonObj = JSON.parseObject(dataZaModuleInfo);
				JSONObject card = (JSONObject)jsonObj.get("card");
				JSONObject content = (JSONObject)card.get("content");
				String type = (String)content.get("type");
				String memberHashId = (String)content.get("member_hash_id");
				
				zhiHuPeople.setMemberHashId(memberHashId);
				
				//classname为“ContentItem”的div，这个div中包含着一个classname为“ContentItem-main”的div，所以直接获取后者会方便些
				WebElement contentItemMainElement = itemElement.findElement(By.className("ContentItem-main"));
				
				WebElement imageDivElement = contentItemMainElement.findElement(By.className("ContentItem-image"));
				WebElement headDivElement = contentItemMainElement.findElement(By.className("ContentItem-head"));
				
				//imageDivElement是classname为“ContentItem-image”的div
				//获取图片的src，srcset，width，height
				WebElement imgElement = imageDivElement.findElement(By.tagName("img"));
				String imageSrc = imgElement.getAttribute("src");
				//srcset比src的图片大，大约是后者的2倍
				String imageSrcset = imgElement.getAttribute("srcset");
				
				String imageWidth = imgElement.getAttribute("width");
				String imageHeight = imgElement.getAttribute("height");
				
				zhiHuPeople.setImageSrc(imageSrc);
				zhiHuPeople.setImageSrcset(imageSrcset);
				zhiHuPeople.setImageWidth(Integer.parseInt(imageWidth));
				zhiHuPeople.setImageHeight(Integer.parseInt(imageHeight));
				
				//headDivElement是classname为“ContentItem-head”的div
				WebElement contentItemTitleElement = headDivElement.findElement(By.className("ContentItem-title"));
				
				WebElement linkElement = contentItemTitleElement.findElement(By.className("Popover"));

				WebElement aElement = linkElement.findElement(By.tagName("a"));
				String userName = aElement.getText();
				//     /people/wang-xi-65-12
				String userUrl = aElement.getAttribute("href");
				int index = userUrl.lastIndexOf("/");
				String userId2 = userUrl.substring(index + 1);
				
				zhiHuPeople.setUserId(userId2);
				zhiHuPeople.setRelativeUrl(userUrl);
				zhiHuPeople.setUserName(userName);

				WebElement peopleItemMetaElement = headDivElement.findElement(By.className("ContentItem-meta"));
				WebElement peopleItemStatusElement = peopleItemMetaElement.findElement(By.className("ContentItem-status"));
				List<WebElement> spanElements = peopleItemStatusElement.findElements(By.tagName("span"));
				
				if(spanElements != null && spanElements.size() > 0) {
					int i = 0;
					for(WebElement spanEle:spanElements) {
						//15个答案
						String linkText = spanEle.getText();
						String regEx = "\\d+";
						// 编译正则表达式
						Pattern pattern = Pattern.compile(regEx);
						Matcher matcher = pattern.matcher(linkText);
						if(matcher.find()) {
							String answersInTopicCountStr = matcher.group();
							int count = Integer.parseInt(answersInTopicCountStr);
							//System.out.println(answersInTopicCount);
							if(i == 0) {
								//回答
								zhiHuPeople.setAnswerCount(count);
							}else if(i == 1) {
								//文章
								zhiHuPeople.setArticleCount(count);
							}else if(i == 2) {
								//关注者
								zhiHuPeople.setFollowingCount(count);
							}
						}
						i++;
					}
					
				}
		    	
		    	
		    	//TODO 先保存关注的这个用户的基本信息
				ZhiHuPeopleMongoDaoUtil zhiHuTopicMongoDaoUtil = new ZhiHuPeopleMongoDaoUtil();
				ZhiHuPeople people = zhiHuTopicMongoDaoUtil.findZhiHuPeopleByUserId(userId2);
		    	if(people == null) {
		    		zhiHuPeople.setInsertTime(new Date());
		    		zhiHuPeople.setUpdateTime(new Date());
		    		zhiHuTopicMongoDaoUtil.insertZhiHuPeople(zhiHuPeople);
		    	}else {
		    		String id = people.getId();
		    		zhiHuPeople.setId(id);
		    		zhiHuPeople.setUpdateTime(new Date());
		    		zhiHuTopicMongoDaoUtil.updateZhiHuPeople(zhiHuPeople);
		    	}
		    	//TODO 维护下当前视角知乎用户关注数据中关注的数组数据
		    	zhiHuTopicMongoDaoUtil.updateZhiHuPeopleAddToFollowerSet(userId, userId2);
		    	
			}
		}
		
		
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
			followerPeoples(driver, userId, currentPageNo + 1);
		}else {
			//没有下一页了，不继续下面的处理
		}
	}
	
	public static void followingPeoples(WebDriver driver, String userId, int currentPageNo) {
		
		//可能页面还没有加载出来
		try {
			Thread.sleep(3 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//Profile-following
		WebElement profileFollowingElement = driver.findElement(By.id("Profile-following"));
		
		//第一个div元素是表头
		//第二个div元素中是关注的话题列表
		WebElement followingTopicListBodyElement = profileFollowingElement.findElements(By.tagName("div")).get(1);
		
		List<WebElement> listItemElements = profileFollowingElement.findElements(By.className("List-item"));
		
		if(listItemElements != null && listItemElements.size() > 0) {
			for(WebElement itemElement:listItemElements) {
				ZhiHuPeople zhiHuPeople = new ZhiHuPeople();
				
				//data-za-module="TopicItem"
				WebElement contentItemElement = itemElement.findElement(By.className("ContentItem"));
				String dataZaModuleInfo = contentItemElement.getAttribute("data-za-module-info");
				
				//data-za-module-info='{"card":{"content":{"type":"User","member_hash_id":"121b9970625a84d7ad8d35bd4e97de71","follower_num":5676}}}'
				
				JSONObject jsonObj = JSON.parseObject(dataZaModuleInfo);
				JSONObject card = (JSONObject)jsonObj.get("card");
				JSONObject content = (JSONObject)card.get("content");
				String type = (String)content.get("type");
				String memberHashId = (String)content.get("member_hash_id");
				
				zhiHuPeople.setMemberHashId(memberHashId);
				
				//classname为“ContentItem”的div，这个div中包含着一个classname为“ContentItem-main”的div，所以直接获取后者会方便些
				WebElement contentItemMainElement = itemElement.findElement(By.className("ContentItem-main"));
				
				WebElement imageDivElement = contentItemMainElement.findElement(By.className("ContentItem-image"));
				WebElement headDivElement = contentItemMainElement.findElement(By.className("ContentItem-head"));
				
				//imageDivElement是classname为“ContentItem-image”的div
				//获取图片的src，srcset，width，height
				WebElement imgElement = imageDivElement.findElement(By.tagName("img"));
				String imageSrc = imgElement.getAttribute("src");
				//srcset比src的图片大，大约是后者的2倍
				String imageSrcset = imgElement.getAttribute("srcset");
				
				String imageWidth = imgElement.getAttribute("width");
				String imageHeight = imgElement.getAttribute("height");
				
				zhiHuPeople.setImageSrc(imageSrc);
				zhiHuPeople.setImageSrcset(imageSrcset);
				zhiHuPeople.setImageWidth(Integer.parseInt(imageWidth));
				zhiHuPeople.setImageHeight(Integer.parseInt(imageHeight));
				
				//headDivElement是classname为“ContentItem-head”的div
				WebElement contentItemTitleElement = headDivElement.findElement(By.className("ContentItem-title"));
				
				WebElement linkElement = contentItemTitleElement.findElement(By.className("Popover"));

				WebElement aElement = linkElement.findElement(By.tagName("a"));
				String userName = aElement.getText();
				//     /people/wang-xi-65-12
				String userUrl = aElement.getAttribute("href");
				int index = userUrl.lastIndexOf("/");
				String userId2 = userUrl.substring(index + 1);
				
				zhiHuPeople.setUserId(userId2);
				zhiHuPeople.setRelativeUrl(userUrl);
				zhiHuPeople.setUserName(userName);

				WebElement peopleItemMetaElement = headDivElement.findElement(By.className("ContentItem-meta"));
				WebElement peopleItemStatusElement = peopleItemMetaElement.findElement(By.className("ContentItem-status"));
				List<WebElement> spanElements = peopleItemStatusElement.findElements(By.tagName("span"));
				
				if(spanElements != null && spanElements.size() > 0) {
					int i = 0;
					for(WebElement spanEle:spanElements) {
						//15个答案
						String linkText = spanEle.getText();
						String regEx = "\\d+";
						// 编译正则表达式
						Pattern pattern = Pattern.compile(regEx);
						Matcher matcher = pattern.matcher(linkText);
						if(matcher.find()) {
							String answersInTopicCountStr = matcher.group();
							int count = Integer.parseInt(answersInTopicCountStr);
							//System.out.println(answersInTopicCount);
							if(i == 0) {
								//回答
								zhiHuPeople.setAnswerCount(count);
							}else if(i == 1) {
								//文章
								zhiHuPeople.setArticleCount(count);
							}else if(i == 2) {
								//关注者
								zhiHuPeople.setFollowingCount(count);
							}
						}
						i++;
					}
					
				}
		    	
		    	
		    	//TODO 先保存关注的这个用户的基本信息
				ZhiHuPeopleMongoDaoUtil zhiHuTopicMongoDaoUtil = new ZhiHuPeopleMongoDaoUtil();
				ZhiHuPeople people = zhiHuTopicMongoDaoUtil.findZhiHuPeopleByUserId(userId2);
		    	if(people == null) {
		    		zhiHuPeople.setInsertTime(new Date());
		    		zhiHuPeople.setUpdateTime(new Date());
		    		zhiHuTopicMongoDaoUtil.insertZhiHuPeople(zhiHuPeople);
		    	}else {
		    		String id = people.getId();
		    		zhiHuPeople.setId(id);
		    		zhiHuPeople.setUpdateTime(new Date());
		    		zhiHuTopicMongoDaoUtil.updateZhiHuPeople(zhiHuPeople);
		    	}
		    	//TODO 维护下当前视角知乎用户关注数据中关注的数组数据
		    	zhiHuTopicMongoDaoUtil.updateZhiHuPeopleAddToFollowingSet(userId, userId2);
		    	
			}
		}
		
		
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
			followingPeoples(driver, userId, currentPageNo + 1);
		}else {
			//没有下一页了，不继续下面的处理
		}
	}
	
	public static void followingColumns(WebDriver driver, String userId, int currentPageNo) {
		
		//可能页面还没有加载出来
		try {
			Thread.sleep(3 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//Profile-following
		WebElement profileFollowingElement = driver.findElement(By.id("Profile-following"));
		
		//第一个div元素是表头
		//第二个div元素中是关注的话题列表
		WebElement followingTopicListBodyElement = profileFollowingElement.findElements(By.tagName("div")).get(1);
		
		List<WebElement> listItemElements = profileFollowingElement.findElements(By.className("List-item"));
		
		if(listItemElements != null && listItemElements.size() > 0) {
			ZhiHuColumnMongoDaoUtil zhiHuColumnMongoDaoUtil = new ZhiHuColumnMongoDaoUtil();
			ZhiHuPeopleColumnMongoDaoUtil zhiHuPeopleColumnMongoDaoUtil = new ZhiHuPeopleColumnMongoDaoUtil();
	    	
			for(WebElement itemElement:listItemElements) {
				ZhiHuColumn zhiHuColumn = new ZhiHuColumn();
				
				//data-za-module="TopicItem"
				WebElement contentItemElement = itemElement.findElement(By.className("ContentItem"));
				String dataZaModuleInfo = contentItemElement.getAttribute("data-za-module-info");
				
				//{"card":{"content":{"type":"Column","token":"ityouknow","item_num":12,"follower_num":45,"publish_timestamp":1505702348000,"author_member_hash_id":"c472bd14369be7a8841b8ab5762ce22e"}}}
				
				JSONObject jsonObj = JSON.parseObject(dataZaModuleInfo);
				JSONObject card = (JSONObject)jsonObj.get("card");
				JSONObject content = (JSONObject)card.get("content");
				String type = (String)content.get("type");
				String token = (String)content.get("token");
				String authorMemberHashId = (String)content.get("author_member_hash_id");
				
				zhiHuColumn.setAuthorMemberHashId(authorMemberHashId);
				
				//classname为“ContentItem”的div，这个div中包含着一个classname为“ContentItem-main”的div，所以直接获取后者会方便些
				WebElement contentItemMainElement = itemElement.findElement(By.className("ContentItem-main"));
				
				WebElement imageDivElement = contentItemMainElement.findElement(By.className("ContentItem-image"));
				WebElement headDivElement = contentItemMainElement.findElement(By.className("ContentItem-head"));
				
				//imageDivElement是classname为“ContentItem-image”的div
				//获取图片的src，srcset，width，height
				WebElement imgElement = imageDivElement.findElement(By.tagName("img"));
				String imageSrc = imgElement.getAttribute("src");
				//srcset比src的图片大，大约是后者的2倍
				String imageSrcset = imgElement.getAttribute("srcset");
				
				String imageWidth = imgElement.getAttribute("width");
				String imageHeight = imgElement.getAttribute("height");
				
				zhiHuColumn.setImageSrc(imageSrc);
				zhiHuColumn.setImageSrcset(imageSrcset);
				zhiHuColumn.setImageWidth(Integer.parseInt(imageWidth));
				zhiHuColumn.setImageHeight(Integer.parseInt(imageHeight));
				
				//headDivElement是classname为“ContentItem-head”的div
				WebElement contentItemTitleElement = headDivElement.findElement(By.className("ContentItem-title"));
				
				WebElement linkElement = contentItemTitleElement.findElement(By.tagName("a"));

				WebElement popoverElement = linkElement.findElement(By.className("Popover"));
				String columnName = popoverElement.getText();
				//     /people/wang-xi-65-12
				String columnUrl = linkElement.getAttribute("href");
				int index = columnUrl.lastIndexOf("/");
				String columnId = columnUrl.substring(index + 1);
				
				zhiHuColumn.setColumnId(columnId);
				zhiHuColumn.setRelativeUrl(columnUrl);
				zhiHuColumn.setColumnName(columnName);

				WebElement peopleItemMetaElement = headDivElement.findElement(By.className("ContentItem-meta"));
				WebElement peopleItemStatusElement = peopleItemMetaElement.findElement(By.className("ContentItem-status"));
				List<WebElement> spanElements = peopleItemStatusElement.findElements(By.tagName("span"));
				
				if(spanElements != null && spanElements.size() > 0) {
					int i = 0;
					for(WebElement spanEle:spanElements) {
						//15个答案
						String linkText = spanEle.getText();
						String regEx = "\\d+";
						// 编译正则表达式
						Pattern pattern = Pattern.compile(regEx);
						Matcher matcher = pattern.matcher(linkText);
						if(matcher.find()) {
							String answersInTopicCountStr = matcher.group();
							int count = Integer.parseInt(answersInTopicCountStr);
							//System.out.println(answersInTopicCount);
							if(i == 0) {
								//文章数
								zhiHuColumn.setArticleCount(count);
							}else if(i == 1) {
								//关注人数
								zhiHuColumn.setFollowersCount(count);;
							}
						}
						i++;
					}
					
				}
		    	
		    	
		    	//TODO 先保存关注的这个专栏的基本信息
				ZhiHuColumn column = zhiHuColumnMongoDaoUtil.findZhiHuColumnByColumnId(columnId);
		    	if(column == null) {
		    		zhiHuColumn.setInsertTime(new Date());
		    		zhiHuColumn.setUpdateTime(new Date());
		    		zhiHuColumnMongoDaoUtil.insertZhiHuColumn(zhiHuColumn);
		    	}else {
		    		String id = column.getId();
		    		zhiHuColumn.setId(id);
		    		zhiHuColumn.setUpdateTime(new Date());
		    		zhiHuColumnMongoDaoUtil.updateZhiHuColumn(zhiHuColumn);
		    	}
		    	
		    	ZhiHuPeopleColumn zhiHuPeopleColumn = zhiHuPeopleColumnMongoDaoUtil.findZhiHuPeopleColumnByUserIdAndColumnId(userId, columnId);
		    	if(zhiHuPeopleColumn == null) {
		    		zhiHuPeopleColumn = new ZhiHuPeopleColumn();
		    		zhiHuPeopleColumn.setInsertTime(new Date());
		    		zhiHuPeopleColumn.setUpdateTime(new Date());
		    		zhiHuPeopleColumn.setUserId(userId);
		    		zhiHuPeopleColumn.setColumnId(columnId);
		    		zhiHuPeopleColumn.setColumnName(columnName);
		    		zhiHuPeopleColumn.setType("column");
		    		
		    		zhiHuPeopleColumnMongoDaoUtil.insertZhiHuPeopleColumn(zhiHuPeopleColumn);
		    	}
		    	
		    	
			}
		}
		
		
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
			followingColumns(driver, userId, currentPageNo + 1);
		}else {
			//没有下一页了，不继续下面的处理
		}
	}
	
	public static void followingQuestions(WebDriver driver, String userId, int currentPageNo, String enterNextPageType) {
		logger.info("当前页第" + currentPageNo);
		
		//enterNextPageType 1表示在页面上点击下一页进入下一页,2表示通过url中修改url进入下一页
		if(currentPageNo > 1 && enterNextPageType != null && "2".equals(enterNextPageType.trim())) {
			driver.get("https://www.zhihu.com/people/" + userId + "/following/questions?page=" + currentPageNo);
		}
		
		//可能页面还没有加载出来
		try {
			Thread.sleep(3 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//Profile-following
		WebElement profileFollowingElement = driver.findElement(By.id("Profile-following"));
		
		List<WebElement> listItemElements = profileFollowingElement.findElements(By.className("List-item"));
		
		if(listItemElements != null && listItemElements.size() > 0) {
			ZhiHuQuestionMongoDaoUtil zhiHuQuestionMongoDaoUtil = new ZhiHuQuestionMongoDaoUtil();
	    	ZhiHuPeopleQuestionMongoDaoUtil zhiHuPeopleQuestionMongoDaoUtil = new ZhiHuPeopleQuestionMongoDaoUtil();
	    	
			for(WebElement itemElement:listItemElements) {
				ZhiHuQuestion zhiHuQuestion = new ZhiHuQuestion();
				ZhiHuPeopleQuestion zhiHuPeopleQuestion = new ZhiHuPeopleQuestion();
				
				//data-za-module="TopicItem"
				WebElement contentItemElement = itemElement.findElement(By.className("ContentItem"));
				
				//headDivElement是classname为“ContentItem-head”的div
				WebElement contentItemTitleElement = contentItemElement.findElement(By.className("ContentItem-title"));
				
				WebElement aElement = contentItemTitleElement.findElement(By.tagName("a"));
				String questionName = aElement.getText();
				//     /people/wang-xi-65-12
				String questionUrl = aElement.getAttribute("href");
				int index = questionUrl.lastIndexOf("/");
				String questionId = questionUrl.substring(index + 1);
				
				zhiHuPeopleQuestion.setUserId(userId);
				zhiHuPeopleQuestion.setQuestionId(questionId);
				zhiHuPeopleQuestion.setQuestionName(questionName);
				zhiHuPeopleQuestion.setType("question");
				
				zhiHuQuestion.setQuestionId(questionId);
				zhiHuQuestion.setQuestionName(questionName);
				zhiHuQuestion.setQuestionRelativeUrl(questionUrl);

				WebElement contentItemStatusElement = contentItemElement.findElement(By.className("ContentItem-status"));
				List<WebElement> spanElements = contentItemStatusElement.findElements(By.tagName("span"));
				
				if(spanElements != null && spanElements.size() > 0) {
					for(int i = 0;i<spanElements.size();i++) {
						WebElement spanEle = spanElements.get(i);
						
						//15个答案
						String linkText = spanEle.getText();
						
						if(i == 0) {
							zhiHuQuestion.setQuestionDay(linkText);
						}else{
							String regEx = "\\d+";
							// 编译正则表达式
							Pattern pattern = Pattern.compile(regEx);
							Matcher matcher = pattern.matcher(linkText);
							if(matcher.find()) {
								String str = matcher.group();
								int count = Integer.parseInt(str);
								//System.out.println(answersInTopicCount);
								if(i == 1) {
									//回答
									zhiHuQuestion.setAnswerCount(count);
								}else if(i == 2) {
									//关注者
									zhiHuQuestion.setFollowerCount(count);
								}
							}
						}
					}
					
				}
		    	
		    	ZhiHuQuestion question = zhiHuQuestionMongoDaoUtil.findZhiHuQuestionByQuestionId(questionId);
		    	if(question == null) {
		    		zhiHuQuestion.setInsertTime(new Date());
		    		zhiHuQuestion.setUpdateTime(new Date());
		    		zhiHuQuestionMongoDaoUtil.insertZhiHuQuestion(zhiHuQuestion);
		    	}else {
		    		String id = question.getId();
		    		zhiHuQuestion.setId(id);
		    		zhiHuQuestion.setUpdateTime(new Date());
		    		zhiHuQuestionMongoDaoUtil.updateZhiHuQuestion(zhiHuQuestion);
		    	}
		    	
		    	ZhiHuPeopleQuestion peopleQuestion = zhiHuPeopleQuestionMongoDaoUtil.findZhiHuPeopleQuestionByUserIdAndQuestionId(userId, questionId);
		    	if(peopleQuestion == null) {
		    		zhiHuPeopleQuestion.setInsertTime(new Date());
		    		zhiHuPeopleQuestion.setUpdateTime(new Date());
		    		zhiHuPeopleQuestionMongoDaoUtil.insertZhiHuPeopleQuestion(zhiHuPeopleQuestion);
		    	}
		    	
			}
		}
		
		
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
			followingQuestions(driver, userId, currentPageNo + 1, "1");
		}else {
			//没有下一页了，不继续下面的处理
		}
	}
}
