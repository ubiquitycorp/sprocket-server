USE sprocket;
DELETE FROM external_network_application;

-- Twitter "0"
-- Web / IOS / Android
INSERT INTO external_network_application (ex_app_id, external_network, consumer_key, consumer_secret, redirect_url, app_id, is_deleted, last_updated, created_at) VALUES (1, 0, "eIxhGVt1wo8yNs7cTq0mNHsi0", "qBTsSTelIlh0Lq4ILPlxgeTfhKk1GR8uetrlgaGExNulfxx37n", "http://sprocket-qa.ubiquitycorp.com?nw=twitter", 1, 0, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000);

-- Facebook "1"
-- Web / IOS / Android
INSERT INTO external_network_application (ex_app_id, external_network, consumer_key, consumer_secret, app_id, is_deleted, last_updated, created_at) VALUES (2, 1, "554461001341249", "06b1a984c9a65db7a42bdbe83c2738f1", 1, 0, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000);

-- LinkedIn "3"
-- Web / IOS / Android
INSERT INTO external_network_application (ex_app_id, external_network, consumer_key, consumer_secret, app_id, is_deleted, last_updated, created_at) VALUES (3, 3, "77fa6kjljumj8x", "Qp7qxRm1usYix65e", 1, 0, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000);

-- Google "4"
-- Android
INSERT INTO external_network_application (ex_app_id, external_network, consumer_key, consumer_secret, api_key, app_id, is_deleted, last_updated, created_at) VALUES (4, 4, "518857244614-odu6ukf381cd2vmg8e0rphkp0hfksg9l.apps.googleusercontent.com", "iLUKupqRjUl6gybpP7YYINE6", "AIzaSyC-EfEn6WHFEDg3UjuaNxb-ZhqsCzY1D84", 1, 0, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000);
-- IOS
INSERT INTO external_network_application (ex_app_id, external_network, consumer_key, consumer_secret, api_key, app_id, is_deleted, last_updated, created_at) VALUES (5, 4, "74160880049-aji0lam7cfclo2a370ep2ugf7niii982.apps.googleusercontent.com", "JZ52sjm4ifLV0sE1Lxwxk6pb", "AIzaSyCrTjAqBZK6DkGhFmuAu6-urDMABu0b_vk", 1, 0, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000);
-- Web
INSERT INTO external_network_application (ex_app_id, external_network, consumer_key, consumer_secret, api_key, app_id, is_deleted, last_updated, created_at) VALUES (6, 4, "320470378854-06esrc32feq3nstb9t9ovp95jm50skvu.apps.googleusercontent.com", "gI2eS5OH9zp_PPB089sDafjR", "AIzaSyCY3woAWsatYnwhkYOeBqVjQ8V9hGa2vt8", 1, 0, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000);

-- Vimeo "6"
-- Android / IOS
INSERT INTO external_network_application (ex_app_id, external_network, consumer_key, consumer_secret, redirect_url, app_id, is_deleted, last_updated, created_at) VALUES (7, 6, "00a3112427cc0de8be5710eceb5bd83524001a6b", "6814eea3b1195e9f8cd81688481c76688fea1120", "http://localhost/", 1, 0, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000);
-- Web
INSERT INTO external_network_application (ex_app_id, external_network, consumer_key, consumer_secret, redirect_url, app_id, is_deleted, last_updated, created_at) VALUES (8, 6, "5be395b360ef0be2c11ed838505fe49d39b1c113", "9258a21966cadc0fb89b45281b70ceb5081f32fa", "http://sprocket-qa.ubiquitycorp.com?nw=vimeo", 1, 0, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000);

-- Yelp "8"
-- Web / IOS / Android
INSERT INTO external_network_application (ex_app_id, external_network, consumer_key, consumer_secret, token, token_secret, app_id, is_deleted, last_updated, created_at) VALUES (9, 8, "mnm5G8XclwYIwbED8HV4EQ", "3X5_IqGMVRE9dwb_pxV23FxS68k", "wjVmlQFhPyMNYEPcyIyCenucRoBPPwJp", "w7oeFsjoVLEb4c8bUbgRiQ4smec", 1, 0, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000);

-- Tumblr "9"
-- Web / IOS / Android
INSERT INTO external_network_application (ex_app_id, external_network, consumer_key, consumer_secret, redirect_url, app_id, is_deleted, last_updated, created_at) VALUES (10, 9, "kIdbuVYutk0G294pS4et73SI9k9TGP4ONpjGPcXekNr8fQOMWZ", "C0vBoxsctnB6qEvtGIv3xhYLGIIyw1a7E43AQQDLrfZfY37Ns2", "http://sprocket-qa.ubiquitycorp.com?nw=tumblr", 1, 0, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000);

-- Reddit "10"
-- Android
INSERT INTO external_network_application (ex_app_id, external_network, consumer_key, user_agent, redirect_url, app_id, is_deleted, last_updated, created_at) VALUES (11, 10, "xvOJZJMeG5HQZw", "andriod:com.ubiquiy.sprocketQAMobile:v1.0.1 (by /u/ubiquitybc)", "http://sprocket-qa.ubiquitycorp.com?nw=reddit", 1, 0, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000);
-- IOS
INSERT INTO external_network_application (ex_app_id, external_network, consumer_key, user_agent, redirect_url, app_id, is_deleted, last_updated, created_at) VALUES (12, 10, "xvOJZJMeG5HQZw", "ios:com.ubiquiy.sprocketQAMobile:v1.0.1 (by /u/ubiquitybc)", "http://sprocket-qa.ubiquitycorp.com?nw=reddit", 1, 0, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000);
-- Web
INSERT INTO external_network_application (ex_app_id, external_network, consumer_key, consumer_secret, user_agent, redirect_url, app_id, is_deleted, last_updated, created_at) VALUES (13, 10, "zJ60a4wDHj6oMQ", "3t4KMhBuPrtAeS2sBoGRboqieng", "web:com.ubiquiy.sprocketQAWeb:v1.0.1 (by /u/ubiquitybc)", "http://sprocket-qa.ubiquitycorp.com?nw=reddit", 1, 0, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000);

