-- MySQL dump 10.13  Distrib 5.1.54, for Win32 (ia32)
--
-- Host: localhost    Database: showcasedb
-- ------------------------------------------------------
-- Server version	5.1.54-community

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
-- Dumping data for table `accountmember`
--

LOCK TABLES `accountmember` WRITE;
/*!40000 ALTER TABLE `accountmember` DISABLE KEYS */;
INSERT INTO `accountmember` (`accountId`, `activationKey`, `createdOn`, `email`, `enabled`, `language`, `lastLogin`, `loginCount`, `password`, `passwordSalt`, `temporaryPassword`, `userName`, `member_id`) VALUES (1,NULL,'2009-10-04 19:43:55','faccadmin@showcase.com',0x01,'en','2011-02-02 23:28:38',1326,'52EE89BECBBC1754F19F8F9F1E1C7BC0B977AAF7','B90D97A8F1BBFBD2',0x00,'faccadmin',1);
/*!40000 ALTER TABLE `accountmember` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `accountmemberrole`
--

LOCK TABLES `accountmemberrole` WRITE;
/*!40000 ALTER TABLE `accountmemberrole` DISABLE KEYS */;
INSERT INTO `accountmemberrole` (`roleId`, `conditional`, `description`, `name`) VALUES (1,0x00,'Web User','webuser'),(2,0x00,'Administrator Role Group','admin'),(3,0x00,'NewsLetter Manager','newsmgr'),(4,0x00,'Club And Activities Manager','clubmgr'),(5,0x00,'Member Manager','membermgr'),(6,0x00,'Accounting','accounting'),(7,0x00,'Member','member'),(8,0x00,'Web Content Manager','webcontentmgr'),(9,0x00,'Board Member Role Group','boardmember'),(10,0x00,'Member Viewer','memberviewer'),(11,0x00,'Event Manager','eventmgr'),(12,0x00,NULL,'clubadm');
/*!40000 ALTER TABLE `accountmemberrole` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `accountmemberrolegroup`
--

LOCK TABLES `accountmemberrolegroup` WRITE;
/*!40000 ALTER TABLE `accountmemberrolegroup` DISABLE KEYS */;
INSERT INTO `accountmemberrolegroup` (`roleId`, `memberOf`) VALUES (2,1),(7,1),(9,1),(2,3),(9,3),(12,4),(2,5),(2,7),(9,7),(2,8),(9,8),(5,10),(9,10),(4,11),(2,12),(9,12);
/*!40000 ALTER TABLE `accountmemberrolegroup` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `accountmembership`
--

LOCK TABLES `accountmembership` WRITE;
/*!40000 ALTER TABLE `accountmembership` DISABLE KEYS */;
INSERT INTO `accountmembership` (`accountId`, `memberOf`) VALUES (1,2);
/*!40000 ALTER TABLE `accountmembership` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `assocmember`
--

LOCK TABLES `assocmember` WRITE;
/*!40000 ALTER TABLE `assocmember` DISABLE KEYS */;
INSERT INTO `assocmember` (`id`, `EMailValid`, `GMAPLat`, `GMAPLng`, `MKind`, `address`, `addressValid`, `aptOrSuite`, `assocName`, `cafeRencontre`, `city`, `country`, `dateInscription`, `dureeAdhesion`, `email`, `enfants`, `formulaire`, `freeMembership`, `guide`, `homePhone`, `inTheCitySince`, `lastPaymentDate`, `memberSince`, `memberUntil`, `membershipDues`, `membershipValid`, `nation1`, `nation2`, `nouveauMember`, `paymentInfo`, `portable`, `potluck`, `prenoms`, `state`, `useEMail`, `useUSPS`, `versionNum`, `zip`, `picture_imageId`) VALUES (1,0x00,NULL,NULL,0,'',0x00,'','FACCAdmin',0x01,'Dallas','','','','faccadmin@showcase.com','','',0x00,0x01,'000-000-0000',NULL,'2011-07-01 00:00:00','2009-10-02 00:00:00','2011-06-30 00:00:00',0,0x01,73,0,'','','',0x01,'','TX',0x00,0x00,87,'75021',NULL);
/*!40000 ALTER TABLE `assocmember` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2011-02-02 23:32:44
