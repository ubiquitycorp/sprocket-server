-- MySQL dump 10.13  Distrib 5.1.73, for redhat-linux-gnu (x86_64)
--
-- Host: localhost    Database: sprocket
-- ------------------------------------------------------
-- Server version	5.1.73

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `activity`
--

DROP TABLE IF EXISTS `activity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `activity` (
  `activity_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `activity_type` int(11) NOT NULL,
  `body` longtext,
  `category` int(11) DEFAULT NULL,
  `creation_date` bigint(20) DEFAULT NULL,
  `external_identifier` varchar(255) DEFAULT NULL,
  `external_network` int(11) NOT NULL,
  `photo_content_length` bigint(20) DEFAULT NULL,
  `photo_item_key` varchar(255) DEFAULT NULL,
  `photo_url` longtext,
  `last_updated` bigint(20) NOT NULL,
  `link` longtext,
  `title` varchar(150) DEFAULT NULL,
  `video_content_length` bigint(20) DEFAULT NULL,
  `video_item_key` varchar(255) DEFAULT NULL,
  `video_url` longtext,
  `owner_id` bigint(20) DEFAULT NULL,
  `place_id` bigint(20) DEFAULT NULL,
  `posted_by_contact_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`activity_id`),
  KEY `idx_external` (`external_network`,`external_identifier`),
  KEY `FK_tqlrq9kexapivp9l7cphux664` (`owner_id`),
  KEY `FK_7pdrjprva7uu87omjaa88fv1m` (`place_id`),
  KEY `FK_lg6r9avrgyvuua3la30qw7534` (`posted_by_contact_id`),
  CONSTRAINT `FK_lg6r9avrgyvuua3la30qw7534` FOREIGN KEY (`posted_by_contact_id`) REFERENCES `contact` (`contact_id`),
  CONSTRAINT `FK_7pdrjprva7uu87omjaa88fv1m` FOREIGN KEY (`place_id`) REFERENCES `place` (`place_id`),
  CONSTRAINT `FK_tqlrq9kexapivp9l7cphux664` FOREIGN KEY (`owner_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2604 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `activity_interests`
--

DROP TABLE IF EXISTS `activity_interests`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `activity_interests` (
  `activity_id` bigint(20) NOT NULL,
  `interest_id` bigint(20) NOT NULL,
  PRIMARY KEY (`activity_id`,`interest_id`),
  KEY `FK_sanldcwierv4ooq36q41kw6nf` (`interest_id`),
  CONSTRAINT `FK_jd3leq7qqeqldsp31n1op3ets` FOREIGN KEY (`activity_id`) REFERENCES `activity` (`activity_id`),
  CONSTRAINT `FK_sanldcwierv4ooq36q41kw6nf` FOREIGN KEY (`interest_id`) REFERENCES `interest` (`interest_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `contact`
--

DROP TABLE IF EXISTS `contact`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `contact` (
  `contact_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `max_age` int(11) DEFAULT NULL,
  `min_age` int(11) DEFAULT NULL,
  `current_location_description` varchar(255) DEFAULT NULL,
  `display_name` varchar(100) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `first_name` varchar(100) DEFAULT NULL,
  `gender` int(11) DEFAULT NULL,
  `content_length` bigint(20) DEFAULT NULL,
  `item_key` varchar(255) DEFAULT NULL,
  `url` longtext,
  `income_group` int(11) DEFAULT NULL,
  `last_name` varchar(100) DEFAULT NULL,
  `last_updated` bigint(20) NOT NULL,
  `location` varchar(255) DEFAULT NULL,
  `profile_url` longtext,
  `external_identity` bigint(20) DEFAULT NULL,
  `owner_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`contact_id`),
  KEY `idx_external` (`external_identity`),
  KEY `FK_cmaoslcxl4a8rr6co6epf8nkj` (`owner_id`),
  CONSTRAINT `FK_cmaoslcxl4a8rr6co6epf8nkj` FOREIGN KEY (`owner_id`) REFERENCES `user` (`user_id`),
  CONSTRAINT `FK_j6eahvdieq1mtawyfbmxfpi6m` FOREIGN KEY (`external_identity`) REFERENCES `external_identity` (`identity_id`)
) ENGINE=InnoDB AUTO_INCREMENT=734 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `conversation`
--

DROP TABLE IF EXISTS `conversation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `conversation` (
  `conversation_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `conversation_identifier` varchar(255) DEFAULT NULL,
  `conversation_name` varchar(255) DEFAULT NULL,
  `external_network` int(11) NOT NULL,
  PRIMARY KEY (`conversation_id`),
  KEY `idx_external` (`external_network`,`conversation_identifier`)
) ENGINE=InnoDB AUTO_INCREMENT=183 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `conversation_contact`
--

DROP TABLE IF EXISTS `conversation_contact`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `conversation_contact` (
  `conversation_id` bigint(20) NOT NULL,
  `contact_id` bigint(20) NOT NULL,
  PRIMARY KEY (`conversation_id`,`contact_id`),
  KEY `FK_kc2pqsluafvm87l8w55phxwl6` (`contact_id`),
  CONSTRAINT `FK_mxvrdy1o4nxy7vtwh0jxvja2k` FOREIGN KEY (`conversation_id`) REFERENCES `conversation` (`conversation_id`),
  CONSTRAINT `FK_kc2pqsluafvm87l8w55phxwl6` FOREIGN KEY (`contact_id`) REFERENCES `contact` (`contact_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `engaged_item`
--

DROP TABLE IF EXISTS `engaged_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `engaged_item` (
  `item_type` varchar(31) NOT NULL,
  `engaged_item_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `document_data_type` varchar(255) DEFAULT NULL,
  `search_term` varchar(255) DEFAULT NULL,
  `user_id` bigint(20) NOT NULL,
  `activity_id` bigint(20) DEFAULT NULL,
  `video_content_id` bigint(20) DEFAULT NULL,
  `message_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`engaged_item_id`),
  KEY `FK_2i68ovume39fa43dgueirnwcl` (`user_id`),
  KEY `FK_srmtvgnrnoyc9x3a00vsttxhr` (`activity_id`),
  KEY `FK_e9xa73id7poyjwun65wks2s9q` (`video_content_id`),
  KEY `FK_tlgcqjfhsmkfg5083xixh57ee` (`message_id`),
  CONSTRAINT `FK_tlgcqjfhsmkfg5083xixh57ee` FOREIGN KEY (`message_id`) REFERENCES `message` (`message_id`),
  CONSTRAINT `FK_2i68ovume39fa43dgueirnwcl` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`),
  CONSTRAINT `FK_e9xa73id7poyjwun65wks2s9q` FOREIGN KEY (`video_content_id`) REFERENCES `video_content` (`video_content_id`),
  CONSTRAINT `FK_srmtvgnrnoyc9x3a00vsttxhr` FOREIGN KEY (`activity_id`) REFERENCES `activity` (`activity_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `event`
--

DROP TABLE IF EXISTS `event`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `event` (
  `event_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `end_date` bigint(20) DEFAULT NULL,
  `last_updated` bigint(20) NOT NULL,
  `name` varchar(150) NOT NULL,
  `social_provider_identifier` varchar(255) DEFAULT NULL,
  `start_date` bigint(20) NOT NULL,
  `contact_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`event_id`),
  KEY `FK_b48aje8o3s18j13xwcmfmsp6b` (`contact_id`),
  KEY `FK_p84ruvsg7mfwb2x5p7iq3q103` (`user_id`),
  CONSTRAINT `FK_p84ruvsg7mfwb2x5p7iq3q103` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`),
  CONSTRAINT `FK_b48aje8o3s18j13xwcmfmsp6b` FOREIGN KEY (`contact_id`) REFERENCES `contact` (`contact_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `external_identity`
--

DROP TABLE IF EXISTS `external_identity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `external_identity` (
  `access_token` varchar(700) DEFAULT NULL,
  `client_platform` int(11) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `expiry_time` bigint(20) DEFAULT NULL,
  `external_network` int(11) DEFAULT NULL,
  `identifier` varchar(255) NOT NULL,
  `refresh_token` varchar(255) DEFAULT NULL,
  `secret_token` varchar(350) DEFAULT NULL,
  `identity_id` bigint(20) NOT NULL,
  PRIMARY KEY (`identity_id`),
  KEY `idx_external` (`external_network`,`identifier`),
  CONSTRAINT `FK_ivf5xpsdvpmk7344pi6120yas` FOREIGN KEY (`identity_id`) REFERENCES `identity` (`identity_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `external_interest`
--

DROP TABLE IF EXISTS `external_interest`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `external_interest` (
  `external_interest_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `external_network` int(11) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `interest_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`external_interest_id`),
  UNIQUE KEY `idx_external_network_name` (`external_network`,`name`),
  KEY `FK_sesci52qkh0pf6a1s4mim02lr` (`interest_id`),
  CONSTRAINT `FK_sesci52qkh0pf6a1s4mim02lr` FOREIGN KEY (`interest_id`) REFERENCES `interest` (`interest_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `group_membership`
--

DROP TABLE IF EXISTS `group_membership`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `group_membership` (
  `group_membership_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `external_network` int(11) DEFAULT NULL,
  `group_identifier` varchar(255) NOT NULL,
  `external_identity_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`group_membership_id`),
  UNIQUE KEY `idx_external_network_group_identifier_identity` (`external_network`,`group_identifier`,`external_identity_id`),
  UNIQUE KEY `idx_external_network_group_identifier_user` (`external_network`,`group_identifier`,`user_id`),
  KEY `FK_cpexo2r8f9i4dhrlkg5uq2kij` (`external_identity_id`),
  KEY `FK_ke4ahw3uj19xloasipu5m0d4u` (`user_id`),
  CONSTRAINT `FK_ke4ahw3uj19xloasipu5m0d4u` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`),
  CONSTRAINT `FK_cpexo2r8f9i4dhrlkg5uq2kij` FOREIGN KEY (`external_identity_id`) REFERENCES `external_identity` (`identity_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `identity`
--

DROP TABLE IF EXISTS `identity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `identity` (
  `identity_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `is_active` bit(1) NOT NULL,
  `last_updated` bigint(20) NOT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`identity_id`),
  KEY `FK_gi5aydpad5j0hnvjrfv1716rv` (`user_id`),
  CONSTRAINT `FK_gi5aydpad5j0hnvjrfv1716rv` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=746 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `interest`
--

DROP TABLE IF EXISTS `interest`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `interest` (
  `interest_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `parent_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`interest_id`),
  UNIQUE KEY `UK_n9khibjfvt2sanahgb13qslgd` (`name`),
  KEY `FK_qx74g7ka5todxgwl2kta6b9au` (`parent_id`),
  CONSTRAINT `FK_qx74g7ka5todxgwl2kta6b9au` FOREIGN KEY (`parent_id`) REFERENCES `interest` (`interest_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `message`
--

DROP TABLE IF EXISTS `message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `message` (
  `message_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `body` longtext,
  `external_identifier` varchar(255) DEFAULT NULL,
  `external_network` int(11) NOT NULL,
  `last_updated` bigint(20) NOT NULL,
  `send_date` bigint(20) NOT NULL,
  `title` varchar(250) DEFAULT NULL,
  `conversation_id` bigint(20) DEFAULT NULL,
  `owner_id` bigint(20) NOT NULL,
  `sender_contact_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`message_id`),
  UNIQUE KEY `idx_external` (`external_network`,`owner_id`,`external_identifier`),
  KEY `idx_network_owner` (`external_network`,`owner_id`,`last_updated`,`send_date`),
  KEY `FK_b7wvrkc75w1gdtiru3r4ur1n3` (`conversation_id`),
  KEY `FK_c9d28knw5rhdyboa299gdbffv` (`owner_id`),
  KEY `FK_p7vhy1xb6t1pvfbu6xanegu5m` (`sender_contact_id`),
  CONSTRAINT `FK_p7vhy1xb6t1pvfbu6xanegu5m` FOREIGN KEY (`sender_contact_id`) REFERENCES `contact` (`contact_id`),
  CONSTRAINT `FK_b7wvrkc75w1gdtiru3r4ur1n3` FOREIGN KEY (`conversation_id`) REFERENCES `conversation` (`conversation_id`),
  CONSTRAINT `FK_c9d28knw5rhdyboa299gdbffv` FOREIGN KEY (`owner_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1367 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `native_identity`
--

DROP TABLE IF EXISTS `native_identity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `native_identity` (
  `is_reset_verified` bit(1) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `reset_expiry_time` bigint(20) DEFAULT NULL,
  `reset_token` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `identity_id` bigint(20) NOT NULL,
  PRIMARY KEY (`identity_id`),
  CONSTRAINT `FK_8xhu1rvw1vs804w60ipqyjiu5` FOREIGN KEY (`identity_id`) REFERENCES `identity` (`identity_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `place`
--

DROP TABLE IF EXISTS `place`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `place` (
  `place_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `box_center_altitude` decimal(19,2) DEFAULT NULL,
  `box_center_latitude` decimal(19,2) DEFAULT NULL,
  `box_center_longitude` decimal(19,2) DEFAULT NULL,
  `box_lower_left_altitude` decimal(19,2) DEFAULT NULL,
  `box_lower_left_latitude` decimal(19,2) DEFAULT NULL,
  `box_lower_left_longitude` decimal(19,2) DEFAULT NULL,
  `box_lower_right_altitude` decimal(19,2) DEFAULT NULL,
  `box_lower_right_latitude` decimal(19,2) DEFAULT NULL,
  `box_lower_right_longitude` decimal(19,2) DEFAULT NULL,
  `box_upper_left_altitude` decimal(19,2) DEFAULT NULL,
  `box_upper_left_latitude` decimal(19,2) DEFAULT NULL,
  `box_upper_left_longitude` decimal(19,2) DEFAULT NULL,
  `box_upper_right_altitude` decimal(19,2) DEFAULT NULL,
  `box_upper_right_latitude` decimal(19,2) DEFAULT NULL,
  `box_upper_right_longitude` decimal(19,2) DEFAULT NULL,
  `locale` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`place_id`),
  UNIQUE KEY `idx_place_name_locale` (`name`,`locale`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `recommended_activity`
--

DROP TABLE IF EXISTS `recommended_activity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `recommended_activity` (
  `recommended_activity_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `group_identifier` varchar(255) DEFAULT NULL,
  `activity_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`recommended_activity_id`),
  UNIQUE KEY `idx_activity_group_identifier` (`activity_id`,`group_identifier`),
  CONSTRAINT `FK_iay0ynlh3ywhmm3g21uteqq77` FOREIGN KEY (`activity_id`) REFERENCES `activity` (`activity_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `recommended_video`
--

DROP TABLE IF EXISTS `recommended_video`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `recommended_video` (
  `recommended_video_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `group_identifier` varchar(255) DEFAULT NULL,
  `video_content_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`recommended_video_id`),
  KEY `FK_bms4bvw7wx488g1t9lce0bcnl` (`video_content_id`),
  CONSTRAINT `FK_bms4bvw7wx488g1t9lce0bcnl` FOREIGN KEY (`video_content_id`) REFERENCES `video_content` (`video_content_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `client_platform` int(11) NOT NULL,
  `display_name` varchar(100) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `first_name` varchar(100) DEFAULT NULL,
  `content_length` bigint(20) DEFAULT NULL,
  `item_key` varchar(255) DEFAULT NULL,
  `url` longtext,
  `is_verified` bit(1) DEFAULT NULL,
  `last_login` bigint(20) NOT NULL,
  `last_name` varchar(100) DEFAULT NULL,
  `last_updated` bigint(20) NOT NULL,
  `verification_time` bigint(20) DEFAULT NULL,
  `verification_token` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `UK_ob8kqyqqgmefl0aco34akdtpe` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_location`
--

DROP TABLE IF EXISTS `user_location`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_location` (
  `location_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `horizontal_accuracy` double DEFAULT NULL,
  `last_updated` bigint(20) NOT NULL,
  `altitude` decimal(19,12) DEFAULT NULL,
  `latitude` decimal(19,12) NOT NULL,
  `longitude` decimal(19,12) NOT NULL,
  `timestamp` bigint(20) NOT NULL,
  `vertical_accuracy` double DEFAULT NULL,
  `nearest_place_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`location_id`),
  UNIQUE KEY `UK_9jw1di7ynibylp5xt7bl8f5s3` (`user_id`),
  KEY `FK_527jp1hr2tmebu4uxhl1w1hhl` (`nearest_place_id`),
  CONSTRAINT `FK_9jw1di7ynibylp5xt7bl8f5s3` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`),
  CONSTRAINT `FK_527jp1hr2tmebu4uxhl1w1hhl` FOREIGN KEY (`nearest_place_id`) REFERENCES `place` (`place_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `video_content`
--

DROP TABLE IF EXISTS `video_content`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `video_content` (
  `video_content_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `category` int(11) DEFAULT NULL,
  `category_external_identifier` varchar(255) DEFAULT NULL,
  `description` longtext NOT NULL,
  `etag` varchar(255) DEFAULT NULL,
  `external_network` int(11) DEFAULT NULL,
  `last_updated` bigint(20) NOT NULL,
  `published_at` bigint(20) DEFAULT NULL,
  `thumb_content_length` bigint(20) DEFAULT NULL,
  `thumb_item_key` varchar(255) DEFAULT NULL,
  `thumb_url` longtext,
  `title` varchar(255) NOT NULL,
  `video_content_length` bigint(20) DEFAULT NULL,
  `video_item_key` varchar(255) DEFAULT NULL,
  `video_url` longtext,
  `owner_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`video_content_id`),
  KEY `idx_external` (`external_network`,`video_item_key`),
  KEY `FK_lf5cvly2959trmrkqqmvvwbxm` (`owner_id`),
  CONSTRAINT `FK_lf5cvly2959trmrkqqmvvwbxm` FOREIGN KEY (`owner_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=90 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2014-09-22 19:11:47
