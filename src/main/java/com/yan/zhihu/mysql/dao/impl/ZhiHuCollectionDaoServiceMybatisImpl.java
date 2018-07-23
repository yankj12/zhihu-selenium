package com.yan.zhihu.mysql.dao.impl;

import java.util.Date;

import org.apache.ibatis.session.SqlSession;

import com.yan.zhihu.mysql.dao.facade.ZhiHuCollectionDaoService;
import com.yan.zhihu.mysql.schema.ZhiHuCollection;
import com.yan.zhihu.mysql.utils.JdbcUtil;

public class ZhiHuCollectionDaoServiceMybatisImpl implements ZhiHuCollectionDaoService{

	@Override
	public boolean insertZhiHuCollection(ZhiHuCollection zhiHuCollection) {
		boolean result = false;
		zhiHuCollection.setInsertTime(new Date());
		zhiHuCollection.setUpdateTime(new Date());
		
		SqlSession sqlSession = JdbcUtil.getSqlSession(true);
		
		String statement = MAPPER_NAME_SPACE + "." + "insertZhiHuCollection";
		sqlSession.insert(statement, zhiHuCollection);
		
		sqlSession.close();
		
		result = true;
		return result;
	}

	@Override
	public ZhiHuCollection queryZhiHuCollectionByCollectionId(String collectionId) {
		SqlSession sqlSession = JdbcUtil.getSqlSession(true);
		
		String statement = MAPPER_NAME_SPACE + "." + "queryZhiHuCollectionByCollectionId";
		ZhiHuCollection zhiHuCollection = sqlSession.selectOne(statement, collectionId);
		
		sqlSession.close();
		return zhiHuCollection;
	}

}
