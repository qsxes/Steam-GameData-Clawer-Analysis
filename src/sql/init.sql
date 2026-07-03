/*
 Navicat Premium Data Transfer

 Source Server         : MySQL
 Source Server Type    : MySQL
 Source Server Version : 80015
 Source Host           : localhost:3306
 Source Schema         : steam_games

 Target Server Type    : MySQL
 Target Server Version : 80015
 File Encoding         : 65001

 Date: 03/07/2026 08:58:45
*/

CREATE DATABASE IF NOT EXISTS `steam_games`
CHARACTER SET utf8mb4
COLLATE utf8mb4_0900_ai_ci;

USE `steam_games`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for games
-- ----------------------------
DROP TABLE IF EXISTS `games`;
CREATE TABLE `games`  (
  `app_id` int(11) NOT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `developer` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `publisher` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `price` decimal(10, 2) NULL DEFAULT 0.00,
  `release_date` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `positive_reviews` bigint(20) NULL DEFAULT 0,
  `negative_reviews` bigint(20) NULL DEFAULT 0,
  `total_reviews` bigint(20) NULL DEFAULT 0,
  `review_score_desc` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'game',
  `release_date_parsed` date NULL DEFAULT NULL,
  PRIMARY KEY (`app_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for games_tags
-- ----------------------------
DROP TABLE IF EXISTS `games_tags`;
CREATE TABLE `games_tags`  (
  `app_id` int(11) NOT NULL,
  `tag_id` int(11) NOT NULL,
  PRIMARY KEY (`app_id`, `tag_id`) USING BTREE,
  INDEX `fk_game_tag_tags`(`tag_id`) USING BTREE,
  CONSTRAINT `fk_game_tag_games` FOREIGN KEY (`app_id`) REFERENCES `games` (`app_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_game_tag_tags` FOREIGN KEY (`tag_id`) REFERENCES `tags` (`tag_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for not_in_china
-- ----------------------------
DROP TABLE IF EXISTS `not_in_china`;
CREATE TABLE `not_in_china`  (
  `app_id` int(11) NOT NULL,
  PRIMARY KEY (`app_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tags
-- ----------------------------
DROP TABLE IF EXISTS `tags`;
CREATE TABLE `tags`  (
  `tag_id` int(11) NOT NULL AUTO_INCREMENT,
  `tag_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`tag_id`) USING BTREE,
  UNIQUE INDEX `tag_name`(`tag_name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- View structure for v_game_summary
-- ----------------------------
DROP VIEW IF EXISTS `v_game_summary`;
CREATE VIEW `v_game_summary` AS select `g`.`app_id` AS `app_id`,`g`.`name` AS `name`,`g`.`developer` AS `developer`,`g`.`publisher` AS `publisher`,`g`.`price` AS `price`,`g`.`release_date_parsed` AS `release_date_parsed`,`g`.`positive_reviews` AS `positive_reviews`,`g`.`negative_reviews` AS `negative_reviews`,`g`.`total_reviews` AS `total_reviews`,`g`.`review_score_desc` AS `review_score_desc`,`g`.`type` AS `type`,group_concat(distinct `t`.`tag_name` order by `t`.`tag_name` ASC separator ', ') AS `tags`,round((case when (`g`.`total_reviews` > 0) then ((`g`.`positive_reviews` * 100.0) / `g`.`total_reviews`) else NULL end),1) AS `positive_rate_percent` from ((`games` `g` left join `games_tags` `gt` on((`g`.`app_id` = `gt`.`app_id`))) left join `tags` `t` on((`gt`.`tag_id` = `t`.`tag_id`))) group by `g`.`app_id`,`g`.`name`,`g`.`developer`,`g`.`publisher`,`g`.`price`,`g`.`release_date`,`g`.`positive_reviews`,`g`.`negative_reviews`,`g`.`total_reviews`,`g`.`review_score_desc`;


SET FOREIGN_KEY_CHECKS = 1;
