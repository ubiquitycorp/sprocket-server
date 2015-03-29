
update sprocket.activity set posted_date = creation_date , created_at = UNIX_TIMESTAMP() *1000;
alter table sprocket.activity drop column creation_date;
