CREATE DATABASE if NOT EXISTS `inauth` DEFAULT CHARACTER SET = utf8mb4;

CREATE TABLE `location` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `latitude` decimal(9,6),
  `longitude` decimal(9,6),
  PRIMARY KEY (`id`),
  INDEX `lat_long` (`latitude`,`longitude`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;