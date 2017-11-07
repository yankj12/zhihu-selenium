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
import com.yan.zhihu.model.ZhiHuColumn;
import com.yan.zhihu.util.SchameDocumentUtil;

public class ZhiHuColumnMongoDaoUtil {

	public static void main(String[] args) throws Exception {
		ZhiHuColumnMongoDaoUtil zhiHuColumnMongoDaoUtil = new ZhiHuColumnMongoDaoUtil();
	}
	
	public String insertZhiHuColumn(ZhiHuColumn zhiHuColumn){

		//To connect to a single MongoDB instance:
	    //You can explicitly specify the hostname and the port:
		MongoClient mongoClient = new MongoClient( "localhost" , 27017 );

		//Access a Database
		MongoDatabase database = mongoClient.getDatabase("zhihu");
		
		//Access a Collection
		MongoCollection<Document> collection = database.getCollection("ZhiHuColumn");
		
		//Create a Document
		Document doc = SchameDocumentUtil.schameToDocument(zhiHuColumn, ZhiHuColumn.class);

		//Insert a Document
		collection.insertOne(doc);
		
		//System.out.println("id:" + doc.get("_id"));
		String id = null;
		if(doc.get("_id") != null){
			id = doc.get("_id").toString();
		}
		return id;
	}

	public ZhiHuColumn findZhiHuColumnById(String id) {
		ZhiHuColumn zhiHuColumn = null;
		if(id!= null && !"".equals(id.trim())) {
			//To connect to a single MongoDB instance:
			//You can explicitly specify the hostname and the port:
			MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
			
			//Access a Database
			MongoDatabase database = mongoClient.getDatabase("zhihu");
			
			//Access a Collection
			MongoCollection<Document> collection = database.getCollection("ZhiHuColumn");
			
			List<Document> docs = collection.find(Filters.eq("_id", new ObjectId(id))).into(new ArrayList<Document>());
			if(docs != null && docs.size() > 0) {
				zhiHuColumn = (ZhiHuColumn)SchameDocumentUtil.documentToSchame(docs.get(0), ZhiHuColumn.class);
			}
		}
		
		return zhiHuColumn;
	}
	
	public ZhiHuColumn findZhiHuColumnByColumnId(String columnId) {
		ZhiHuColumn zhiHuColumn = null;
		if(columnId!= null && !"".equals(columnId.trim())) {
			//To connect to a single MongoDB instance:
			//You can explicitly specify the hostname and the port:
			MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
			
			//Access a Database
			MongoDatabase database = mongoClient.getDatabase("zhihu");
			
			//Access a Collection
			MongoCollection<Document> collection = database.getCollection("ZhiHuColumn");
			
			List<Document> docs = collection.find(Filters.eq("columnId", columnId)).into(new ArrayList<Document>());
			if(docs != null && docs.size() > 0) {
				zhiHuColumn = (ZhiHuColumn)SchameDocumentUtil.documentToSchame(docs.get(0), ZhiHuColumn.class);
			}
		}
		
		return zhiHuColumn;
	}
	
	public void updateZhiHuColumn(ZhiHuColumn zhiHuColumn){
		//To connect to a single MongoDB instance:
	    //You can explicitly specify the hostname and the port:
		MongoClient mongoClient = new MongoClient( "localhost" , 27017 );

		//Access a Database
		MongoDatabase database = mongoClient.getDatabase("zhihu");
		
		//Access a Collection
		MongoCollection<Document> collection = database.getCollection("ZhiHuColumn");
		
		
		//Create a Document
		 Document doc = SchameDocumentUtil.schameToDocument(zhiHuColumn, ZhiHuColumn.class);
		 
		 //Update a Document
		 collection.updateOne(Filters.eq("_id", doc.get("_id")), new Document("$set", doc));
		 
	}
	
	public void updateZhiHuColumnAddToFollowerSet(String columnId, String followerId){
		//To connect to a single MongoDB instance:
	    //You can explicitly specify the hostname and the port:
		MongoClient mongoClient = new MongoClient( "localhost" , 27017 );

		//Access a Database
		MongoDatabase database = mongoClient.getDatabase("zhihu");
		
		//Access a Collection
		MongoCollection<Document> collection = database.getCollection("ZhiHuColumn");
		
		
		//Create a Document
		 Document doc = new Document();
		 doc.append("followerIds", followerId);
		 
		 //Update a Document
		 collection.updateOne(Filters.eq("columnId", columnId), new Document("$addToSet", doc));
		 
	}
	
	public void updateZhiHuColumnAddToArticleSet(String columnId, String articleId){
		//To connect to a single MongoDB instance:
	    //You can explicitly specify the hostname and the port:
		MongoClient mongoClient = new MongoClient( "localhost" , 27017 );

		//Access a Database
		MongoDatabase database = mongoClient.getDatabase("zhihu");
		
		//Access a Collection
		MongoCollection<Document> collection = database.getCollection("ZhiHuColumn");
		
		
		//Create a Document
		 Document doc = new Document();
		 doc.append("articleIds", articleId);
		 
		 //Update a Document
		 collection.updateOne(Filters.eq("columnId", columnId), new Document("$addToSet", doc));
		 
	}
}
