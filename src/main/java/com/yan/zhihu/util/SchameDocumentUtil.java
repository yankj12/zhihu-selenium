package com.yan.zhihu.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bson.Document;
import org.bson.types.ObjectId;

public class SchameDocumentUtil {

	public static Document schameToDocument(Object object, Class clazz){
		Document doc = new Document();
		
		//Class clazz = ZhiHuPeople.class;
		Method[] methods = clazz.getDeclaredMethods();
		if(methods != null && methods.length > 0){
			for(Method method : methods){
				String methodName = method.getName();
				//遍历get方法
				if(methodName.startsWith("get")){
					String fieldName = methodName.substring(3, 4).toLowerCase() + methodName.substring(4);
					Class returnType = method.getReturnType();
					Object value = null;
					try {
						value = method.invoke(object, new Object[0]);
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						e.printStackTrace();
					}
					
					if(value != null){
						if("id".equals(fieldName)){
							if(!"".equals(value.toString().trim())){
								doc.append("_id", new ObjectId(value.toString()));
							}
						}else{
							doc.append(fieldName, value);
						}
					}
					
				}
				
			}
		}
		
		return doc;
	}
	
	public static Object documentToSchame(Document document, Class clazz){
		Object object = null;
		//ZhiHuPeople zhiHuPeople = null; 
		if(document != null){
			//zhiHuPeople = new ZhiHuPeople();
			//Class clazz = ZhiHuPeople.class;
			try {
				object = clazz.newInstance();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Method[] methods = clazz.getDeclaredMethods();
			if(methods != null && methods.length > 0){
				for(Method method : methods){
					String methodName = method.getName();
					//遍历get方法
					if(methodName.startsWith("get")){
						String fieldName = methodName.substring(3, 4).toLowerCase() + methodName.substring(4);
						Class returnType = method.getReturnType();
						
						String setterMethodName = "set" + methodName.substring(3);
						
						try {
							Method setterMethod = clazz.getMethod(setterMethodName, returnType);
							
							Object value = null;
							
							if("id".equals(fieldName)){
								value = document.get("_id");
								value = value.toString();
							}else{
								value = document.get(fieldName);
							}
							
							setterMethod.invoke(object, value);
							
						} catch (Exception e) {
							e.printStackTrace();
						}
						
					}
					
				}
			}
		}
		
		return object;
	}
}
