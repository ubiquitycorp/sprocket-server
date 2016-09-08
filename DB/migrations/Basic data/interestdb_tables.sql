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
-- Table structure for table `interest`
--

DROP TABLE IF EXISTS `interest`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `interest` (
  `interest_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `parent_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`interest_id`),
  UNIQUE KEY `idx_name_parent` (`name`,`parent_id`),
  KEY `FK_qx74g7ka5todxgwl2kta6b9au` (`parent_id`),
  CONSTRAINT `FK_qx74g7ka5todxgwl2kta6b9au` FOREIGN KEY (`parent_id`) REFERENCES `interest` (`interest_id`)
) ENGINE=InnoDB AUTO_INCREMENT=316 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `interest`
--

LOCK TABLES `interest` WRITE;
/*!40000 ALTER TABLE `interest` DISABLE KEYS */;
INSERT INTO `interest` VALUES (226,'ABA',223),(79,'Accommodations',77),(83,'Accommodations',82),(17,'Action',13),(188,'Active Life',168),(266,'Active Life',241),(174,'Activities',169),(206,'Activities',196),(183,'Activity Venues',169),(204,'Activity Venues',196),(170,'Adult Entertainment',169),(202,'Adult Entertainment',196),(92,'African',88),(113,'African',110),(219,'Alumni Groups',218),(225,'AMA',223),(23,'Amazon',22),(97,'American',88),(119,'American',110),(130,'Anniversary',129),(151,'Anniversary',144),(153,'Anniversary',152),(161,'Anniversary',160),(173,'Art Activities',169),(210,'Art Activities',196),(265,'Articles on weight loss/gain',263),(187,'Arts/Crafts',184),(90,'Asian',88),(115,'Asian',110),(103,'Australian',88),(297,'Automotive',272),(55,'Badminton',52),(43,'Ballet',39),(176,'Bars',169),(197,'Bars',196),(70,'Baseball',67),(73,'Basketball',67),(268,'Beauty Spas',241),(302,'Bicycles',297),(143,'Birthday',137),(134,'Birthdays',129),(150,'Birthdays',144),(159,'Birthdays',152),(164,'Birthdays',160),(66,'Boxing',63),(246,'Brands',242),(122,'Breakfast & Brunch',110),(77,'Business',76),(287,'Business Apps',272),(232,'Business Opportunities',218),(106,'Candy',105),(299,'Car Cleaning',297),(16,'Cartoon',13),(9,'Cartoons',2),(181,'Casinos',169),(205,'Casinos',196),(283,'Child Related',278),(98,'Chinese',88),(117,'Chinese',110),(108,'Chocolate',105),(35,'Classical',29),(282,'Cleaning Services',278),(256,'Clinics and Hospitals',253),(242,'Clothing',241),(279,'Clothing Services',278),(220,'Colleges',219),(10,'Comedy',2),(18,'Comedy',13),(67,'Common North American Sports',45),(40,'Concerts',39),(216,'Concerts',213),(274,'Construction Supplies',273),(109,'Cookies',105),(32,'Country',29),(75,'Cricket',74),(21,'Crime',13),(91,'Cuisine',88),(182,'Cultural Activities',169),(212,'Cultural Activities',196),(213,'Cultural Events',168),(309,'Current Stock Profile',305),(290,'Customer Relationship Management',287),(298,'Customization',297),(199,'Dates',196),(249,'Dating',241),(252,'Dating sites',249),(301,'Dealers',297),(257,'Dentists',253),(112,'Desserts',110),(258,'Diagnostic Services',253),(263,'Dieting',241),(264,'Diets that you are attempting',263),(15,'Drama',13),(7,'Dramas',2),(96,'Eastern European',88),(111,'Eastern European',110),(228,'Education',218),(313,'Email, Calendar and Contacts',310),(1,'Entertainment',NULL),(95,'European',88),(114,'European',110),(36,'Event Planning',1),(221,'Events',219),(190,'Experiences',188),(63,'Extreme Sports',45),(49,'F1',46),(128,'Family',NULL),(185,'Fantasy Sports',184),(126,'Fast Food',110),(131,'Favorite Food',129),(141,'Favorite Food',137),(147,'Favorite Food',144),(154,'Favorite Food',152),(165,'Favorite Food',160),(308,'Favorite Stocks',305),(136,'Favorite Thing',129),(148,'Favorite Thing',144),(157,'Favorite Thing',152),(163,'Favorite Thing',160),(139,'Favorite Toy',137),(288,'File Share and Collaboration',287),(4,'Financial Reports',2),(296,'Financial Services',272),(39,'Fine Arts',1),(195,'Fitness',188),(267,'fitness',266),(87,'Food',NULL),(69,'Football',67),(93,'French',88),(121,'French',110),(101,'Genre',88),(178,'Go on dates',169),(72,'Golf',67),(235,'Groups',234),(271,'Hair Salons',268),(253,'Health Medical',241),(184,'Hobbies',168),(71,'Hockey',67),(273,'Home Services',272),(81,'Hotels & Travel',77),(86,'Hotels & Travel',82),(280,'House Services',278),(250,'How often they go out',249),(26,'Hulu',22),(107,'Ice Cream',105),(94,'Indian',88),(48,'Indy Car',46),(307,'Inside stock information',305),(99,'Italian',88),(127,'Italian',110),(104,'Japanese',88),(120,'Japanese',110),(30,'Jazz',29),(105,'Junk Food',87),(236,'Keywords',234),(129,'Kids',128),(238,'Kiwanis',237),(89,'Latin American',88),(116,'Latin American',110),(168,'Leisure',NULL),(292,'Level of Consumerism',272),(241,'Lifestyle',NULL),(240,'Lions Club',237),(278,'Local Services',272),(133,'Marriage Status',129),(149,'Marriage Status',144),(158,'Marriage Status',152),(162,'Marriage Status',160),(261,'Medical Billing',253),(234,'Meet-Ups',218),(255,'Mental Health',253),(100,'Mexican',88),(118,'Mexican',110),(102,'Middle East',88),(124,'Middle Eastern',110),(315,'Miscellaneous',314),(64,'MMA',63),(46,'Motor Sports',45),(47,'Motorcycle Racing',46),(13,'Movies',1),(3,'Movies',2),(215,'Movies',213),(217,'Museums',213),(29,'Music',1),(171,'Music Events',169),(211,'Music Events',196),(51,'Nascar',46),(24,'Netflix',22),(222,'News from the group',219),(6,'News Reports',2),(172,'Nightlife',169),(207,'Nightlife',196),(169,'Nighttime Activities',168),(285,'Nonprofit',278),(311,'Notes',310),(295,'Number of purchased Apps',292),(50,'Off-Road',46),(289,'Office productivity Suite',287),(57,'Olympics',45),(44,'Opera',39),(314,'Other',NULL),(123,'Other',110),(192,'Outdoors',188),(27,'Pandora',22),(160,'Parents',128),(132,'Past gifts',129),(145,'Past gifts',144),(155,'Past gifts',152),(166,'Past gifts',160),(82,'Personal',76),(137,'Pets',128),(140,'Pets',137),(42,'Philharmonic',39),(254,'Physicians',253),(53,'Ping Pong',52),(179,'Play videogames',169),(201,'Play videogames',196),(34,'Pop',29),(74,'Popular World Sports',45),(259,'Practitioners',253),(5,'Premium Content',2),(272,'Productivity',NULL),(310,'Productivity Apps',272),(277,'Professional',272),(223,'Professional Groups',218),(218,'Professional Organizations',NULL),(227,'Public Services Govt',218),(54,'Racquetball',52),(31,'Rap',29),(291,'Real Estate',272),(275,'Real Estate',273),(110,'Recipes',87),(286,'Recording Services',278),(191,'Recreational Areas',188),(262,'Religious Organizations',241),(38,'Rentals',36),(194,'Rentals',188),(303,'Rentals',297),(284,'Repair Services',278),(88,'Restaurants',87),(180,'Restaurants',169),(198,'Restaurants',196),(33,'Rock',29),(239,'Rotary',237),(58,'Rowing',57),(138,'Schedule',137),(230,'School Services',228),(231,'Schools',228),(19,'Science Fiction',13),(237,'Service Organizations',218),(281,'Service Practitioners',278),(247,'Shoes',242),(248,'Shopping',241),(152,'Siblings',128),(144,'Significant Other',128),(135,'Significant other',129),(146,'Significant other',144),(156,'Significant other',152),(167,'Significant other',160),(60,'Skating',57),(59,'Skiing',57),(270,'Skin',268),(68,'Soccer',67),(269,'Spas',268),(203,'Special Events',196),(229,'Speciality Schools',228),(260,'Speciality Services',253),(293,'Spending Habits on Apps',292),(294,'Spending Habits on other items connected to Apple Pay',292),(224,'SPIE',223),(214,'Sporting Events',213),(45,'Sports',NULL),(11,'Sports',2),(193,'Sports',188),(52,'Sports similar to tennis',45),(37,'Staff',36),(305,'Stock Market',272),(306,'Stocks they are interested in',305),(245,'Stores',242),(22,'Streaming',1),(244,'Sunglasses',242),(233,'Taken from LinkedIn',232),(56,'Tennis',52),(125,'Thai',110),(41,'Theater',39),(20,'Thrillers',13),(80,'Tours',77),(84,'Tours',82),(62,'Track and Field',57),(78,'Transportation',77),(85,'Transportation',82),(76,'Travel',NULL),(2,'TV',1),(142,'Type of Animal',137),(251,'Type of person they are interested in',249),(243,'Undergarments',242),(300,'Vehicle Repair',297),(304,'Vehicle Services',297),(175,'Venues',169),(189,'Venues',188),(200,'Venues',196),(186,'Video Games',184),(12,'Video on Demand',2),(25,'Vimeo',22),(209,'Visit Family',196),(312,'Voice, Video and Text',310),(14,'War',13),(177,'Watch TV',169),(208,'Watch TV',196),(8,'Weather',2),(196,'Weekend Activities',168),(276,'Workers',273),(61,'Wrestling',57),(65,'X-Games',63),(28,'YouTube',22);
/*!40000 ALTER TABLE `interest` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-01-14 17:34:10
