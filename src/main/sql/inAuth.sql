CREATE DATABASE if NOT EXISTS `inauth` DEFAULT CHARACTER SET = utf8mb4;

CREATE TABLE `location` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `latitude` decimal(8,5),
  `longitude` decimal(8,5),
  PRIMARY KEY (`id`),
  INDEX `lat_long` (`latitude`,`longitude`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

insert into location (latitude, longitude) values(1.1, 2.20000);
insert into location (latitude, longitude) values(2.2, 134.07378);
insert into location (latitude, longitude) values(34.0522, -118.2437);
insert into location (latitude, longitude) values(37.7749, -122.4194);
insert into location (latitude, longitude) values(32.7157, -117.1611);

