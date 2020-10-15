-- MySQL dump 10.13  Distrib 5.7.31, for Linux (x86_64)
--
-- Host: localhost    Database: charity
-- ------------------------------------------------------
-- Server version	5.7.31-0ubuntu0.18.04.1

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
-- Dumping data for table `institutions`
--

LOCK TABLES `institutions` WRITE;
/*!40000 ALTER TABLE `institutions` DISABLE KEYS */;
INSERT INTO `institutions` VALUES (1,'we help those in need','We help!'),(2,'We help all children...','Kids of the world'),(3,'We help all animals in need.','Four paws'),(4,'We help lonely elderly people.','Gray hair');
/*!40000 ALTER TABLE `institutions` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

--
-- Dumping data for table `categories`
--

LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
INSERT INTO `categories` VALUES (1,'books'),(2,'second hand clothes'),(3,'toys'),(4,'household appliances\n');
/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (1,'ROLE_USER'),(2,'ROLE_ADMIN'),(3,'ROLE_SUPER_ADMIN');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'dominika.czycz@gmail.com',_binary '','Jan','Kowalski','$2a$10$j6tFTbmYiwTY4sMkDCyNF.Ay916yHuQGMFiDfZIKfeesYxrshhJeW'),(2,'dominika.czycz+809809@gmail.com',_binary '','Mariola','Futerko','$2a$10$eusH3Rp47t5MIRzzmuvWfOdfw2aO/baXAKHMJenHfvKJaIyM91GjC'),(3,'dominika.czycz+2@gmail.com',_binary '','Jane','Helpful','$2a$10$Nxc5EgUE8qk9GfK8Gz4jEuKKnbk3EOcYAbflIEZBG9XHaTPd94aeC'),(4,'dominika.czycz+3@gmail.com',_binary '','Jim','Generous','$2a$10$yWKHKF3TsCuTzFVybW.rm.xHCAe3BOmfyUFkc4pnzuMYf3DJXIemm'),(5,'dominika.czycz+777@gmail.com',_binary '','Kate','Niceperson','$2a$10$C2/fHZBjDrIVP6esqVZJOu/sesDGn1KSNwsOyNrV/o0qyfQWbsA6i'),(6,'dominika.czycz+44@gmail.com',_binary '','Helen','Smith','$2a$10$s1PXJvEAG170w0jtncHmwOiBQhBS2kpDlHkKzHr9xrVvIFXxqRkNO');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `users_roles`
--

LOCK TABLES `users_roles` WRITE;
/*!40000 ALTER TABLE `users_roles` DISABLE KEYS */;
INSERT INTO `users_roles` VALUES (5,1),(6,1),(1,2),(2,2),(3,2),(4,2);
/*!40000 ALTER TABLE `users_roles` ENABLE KEYS */;
UNLOCK TABLES;
--
-- Dumping data for table `donations`
--


--
-- Dumping data for table `donations_categories`
--

LOCK TABLES `donations_categories` WRITE;
/*!40000 ALTER TABLE `donations_categories` DISABLE KEYS */;
/*!40000 ALTER TABLE `donations_categories` ENABLE KEYS */;
UNLOCK TABLES;


/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-10-08 15:45:02
