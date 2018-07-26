package com.yan.zhihu.mysql.utils;

public class ZhiHuUtil {

	/**
	 * 从url中截取userId
	 * @param url
	 * @return
	 */
	public static String subUserIdFormUrl(String url) {
		String userId = null;
		int startOfPeople = 0;
		startOfPeople = url.indexOf("/people/");
		userId = url.substring(startOfPeople + "/people/".length());
		
		// 如果url中people之后还存在/，那么我们还需要将userId中/之后的内容去掉
		if(userId.contains("/")) {
			int firstIndex = userId.indexOf("/");
			userId = userId.substring(0, firstIndex);
		}
		
		return userId;
	}
}
