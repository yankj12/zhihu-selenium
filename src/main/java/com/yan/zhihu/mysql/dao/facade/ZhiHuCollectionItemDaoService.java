package com.yan.zhihu.mysql.dao.facade;

import java.util.List;

import com.yan.zhihu.mysql.schema.ZhiHuCollectionItem;

public interface ZhiHuCollectionItemDaoService {

	public final static String MAPPER_NAME_SPACE ="com.yan.zhihu.mysql.mapping.ZhiHuCollectionItemMapper";

	boolean insertZhiHuCollectionItem(ZhiHuCollectionItem zhiHuCollectionItem);

	List<ZhiHuCollectionItem> queryZhiHuCollectionItemsByTitle(String title);
	
	ZhiHuCollectionItem queryZhiHuCollectionItemByAnswerId(String answerId);
}
