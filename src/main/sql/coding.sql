-- username=coding, password=coding
-- mysql is used

CREATE DATABASE if NOT EXISTS `coding` DEFAULT CHARACTER SET = utf8mb4;

CREATE TABLE `bid` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `source_id` varchar(255),
  `source` varchar(255),
  `bid` decimal(8,5),
  `updatedAt` timestamp null, 
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

