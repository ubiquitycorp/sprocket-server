USE sprocket;
CREATE TABLE `configuration` (
  `configuration_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `configuration_type` int(11) NOT NULL,
  `is_active` bit(1) NOT NULL,
  `last_updated` bigint(20) NOT NULL,
  `name` varchar(200) NOT NULL,
  `value` varchar(200) NOT NULL,
  PRIMARY KEY (`configuration_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;