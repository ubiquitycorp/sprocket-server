-- selection
select * from external_network_application;
select * from external_network_application_platforms;

-- drop client_platform column
ALTER TABLE external_network_application DROP COLUMN client_platform;

-- clear tables
DELETE FROM external_network_application_platforms;
DELETE FROM external_network_application;

--------------------------------- Insertion ------------------------------------
-- Twitter "0"
-- Web / IOS / Android
INSERT INTO external_network_application (external_network, consumer_key, consumer_secret, redirect_url, app_id, is_deleted, last_updated, created_at) VALUES (0, "eIxhGVt1wo8yNs7cTq0mNHsi0", "qBTsSTelIlh0Lq4ILPlxgeTfhKk1GR8uetrlgaGExNulfxx37n", "http://local.sprocket.com?nw=twitter", 1, 0, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000);
INSERT INTO external_network_application_platforms (ex_app_id, client_platform) VALUES (LAST_INSERT_ID(), 0);
INSERT INTO external_network_application_platforms (ex_app_id, client_platform) VALUES (LAST_INSERT_ID(), 1);
INSERT INTO external_network_application_platforms (ex_app_id, client_platform) VALUES (LAST_INSERT_ID(), 2);
------------------------------------
-- Facebook "1"
-- Web / IOS / Android
INSERT INTO external_network_application (external_network, consumer_key, consumer_secret, app_id, is_deleted, last_updated, created_at) VALUES (1, "554461001341249", "06b1a984c9a65db7a42bdbe83c2738f1", 1, 0, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000);
INSERT INTO external_network_application_platforms (ex_app_id, client_platform) VALUES (LAST_INSERT_ID(), 0);
INSERT INTO external_network_application_platforms (ex_app_id, client_platform) VALUES (LAST_INSERT_ID(), 1);
INSERT INTO external_network_application_platforms (ex_app_id, client_platform) VALUES (LAST_INSERT_ID(), 2);
------------------------------------
-- Linkedin "3"
-- Web / IOS / Android
INSERT INTO external_network_application (external_network, consumer_key, consumer_secret, app_id, is_deleted, last_updated, created_at) VALUES (3, "77fa6kjljumj8x", "Qp7qxRm1usYix65e", 1, 0, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000);
INSERT INTO external_network_application_platforms (ex_app_id, client_platform) VALUES (LAST_INSERT_ID(), 0);
INSERT INTO external_network_application_platforms (ex_app_id, client_platform) VALUES (LAST_INSERT_ID(), 1);
INSERT INTO external_network_application_platforms (ex_app_id, client_platform) VALUES (LAST_INSERT_ID(), 2);
------------------------------------
-- Google "4"
-- Android
INSERT INTO external_network_application (external_network, consumer_key, consumer_secret, api_key, app_id, is_deleted, last_updated, created_at) VALUES (4, "518857244614-odu6ukf381cd2vmg8e0rphkp0hfksg9l.apps.googleusercontent.com", "iLUKupqRjUl6gybpP7YYINE6", "AIzaSyC-EfEn6WHFEDg3UjuaNxb-ZhqsCzY1D84", 1, 0, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000);
INSERT INTO external_network_application_platforms (ex_app_id, client_platform) VALUES (LAST_INSERT_ID(), 0);
-- IOS
INSERT INTO external_network_application (external_network, consumer_key, consumer_secret, api_key, app_id, is_deleted, last_updated, created_at) VALUES (4, "74160880049-aji0lam7cfclo2a370ep2ugf7niii982.apps.googleusercontent.com", "JZ52sjm4ifLV0sE1Lxwxk6pb", "AIzaSyCrTjAqBZK6DkGhFmuAu6-urDMABu0b_vk", 1, 0, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000);
INSERT INTO external_network_application_platforms (ex_app_id, client_platform) VALUES (LAST_INSERT_ID(), 1);
-- Web
INSERT INTO external_network_application (external_network, consumer_key, consumer_secret, api_key, app_id, is_deleted, last_updated, created_at) VALUES (4, "320470378854-06esrc32feq3nstb9t9ovp95jm50skvu.apps.googleusercontent.com", "gI2eS5OH9zp_PPB089sDafjR", "AIzaSyCY3woAWsatYnwhkYOeBqVjQ8V9hGa2vt8", 1, 0, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000);
INSERT INTO external_network_application_platforms (ex_app_id, client_platform) VALUES (LAST_INSERT_ID(), 2);
------------------------------------
-- Vimeo "6"
-- Android / IOS
INSERT INTO external_network_application (external_network, consumer_key, consumer_secret, redirect_url, app_id, is_deleted, last_updated, created_at) VALUES (6, "00a3112427cc0de8be5710eceb5bd83524001a6b", "6814eea3b1195e9f8cd81688481c76688fea1120", "http://localhost/", 1, 0, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000);
INSERT INTO external_network_application_platforms (ex_app_id, client_platform) VALUES (LAST_INSERT_ID(), 0);
INSERT INTO external_network_application_platforms (ex_app_id, client_platform) VALUES (LAST_INSERT_ID(), 1);
-- Web
INSERT INTO external_network_application (external_network, consumer_key, consumer_secret, redirect_url, app_id, is_deleted, last_updated, created_at) VALUES (6, "5be395b360ef0be2c11ed838505fe49d39b1c113", "9258a21966cadc0fb89b45281b70ceb5081f32fa", "http://local.sprocket.com?nw=vimeo", 1, 0, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000);
INSERT INTO external_network_application_platforms (ex_app_id, client_platform) VALUES (LAST_INSERT_ID(), 2);
------------------------------------
-- Yelp "8"
-- Web / IOS / Android
INSERT INTO external_network_application (external_network, consumer_key, consumer_secret, token, token_secret, app_id, is_deleted, last_updated, created_at) VALUES (8, "mnm5G8XclwYIwbED8HV4EQ", "3X5_IqGMVRE9dwb_pxV23FxS68k", "wjVmlQFhPyMNYEPcyIyCenucRoBPPwJp", "w7oeFsjoVLEb4c8bUbgRiQ4smec", 1, 0, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000);
INSERT INTO external_network_application_platforms (ex_app_id, client_platform) VALUES (LAST_INSERT_ID(), 0);
INSERT INTO external_network_application_platforms (ex_app_id, client_platform) VALUES (LAST_INSERT_ID(), 1);
INSERT INTO external_network_application_platforms (ex_app_id, client_platform) VALUES (LAST_INSERT_ID(), 2);
------------------------------------
-- Tumblr "9"
-- Web / IOS / Android
INSERT INTO external_network_application (external_network, consumer_key, consumer_secret, redirect_url, app_id, is_deleted, last_updated, created_at) VALUES (9, "kIdbuVYutk0G294pS4et73SI9k9TGP4ONpjGPcXekNr8fQOMWZ", "C0vBoxsctnB6qEvtGIv3xhYLGIIyw1a7E43AQQDLrfZfY37Ns2", "http://local.sprocket.com?nw=tumblr", 1, 0, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000);
INSERT INTO external_network_application_platforms (ex_app_id, client_platform) VALUES (LAST_INSERT_ID(), 0);
INSERT INTO external_network_application_platforms (ex_app_id, client_platform) VALUES (LAST_INSERT_ID(), 1);
INSERT INTO external_network_application_platforms (ex_app_id, client_platform) VALUES (LAST_INSERT_ID(), 2);
------------------------------------
-- reddit "10"
-- Android
INSERT INTO external_network_application (external_network, consumer_key, user_agent, redirect_url, app_id, is_deleted, last_updated, created_at) VALUES (10, "xvOJZJMeG5HQZw", "andriod:com.ubiquiy.sprocketQAMobile:v1.0.1 (by /u/ubiquitybc)", "http://local.sprocket.com?nw=reddit", 1, 0, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000);
INSERT INTO external_network_application_platforms (ex_app_id, client_platform) VALUES (LAST_INSERT_ID(), 0);
-- IOS
INSERT INTO external_network_application (external_network, consumer_key, user_agent, redirect_url, app_id, is_deleted, last_updated, created_at) VALUES (10, "xvOJZJMeG5HQZw", "ios:com.ubiquiy.sprocketQAMobile:v1.0.1 (by /u/ubiquitybc)", "http://local.sprocket.com?nw=reddit", 1, 0, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000);
INSERT INTO external_network_application_platforms (ex_app_id, client_platform) VALUES (LAST_INSERT_ID(), 1);
-- Web
INSERT INTO external_network_application (external_network, consumer_key, consumer_secret, user_agent, redirect_url, app_id, is_deleted, last_updated, created_at) VALUES (10, "zJ60a4wDHj6oMQ", "3t4KMhBuPrtAeS2sBoGRboqieng", "web:com.ubiquiy.sprocketQAWeb:v1.0.1 (by /u/ubiquitybc)", "http://local.sprocket.com?nw=reddit", 1, 0, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000);
INSERT INTO external_network_application_platforms (ex_app_id, client_platform) VALUES (LAST_INSERT_ID(), 2);
