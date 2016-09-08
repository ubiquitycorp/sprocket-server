USE sprocket
ALTER TABLE user ADD UNIQUE (email);
ALTER TABLE user MODIFY display_name VARCHAR(100);
ALTER TABLE native_identity ADD COLUMN reset_token VARCHAR(255); 
ALTER TABLE native_identity ADD COLUMN reset_expiry_time bigint(20); 
ALTER TABLE native_identity ADD COLUMN is_reset_verified tinyint(1); 
ALTER TABLE external_identity MODIFY access_token VARCHAR(350);
ALTER TABLE external_identity MODIFY secret_token VARCHAR(350);