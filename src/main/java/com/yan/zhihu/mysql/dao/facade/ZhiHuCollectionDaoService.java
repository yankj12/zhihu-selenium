package com.yan.zhihu.mysql.dao.facade;

import com.yan.zhihu.mysql.schema.ZhiHuCollection;

public interface ZhiHuCollectionDaoService {

	public final static String MAPPER_NAME_SPACE ="com.yan.zhihu.mysql.mapping.ZhiHuCollectionMapper";

	boolean insertZhiHuCollection(ZhiHuCollection zhiHuCollection);

	ZhiHuCollection queryZhiHuCollectionByCollectionId(String collectionId);
}
