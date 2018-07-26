--收藏夹
CREATE TABLE ZhiHuCollection (
  collectionId varchar(20),
  collectionName varchar(255) DEFAULT NULL,
  authorId varchar(20) DEFAULT NULL,
  relativeUrl varchar(100) DEFAULT NULL,
  itemCount int,
  followersCount int,
  contentLastModifyDay varchar(20) DEFAULT NULL,
  insertTime datetime DEFAULT NULL,
  updateTime datetime DEFAULT NULL,
  PRIMARY KEY (`collectionId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--收藏夹中内容
CREATE TABLE ZhiHuCollectionItem (
  id BIGINT NOT NULL AUTO_INCREMENT,
  collectionId varchar(20) DEFAULT NULL,
  dataType varchar(10) DEFAULT NULL,
  dataModule varchar(16) DEFAULT NULL,
  title varchar(255) DEFAULT NULL,
  answerUrl varchar(255) DEFAULT NULL,
  answerId varchar(10) DEFAULT NULL,
  answerUrlToken varchar(16) DEFAULT NULL,
  voteCount varchar(10) DEFAULT NULL,
  authorName varchar(60) DEFAULT NULL,
  authorId varchar(100) DEFAULT NULL,
  authorRelativeUrl varchar(255) DEFAULT NULL,
  authorSummary varchar(255) DEFAULT NULL,
  contentHTML LONGTEXT DEFAULT NULL,
  summaryInfo TEXT DEFAULT NULL,
  insertTime datetime DEFAULT NULL,
  updateTime datetime DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE zhihu.zhihucollectionitem MODIFY COLUMN contentHTML LONGTEXT CHARACTER SET utf8 COLLATE utf8_general_ci NULL ;

