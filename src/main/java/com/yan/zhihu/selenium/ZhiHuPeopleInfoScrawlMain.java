package com.yan.zhihu.selenium;

import java.util.Date;
import java.util.List;
import java.util.Properties;
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
import com.yan.zhihu.dao.ZhiHuColumnMongoDaoUtil;
import com.yan.zhihu.dao.ZhiHuPeopleMongoDaoUtil;
import com.yan.zhihu.dao.ZhiHuPeopleTopicMongoDaoUtil;
import com.yan.zhihu.dao.ZhiHuTopicMongoDaoUtil;
import com.yan.zhihu.model.ZhiHuColumn;
import com.yan.zhihu.model.ZhiHuPeople;
import com.yan.zhihu.model.ZhiHuPeopleTopic;
import com.yan.zhihu.model.ZhiHuTopic;

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
		personalMainPage(driver, userId, "activities");
		
		//关注了哪些话题
		personalMainPage(driver, userId, "followingTopics");
		
		personalMainPage(driver, userId, "followingColumns");
		
		//关注了哪些人
		personalMainPage(driver, userId, "followings");
		//那些人关注了我
		personalMainPage(driver, userId, "followers");
		
		
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
			
			String getScrollHeight = "document.documentElement.scrollTop";
			
			long currentHeight = 500L;
			
			//最近的用户活动还没有结束
			boolean currentActivitiesNotFinish = true;
			//滚动滚动条的次数
			int i = 0;
			
			while( currentActivitiesNotFinish && i < 100){
//				long currentHeight = (Long)jse.executeScript(getScrollHeight);
				logger.info("滚动条高度 : " + currentHeight);
				
				//每次滚动50%
				currentHeight = currentHeight + currentHeight/2;
				String setScrollHeight = "document.documentElement.scrollTop=" + currentHeight;  
				jse.executeScript(setScrollHeight);
				logger.info("向下滚动滚动条第 " + (i+1) + "次");
				i++;
				
				//TODO 获取用户的活动
				
				//用户的活动类型：收藏了文章，赞同了回答，收藏了回答，关注了问题，关注了专栏，关注了收藏夹，赞了文章，回答了问题，关注了话题，发布了想法，
				//用户的活动时间	1分钟前，1小时前，1天前，1个月前
				
				
				//判断下当前用户活动的活动时间据今天时间，只读取最近几天的活动
				
				//判断用户的活动是否存在
				
				//如果用户的活动已经阅读过了，那么我们
				
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
		    	ZhiHuTopicMongoDaoUtil zhiHuTopicMongoDaoUtil = new ZhiHuTopicMongoDaoUtil();
		    	ZhiHuTopic tp = zhiHuTopicMongoDaoUtil.findZhiHuTopicByToken(token);
		    	if(tp == null) {
		    		zhiHuTopic.setInsertTime(new Date());
		    		zhiHuTopic.setUpdateTime(new Date());
		    		zhiHuTopicMongoDaoUtil.insertZhiHuTopic(zhiHuTopic);
		    	}
		    	//TODO 再保存ZhiHuPeopleTopic
		    	ZhiHuPeopleTopicMongoDaoUtil zhiHuPeopleTopicMongoDaoUtil = new ZhiHuPeopleTopicMongoDaoUtil();
		    	ZhiHuPeopleTopic ptp = zhiHuPeopleTopicMongoDaoUtil.findZhiHuPeopleTopicByUserIdAndToken(userId, token);
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
				ZhiHuColumnMongoDaoUtil zhiHuColumnMongoDaoUtil = new ZhiHuColumnMongoDaoUtil();
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
}
