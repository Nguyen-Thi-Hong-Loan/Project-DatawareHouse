-- MySQL dump 10.13  Distrib 8.0.20, for Win64 (x86_64)
--
-- Host: localhost    Database: controldb
-- ------------------------------------------------------
-- Server version	8.0.20

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
-- Table structure for table `config`
--

DROP TABLE IF EXISTS `config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `config` (
  `idConfig` int NOT NULL AUTO_INCREMENT,
  `serverSou` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `port` int NOT NULL,
  `userSou` varchar(225) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `passSou` varchar(225) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `directorySou` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `fieldName` varchar(450) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `delimeterSou` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `formatSou` varchar(450) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `serverDes` varchar(450) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `DBNameDes` varchar(450) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `userDes` varchar(450) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `passDes` varchar(450) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  PRIMARY KEY (`idConfig`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `config`
--

LOCK TABLES `config` WRITE;
/*!40000 ALTER TABLE `config` DISABLE KEYS */;
INSERT INTO `config` VALUES (1,'drive.ecepvn.org',2227,'guest_access','123456','E:\\Tai_Lieu\\HK2-----3\\DatawareHouse\\FILE','STT|id_sinh_vien|ho_lot|ten|ngaysinh|ma_lop|ten|lop|sdt|email|que_quan|ghi_chu','|','sinhvien_chieu*.*','jdbc:mysql://localhost/information','information','root','1234567890@'),(2,'drive.ecepvn.org',2227,'guest_access','123456','E:\\Tai_Lieu\\HK2-----3\\DatawareHouse\\FILE','STT|id_sinh_vien|ho_lot|ten|ngaysinh|ma_lop|ten|lop|sdt|email|que_quan|ghi_chu','|','sinhvien_sang*.*','jdbc:mysql://localhost/information','information','root','1234567890@');
/*!40000 ALTER TABLE `config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `log`
--

DROP TABLE IF EXISTS `log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `log` (
  `idlog` int NOT NULL AUTO_INCREMENT,
  `idConfig` int NOT NULL,
  `state` varchar(45) DEFAULT NULL,
  `numColumn` varchar(45) DEFAULT NULL,
  `fileName` varchar(45) DEFAULT NULL,
  `dateUserInsert` date DEFAULT NULL,
  PRIMARY KEY (`idlog`),
  KEY `idConfig_idx` (`idConfig`),
  CONSTRAINT `idConfig` FOREIGN KEY (`idConfig`) REFERENCES `config` (`idConfig`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `log`
--

LOCK TABLES `log` WRITE;
/*!40000 ALTER TABLE `log` DISABLE KEYS */;
INSERT INTO `log` VALUES (1,1,'ER','0','E:\\Tai_Lieu\\HK2-----3\\DatawareHouse\\FILE','2020-06-21'),(2,2,'ER','0','E:\\Tai_Lieu\\HK2-----3\\DatawareHouse\\FILE','2020-06-21');
/*!40000 ALTER TABLE `log` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-06-21 22:50:36
