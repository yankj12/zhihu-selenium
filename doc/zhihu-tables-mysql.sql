
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

