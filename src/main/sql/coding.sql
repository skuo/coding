-- username=coding, password=coding
-- mysql is used

CREATE DATABASE if NOT EXISTS `coding` DEFAULT CHARACTER SET = utf8mb4;

CREATE TABLE `bid` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `source_id` varchar(255),
  `source` varchar(255),
  `bid` decimal(8,5),
  `updated_at` timestamp null, 
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `manager_id` bigint(20),
  `first_name` varchar(255),
  `last_name` varchar(255),
  `updated_at` timestamp null, 
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `user_address` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20),
  `address_id` bigint(20),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `address` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `street` varchar(255),
  `city` varchar(255),
  `provinceOrState` varchar(10),
  `zipcode` varchar(20),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

insert into user (manager_id, first_name, last_name)
values
(null, 'one', 'boss')
, (null, 'two', 'bravo')
, (1, 'three', 'charlie')
, (1, 'four', 'delta')
;

insert into address(street, city, provinceOrState, zipcode)
values
('1 first street', 'Santa Monica', 'CA', '90000')
, ('2 second street', 'Santa Monica', 'CA', '90000')
, ('3 third street', 'Los Angeles', 'CA', '90001')
, ('4 phamton street', 'Los Angeles County', 'CA', '90002')
;

insert into user_address(user_id, address_id)
values
(1, 1)
,(1, 2)
,(2, 3)
;

-- Find all user_id that does not have an address
select u.id, ua.user_id
from user u left join user_address ua on u.id = ua.user_id
where ua.user_id is null
;

-- find all user_id with two or more addresses
select ua.user_id
from user_address ua join address a on ua.user_id = a.id
where a.city = 'Santa Monica'
group by ua.user_id
having count(ua.user_id) >= 1
;

-- find all address_id with no user
select ua.address_id
from user_address ua right join address a on ua.address_id = a.id
where ua.address_id is null
;

-- Find all managers who have more than 1 report
select u1.id
from user u1 join user u2 on u1.id = u2.manager_id
group by u1.id
having count(u1.id) > 1
;

