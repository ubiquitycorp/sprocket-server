USE sprocket

--- change updated naming error from 'last_udpated' to 'last_updated'
ALTER TABLE user CHANGE last_udpated last_updated bigint(20) NOT NULL;
ALTER TABLE identity CHANGE last_udpated last_updated bigint(20) NOT NULL

ALTER IGNORE TABLE external_identity ADD COLUMN expiry_time (Long)