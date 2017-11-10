package com.yan.zhihu.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.bson.BsonArray;
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
					//这个是用来处理带泛型的的类型
					Type type = method.getGenericReturnType();
					
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
							if(returnType == List.class) {
//								if(type instanceof ParameterizedType){  
//					                ParameterizedType parameterizedType = (ParameterizedType) type;  
//					                //获取参数的类型  
//					                //System.out.println(parameterizedType.getRawType());  
//					                //获取参数的泛型列表  
//					                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();  
//					                Class type2 = (Class)actualTypeArguments[0];
//					                System.out.println(type2);  
//					            }
								List list = (List)value;
								BsonArray bsonArray = null;
								if(list != null) {
									bsonArray = new BsonArray(list);
								}
								doc.append(fieldName, bsonArray);
							}else {
								if(value.getClass() == byte.class || value.getClass() == Byte.class
										|| value.getClass() == short.class || value.getClass() == Short.class
										|| value.getClass() == int.class || value.getClass() == Integer.class
										|| value.getClass() == long.class || value.getClass() == Long.class
										
										|| value.getClass() == float.class || value.getClass() == Float.class
										|| value.getClass() == double.class || value.getClass() == Double.class
										
										|| value.getClass() == char.class || value.getClass() == Character.class
										|| value.getClass() == String.class
										
										|| value.getClass() == boolean.class || value.getClass() == Boolean.class
										
										|| value.getClass() == Date.class
										){
									doc.append(fieldName, value);
								}else{
									Class subClazz = value.getClass();
									Document subDoc = schameToDocument(value, subClazz);
									doc.append(fieldName, subDoc);
								}
								
							}
							
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
								if(value != null){
									value = value.toString();
								}
							}else{
								value = document.get(fieldName);
								if(value != null){
									if(returnType == List.class) {
										if(value instanceof List || value instanceof ArrayList) {
											//如果document中存的是ArrayList类型，那么不能使用BsonArray来转
											
										}else if (value instanceof BsonArray) {
											BsonArray bsonArray = (BsonArray)value;
											if(bsonArray != null) {
												value = Arrays.asList(bsonArray.toArray());
											}
										}
									}
								}
							}
							
							if(value instanceof Document){
								value = documentToSchame((Document)value, returnType);
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
