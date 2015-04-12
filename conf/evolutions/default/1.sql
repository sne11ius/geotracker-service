# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table `logininfo` (`id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,`providerID` VARCHAR(254) NOT NULL,`providerKey` VARCHAR(254) NOT NULL);
create table `passwordinfo` (`hasher` VARCHAR(254) NOT NULL,`password` VARCHAR(254) NOT NULL,`salt` VARCHAR(254),`loginInfoId` BIGINT NOT NULL);
create table `user` (`userID` VARCHAR(254) NOT NULL PRIMARY KEY,`firstName` VARCHAR(254),`lastName` VARCHAR(254),`fullName` VARCHAR(254),`email` VARCHAR(254),`avatarURL` VARCHAR(254));
create table `userlogininfo` (`userID` VARCHAR(254) NOT NULL,`loginInfoId` BIGINT NOT NULL);

# --- !Downs

drop table `userlogininfo`;
drop table `user`;
drop table `passwordinfo`;
drop table `logininfo`;

