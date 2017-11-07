package com.yan.zhihu.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.yan.zhihu.model.ZhiHuPeople;
import com.yan.zhihu.util.SchameDocumentUtil;

public class ZhiHuPeopleMongoDaoUtil {

	public static void main(String[] args) throws Exception {
		ZhiHuPeopleMongoDaoUtil zhiHuPeopleMongoDaoUtil = new ZhiHuPeopleMongoDaoUtil();
	}
	
	public String insertZhiHuPeople(ZhiHuPeople zhiHuPeople){

		//To connect to a single MongoDB instance:
	    //You can explicitly specify the hostname and the port:
		MongoClient mongoClient = new MongoClient( "localhost" , 27017 );

		//Access a Database
		MongoDatabase database = mongoClient.getDatabase("zhihu");
		
		//Access a Collection
		MongoCollection<Document> collection = database.getCollection("ZhiHuPeople");
		
		//Create a Document
		Document doc = SchameDocumentUtil.schameToDocument(zhiHuPeople, ZhiHuPeople.class);

		//Insert a Document
		collection.insertOne(doc);
		
		//System.out.println("id:" + doc.get("_id"));
		String id = null;
		if(doc.get("_id") != null){
			id = doc.get("_id").toString();
		}
		return id;
	}
		
	public ZhiHuPeople findZhiHuPeopleById(String id) {
		ZhiHuPeople zhiHuPeople = null;
		if(id!= null && !"".equals(id.trim())) {
			//To connect to a single MongoDB instance:
			//You can explicitly specify the hostname and the port:
			MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
			
			//Access a Database
			MongoDatabase database = mongoClient.getDatabase("zhihu");
			
			//Access a Collection
			MongoCollection<Document> collection = database.getCollection("ZhiHuPeople");
			
			List<Document> docs = collection.find(Filters.eq("_id", new ObjectId(id))).into(new ArrayList<Document>());
			if(docs != null && docs.size() > 0) {
				zhiHuPeople = (ZhiHuPeople)SchameDocumentUtil.documentToSchame(docs.get(0), ZhiHuPeople.class);
			}
		}
		
		return zhiHuPeople;
	}
	
	public ZhiHuPeople findZhiHuPeopleByUserId(String userId) {
		ZhiHuPeople zhiHuPeople = null;
		if(userId!= null && !"".equals(userId.trim())) {
			//To connect to a single MongoDB instance:
			//You can explicitly specify the hostname and the port:
			MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
			
			//Access a Database
			MongoDatabase database = mongoClient.getDatabase("zhihu");
			
			//Access a Collection
			MongoCollection<Document> collection = database.getCollection("ZhiHuPeople");
			
			List<Document> docs = collection.find(Filters.eq("userId", userId)).into(new ArrayList<Document>());
			if(docs != null && docs.size() > 0) {
				zhiHuPeople = (ZhiHuPeople)SchameDocumentUtil.documentToSchame(docs.get(0), ZhiHuPeople.class);
			}
		}
		
		return zhiHuPeople;
	}
	
	public void updateZhiHuPeople(ZhiHuPeople zhiHuPeople){
		//To connect to a single MongoDB instance:
	    //You can explicitly specify the hostname and the port:
		MongoClient mongoClient = new MongoClient( "localhost" , 27017 );

		//Access a Database
		MongoDatabase database = mongoClient.getDatabase("zhihu");
		
		//Access a Collection
		MongoCollection<Document> collection = database.getCollection("ZhiHuPeople");
		
		
		//Create a Document
		 Document doc = SchameDocumentUtil.schameToDocument(zhiHuPeople, ZhiHuPeople.class);
		 
		 //Update a Document
		 collection.updateOne(Filters.eq("_id", doc.get("_id")), new Document("$set", doc));
		 
	}
	
	
	public void updateZhiHuPeopleAddToFollowingSet(String userId, String followingId){
		//To connect to a single MongoDB instance:
	    //You can explicitly specify the hostname and the port:
		MongoClient mongoClient = new MongoClient( "localhost" , 27017 );

		//Access a Database
		MongoDatabase database = mongoClient.getDatabase("zhihu");
		
		//Access a Collection
		MongoCollection<Document> collection = database.getCollection("ZhiHuPeople");
		
		
		//Create a Document
		 Document doc = new Document();
		 doc.append("followingIds", followingId);
		 
		 //Update a Document
		 collection.updateOne(Filters.eq("userId", userId), new Document("$addToSet", doc));
		 
	}
	
	public void updateZhiHuPeopleAddToFollowerSet(String userId, String followerId){
		//To connect to a single MongoDB instance:
	    //You can explicitly specify the hostname and the port:
		MongoClient mongoClient = new MongoClient( "localhost" , 27017 );

		//Access a Database
		MongoDatabase database = mongoClient.getDatabase("zhihu");
		
		//Access a Collection
		MongoCollection<Document> collection = database.getCollection("ZhiHuPeople");
		
		
		//Create a Document
		 Document doc = new Document();
		 doc.append("followerIds", followerId);
		 
		 //Update a Document
		 collection.updateOne(Filters.eq("userId", userId), new Document("$addToSet", doc));
		 
	}
}
