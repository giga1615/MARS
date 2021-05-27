-- MySQL dump 10.13  Distrib 8.0.23, for Win64 (x86_64)
--
-- Host: localhost    Database: mars
-- ------------------------------------------------------
-- Server version	8.0.25

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `capsule`
--

DROP TABLE IF EXISTS `capsule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `capsule` (
  `no` int NOT NULL AUTO_INCREMENT,
  `id` varchar(100) DEFAULT NULL,
  `title` varchar(100) DEFAULT NULL,
  `music_title` varchar(200) DEFAULT NULL,
  `memo` varchar(200) DEFAULT NULL,
  `photo_url` text,
  `voice_url` text,
  `video_url` text,
  `gps_x` double DEFAULT NULL,
  `gps_y` double DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `open_date` varchar(200) DEFAULT NULL,
  `address` text,
  `capsule_friends` text,
  `capusle_frineds_by_name` longtext,
  PRIMARY KEY (`no`)
) ENGINE=InnoDB AUTO_INCREMENT=285 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `capsule`
--

LOCK TABLES `capsule` WRITE;
/*!40000 ALTER TABLE `capsule` DISABLE KEYS */;
INSERT INTO `capsule` VALUES (280,'hossi0128@hanmail.net','도건이랑','아이유','재밌다','http://k4a403.p.ssafy.io:8000/api/capsule/image/202105190946311621333684581.jpg','','',37.6508818,127.0628462,'2021-05-19 09:46:31','2021-05-20','서울대학교','hossi0128@hanmail.net,giga1422@naver.com,ldkgang@kakao.com','황윤호,지선,이도건'),(281,'hossi0128@hanmail.net','맛있다','노래','고기가 야무지다.','http://k4a403.p.ssafy.io:8000/api/capsule/image/2021051909530320210518_115957.jpg','','',37.6508936,127.0628699,'2021-05-19 09:53:03','2021-07-31','무슨 서울','hossi0128@hanmail.net,giga1422@naver.com,ldkgang@kakao.com','황윤호,지선,이도건'),(282,'giga1422@naver.com','그냥그냥','그냥','그냥','http://k4a403.p.ssafy.io:8000/api/capsule/image/20210519095429Mars20210519185400.png','','',37.6336122,126.698552,'2021-05-19 09:54:29','2021-05-19','우리집','giga1422@naver.com,hossi0128@hanmail.net,qkrekdbs69@hanmail.net,ldkgang@kakao.com','지선,황윤호,박다윤,이도건'),(283,'giga1422@naver.com','생애 첫 부산 여행','볼빨간사춘기 - 여행','오늘 처음으로 부산에 왔다.\n부산은 생각보다 엄청 크고 복잡한 도시였다….\n난 누구고 여긴 어디..?ㅋㅋㅋ\n맛있는거 많이 먹고 가겠습니돠..','http://k4a403.p.ssafy.io:8000/api/capsule/image/20210519101015capsule.png','','',35.161128417596835,129.19111376441438,'2021-05-19 10:10:15','2021-06-14','해운대 어딘가','giga1422@naver.com,qkrekdbs69@hanmail.net','지선,박다윤'),(284,'giga1422@naver.com','MARS 짱♡','이무진-신호등','하튜하튜♡\n이제 진짜 얼마 안 남았다..\nA403 넘 스릉한다 >_<\n홧티이잉!!!','http://k4a403.p.ssafy.io:8000/api/capsule/image/20210519202112Mars20210520051733.png','','',37.6336092,126.6985666,'2021-05-19 20:21:12','2021-05-20','우리집','giga1422@naver.com,qkrekdbs69@hanmail.net','지선,박다윤');
/*!40000 ALTER TABLE `capsule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `friend`
--

DROP TABLE IF EXISTS `friend`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `friend` (
  `no` int NOT NULL AUTO_INCREMENT,
  `myname` varchar(255) NOT NULL,
  `yourname` varchar(255) NOT NULL,
  `profileimage` varchar(400) DEFAULT NULL,
  `myid` varchar(255) NOT NULL,
  `yourid` varchar(255) NOT NULL,
  `profile_image` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`no`)
) ENGINE=InnoDB AUTO_INCREMENT=118 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `friend`
--

LOCK TABLES `friend` WRITE;
/*!40000 ALTER TABLE `friend` DISABLE KEYS */;
INSERT INTO `friend` VALUES (15,'박다윤','지선','https://p.kakaocdn.net/th/talkp/wmk0mer9bW/vnbGtx9BMCfNxEza2RWGpK/gw1jl3_640x640_s.jpg','qkrekdbs69@hanmail.net','giga1422@naver.com',NULL),(16,'박다윤','황윤호','https://p.kakaocdn.net/th/talkp/wlWGjy2L0j/54ZkKpiAcU3TDkgkKQsEuk/xidjrg_640x640_s.jpg','qkrekdbs69@hanmail.net','hossi0128@hanmail.net',NULL),(24,'지선','박다윤','https://p.kakaocdn.net/th/talkp/wmjJ47muIj/EG0uS3Nj02Bq5rAb8SPK21/2z77j1_640x640_s.jpg','giga1422@naver.com','qkrekdbs69@hanmail.net',NULL),(25,'지선','황윤호','https://p.kakaocdn.net/th/talkp/wlWGjy2L0j/54ZkKpiAcU3TDkgkKQsEuk/xidjrg_640x640_s.jpg','giga1422@naver.com','hossi0128@hanmail.net',NULL),(26,'지선','이도건','https://p.kakaocdn.net/th/talkp/wmjKzNcqiv/WSzrtmbO58ikWTRpgHkJh0/ukz1wz_640x640_s.jpg','giga1422@naver.com','ldkgang@kakao.com',NULL),(29,'황윤호','이도건','https://p.kakaocdn.net/th/talkp/wmjKzNcqiv/WSzrtmbO58ikWTRpgHkJh0/ukz1wz_640x640_s.jpg','hossi0128@hanmail.net','ldkgang@kakao.com',NULL),(30,'황윤호','지선','https://p.kakaocdn.net/th/talkp/wmk0mer9bW/vnbGtx9BMCfNxEza2RWGpK/gw1jl3_640x640_s.jpg','hossi0128@hanmail.net','giga1422@naver.com',NULL),(31,'황윤호','박다윤','https://p.kakaocdn.net/th/talkp/wmjJ47muIj/EG0uS3Nj02Bq5rAb8SPK21/2z77j1_640x640_s.jpg','hossi0128@hanmail.net','qkrekdbs69@hanmail.net',NULL),(107,'황윤호','충현','https://p.kakaocdn.net/th/talkp/wmdz4zbqRb/Dtcdmyhv3mbkNugTDqQ5CK/62cdga_640x640_s.jpg','hossi0128@hanmail.net','ybj3@naver.com',NULL),(108,'이도건','충현','https://p.kakaocdn.net/th/talkp/wmdz4zbqRb/Dtcdmyhv3mbkNugTDqQ5CK/62cdga_640x640_s.jpg','ldkgang@kakao.com','ybj3@naver.com',NULL),(109,'이도건','황윤호','https://p.kakaocdn.net/th/talkp/wlWGjy2L0j/54ZkKpiAcU3TDkgkKQsEuk/xidjrg_640x640_s.jpg','ldkgang@kakao.com','hossi0128@hanmail.net',NULL),(110,'이도건','박다윤','https://p.kakaocdn.net/th/talkp/wmjJ47muIj/EG0uS3Nj02Bq5rAb8SPK21/2z77j1_640x640_s.jpg','ldkgang@kakao.com','qkrekdbs69@hanmail.net',NULL),(111,'이도건','지선','https://p.kakaocdn.net/th/talkp/wmk0mer9bW/vnbGtx9BMCfNxEza2RWGpK/gw1jl3_640x640_s.jpg','ldkgang@kakao.com','giga1422@naver.com',NULL),(112,'충현','이도건','https://p.kakaocdn.net/th/talkp/wmjKzNcqiv/WSzrtmbO58ikWTRpgHkJh0/ukz1wz_640x640_s.jpg','ybj3@naver.com','ldkgang@kakao.com',NULL),(113,'충현','황윤호','https://p.kakaocdn.net/th/talkp/wlWGjy2L0j/54ZkKpiAcU3TDkgkKQsEuk/xidjrg_640x640_s.jpg','ybj3@naver.com','hossi0128@hanmail.net',NULL),(114,'충현','박다윤','https://p.kakaocdn.net/th/talkp/wmjJ47muIj/EG0uS3Nj02Bq5rAb8SPK21/2z77j1_640x640_s.jpg','ybj3@naver.com','qkrekdbs69@hanmail.net',NULL),(115,'충현','지선','https://p.kakaocdn.net/th/talkp/wmk0mer9bW/vnbGtx9BMCfNxEza2RWGpK/gw1jl3_640x640_s.jpg','ybj3@naver.com','giga1422@naver.com',NULL),(116,'지선','충현','https://p.kakaocdn.net/th/talkp/wmdz4zbqRb/Dtcdmyhv3mbkNugTDqQ5CK/62cdga_640x640_s.jpg','giga1422@naver.com','ybj3@naver.com',NULL),(117,'김광숙','지선','https://p.kakaocdn.net/th/talkp/wmk0mer9bW/vnbGtx9BMCfNxEza2RWGpK/gw1jl3_640x640_s.jpg','ksy3598@hanmail.net','giga1422@naver.com',NULL);
/*!40000 ALTER TABLE `friend` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `member`
--

DROP TABLE IF EXISTS `member`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `member` (
  `no` int NOT NULL AUTO_INCREMENT,
  `id` varchar(200) DEFAULT NULL,
  `name` varchar(200) DEFAULT NULL,
  `password` varchar(200) DEFAULT NULL,
  `profile_image` text,
  `android_token` text,
  PRIMARY KEY (`no`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `member`
--

LOCK TABLES `member` WRITE;
/*!40000 ALTER TABLE `member` DISABLE KEYS */;
INSERT INTO `member` VALUES (3,'ldkgang@kakao.com','이도건','�h���Oэ�p=��','https://p.kakaocdn.net/th/talkp/wmjKzNcqiv/WSzrtmbO58ikWTRpgHkJh0/ukz1wz_640x640_s.jpg','f1PC_-X6MNg:APA91bFbXFc9LTEtu07Vv5XaSZve-terl-WxuiaJJNBXbVWhxL6MECm4k3pDkAuq83rUvL4pg00TTw0QwRbkXFx1OcliqHvU1nr5iIlq8ZF98nf6IruBYbU5i47smU7OtlMR0nAxQQzx'),(6,'ybj3@naver.com','충현','912ujsad92qy','https://p.kakaocdn.net/th/talkp/wmdz4zbqRb/Dtcdmyhv3mbkNugTDqQ5CK/62cdga_640x640_s.jpg','dD_zPQahJ9Q:APA91bH1V-6QpKXLlxmGBpY2--o2XqDtjTPAXPoY3imOwct_GvboGSADcPfMQQX6hbqVKfqqXxbFHo0qAYuCxXUIMvCGfAIMVK0sY8Uf5UHzfrDJZFkK-kLDjrfNULszGCtSk_2jbtAd'),(7,'hossi0128@hanmail.net','황윤호','@w���F}�&7�J?','https://p.kakaocdn.net/th/talkp/wlWGjy2L0j/54ZkKpiAcU3TDkgkKQsEuk/xidjrg_640x640_s.jpg','dVqdo2ggHvA:APA91bHGIpSkrjiY5JshRW7qop67qNotp_04ygGoUigd_AXgFZEgB4l_PR_IoN1WjgabAjnWhkUJ-CQJfgofQVyB1T4sWfWDi1Q8DqAKXpU5g_88LwxbSmJocBFBu4cAenZ2FpoBxd3r'),(9,'qkrekdbs69@hanmail.net','박다윤','Y5��l���:�','https://p.kakaocdn.net/th/talkp/wmjJ47muIj/EG0uS3Nj02Bq5rAb8SPK21/2z77j1_640x640_s.jpg','fLumqUyeFz8:APA91bG8WPBTAjfX7of0fXjsD2Zyoa0Y-JozuGvn1yFzm7BLKg4mZokVc_g815SNWzseIpsFGloScvkL5O8OT_BhCfyAtubMRizPV31nHPMIG91wlz0RzlHpiYGcmiZQCLuTexahTObv'),(10,'giga1422@naver.com','지선','K=�u�v��=��','https://p.kakaocdn.net/th/talkp/wmk0mer9bW/vnbGtx9BMCfNxEza2RWGpK/gw1jl3_640x640_s.jpg','eM_TjMj3_ew:APA91bHUTbWlekFocVR0rRFxsiPjJhBwI7z7boCWD9tqAorg14wbhmZtjzDaMV8WM9cyUM7RkyeOCfOBiD310aCgfz04X0mVm6z5m4oss4ddlxFFB-1g1s72SHpL2Zh044xlJLQIytDd'),(11,'ksy3598@hanmail.net','김광숙','����tgWr��','https://p.kakaocdn.net/th/talkp/wmimG1eAks/JZPc3SMgB7By6rZdjBPlyk/nw0a1v_640x640_s.jpg','ez8c6JQvX2E:APA91bFSzJnh2GPvCmHjkKMwso_CTPKAq-n8WpRyrzj2GVM-SrChCoEsrYU7YQgFuK7p5SvjZF7r_HdCMXd-teOZLzD3cxgyS2aG7B5jYSxCkyIi4jbr5nnjCgl0muKZbNtrGAhrF53u');
/*!40000 ALTER TABLE `member` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-05-20  9:51:19
