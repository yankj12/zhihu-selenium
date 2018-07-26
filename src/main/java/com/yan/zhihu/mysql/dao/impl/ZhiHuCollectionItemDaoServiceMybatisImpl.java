package com.yan.zhihu.mysql.dao.impl;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.yan.zhihu.mysql.dao.facade.ZhiHuCollectionItemDaoService;
import com.yan.zhihu.mysql.schema.ZhiHuCollectionItem;
import com.yan.zhihu.mysql.utils.JdbcUtil;

public class ZhiHuCollectionItemDaoServiceMybatisImpl implements ZhiHuCollectionItemDaoService{

	@Override
	public boolean insertZhiHuCollectionItem(ZhiHuCollectionItem zhiHuCollectionItem) {
		boolean result = false;
		zhiHuCollectionItem.setInsertTime(new Date());
		zhiHuCollectionItem.setUpdateTime(new Date());
		
		SqlSession sqlSession = JdbcUtil.getSqlSession(true);
		
		String statement = MAPPER_NAME_SPACE + "." + "insertZhiHuCollectionItem";
		sqlSession.insert(statement, zhiHuCollectionItem);
		
		sqlSession.close();
		
		result = true;
		return result;
	}

	@Override
	public List<ZhiHuCollectionItem> queryZhiHuCollectionItemsByTitle(String title) {
		SqlSession sqlSession = JdbcUtil.getSqlSession(true);
		
		String statement = MAPPER_NAME_SPACE + "." + "queryZhiHuCollectionItemsByTitle";
		List<ZhiHuCollectionItem> zhiHuCollectionItems = sqlSession.selectList(statement, title);
		
		sqlSession.close();
		return zhiHuCollectionItems;
	}

	@Override
	public ZhiHuCollectionItem queryZhiHuCollectionItemByAnswerId(String answerId) {
		SqlSession sqlSession = JdbcUtil.getSqlSession(true);
		
		String statement = MAPPER_NAME_SPACE + "." + "queryZhiHuCollectionItemByAnswerId";
		ZhiHuCollectionItem zhiHuCollectionItem = sqlSession.selectOne(statement, answerId);
		
		sqlSession.close();
		return zhiHuCollectionItem;
	}

}
