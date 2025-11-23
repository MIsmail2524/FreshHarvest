-- MySQL dump 10.13  Distrib 8.0.43, for Linux (x86_64)
--
-- Host: localhost    Database: freshharvest
-- ------------------------------------------------------
-- Server version	8.0.43-0ubuntu0.24.04.2

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `batch_processing`
--

DROP TABLE IF EXISTS `batch_processing`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `batch_processing` (
  `process_id` int NOT NULL AUTO_INCREMENT,
  `crop_id` int DEFAULT NULL,
  `process_name` varchar(100) NOT NULL,
  `process_status` enum('Pending','In Progress','Completed','Failed') DEFAULT 'Pending',
  `start_time` timestamp NULL DEFAULT NULL,
  `end_time` timestamp NULL DEFAULT NULL,
  `operator_name` varchar(100) DEFAULT NULL,
  `notes` text,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `grade` varchar(50) DEFAULT 'Not Graded',
  `processed_date` timestamp NULL DEFAULT NULL,
  `estimated_cost` decimal(10,2) DEFAULT '0.00',
  `labor_hours` decimal(5,2) DEFAULT '0.00',
  `material_cost` decimal(10,2) DEFAULT '0.00',
  PRIMARY KEY (`process_id`),
  KEY `crop_id` (`crop_id`),
  CONSTRAINT `batch_processing_ibfk_1` FOREIGN KEY (`crop_id`) REFERENCES `crops` (`crop_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `batch_processing`
--

LOCK TABLES `batch_processing` WRITE;
/*!40000 ALTER TABLE `batch_processing` DISABLE KEYS */;
INSERT INTO `batch_processing` VALUES (3,3,'Quality Grading','Completed',NULL,'2025-11-22 18:38:25',NULL,'Awaiting initial inspection.','2025-11-22 18:35:29','Grade A','2025-11-22 18:38:25',661.11,7.40,630.51),(5,5,'Washing Process','Failed','2025-01-15 06:00:00','2025-01-15 06:30:00','Sarah Wilson','Equipment malfunction. Process halted for maintenance.','2025-11-22 18:43:06','Rejected','2025-01-15 06:30:00',798.48,9.30,525.59),(6,6,'Sorting Process','Completed','2025-01-15 08:00:00','2025-11-23 07:57:35','Robert Brown','Manual sorting in progress. Good quality produce.','2025-11-22 18:43:14','Premium','2025-11-23 07:57:35',1027.94,7.50,623.48),(7,7,'Quality Grading','Completed','2025-01-14 05:00:00','2025-01-14 07:00:00','Emily Davis','Excellent quality. Met all premium standards.','2025-11-22 18:43:29','Premium','2025-01-14 07:00:00',676.35,4.70,287.63),(8,8,'Packaging Process','In Progress',NULL,NULL,NULL,'Scheduled for tomorrow morning.','2025-11-22 18:43:38','Grade A',NULL,1353.75,7.90,276.71),(9,NULL,'ddd','Pending','2025-11-23 09:32:02',NULL,'ss','','2025-11-23 09:32:02','Not Graded',NULL,0.00,0.00,0.00);
/*!40000 ALTER TABLE `batch_processing` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `crops`
--

DROP TABLE IF EXISTS `crops`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `crops` (
  `crop_id` int NOT NULL AUTO_INCREMENT,
  `crop_name` varchar(100) NOT NULL,
  `crop_type` varchar(50) DEFAULT NULL,
  `planting_date` date NOT NULL,
  `harvest_date` date NOT NULL,
  `status` enum('Planted','Growing','Ready for Harvest','Harvested','Sold') DEFAULT 'Planted',
  `quantity` decimal(10,2) NOT NULL,
  `field_name` varchar(100) DEFAULT NULL,
  `farmer_id` int NOT NULL,
  `created_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `grade` varchar(20) DEFAULT 'Not Graded',
  `locationf` varchar(100) DEFAULT NULL,
  `storage_conditions` varchar(200) DEFAULT NULL,
  `package_type` varchar(50) DEFAULT 'Bulk',
  `processed_date` timestamp NULL DEFAULT NULL,
  `farm_name` varchar(100) NOT NULL DEFAULT 'Unknown Farm',
  `received_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `unit` varchar(20) DEFAULT 'kg',
  PRIMARY KEY (`crop_id`),
  KEY `farmer_id` (`farmer_id`),
  CONSTRAINT `crops_ibfk_1` FOREIGN KEY (`farmer_id`) REFERENCES `farmers` (`farmer_id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `crops`
--

LOCK TABLES `crops` WRITE;
/*!40000 ALTER TABLE `crops` DISABLE KEYS */;
INSERT INTO `crops` VALUES (3,'Kinnow','Fruits','2023-02-01','2024-01-15','Harvested',2500.00,'Orchard A',1,'2025-11-16 06:23:17','Not Graded',NULL,NULL,'Bulk',NULL,'Unknown Farm','2025-11-22 16:56:18','kg'),(4,'Mango (Sindhri)','Fruits','2023-03-01','2024-06-01','Ready for Harvest',1800.00,'Mango Grove',2,'2025-11-16 06:23:17','Not Graded',NULL,NULL,'Bulk',NULL,'Unknown Farm','2025-11-22 16:56:18','kg'),(5,'Sugarcane','Cash Crop','2024-02-15','2024-12-15','Growing',8000.00,'Sugarcane Field',2,'2025-11-16 06:23:17','Not Graded',NULL,NULL,'Bulk',NULL,'Unknown Farm','2025-11-22 16:56:18','kg'),(6,'Cotton','Cash Crop','2024-05-01','2024-10-01','Planted',4000.00,'Cotton Field',3,'2025-11-16 06:23:17','Not Graded',NULL,NULL,'Bulk',NULL,'Unknown Farm','2025-11-22 16:56:18','kg'),(7,'Maize','Grains','2024-07-01','2024-10-01','Growing',2500.00,'Maize Field',3,'2025-11-16 06:23:17','Not Graded',NULL,NULL,'Bulk',NULL,'Unknown Farm','2025-11-22 16:56:18','kg'),(8,'Potatoes','Vegetables','2024-08-15','2024-12-01','Growing',1500.00,'Vegetable Plot A',4,'2025-11-16 06:23:17','Not Graded',NULL,NULL,'Bulk',NULL,'Unknown Farm','2025-11-22 16:56:18','kg'),(9,'Onions','Vegetables','2024-09-01','2025-01-15','Planted',1200.00,'Vegetable Plot B',4,'2025-11-16 06:23:17','Not Graded',NULL,NULL,'Bulk',NULL,'Unknown Farm','2025-11-22 16:56:18','kg'),(10,'Tomatoes','Vegetables','2024-07-20','2024-10-20','Growing',800.00,'Greenhouse A',5,'2025-11-16 06:23:17','Not Graded',NULL,NULL,'Bulk',NULL,'Unknown Farm','2025-11-22 16:56:18','kg'),(11,'Okra','Vegetables','2024-06-15','2024-09-15','Ready for Harvest',600.00,'Vegetable Plot C',5,'2025-11-16 06:23:17','Not Graded',NULL,NULL,'Bulk',NULL,'Unknown Farm','2025-11-22 16:56:18','kg'),(12,'orange','fruit','2025-11-16','2026-02-14','Planted',0.00,'Field A',1,'2025-11-16 08:10:13','Not Graded',NULL,NULL,'Bulk',NULL,'Unknown Farm','2025-11-22 16:56:18','kg'),(13,'orange','fruit','2025-11-18','2026-02-16','Planted',0.00,'Field A',1,'2025-11-18 09:28:54','Not Graded',NULL,NULL,'Bulk',NULL,'Unknown Farm','2025-11-22 16:56:18','kg'),(25,'added','newone','2025-11-23','2026-02-21','Planted',0.00,'Field A',1,'2025-11-23 04:37:36','Not Graded',NULL,NULL,'Bulk',NULL,'Unknown Farm','2025-11-23 04:37:36','kg'),(26,'hello','hy','2025-11-23','2026-02-21','Planted',0.00,'Field A',1,'2025-11-23 09:23:46','Not Graded',NULL,NULL,'Bulk',NULL,'Unknown Farm','2025-11-23 09:23:46','kg'),(27,'hello','hy','2025-11-23','2026-02-21','Planted',0.00,'Field A',1,'2025-11-23 09:28:01','Not Graded',NULL,NULL,'Bulk',NULL,'Unknown Farm','2025-11-23 09:28:01','kg');
/*!40000 ALTER TABLE `crops` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customers`
--

DROP TABLE IF EXISTS `customers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customers` (
  `customer_id` int NOT NULL AUTO_INCREMENT,
  `first_name` varchar(50) NOT NULL,
  `last_name` varchar(50) NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `address` text,
  `registration_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `is_active` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`customer_id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customers`
--

LOCK TABLES `customers` WRITE;
/*!40000 ALTER TABLE `customers` DISABLE KEYS */;
INSERT INTO `customers` VALUES (1,'Zainab','Shah','zainab.shah@email.com','+92-300-1112233','House 45, Street 8, DHA Phase 5, Karachi','2025-11-16 06:22:52',1),(2,'Omar','Sheikh','omar.sheikh@email.com','+92-301-2223344','Flat 302, Al-Habib Apartments, Gulberg 3, Lahore','2025-11-16 06:22:52',1),(3,'Sara','Iqbal','sara.iqbal@email.com','+92-302-3334455','House 23, Sector F-10/2, Islamabad','2025-11-16 06:22:52',1),(4,'Haris','Raza','haris.raza@email.com','+92-303-4445566','Shop 15, Commercial Area, Bahria Town, Rawalpindi','2025-11-16 06:22:52',1),(5,'Amina','Khalid','amina.khalid@email.com','+92-304-5556677','House 78, GULSHAN-E-IQBAL, Block 7, Karachi','2025-11-16 06:22:52',1),(6,'Kamran','Abbasi','kamran.abbasi@email.com','+92-305-6667788','Flat 501, Sky Gardens, Clifton, Karachi','2025-11-16 06:22:52',1),(7,'Nadia','Mirza','nadia.mirza@email.com','+92-306-7778899','House 34, Model Town, Multan','2025-11-16 06:22:52',1),(8,'Faisal','Qureshi','faisal.qureshi@email.com','+92-307-8889900','Shop 22, Saddar Bazar, Peshawar','2025-11-16 06:22:52',1),(9,'Ali','Khan','ali.khan01@gmail.com','03001234567','G-10/2, Islamabad','2024-01-09 19:00:00',1),(10,'Ayesha','Malik','ayesha.malik92@yahoo.com','03115550022','DHA Phase 6, Lahore','2024-02-13 19:00:00',1),(11,'Bilal','Ahmed','bilal.ahmed.pk@gmail.com','03458881234','North Nazimabad Block H, Karachi','2024-02-29 19:00:00',1),(12,'Fatima','Zafar','fatima.zafar12@hotmail.com','03214445566','Satellite Town, Rawalpindi','2024-04-19 19:00:00',1),(13,'Hamza','Yousaf','hamza.yousaf88@gmail.com','03005553321','Gulshan-e-Iqbal Block 4, Karachi','2024-05-17 19:00:00',1),(14,'Mehwish','Shah','mehwish.shah.pk@yahoo.com','03337779944','Wapda Town, Lahore','2024-06-24 19:00:00',1),(15,'Usman','Qureshi','usman.q92@gmail.com','03117770083','Bahria Town Phase 8, Rawalpindi','2024-07-04 19:00:00',1),(16,'Zainab','Rehman','zainab.rehman11@gmail.com','03451231231','F-11 Markaz, Islamabad','2024-08-08 19:00:00',1),(17,'Saad','Farooq','saad.farooq33@gmail.com','03028886654','Gulberg III, Lahore','2024-09-11 19:00:00',1),(18,'Hira','Kamal','hira.kamal09@gmail.com','03216660077','Clifton Block 5, Karachi','2024-09-30 19:00:00',1);
/*!40000 ALTER TABLE `customers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `expenses`
--

DROP TABLE IF EXISTS `expenses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `expenses` (
  `expense_id` int NOT NULL AUTO_INCREMENT,
  `farmer_id` int NOT NULL,
  `expense_date` date NOT NULL,
  `category` varchar(50) NOT NULL,
  `description` text NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `crop_id` int DEFAULT NULL,
  `created_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`expense_id`),
  KEY `farmer_id` (`farmer_id`),
  KEY `crop_id` (`crop_id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `expenses`
--

LOCK TABLES `expenses` WRITE;
/*!40000 ALTER TABLE `expenses` DISABLE KEYS */;
INSERT INTO `expenses` VALUES (1,1,'2024-11-10','Seeds','Wheat seeds purchase from Lahore Market',45000.00,1,'2025-11-16 06:23:23'),(2,1,'2024-11-12','Labor','Field preparation labor - 10 workers',25000.00,1,'2025-11-16 06:23:23'),(3,1,'2024-11-15','Fertilizers','DAP and Urea fertilizers',35000.00,1,'2025-11-16 06:23:23'),(4,1,'2024-11-20','Irrigation','Tube well electricity and maintenance',15000.00,NULL,'2025-11-16 06:23:23'),(5,2,'2024-06-05','Seeds','Mango plant maintenance and care',20000.00,4,'2025-11-16 06:23:23'),(6,2,'2024-06-15','Labor','Harvesting labor for mango crop',30000.00,4,'2025-11-16 06:23:23'),(7,2,'2024-02-20','Pesticides','Organic pesticides for sugarcane',18000.00,5,'2025-11-16 06:23:23'),(8,3,'2024-05-05','Seeds','Cotton seeds purchase',40000.00,6,'2025-11-16 06:23:23'),(9,3,'2024-05-20','Labor','Cotton field plantation labor',22000.00,6,'2025-11-16 06:23:23'),(10,4,'2024-08-20','Seeds','Potato seeds from local market',15000.00,8,'2025-11-16 06:23:23'),(11,4,'2024-08-25','Fertilizers','Vegetable specific fertilizers',12000.00,8,'2025-11-16 06:23:23'),(12,5,'2024-07-25','Equipment','Greenhouse maintenance and repair',8000.00,10,'2025-11-16 06:23:23'),(13,5,'2024-06-20','Labor','Okra plantation and care',10000.00,11,'2025-11-16 06:23:23');
/*!40000 ALTER TABLE `expenses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `farmers`
--

DROP TABLE IF EXISTS `farmers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `farmers` (
  `farmer_id` int NOT NULL AUTO_INCREMENT,
  `first_name` varchar(50) NOT NULL,
  `last_name` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `farm_name` varchar(100) DEFAULT NULL,
  `address` text,
  `registration_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `is_active` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`farmer_id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `farmers`
--

LOCK TABLES `farmers` WRITE;
/*!40000 ALTER TABLE `farmers` DISABLE KEYS */;
INSERT INTO `farmers` VALUES (1,'Ahmed','Khan','ahmed.khan@freshfarm.com','+92-300-1234567','Green Valley Farm','123 Agricultural Road, Model Town, Lahore','2025-11-16 06:22:04',1),(2,'Fatima','Ali','fatima.ali@organicfarm.com','+92-301-2345678','Organic Harvest Pakistan','456 Green Lane, Faisal Town, Karachi','2025-11-16 06:22:04',1),(3,'Bilal','Hussain','bilal.hussain@freshproduce.com','+92-302-3456789','Punjab Fresh Farms','789 Farm Street, Gulberg, Islamabad','2025-11-16 06:22:04',1),(4,'Ayesha','Raza','ayesha.raza@farmfresh.com','+92-303-4567890','Sindh Organic Farms','321 Orchard Road, Clifton, Karachi','2025-11-16 06:22:04',1),(5,'Usman','Malik','usman.malik@harvest.com','+92-304-5678901','KPK Fresh Produce','654 Garden Avenue, University Road, Peshawar','2025-11-16 06:22:04',1);
/*!40000 ALTER TABLE `farmers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `inventory_locations`
--

DROP TABLE IF EXISTS `inventory_locations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inventory_locations` (
  `location_id` int NOT NULL AUTO_INCREMENT,
  `location_name` varchar(100) NOT NULL,
  `capacity` int NOT NULL,
  `current_quantity` int DEFAULT '0',
  `temperature_range` varchar(50) DEFAULT NULL,
  `humidity_range` varchar(50) DEFAULT NULL,
  `is_available` tinyint(1) DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`location_id`),
  UNIQUE KEY `location_name` (`location_name`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inventory_locations`
--

LOCK TABLES `inventory_locations` WRITE;
/*!40000 ALTER TABLE `inventory_locations` DISABLE KEYS */;
INSERT INTO `inventory_locations` VALUES (1,'Cold Storage A1',1000,0,'1-4°C','85-90%',1,'2025-11-22 16:23:58'),(2,'Cold Storage A2',800,0,'0-2°C','90-95%',1,'2025-11-22 16:23:58'),(3,'Cold Storage B1',1200,0,'3-5°C','80-85%',1,'2025-11-22 16:23:58'),(4,'Cold Storage B2',600,0,'2-4°C','85-90%',1,'2025-11-22 16:23:58'),(5,'Ambient Storage C1',2000,0,'15-18°C','65-70%',1,'2025-11-22 16:23:58'),(6,'Ambient Storage C2',1500,0,'16-20°C','60-65%',1,'2025-11-22 16:23:58'),(7,'Freezer Storage D1',500,0,'-18°C','N/A',1,'2025-11-22 16:23:58'),(8,'Processing Area E1',300,0,'18-22°C','50-60%',1,'2025-11-22 16:23:58'),(9,'Cold Storage A',1000,750,'0-4°C','85-90%',1,'2025-11-23 07:31:48'),(10,'Cold Storage B',800,450,'0-4°C','85-90%',1,'2025-11-23 07:31:48'),(11,'Dry Storage',1500,1200,'15-20°C','60-65%',1,'2025-11-23 07:31:48'),(12,'Processing Area',500,180,'18-22°C','70-75%',1,'2025-11-23 07:31:48'),(13,'Quality Control',300,75,'18-22°C','65-70%',1,'2025-11-23 07:31:48');
/*!40000 ALTER TABLE `inventory_locations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `inventory_movements`
--

DROP TABLE IF EXISTS `inventory_movements`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inventory_movements` (
  `movement_id` int NOT NULL AUTO_INCREMENT,
  `product_id` int NOT NULL,
  `farmer_id` int NOT NULL,
  `movement_type` enum('IN','OUT','ADJUST') NOT NULL,
  `quantity` decimal(10,2) NOT NULL,
  `previous_stock` decimal(10,2) NOT NULL,
  `new_stock` decimal(10,2) NOT NULL,
  `reason` varchar(100) NOT NULL,
  `reference_id` int DEFAULT NULL COMMENT 'Order ID or other reference',
  `movement_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `notes` text,
  PRIMARY KEY (`movement_id`),
  KEY `product_id` (`product_id`),
  KEY `farmer_id` (`farmer_id`),
  CONSTRAINT `inventory_movements_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`),
  CONSTRAINT `inventory_movements_ibfk_2` FOREIGN KEY (`farmer_id`) REFERENCES `farmers` (`farmer_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inventory_movements`
--

LOCK TABLES `inventory_movements` WRITE;
/*!40000 ALTER TABLE `inventory_movements` DISABLE KEYS */;
INSERT INTO `inventory_movements` VALUES (1,1,1,'OUT',10.00,260.50,250.50,'Order PK-ORD-001',1,'2025-11-16 07:00:47',NULL),(2,3,1,'OUT',5.00,505.00,500.00,'Order PK-ORD-001',1,'2025-11-16 07:00:47',NULL),(3,4,1,'OUT',8.00,308.75,300.75,'Order PK-ORD-001',1,'2025-11-16 07:00:47',NULL),(4,5,1,'OUT',3.00,203.00,200.00,'Order PK-ORD-001',1,'2025-11-16 07:00:47',NULL),(5,2,1,'OUT',15.00,165.25,150.25,'Order PK-ORD-002',2,'2025-11-16 07:00:47',NULL),(6,1,1,'OUT',8.00,250.50,242.50,'Order PK-ORD-002',2,'2025-11-16 07:00:47',NULL),(7,1,1,'IN',50.00,242.50,292.50,'New harvest',NULL,'2025-11-16 07:00:47',NULL),(8,2,1,'IN',30.00,150.25,180.25,'New harvest',NULL,'2025-11-16 07:00:47',NULL),(9,3,1,'IN',100.00,500.00,600.00,'Rice milling complete',NULL,'2025-11-16 07:00:47',NULL),(10,4,1,'IN',40.00,300.75,340.75,'Potato harvest',NULL,'2025-11-16 07:00:47',NULL);
/*!40000 ALTER TABLE `inventory_movements` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_items`
--

DROP TABLE IF EXISTS `order_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_items` (
  `order_item_id` int NOT NULL AUTO_INCREMENT,
  `order_id` int NOT NULL,
  `product_id` int NOT NULL,
  `quantity` decimal(10,2) NOT NULL,
  `unit_price` decimal(10,2) NOT NULL,
  `total_price` decimal(10,2) NOT NULL,
  PRIMARY KEY (`order_item_id`),
  KEY `order_id` (`order_id`),
  KEY `product_id` (`product_id`),
  CONSTRAINT `order_items_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`) ON DELETE CASCADE,
  CONSTRAINT `order_items_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_items`
--

LOCK TABLES `order_items` WRITE;
/*!40000 ALTER TABLE `order_items` DISABLE KEYS */;
INSERT INTO `order_items` VALUES (1,1,1,10.00,120.00,1200.00),(2,1,3,5.00,180.00,900.00),(3,1,4,8.00,60.00,480.00),(4,1,5,3.00,80.00,240.00),(5,2,2,15.00,200.00,3000.00),(6,2,1,8.00,120.00,960.00),(7,2,3,2.00,180.00,360.00),(8,3,6,20.00,100.00,2000.00),(9,3,7,10.00,90.00,900.00),(10,3,8,2.00,450.00,900.00),(11,4,9,12.00,120.00,1440.00),(12,4,10,2.00,180.00,360.00),(13,5,11,8.00,220.00,1760.00),(14,5,12,5.00,160.00,800.00),(15,6,9,25.00,120.00,3000.00),(16,6,10,5.00,180.00,900.00),(17,7,11,7.00,220.00,1540.00),(18,7,12,4.00,160.00,640.00),(19,8,13,8.00,140.00,1120.00),(20,8,14,4.00,90.00,360.00),(21,10,6,1.00,100.00,100.00),(22,11,1,4.00,120.00,480.00),(23,12,5,2.00,80.00,160.00),(24,18,4,6.00,60.00,360.00),(25,19,3,5.00,180.00,900.00);
/*!40000 ALTER TABLE `order_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_sequence`
--

DROP TABLE IF EXISTS `order_sequence`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_sequence` (
  `sequence_name` varchar(50) NOT NULL,
  `next_value` int DEFAULT '1',
  PRIMARY KEY (`sequence_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_sequence`
--

LOCK TABLES `order_sequence` WRITE;
/*!40000 ALTER TABLE `order_sequence` DISABLE KEYS */;
INSERT INTO `order_sequence` VALUES ('order_number',1);
/*!40000 ALTER TABLE `order_sequence` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `order_id` int NOT NULL AUTO_INCREMENT,
  `order_number` varchar(20) DEFAULT NULL,
  `customer_id` int NOT NULL,
  `farmer_id` int NOT NULL DEFAULT '1',
  `order_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `delivery_date` date DEFAULT NULL,
  `total_amount` decimal(10,2) NOT NULL,
  `status` enum('Pending','Confirmed','Shipped','Delivered','Cancelled') DEFAULT 'Pending',
  `notes` text,
  `delivery_type` enum('PICKUP','DELIVERY') NOT NULL DEFAULT 'PICKUP',
  `delivery_address` varchar(255) DEFAULT NULL,
  `payment_method` varchar(50) DEFAULT 'Cash',
  `special_instructions` varchar(50) DEFAULT 'nill',
  `amount_paid` decimal(10,2) DEFAULT '0.00',
  `payment_status` varchar(20) DEFAULT 'PENDING',
  PRIMARY KEY (`order_id`),
  UNIQUE KEY `order_number` (`order_number`),
  KEY `customer_id` (`customer_id`),
  KEY `farmer_id` (`farmer_id`),
  CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`),
  CONSTRAINT `orders_ibfk_2` FOREIGN KEY (`farmer_id`) REFERENCES `farmers` (`farmer_id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (1,'PK-ORD-001',3,1,'2024-01-15 05:30:00','2024-01-17',2850.00,'Delivered','Weekly grocery order','PICKUP','House #25, Street 12, Islamabad','Credit','nill',0.00,'PENDING'),(2,'PK-ORD-002',3,1,'2024-02-10 09:45:00','2024-02-12',4200.00,'Delivered','Restaurant supply','PICKUP',NULL,'Cash','nill',0.00,'PENDING'),(3,'PK-ORD-003',3,1,'2025-11-16 06:23:01',NULL,3150.00,'Cancelled','Office cafeteria','PICKUP',NULL,'Credit','nill',0.00,'PENDING'),(4,'PK-ORD-004',3,1,'2025-11-16 06:23:01',NULL,1800.00,'Confirmed','Small retail store','PICKUP',NULL,'Cash','nill',0.00,'PENDING'),(5,'PK-ORD-005',3,1,'2025-11-16 06:23:01',NULL,2450.00,'Cancelled','Home delivery','PICKUP',NULL,'Credit','nill',0.00,'PENDING'),(6,'PK-ORD-006',3,1,'2025-11-16 06:23:01',NULL,3900.00,'Delivered','Bulk order for event','PICKUP',NULL,'Cash','nill',0.00,'PENDING'),(7,'PK-ORD-007',3,1,'2025-11-16 06:23:01',NULL,2200.00,'Shipped','Regular monthly order','PICKUP',NULL,'Credit','nill',0.00,'PENDING'),(8,'PK-ORD-008',3,1,'2025-11-16 06:23:01',NULL,1500.00,'Confirmed','Local store supply','PICKUP',NULL,'Cash','nill',0.00,'PENDING'),(10,NULL,3,1,'2025-11-21 11:56:58',NULL,100.00,'Confirmed',NULL,'PICKUP',NULL,'Cash','nill',100.00,'PAID'),(11,NULL,3,1,'2025-11-21 15:06:30',NULL,480.00,'Confirmed',NULL,'PICKUP',NULL,'Cash','nill',480.00,'PAID'),(12,NULL,3,1,'2025-11-22 03:54:08',NULL,160.00,'Pending',NULL,'PICKUP',NULL,'Credit','nill',0.00,'PENDING'),(13,'ORD000001-240115-001',1,1,'2024-01-20 05:30:00',NULL,2500.00,'Confirmed','Fresh vegetables order','PICKUP','House 123, Street 5, DHA Phase 1, Karachi','Cash','nill',0.00,'PENDING'),(14,'ORD000001-240120-001',2,1,'2024-01-25 09:20:00',NULL,1800.50,'Pending','Fruits package','PICKUP','Flat 45, Block B, Gulberg III, Lahore','Cash','nill',0.00,'PENDING'),(16,'ORD000001-240205-001',3,1,'2024-02-10 06:45:00',NULL,3200.75,'Confirmed','Bulk order for restaurant','PICKUP','Shop 12, Commercial Area, F-7 Markaz, Islamabad','Cash','nill',3200.75,'PAID'),(17,'ORD000001-240220-001',6,1,'2024-02-25 08:10:00',NULL,1900.00,'Pending','Family order','DELIVERY','House 34, Street 8, PECHS, Karachi','Cash','nill',0.00,'PENDING'),(18,NULL,1,1,'2025-11-23 05:15:46',NULL,360.00,'Pending',NULL,'PICKUP',NULL,'Cash','nill',0.00,'PENDING'),(19,NULL,1,1,'2025-11-23 09:45:55',NULL,900.00,'Pending',NULL,'PICKUP',NULL,'Cash','nill',0.00,'PENDING');
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payments`
--

DROP TABLE IF EXISTS `payments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payments` (
  `payment_id` int NOT NULL AUTO_INCREMENT,
  `order_id` int NOT NULL,
  `customer_id` int NOT NULL,
  `farmer_id` int NOT NULL DEFAULT '1',
  `payment_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `amount` decimal(10,2) NOT NULL,
  `payment_method` enum('Cash','Bank Transfer','JazzCash','EasyPaisa','Cheque') DEFAULT 'Cash',
  `payment_status` enum('Pending','Completed','Failed','Refunded') DEFAULT 'Completed',
  `transaction_id` varchar(100) DEFAULT NULL,
  `notes` text,
  PRIMARY KEY (`payment_id`),
  KEY `order_id` (`order_id`),
  KEY `customer_id` (`customer_id`),
  KEY `farmer_id` (`farmer_id`),
  CONSTRAINT `payments_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`),
  CONSTRAINT `payments_ibfk_2` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`),
  CONSTRAINT `payments_ibfk_3` FOREIGN KEY (`farmer_id`) REFERENCES `farmers` (`farmer_id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payments`
--

LOCK TABLES `payments` WRITE;
/*!40000 ALTER TABLE `payments` DISABLE KEYS */;
INSERT INTO `payments` VALUES (1,1,1,1,'2025-11-16 06:55:10',2850.00,'JazzCash','Completed','JC20240001',NULL),(2,2,2,1,'2025-11-16 06:55:10',4200.00,'Bank Transfer','Completed','BT20240002',NULL),(3,3,3,2,'2025-11-16 06:55:10',3150.00,'Cash','Completed','CASH20240003',NULL),(4,4,4,2,'2025-11-16 06:55:10',1800.00,'EasyPaisa','Pending','EP20240004',NULL),(5,5,5,3,'2025-11-16 06:55:10',2450.00,'Cash','Completed','CASH20240005',NULL),(6,6,6,3,'2025-11-16 06:55:10',3900.00,'Bank Transfer','Completed','BT20240006',NULL),(11,10,3,1,'2025-11-22 10:23:23',100.00,'Cash','Completed','TXN1763807001114',NULL),(12,11,3,1,'2025-11-23 05:41:05',480.00,'Cash','Completed','TXN1763876463197',NULL),(13,16,3,1,'2025-11-23 09:46:20',3200.75,'Cash','Completed','TXN1763891178157',NULL);
/*!40000 ALTER TABLE `payments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `products` (
  `product_id` int NOT NULL AUTO_INCREMENT,
  `product_name` varchar(100) NOT NULL,
  `category` varchar(50) DEFAULT NULL,
  `current_stock` decimal(10,2) DEFAULT '0.00',
  `min_stock` decimal(10,2) DEFAULT '0.00',
  `unit` varchar(20) DEFAULT 'kg',
  `price` decimal(10,2) NOT NULL,
  `farmer_id` int NOT NULL,
  `last_updated` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_active` tinyint(1) DEFAULT '1',
  `stock_quantity` int DEFAULT '0',
  PRIMARY KEY (`product_id`),
  KEY `farmer_id` (`farmer_id`),
  CONSTRAINT `products_ibfk_1` FOREIGN KEY (`farmer_id`) REFERENCES `farmers` (`farmer_id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES (1,'Kinnow','Fruits',250.50,30.00,'kg',120.00,1,'2025-11-21 10:32:30',1,10),(2,'Mango (Sindhri)','Fruits',100.00,20.00,'kg',200.00,1,'2025-11-23 09:32:23',1,10),(3,'Basmati Rice','Grains',500.00,100.00,'kg',180.00,1,'2025-11-22 18:13:41',1,10),(4,'Potatoes','Vegetables',100.00,50.00,'kg',60.00,1,'2025-11-21 10:32:30',1,10),(5,'Onions','Vegetables',200.00,30.00,'kg',80.00,1,'2025-11-21 10:32:30',1,10),(6,'Tomatoes','Vegetables',150.00,25.00,'kg',100.00,2,'2025-11-21 10:32:30',1,10),(7,'Wheat','Grains',800.00,150.00,'kg',90.00,2,'2025-11-21 10:32:30',1,10),(8,'Chicken (Live)','Poultry',50.00,10.00,'birds',450.00,2,'2025-11-21 10:32:30',1,10),(9,'Milk','Dairy',100.00,20.00,'liters',120.00,3,'2025-11-21 10:32:30',1,10),(10,'Eggs','Poultry',200.00,40.00,'dozen',180.00,3,'2025-11-21 10:32:30',1,10),(11,'Lentils (Masoor)','Pulses',150.00,25.00,'kg',220.00,4,'2025-11-21 10:32:30',1,10),(12,'Chickpeas (Chana)','Pulses',180.00,30.00,'kg',160.00,4,'2025-11-21 10:32:30',1,10),(13,'Okra (Bhindi)','Vegetables',75.00,15.00,'kg',140.00,5,'2025-11-21 10:32:30',1,10),(14,'Brinjal (Baingan)','Vegetables',60.00,12.00,'kg',90.00,5,'2025-11-21 10:32:30',1,10);
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `quality_control`
--

DROP TABLE IF EXISTS `quality_control`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `quality_control` (
  `qc_id` int NOT NULL AUTO_INCREMENT,
  `crop_id` int NOT NULL,
  `inspector_name` varchar(100) NOT NULL,
  `inspection_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `quality_score` int DEFAULT NULL,
  `defects_found` text,
  `approved` tinyint(1) DEFAULT '0',
  `recommendations` text,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `batch_id` int DEFAULT NULL,
  `grade` varchar(50) DEFAULT 'Not Graded',
  PRIMARY KEY (`qc_id`),
  KEY `crop_id` (`crop_id`),
  CONSTRAINT `quality_control_ibfk_1` FOREIGN KEY (`crop_id`) REFERENCES `crops` (`crop_id`) ON DELETE CASCADE,
  CONSTRAINT `quality_control_chk_1` CHECK (((`quality_score` >= 1) and (`quality_score` <= 10)))
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `quality_control`
--

LOCK TABLES `quality_control` WRITE;
/*!40000 ALTER TABLE `quality_control` DISABLE KEYS */;
INSERT INTO `quality_control` VALUES (2,4,'Robert Brown','2025-11-22 16:24:06',8,'Slight color variation',1,'Grade A approved','2025-11-22 16:24:06',NULL,'Not Graded'),(3,6,'Robert Brown','2025-11-22 16:24:06',9,'No significant defects found',1,'Excellent quality','2025-11-22 16:24:06',NULL,'Not Graded'),(4,9,'Jane Smith','2025-11-22 16:24:06',8,'Minor moisture on surface',1,'Ensure proper drying before storage','2025-11-22 16:24:06',NULL,'Not Graded');
/*!40000 ALTER TABLE `quality_control` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shipments`
--

DROP TABLE IF EXISTS `shipments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shipments` (
  `shipment_id` int NOT NULL AUTO_INCREMENT,
  `customer_name` varchar(100) NOT NULL,
  `carrier` varchar(100) NOT NULL,
  `status` enum('Pending','Shipped','Delivered','Cancelled') DEFAULT 'Pending',
  `tracking_number` varchar(100) DEFAULT NULL,
  `shipment_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `order_id` int DEFAULT NULL,
  `total_weight` decimal(10,2) DEFAULT '0.00',
  `delivery_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`shipment_id`),
  KEY `idx_shipments_order_id` (`order_id`),
  CONSTRAINT `fk_shipments_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shipments`
--

LOCK TABLES `shipments` WRITE;
/*!40000 ALTER TABLE `shipments` DISABLE KEYS */;
INSERT INTO `shipments` VALUES (1,'Fresh Market Co.','FedEx','Shipped','2','2024-01-20 04:00:00','2025-11-22 16:23:54','2025-11-23 09:32:43',NULL,0.00,NULL),(2,'Organic Foods Ltd.','UPS','Shipped','12','2024-01-21 05:30:00','2025-11-22 16:23:54','2025-11-22 18:23:22',NULL,0.00,NULL),(3,'City Grocers Inc.','DHL','Shipped','DHL123456789','2024-01-18 09:15:00','2025-11-22 16:23:54','2025-11-22 16:23:54',NULL,0.00,NULL),(4,'Farm to Table LLC','USPS','Delivered','USPS987654321','2024-01-15 06:45:00','2025-11-22 16:23:54','2025-11-22 16:23:54',NULL,0.00,NULL),(5,'Healthy Eats Market','FedEx','Shipped','12','2024-01-22 03:00:00','2025-11-22 16:23:54','2025-11-22 18:23:10',NULL,0.00,NULL),(6,'Green Valley Stores','UPS','Shipped','UPS555666777','2024-01-19 11:20:00','2025-11-22 16:23:54','2025-11-22 16:23:54',NULL,0.00,NULL),(7,'Premium Produce Co.','DHL','Pending',NULL,'2024-01-23 08:30:00','2025-11-22 16:23:54','2025-11-22 16:23:54',NULL,0.00,NULL),(8,'is','as','Pending','r','2025-11-22 18:23:51','2025-11-22 18:23:51','2025-11-22 18:23:51',NULL,0.00,NULL),(9,'Sara Iqbal','noone','Shipped','nop',NULL,'2025-11-23 04:06:04','2025-11-23 04:07:16',12,0.00,NULL),(10,'Sara Iqbal','noone','Pending','null',NULL,'2025-11-23 08:02:22','2025-11-23 08:02:22',10,0.00,NULL),(11,'Sara Iqbal','noone','Pending','null',NULL,'2025-11-23 09:29:04','2025-11-23 09:29:04',11,0.00,NULL);
/*!40000 ALTER TABLE `shipments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `suppliers`
--

DROP TABLE IF EXISTS `suppliers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `suppliers` (
  `supplier_id` int NOT NULL AUTO_INCREMENT,
  `supplier_name` varchar(100) NOT NULL,
  `contact_person` varchar(100) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `address` text,
  `supplier_type` enum('Seeds','Fertilizers','Equipment','General') DEFAULT 'General',
  `is_active` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`supplier_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `suppliers`
--

LOCK TABLES `suppliers` WRITE;
/*!40000 ALTER TABLE `suppliers` DISABLE KEYS */;
INSERT INTO `suppliers` VALUES (1,'Punjab Seeds Corporation','Rashid Mehmood','+92-42-37894561','info@punjabseeds.com','23 Mall Road, Lahore','Seeds',1),(2,'Green Fertilizers Ltd','Aslam Khan','+92-21-34567890','sales@greenfert.com','45 Industrial Area, Karachi','Fertilizers',1),(3,'Agri Tools Pakistan','Naveed Ahmed','+92-51-23456789','tools@agripak.com','78 Blue Area, Islamabad','Equipment',1),(4,'Sindh Farm Supplies','Tariq Hussain','+92-22-45678901','contact@sindhfarms.com','12 Saddar, Hyderabad','General',1),(5,'KPK Agriculture','Javed Iqbal','+92-91-56789012','info@kpkagri.com','34 University Road, Peshawar','General',1);
/*!40000 ALTER TABLE `suppliers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `full_name` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `role` enum('ADMIN','FARMER','FACTORY_MANAGER','CUSTOMER') NOT NULL DEFAULT 'CUSTOMER',
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'Ismail','123','Ismail','ismail@freshharvest.com','FARMER',1,'2025-11-22 03:25:05','2025-11-22 03:25:05'),(2,'Haris','123','Haris','haris@freshharvest.com','FACTORY_MANAGER',1,'2025-11-22 03:25:05','2025-11-22 03:25:05'),(3,'Saeed','123','Saeed','saeed@freshharvest.com','CUSTOMER',1,'2025-11-22 03:25:05','2025-11-22 03:25:05'),(4,'Ibrahim','123','Ibrahim','ibrahim@freshharvest.com','CUSTOMER',1,'2025-11-22 03:25:05','2025-11-22 03:25:05');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-11-23 22:50:15
