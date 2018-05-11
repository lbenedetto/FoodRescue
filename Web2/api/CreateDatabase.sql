-- phpMyAdmin SQL Dump
-- version 4.5.4.1deb2ubuntu2
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: May 11, 2018 at 01:49 PM
-- Server version: 5.7.22-0ubuntu0.16.04.1
-- PHP Version: 7.0.28-0ubuntu0.16.04.1

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `foodrescue`
--
CREATE DATABASE IF NOT EXISTS `foodrescue` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;
USE `foodrescue`;

-- --------------------------------------------------------

--
-- Table structure for table `sms_info`
--

CREATE TABLE `sms_info` (
  `uname` varchar(20) NOT NULL,
  `number` int(12) UNSIGNED DEFAULT NULL,
  `carrier` varchar(25) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='For storing the sms numbers and carriers of eaters';

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `uname` varchar(20) NOT NULL,
  `auth_token` varchar(300) NOT NULL,
  `perm` tinyint(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`uname`, `auth_token`, `perm`) VALUES
('bludington', '475498ce0f7c4ca0e642140b7c8f3fff4a8b846586632683a36fade941ecdb38f857983c622f62e73c18844afe312170c4eff6b4eced6e1b804d5cd688b60f7e', 2),
('bstephenson', '977d914b363d07634fbe8daece2caa39532e14a46261730dce47a84bf444bb57cc1e600ad70f129ba93866968aeb1c582f889680cf010d4874deb5b6996bd9fb', 2),
('gedwards', '81e3f444c9ce0f90b001bd1876a42e51231ea1c38815d958d8eb11d32f4bdc72e00eb5c3f32adc4b56ec76081a37d8319f42bd9e80bd0257a7f41662de6177dd', 2),
('lbenedetto', '20ad646e676ea1ca339c71f5870ca79322c438f787f7dcc5f3d8e0dcfa6ba7ad831f580ad0b46d1343e82fbcbd205a37321c8226f36f3c78f44e104fd2ec2d52', 2),
('lstephenson1', '0ba9d28982a172feb8957f1468c8bcc84fd39655ac94db51e9c3ab4f986d177292a84294842186284b8c0f6e207a0e686e690f8f6fbc31b6eeb6cbc9a90074c1', 0);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `sms_info`
--
ALTER TABLE `sms_info`
  ADD PRIMARY KEY (`uname`),
  ADD UNIQUE KEY `uname` (`uname`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`uname`),
  ADD UNIQUE KEY `uname` (`uname`);

--
-- Constraints for dumped tables
--

--
-- Constraints for table `sms_info`
--
ALTER TABLE `sms_info`
  ADD CONSTRAINT `sms_info_ibfk_1` FOREIGN KEY (`uname`) REFERENCES `users` (`uname`) ON DELETE CASCADE ON UPDATE NO ACTION;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
