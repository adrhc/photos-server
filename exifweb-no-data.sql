-- MySQL dump 10.13  Distrib 5.7.21, for Linux (x86_64)
--
-- Host: localhost    Database: exifweb
-- ------------------------------------------------------
-- Server version	5.7.21-0ubuntu0.16.04.1-log

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
-- Table structure for table `Album`
--

DROP TABLE IF EXISTS `Album`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Album` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(512) CHARACTER SET utf8 NOT NULL,
  `FK_IMAGE` int(11) DEFAULT NULL,
  `dirty` tinyint(1) NOT NULL DEFAULT '0',
  `last_update` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_name_fkimg` (`name`(255),`FK_IMAGE`) USING BTREE,
  KEY `idx_album_image` (`FK_IMAGE`),
  KEY `idx_album_last_update` (`last_update`),
  CONSTRAINT `fk_album_image` FOREIGN KEY (`FK_IMAGE`) REFERENCES `Image` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=66 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `AppConfig`
--

DROP TABLE IF EXISTS `AppConfig`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `AppConfig` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `value` varchar(255) CHARACTER SET utf8 NOT NULL,
  `name` varchar(255) CHARACTER SET utf8 NOT NULL,
  `last_update` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Image`
--

DROP TABLE IF EXISTS `Image`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Image` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `apertureValue` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `contrast` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `dateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `dateTimeOriginal` timestamp NULL DEFAULT NULL,
  `exposureBiasValue` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `exposureMode` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `exposureProgram` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `exposureTime` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `fNumber` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `flash` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `focalLength` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `gainControl` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `imageHeight` int(11) NOT NULL,
  `imageWidth` int(11) NOT NULL,
  `isoSpeedRatings` int(11) DEFAULT NULL,
  `lensModel` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `meteringMode` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `model` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `name` varchar(256) CHARACTER SET utf8 NOT NULL,
  `saturation` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `sceneCaptureType` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `sharpness` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `shutterSpeedValue` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `subjectDistanceRange` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `whiteBalanceMode` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `FK_ALBUM` int(11) NOT NULL,
  `status` tinyint(4) NOT NULL DEFAULT '0',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  `thumb_last_modified` timestamp NULL DEFAULT NULL,
  `rating` int(1) NOT NULL DEFAULT '1',
  `last_update` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_IMAGE_NAME_FK_ALBUM` (`name`(255),`FK_ALBUM`) USING BTREE,
  KEY `IDX_FK_ALBUM_DEL_STATUS` (`FK_ALBUM`,`deleted`,`status`) USING BTREE,
  CONSTRAINT `FK_IMAGE_ALBUM` FOREIGN KEY (`FK_ALBUM`) REFERENCES `Album` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=21742 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `group_authorities`
--

DROP TABLE IF EXISTS `group_authorities`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `group_authorities` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `group_id` int(11) NOT NULL,
  `authority` varchar(64) CHARACTER SET utf8 NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_GAUTH_GROUPS_idx` (`group_id`),
  CONSTRAINT `FK_GAUTH_GROUPS` FOREIGN KEY (`group_id`) REFERENCES `groups` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `group_authorities_album`
--

DROP TABLE IF EXISTS `group_authorities_album`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `group_authorities_album` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `album_id` int(11) NOT NULL,
  `group_authority_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_gam_album_id` (`album_id`),
  KEY `idx_gam_group_authority_id` (`group_authority_id`),
  CONSTRAINT `FK_GAUTHALBUM_ALBUM` FOREIGN KEY (`album_id`) REFERENCES `Album` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_GAUTHALBUM_GAUTH` FOREIGN KEY (`group_authority_id`) REFERENCES `group_authorities` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `group_members`
--

DROP TABLE IF EXISTS `group_members`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `group_members` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `group_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_GMEMBERS_USERS_idx` (`user_id`),
  KEY `FK_GMEMBERS_GROUPS_idx` (`group_id`),
  CONSTRAINT `FK_GMEMBERS_GROUPS` FOREIGN KEY (`group_id`) REFERENCES `groups` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_GMEMBERS_USERS` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `groups`
--

DROP TABLE IF EXISTS `groups`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `groups` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `group_name` varchar(64) CHARACTER SET utf8 NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(32) CHARACTER SET utf8 NOT NULL,
  `password` varchar(64) CHARACTER SET utf8 NOT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_image`
--

DROP TABLE IF EXISTS `user_image`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_image` (
  `id` int(11) NOT NULL,
  `FK_USER` int(11) NOT NULL,
  `FK_IMAGE` int(11) NOT NULL,
  `rating` tinyint(4) NOT NULL DEFAULT '5',
  PRIMARY KEY (`id`),
  KEY `fk_userimage_image` (`FK_IMAGE`),
  KEY `fk_userimage_user` (`FK_USER`),
  CONSTRAINT `fk_userimage_image` FOREIGN KEY (`FK_IMAGE`) REFERENCES `Image` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_userimage_user` FOREIGN KEY (`FK_USER`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Temporary table structure for view `v_album_cover`
--

DROP TABLE IF EXISTS `v_album_cover`;
/*!50001 DROP VIEW IF EXISTS `v_album_cover`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE VIEW `v_album_cover` AS SELECT 
 1 AS `id`,
 1 AS `albumName`,
 1 AS `dirty`,
 1 AS `imgName`,
 1 AS `imageWidth`,
 1 AS `imageHeight`,
 1 AS `thumbPath`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `v_album_cover_lua`
--

DROP TABLE IF EXISTS `v_album_cover_lua`;
/*!50001 DROP VIEW IF EXISTS `v_album_cover_lua`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE VIEW `v_album_cover_lua` AS SELECT 
 1 AS `id`,
 1 AS `albumName`,
 1 AS `dirty`,
 1 AS `imgName`,
 1 AS `imageHeight`,
 1 AS `imageWidth`,
 1 AS `thumbPath`*/;
SET character_set_client = @saved_cs_client;

--
-- Dumping routines for database 'exifweb'
--
/*!50003 DROP FUNCTION IF EXISTS `imagePath` */;
ALTER DATABASE `exifweb` CHARACTER SET utf8 COLLATE utf8_general_ci ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_unicode_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`exifweb`@`localhost` FUNCTION `imagePath`(`albumName` VARCHAR(512) CHARSET utf8, `thumbLastModified` DATETIME, `imgName` VARCHAR(256) CHARSET utf8) RETURNS varchar(1024) CHARSET utf8
    NO SQL
RETURN CONCAT_WS('/','albums', albumName, unix_timestamp(thumbLastModified), imgName) ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
ALTER DATABASE `exifweb` CHARACTER SET utf8 COLLATE utf8_unicode_ci ;
/*!50003 DROP FUNCTION IF EXISTS `thumbHeight` */;
ALTER DATABASE `exifweb` CHARACTER SET utf8 COLLATE utf8_general_ci ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_unicode_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`exifweb`@`%` FUNCTION `THUMBHEIGHT`(`imageHeight` SMALLINT, `imageWidth` SMALLINT) RETURNS smallint(6)
    NO SQL
IF imageHeight IS NULL THEN
	RETURN 157;
ELSEIF imageHeight < imageWidth THEN
	RETURN FLOOR(157 * imageHeight / imageWidth);
ELSE
	RETURN 157;
END IF ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
ALTER DATABASE `exifweb` CHARACTER SET utf8 COLLATE utf8_unicode_ci ;
/*!50003 DROP FUNCTION IF EXISTS `thumbPath` */;
ALTER DATABASE `exifweb` CHARACTER SET utf8 COLLATE utf8_general_ci ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_unicode_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`exifweb`@`%` FUNCTION `THUMBPATH`(`albumName` VARCHAR(512) CHARSET utf8, `thumbLastModified` DATETIME, `imgName` VARCHAR(256) CHARSET utf8) RETURNS varchar(1024) CHARSET utf8
    NO SQL
RETURN CONCAT_WS('/','thumbs', albumName, unix_timestamp(thumbLastModified), imgName) ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
ALTER DATABASE `exifweb` CHARACTER SET utf8 COLLATE utf8_unicode_ci ;
/*!50003 DROP FUNCTION IF EXISTS `thumbWidth` */;
ALTER DATABASE `exifweb` CHARACTER SET utf8 COLLATE utf8_general_ci ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_unicode_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`exifweb`@`%` FUNCTION `THUMBWIDTH`(`imageHeight` SMALLINT, `imageWidth` SMALLINT) RETURNS smallint(6)
    NO SQL
IF imageHeight IS NULL THEN
	RETURN 157;
ELSEIF imageHeight < imageWidth THEN
	RETURN 157;
ELSE
	RETURN FLOOR(157 * imageWidth / imageHeight);
END IF ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
ALTER DATABASE `exifweb` CHARACTER SET utf8 COLLATE utf8_unicode_ci ;

--
-- Final view structure for view `v_album_cover`
--

/*!50001 DROP VIEW IF EXISTS `v_album_cover`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`exifweb`@`%` SQL SECURITY DEFINER */
/*!50001 VIEW `v_album_cover` AS select `a`.`id` AS `id`,`a`.`name` AS `albumName`,`a`.`dirty` AS `dirty`,`i`.`name` AS `imgName`,`i`.`imageWidth` AS `imageWidth`,`i`.`imageHeight` AS `imageHeight`,`THUMBPATH`(`a`.`name`,`i`.`thumb_last_modified`,`i`.`name`) AS `thumbPath` from (`Album` `a` left join `Image` `i` on((`i`.`id` = `a`.`FK_IMAGE`))) where (`a`.`deleted` = 0) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_album_cover_lua`
--

/*!50001 DROP VIEW IF EXISTS `v_album_cover_lua`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`exifweb`@`%` SQL SECURITY DEFINER */
/*!50001 VIEW `v_album_cover_lua` AS select `a`.`id` AS `id`,`a`.`name` AS `albumName`,`a`.`dirty` AS `dirty`,`i`.`name` AS `imgName`,`THUMBHEIGHT`(`i`.`imageHeight`,`i`.`imageWidth`) AS `imageHeight`,`THUMBWIDTH`(`i`.`imageHeight`,`i`.`imageWidth`) AS `imageWidth`,`THUMBPATH`(`a`.`name`,`i`.`thumb_last_modified`,`i`.`name`) AS `thumbPath` from (`Album` `a` left join `Image` `i` on((`i`.`id` = `a`.`FK_IMAGE`))) where (`a`.`deleted` = 0) order by `a`.`name` desc */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

