USE sprocket

--- change updated naming error from 'last_udpated' to 'last_updated'
ALTER TABLE user CHANGE last_udpated last_updated bigint(20) NOT NULL;
ALTER TABLE identity CHANGE last_udpated last_updated bigint(20) NOT NULL;
--- add indices  -----------------------------------
------external_identity------
ALTER TABLE external_identity ADD INDEX identifier (idx_external_identifier);
ALTER TABLE external_identity ADD INDEX external_network (idx_external_network);
------Activity------
ALTER TABLE activity ADD INDEX identifier (idx_external_identifier);
ALTER TABLE activity ADD INDEX external_network (idx_external_network);
------Message------
ALTER TABLE message ADD INDEX identifier (idx_external_identifier);
ALTER TABLE message ADD INDEX external_network (idx_external_network);

ALTER IGNORE TABLE external_identity ADD COLUMN expiry_time (Long)