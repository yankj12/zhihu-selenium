package com.yan.zhihu.dao;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.yan.zhihu.model.ZhiHuQuestion;
import com.yan.zhihu.util.SchameDocumentUtil;

public class ZhiHuQuestionMongoDaoUtil {
	
	public String insertZhiHuQuestion(ZhiHuQuestion zhiHuQuestion){

		//To connect to a single MongoDB instance:
	    //You can explicitly specify the hostname and the port:
		MongoClient mongoClient = new MongoClient( "localhost" , 27017 );

		//Access a Database
		MongoDatabase database = mongoClient.getDatabase("zhihu");
		
		//Access a Collection
		MongoCollection<Document> collection = database.getCollection("ZhiHuQuestion");
		
		//Create a Document
		Document doc = SchameDocumentUtil.schameToDocument(zhiHuQuestion, ZhiHuQuestion.class);

		//Insert a Document
		collection.insertOne(doc);
		
		//System.out.println("id:" + doc.get("_id"));
		String id = null;
		if(doc.get("_id") != null){
			id = doc.get("_id").toString();
		}
		return id;
	}

	public ZhiHuQuestion findZhiHuQuestionById(String id) {
		ZhiHuQuestion zhiHuQuestion = null;
		if(id!= null && !"".equals(id.trim())) {
			//To connect to a single MongoDB instance:
			//You can explicitly specify the hostname and the port:
			MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
			
			//Access a Database
			MongoDatabase database = mongoClient.getDatabase("zhihu");
			
			//Access a Collection
			MongoCollection<Document> collection = database.getCollection("ZhiHuQuestion");
			
			List<Document> docs = collection.find(Filters.eq("_id", new ObjectId(id))).into(new ArrayList<Document>());
			if(docs != null && docs.size() > 0) {
				zhiHuQuestion = (ZhiHuQuestion)SchameDocumentUtil.documentToSchame(docs.get(0), ZhiHuQuestion.class);
			}
		}
		
		return zhiHuQuestion;
	}
	
	public ZhiHuQuestion findZhiHuQuestionByQuestionId(String questionId) {
		ZhiHuQuestion zhiHuQuestion = null;
		if(questionId!= null && !"".equals(questionId.trim())) {
			//To connect to a single MongoDB instance:
			//You can explicitly specify the hostname and the port:
			MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
			
			//Access a Database
			MongoDatabase database = mongoClient.getDatabase("zhihu");
			
			//Access a Collection
			MongoCollection<Document> collection = database.getCollection("ZhiHuQuestion");
			
			List<Document> docs = collection.find(Filters.eq("questionId", questionId)).into(new ArrayList<Document>());
			if(docs != null && docs.size() > 0) {
				zhiHuQuestion = (ZhiHuQuestion)SchameDocumentUtil.documentToSchame(docs.get(0), ZhiHuQuestion.class);
			}
		}
		
		return zhiHuQuestion;
	}
	
	public void updateZhiHuQuestion(ZhiHuQuestion zhiHuQuestion){
		//To connect to a single MongoDB instance:
	    //You can explicitly specify the hostname and the port:
		MongoClient mongoClient = new MongoClient( "localhost" , 27017 );

		//Access a Database
		MongoDatabase database = mongoClient.getDatabase("zhihu");
		
		//Access a Collection
		MongoCollection<Document> collection = database.getCollection("ZhiHuQuestion");
		
		
		//Create a Document
		 Document doc = SchameDocumentUtil.schameToDocument(zhiHuQuestion, ZhiHuQuestion.class);
		 
		 //Update a Document
		 collection.updateOne(Filters.eq("_id", doc.get("_id")), new Document("$set", doc));
		 
	}
	
}
