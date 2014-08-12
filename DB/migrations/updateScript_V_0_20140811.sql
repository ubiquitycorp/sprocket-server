USE sprocket
-- added field for last login
ALTER IGNORE TABLE user ADD COLUMN last_login bigint(20) NOT NULL; 
UPDATE user SET last_login = UNIX_TIMESTAMP()*1000 WHERE last_login =0;
--- change updated naming error from 'last_udpated' to 'last_updated'
ALTER TABLE user CHANGE last_udpated last_updated bigint(20) NOT NULL;
ALTER TABLE identity CHANGE last_udpated last_updated bigint(20) NOT NULL