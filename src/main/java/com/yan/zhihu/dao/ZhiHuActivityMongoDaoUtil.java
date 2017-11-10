package com.yan.zhihu.dao;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.yan.zhihu.model.ZhiHuActivity;
import com.yan.zhihu.util.SchameDocumentUtil;

public class ZhiHuActivityMongoDaoUtil {
	
	public String insertZhiHuActivity(ZhiHuActivity zhiHuActivity){

		//To connect to a single MongoDB instance:
	    //You can explicitly specify the hostname and the port:
		MongoClient mongoClient = new MongoClient( "localhost" , 27017 );

		//Access a Database
		MongoDatabase database = mongoClient.getDatabase("zhihu");
		
		//Access a Collection
		MongoCollection<Document> collection = database.getCollection("ZhiHuActivity");
		
		//Create a Document
		Document doc = SchameDocumentUtil.schameToDocument(zhiHuActivity, ZhiHuActivity.class);

		//Insert a Document
		collection.insertOne(doc);
		
		//System.out.println("id:" + doc.get("_id"));
		String id = null;
		if(doc.get("_id") != null){
			id = doc.get("_id").toString();
		}
		return id;
	}
		
	public ZhiHuActivity findZhiHuActivityById(String id) {
		ZhiHuActivity zhiHuActivity = null;
		if(id!= null && !"".equals(id.trim())) {
			//To connect to a single MongoDB instance:
			//You can explicitly specify the hostname and the port:
			MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
			
			//Access a Database
			MongoDatabase database = mongoClient.getDatabase("zhihu");
			
			//Access a Collection
			MongoCollection<Document> collection = database.getCollection("ZhiHuActivity");
			
			List<Document> docs = collection.find(Filters.eq("_id", new ObjectId(id))).into(new ArrayList<Document>());
			if(docs != null && docs.size() > 0) {
				zhiHuActivity = (ZhiHuActivity)SchameDocumentUtil.documentToSchame(docs.get(0), ZhiHuActivity.class);
			}
		}
		
		return zhiHuActivity;
	}
	
	public ZhiHuActivity findZhiHuActivityByActivityTypeFullNameAndItemName(String activityTypeFullName, String itemName) {
		ZhiHuActivity zhiHuActivity = null;
		if(activityTypeFullName!= null && !"".equals(activityTypeFullName.trim())
				&& itemName != null && !"".equals(itemName.trim())) {
			//To connect to a single MongoDB instance:
			//You can explicitly specify the hostname and the port:
			MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
			
			//Access a Database
			MongoDatabase database = mongoClient.getDatabase("zhihu");
			
			//Access a Collection
			MongoCollection<Document> collection = database.getCollection("ZhiHuActivity");
			List<Bson> bsons = new ArrayList<Bson>(0);
			bsons.add(Filters.eq("activityTypeFullName", activityTypeFullName));
			bsons.add(Filters.eq("itemName", itemName));
			
			List<Document> docs = collection.find(Filters.and(bsons)).into(new ArrayList<Document>());
			if(docs != null && docs.size() > 0) {
				zhiHuActivity = (ZhiHuActivity)SchameDocumentUtil.documentToSchame(docs.get(0), ZhiHuActivity.class);
			}
		}
		
		return zhiHuActivity;
	}
	
	public void updateZhiHuActivity(ZhiHuActivity zhiHuActivity){
		//To connect to a single MongoDB instance:
	    //You can explicitly specify the hostname and the port:
		MongoClient mongoClient = new MongoClient( "localhost" , 27017 );

		//Access a Database
		MongoDatabase database = mongoClient.getDatabase("zhihu");
		
		//Access a Collection
		MongoCollection<Document> collection = database.getCollection("ZhiHuActivity");
		
		
		//Create a Document
		 Document doc = SchameDocumentUtil.schameToDocument(zhiHuActivity, ZhiHuActivity.class);
		 
		 //Update a Document
		 collection.updateOne(Filters.eq("_id", doc.get("_id")), new Document("$set", doc));
		 
	}
	
}
