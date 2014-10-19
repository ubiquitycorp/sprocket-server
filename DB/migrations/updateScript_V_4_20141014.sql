USE sprocket
ALTER TABLE video_content MODIFY COLUMN external_network int(11) not null;
ALTER TABLE external_identity MODIFY COLUMN external_network int(11) not null;
ALTER TABLE interest MODIFY COLUMN name varchar(255) not null;
ALTER TABLE place MODIFY COLUMN external_network int(11) not null;
ALTER TABLE external_interest MODIFY COLUMN external_network int(11) not null default -1;