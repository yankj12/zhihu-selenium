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
import com.yan.zhihu.model.ZhiHuCollection;
import com.yan.zhihu.util.SchameDocumentUtil;

public class ZhiHuCollectionMongoDaoUtil {
	
	public String insertZhiHuCollection(ZhiHuCollection zhiHuCollection){

		//To connect to a single MongoDB instance:
	    //You can explicitly specify the hostname and the port:
		MongoClient mongoClient = new MongoClient( "localhost" , 27017 );

		//Access a Database
		MongoDatabase database = mongoClient.getDatabase("zhihu");
		
		//Access a Collection
		MongoCollection<Document> collection = database.getCollection("ZhiHuCollection");
		
		//Create a Document
		Document doc = SchameDocumentUtil.schameToDocument(zhiHuCollection, ZhiHuCollection.class);

		//Insert a Document
		collection.insertOne(doc);
		
		//System.out.println("id:" + doc.get("_id"));
		String id = null;
		if(doc.get("_id") != null){
			id = doc.get("_id").toString();
		}
		return id;
	}

	public ZhiHuCollection findZhiHuCollectionById(String id) {
		ZhiHuCollection zhiHuCollection = null;
		if(id!= null && !"".equals(id.trim())) {
			//To connect to a single MongoDB instance:
			//You can explicitly specify the hostname and the port:
			MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
			
			//Access a Database
			MongoDatabase database = mongoClient.getDatabase("zhihu");
			
			//Access a Collection
			MongoCollection<Document> collection = database.getCollection("ZhiHuCollection");
			
			List<Document> docs = collection.find(Filters.eq("_id", new ObjectId(id))).into(new ArrayList<Document>());
			if(docs != null && docs.size() > 0) {
				zhiHuCollection = (ZhiHuCollection)SchameDocumentUtil.documentToSchame(docs.get(0), ZhiHuCollection.class);
			}
		}
		
		return zhiHuCollection;
	}
	
	public ZhiHuCollection findZhiHuCollectionByCollectionId(String collectionId) {
		ZhiHuCollection zhiHuCollection = null;
		if(collectionId!= null && !"".equals(collectionId.trim())) {
			//To connect to a single MongoDB instance:
			//You can explicitly specify the hostname and the port:
			MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
			
			//Access a Database
			MongoDatabase database = mongoClient.getDatabase("zhihu");
			
			//Access a Collection
			MongoCollection<Document> collection = database.getCollection("ZhiHuCollection");
			
			List<Document> docs = collection.find(Filters.eq("collectionId", collectionId)).into(new ArrayList<Document>());
			if(docs != null && docs.size() > 0) {
				zhiHuCollection = (ZhiHuCollection)SchameDocumentUtil.documentToSchame(docs.get(0), ZhiHuCollection.class);
			}
		}
		
		return zhiHuCollection;
	}
	
	public void updateZhiHuCollection(ZhiHuCollection zhiHuCollection){
		//To connect to a single MongoDB instance:
	    //You can explicitly specify the hostname and the port:
		MongoClient mongoClient = new MongoClient( "localhost" , 27017 );

		//Access a Database
		MongoDatabase database = mongoClient.getDatabase("zhihu");
		
		//Access a Collection
		MongoCollection<Document> collection = database.getCollection("ZhiHuCollection");
		
		
		//Create a Document
		 Document doc = SchameDocumentUtil.schameToDocument(zhiHuCollection, ZhiHuCollection.class);
		 
		 //Update a Document
		 collection.updateOne(Filters.eq("_id", doc.get("_id")), new Document("$set", doc));
		 
	}
	
}
