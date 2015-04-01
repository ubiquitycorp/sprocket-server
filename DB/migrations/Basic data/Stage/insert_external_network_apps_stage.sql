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
INSERT INTO external_network_application (external_network, consumer_key, consumer_secret, redirect_url, app_id, is_deleted, last_updated, created_at) VALUES (0, "f5cMrdDfL4mxP1eaUOwAvUtfJ", "Z7Q61m3kHejh09muK0aX97Ed1baamdF70svowkM2nKLYvh6EYp", "http://local.sprocket.com?nw=twitter", 1, 0, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000);
INSERT INTO external_network_application_platforms (ex_app_id, client_platform) VALUES (LAST_INSERT_ID(), 0);
INSERT INTO external_network_application_platforms (ex_app_id, client_platform) VALUES (LAST_INSERT_ID(), 1);
INSERT INTO external_network_application_platforms (ex_app_id, client_platform) VALUES (LAST_INSERT_ID(), 2);
------------------------------------
-- Facebook "1"
-- Web / IOS / Android
INSERT INTO external_network_application (external_network, consumer_key, consumer_secret, app_id, is_deleted, last_updated, created_at) VALUES (1, "581439761976706", "5968babeab5c14f940ac925da686c63f", 1, 0, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000);
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
INSERT INTO external_network_application (external_network, consumer_key, consumer_secret, api_key, app_id, is_deleted, last_updated, created_at) VALUES (4, "441395842147-p7a9cau2ptsv0ebvhea27n84u1cjcia9.apps.googleusercontent.com", "y_5Qz6RSm7_LErx8TT8r_GCF", "AIzaSyDcW0lXISh704Z6tVwru78yLLuG6629bAk", 1, 0, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000);
INSERT INTO external_network_application_platforms (ex_app_id, client_platform) VALUES (LAST_INSERT_ID(), 1);
-- Web
INSERT INTO external_network_application (external_network, consumer_key, consumer_secret, api_key, app_id, is_deleted, last_updated, created_at) VALUES (4, "41815987463-mbca1f4ojnj29qed9abor6rjsbat61ld.apps.googleusercontent.com", "apLfEWdotpcaonJRFaior5wn", "AIzaSyAAohwXAj4cwNQt5Gjylf7ZV7ObZiMZH_o", 1, 0, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000);
INSERT INTO external_network_application_platforms (ex_app_id, client_platform) VALUES (LAST_INSERT_ID(), 2);
------------------------------------
-- Vimeo "6"
-- Android / IOS
INSERT INTO external_network_application (external_network, consumer_key, consumer_secret, redirect_url, app_id, is_deleted, last_updated, created_at) VALUES (6, "00a3112427cc0de8be5710eceb5bd83524001a6b", "6814eea3b1195e9f8cd81688481c76688fea1120", "http://localhost/", 1, 0, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000);
INSERT INTO external_network_application_platforms (ex_app_id, client_platform) VALUES (LAST_INSERT_ID(), 0);
INSERT INTO external_network_application_platforms (ex_app_id, client_platform) VALUES (LAST_INSERT_ID(), 1);
-- Web
INSERT INTO external_network_application (external_network, consumer_key, consumer_secret, redirect_url, app_id, is_deleted, last_updated, created_at) VALUES (6, "32de4824c358a86ff15b5f53f7b840b044317390", "ac0720d3536d96815996ed0da0471418a0c55524", "http://local.sprocket.com?nw=vimeo", 1, 0, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000);
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
INSERT INTO external_network_application (external_network, consumer_key, consumer_secret, redirect_url, app_id, is_deleted, last_updated, created_at) VALUES (9, "HXpAQmGwYutSonQNixGf23uy8SifTZU5WG2iALLwNgdzYhGYfs", "lbsptcaAFUhLExR2UllZZ1FqG0r0uIWeHD9UOP9lF5ywGCI92W", "http://local.sprocket.com?nw=tumblr", 1, 0, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000);
INSERT INTO external_network_application_platforms (ex_app_id, client_platform) VALUES (LAST_INSERT_ID(), 0);
INSERT INTO external_network_application_platforms (ex_app_id, client_platform) VALUES (LAST_INSERT_ID(), 1);
INSERT INTO external_network_application_platforms (ex_app_id, client_platform) VALUES (LAST_INSERT_ID(), 2);
------------------------------------
-- reddit "10"
-- Android
INSERT INTO external_network_application (external_network, consumer_key, user_agent, redirect_url, app_id, is_deleted, last_updated, created_at) VALUES (10, "Fm2rYVyn5bVhmw", "andriod:com.ubiquiy.sprocketStageMobile:v1.0.1 (by /u/ubiquitybc)", "http://local.sprocket.com?nw=reddit", 1, 0, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000);
INSERT INTO external_network_application_platforms (ex_app_id, client_platform) VALUES (LAST_INSERT_ID(), 0);
-- IOS
INSERT INTO external_network_application (external_network, consumer_key, user_agent, redirect_url, app_id, is_deleted, last_updated, created_at) VALUES (10, "Fm2rYVyn5bVhmw", "ios:com.ubiquiy.sprocketStageMobile:v1.0.1 (by /u/ubiquitybc)", "http://local.sprocket.com?nw=reddit", 1, 0, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000);
INSERT INTO external_network_application_platforms (ex_app_id, client_platform) VALUES (LAST_INSERT_ID(), 1);
-- Web
INSERT INTO external_network_application (external_network, consumer_key, consumer_secret, user_agent, redirect_url, app_id, is_deleted, last_updated, created_at) VALUES (10, "gTwisdAm0FV8DQ", "dJ-nLWxl_TuAoDnUnYXWkj3N9QM", "web:com.ubiquiy.sprocketStageWeb:v1.0.1 (by /u/ubiquitybc)", "http://local.sprocket.com?nw=reddit", 1, 0, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000);
INSERT INTO external_network_application_platforms (ex_app_id, client_platform) VALUES (LAST_INSERT_ID(), 2);
