USE sprocket
ALTER TABLE video_content change category category_external_identifier varchar(255);
ALTER TABLE video_content ADD COLUMN category int(11); 
ALTER TABLE user ADD COLUMN latitude double; 
ALTER TABLE user ADD COLUMN longitude double; 
ALTER TABLE user ADD COLUMN location_timestamp bigint(20); 