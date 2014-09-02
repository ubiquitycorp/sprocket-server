USE sprocket
ALTER TABLE video_content change category category_external_identifier varchar(255);
ALTER TABLE video_content ADD COLUMN category int(11); 