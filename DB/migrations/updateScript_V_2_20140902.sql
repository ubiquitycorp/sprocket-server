USE sprocket
ALTER TABLE video_content change category category_external_identifier varchar(255);
ALTER TABLE video_content ADD COLUMN category int(11); 
update video_content set category =0 where category is null
ALTER TABLE video_content ADD COLUMN category int(11); 
ALTER TABLE user ADD COLUMN latitude double; 
ALTER TABLE user ADD COLUMN longitude double; 
ALTER TABLE user ADD COLUMN location_timestamp bigint(20); 
ALTER TABLE video_content ADD COLUMN published_at bigint(20);