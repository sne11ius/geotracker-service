# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table `geocoords` (`id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,`userId` VARCHAR(254) NOT NULL,`latitude` DOUBLE NOT NULL,`longitude` DOUBLE NOT NULL,`altitude` DOUBLE NOT NULL,`accuracy` REAL NOT NULL,`speed` REAL NOT NULL,`time` BIGINT NOT NULL);
create table `logininfo` (`id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,`providerID` VARCHAR(254) NOT NULL,`providerKey` VARCHAR(254) NOT NULL);
create table `passwordinfo` (`hasher` VARCHAR(254) NOT NULL,`password` VARCHAR(254) NOT NULL,`salt` VARCHAR(254),`loginInfoId` BIGINT NOT NULL);
create table `user` (`userID` VARCHAR(254) NOT NULL PRIMARY KEY,`apiKey` VARCHAR(254) NOT NULL,`firstName` VARCHAR(254),`lastName` VARCHAR(254),`fullName` VARCHAR(254),`email` VARCHAR(254),`avatarURL` VARCHAR(254));
create table `userlogininfo` (`userID` VARCHAR(254) NOT NULL,`loginInfoId` BIGINT NOT NULL);

# --- !Downs

drop table `userlogininfo`;
drop table `user`;
drop table `passwordinfo`;
drop table `logininfo`;
drop table `geocoords`;

