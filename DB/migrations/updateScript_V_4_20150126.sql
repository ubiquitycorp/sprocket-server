USE sprocket;
ALTER TABLE contact Drop COLUMN is_deleted ;
ALTER TABLE contact Drop FOREIGN KEY FK_cmaoslcxl4a8rr6co6epf8nkj;
ALTER TABLE contact Drop COLUMN owner_id ;