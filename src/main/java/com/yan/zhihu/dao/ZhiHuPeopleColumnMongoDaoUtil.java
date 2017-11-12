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
import com.yan.zhihu.model.ZhiHuPeopleColumn;
import com.yan.zhihu.util.SchameDocumentUtil;

public class ZhiHuPeopleColumnMongoDaoUtil {

	public String insertZhiHuPeopleColumn(ZhiHuPeopleColumn zhiHuPeopleColumn){

		//To connect to a single MongoDB instance:
	    //You can explicitly specify the hostname and the port:
		MongoClient mongoClient = new MongoClient( "localhost" , 27017 );

		//Access a Database
		MongoDatabase database = mongoClient.getDatabase("zhihu");
		
		//Access a Collection
		MongoCollection<Document> collection = database.getCollection("ZhiHuPeopleColumn");
		
		//Create a Document
		Document doc = SchameDocumentUtil.schameToDocument(zhiHuPeopleColumn, ZhiHuPeopleColumn.class);

		//Insert a Document
		collection.insertOne(doc);
		
		//System.out.println("id:" + doc.get("_id"));
		String id = null;
		if(doc.get("_id") != null){
			id = doc.get("_id").toString();
		}
		return id;
	}
	
	public ZhiHuPeopleColumn findZhiHuPeopleColumnById(String id) {
		ZhiHuPeopleColumn zhiHuPeopleColumn = null;
		if(id!= null && !"".equals(id.trim())) {
			//To connect to a single MongoDB instance:
			//You can explicitly specify the hostname and the port:
			MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
			
			//Access a Database
			MongoDatabase database = mongoClient.getDatabase("zhihu");
			
			//Access a Collection
			MongoCollection<Document> collection = database.getCollection("ZhiHuPeopleColumn");
			
			List<Document> docs = collection.find(Filters.eq("_id", new ObjectId(id))).into(new ArrayList<Document>());
			if(docs != null && docs.size() > 0) {
				zhiHuPeopleColumn = (ZhiHuPeopleColumn)SchameDocumentUtil.documentToSchame(docs.get(0), ZhiHuPeopleColumn.class);
			}
		}
		
		return zhiHuPeopleColumn;
	}
	
	public ZhiHuPeopleColumn findZhiHuPeopleColumnByUserIdAndColumnId(String userId, String columnId) {
		ZhiHuPeopleColumn zhiHuPeopleColumn = null;
		if(userId!= null && !"".equals(userId.trim())) {
			//To connect to a single MongoDB instance:
			//You can explicitly specify the hostname and the port:
			MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
			
			//Access a Database
			MongoDatabase database = mongoClient.getDatabase("zhihu");
			
			//Access a Collection
			MongoCollection<Document> collection = database.getCollection("ZhiHuPeopleColumn");
			
			List<Bson> bsons = new ArrayList<Bson>(0);
			bsons.add(Filters.eq("userId", userId));
			bsons.add(Filters.eq("columnId", columnId));
			
			List<Document> docs = collection.find(Filters.and(bsons)).into(new ArrayList<Document>());
			if(docs != null && docs.size() > 0) {
				zhiHuPeopleColumn = (ZhiHuPeopleColumn)SchameDocumentUtil.documentToSchame(docs.get(0), ZhiHuPeopleColumn.class);
			}
		}
		
		return zhiHuPeopleColumn;
	}
	
	public void updateZhiHuPeopleColumn(ZhiHuPeopleColumn zhiHuPeopleColumn){
		//To connect to a single MongoDB instance:
	    //You can explicitly specify the hostname and the port:
		MongoClient mongoClient = new MongoClient( "localhost" , 27017 );

		//Access a Database
		MongoDatabase database = mongoClient.getDatabase("zhihu");
		
		//Access a Collection
		MongoCollection<Document> collection = database.getCollection("ZhiHuPeopleColumn");
		
		
		//Create a Document
		 Document doc = SchameDocumentUtil.schameToDocument(zhiHuPeopleColumn, ZhiHuPeopleColumn.class);
		 
		 //Update a Document
		 collection.updateOne(Filters.eq("_id", doc.get("_id")), new Document("$set", doc));
		 
	}
	
}
