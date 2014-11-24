#change title lenght to 300 
ALTER TABLE `sprocket`.`activity` CHANGE COLUMN `title` `title` VARCHAR(300) NULL DEFAULT NULL ;

#drop commnet table foreign keys
ALTER TABLE `sprocket`.`comment` 
DROP FOREIGN KEY `Fk_comment_activity`,
DROP FOREIGN KEY `Fk_comment_commentParent`;

#create commnet table foreign keys
ALTER TABLE `sprocket`.`comment` 
ADD CONSTRAINT `Fk_comment_activity`  FOREIGN KEY (`activity_id`)  REFERENCES `sprocket`.`activity` (`activity_id`)  ON DELETE CASCADE,
ADD CONSTRAINT `Fk_comment_commentParent`  FOREIGN KEY (`parent_id`)  REFERENCES `sprocket`.`comment` (`comment_id`)  ON DELETE CASCADE;