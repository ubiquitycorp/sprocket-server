USE sprocket

-- added field for last login
ALTER IGNORE TABLE user ADD COLUMN last_login bigint(20) NOT NULL; 
UPDATE user SET last_login = UNIX_TIMESTAMP()*1000 WHERE last_login =0;