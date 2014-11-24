USE sprocket
ALTER TABLE native_identity MODIFY COLUMN username varchar(255) not null unique;