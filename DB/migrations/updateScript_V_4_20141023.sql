USE sprocket
ALTER TABLE native_identity MODIFY COLUMN username varchar(255) not null unique;

ALTER TABLE activity ADD COLUMN audio_content_length bigint(20) null;
ALTER TABLE activity ADD COLUMN audio_embed_code longtext null;
ALTER TABLE activity ADD COLUMN audio_item_key varchar(255) null;
ALTER TABLE activity ADD COLUMN audio_url longtext null;
ALTER TABLE activity ADD COLUMN video_embed_code longtext null;
ALTER TABLE video_content ADD COLUMN embed_code longtext null;


