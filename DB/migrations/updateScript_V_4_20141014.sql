USE sprocket
ALTER TABLE video_content MODIFY COLUMN external_network int(11) not null;
ALTER TABLE external_identity MODIFY COLUMN external_network int(11) not null;