USE sprocket;
ALTER TABLE activity MODIFY COLUMN external_identifier varchar(255) not null;
ALTER TABLE activity MODIFY COLUMN creation_date bigint(20) not null;