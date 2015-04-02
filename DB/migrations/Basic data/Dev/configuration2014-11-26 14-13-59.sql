USE sprocket;
delete from configuration;
ALTER TABLE configuration AUTO_INCREMENT = 1;

insert into `configuration` (`configuration_id`,`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,0,1,1415285840335,'message.service.host','localhost',-1);
insert into `configuration` (`configuration_id`,`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (2,0,1,1415285845693,'message.service.port','5222',-1);
insert into `configuration` (`configuration_id`,`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (3,0,1,1415285845698,'message.service.protocol','xmpp',-1);
insert into `configuration` (`configuration_id`,`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (4,1,1,1415285845703,'http.connnection.timeout','5',-1);
insert into `configuration` (`configuration_id`,`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (5,1,1,1415285845707,'http.transmission.timeout','20',-1);
insert into `configuration` (`configuration_id`,`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (6,1,1,1415285845712,'xmpp.connection.timeout','5',-1);
insert into `configuration` (`configuration_id`,`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (7,1,1,1415285845716,'xmpp.transmission.timeout','20',-1);
insert into `configuration` (`configuration_id`,`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (8,1,1,1415285845716,'activities.engaged','true',-1);
insert into `configuration` (`configuration_id`,`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (9,1,1,1415285845716,'videos.engaged','true',-1);
insert into `configuration` (`configuration_id`,`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (10,1,1,1415285845716,'search.private.enabled','true',-1);
insert into `configuration` (`configuration_id`,`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (11,1,1,1415285845716,'search.mostpopular.enabled','true',-1);

#########
#TWITTER#
#########
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'messages.enabled','true',0);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'contacts.enabled','false',0);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.enabled','true',0);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'favorite.enabled','false',0);

insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.categories','{"categories":[{"id":7, "name":"Tweets", "isDefault":"true"}, {"id":6, "name":"My Tweets", "isDefault":"false"}]}',0);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.rate.display','false',0);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.comment.display','false',0);

insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'messages.post.contribute','true',0);

insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.contribute','true',0);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.image','false',0);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.url','false',0);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.text','true',0);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.audio','false',0);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.video','false',0);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.recommended.enabled','false',0);

insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'search.private.enabled','true',0);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'search.live.enabled','true',0);

##########
#FACEBOOK#
##########
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'messages.enabled','true',1);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.enabled','true',1);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'localfeed.enabled','true',1);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'contacts.enabled','true',1);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'favorite.enabled','false',1);

insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.categories','{"categories":[{"id":3, "name":"News Feed", "isDefault":"true"}, {"id":4, "name":"Local News Feed", "isDefault":"false"}]}',1);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.rate.display','false',1);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.comment.display','false',1);

insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'messages.post.contribute','false',1);

insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.contribute','false',1);

insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.recommended.enabled','false',1);

insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'search.private.enabled','true',1);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'search.live.enabled','true',1);
#######
#YAHOO#
#######

##########
#LinkedIn#
##########
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'messages.enabled','false',3);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.enabled','true',3);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'contacts.enabled','true',3);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'favorite.enabled','false',3);

insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.categories','{"categories":[{"id":5, "name":"My Activities", "isDefault":"true"}]}',3);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.rate.display','false',3);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.comment.display','false',3);

insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.contribute','true',3);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.image','false',3);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.url','false',3);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.text','true',3);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.audio','false',3);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.video','false',3);

insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.recommended.enabled','false',3);

insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'search.private.enabled','true',3);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'search.live.enabled','true',3);
########
#Google#
########
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'messages.enabled','true',4);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'contacts.enabled','true',4);

insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'messages.post.contribute','true',4);

insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'search.private.enabled','true',4);

#########
#YouTube#
#########
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'videos.enabled','true',5);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'favorite.enabled','false',5);

insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'videos.categories','{"categories":[{"id":0, "name":"Most Popular", "isDefault":"true"}, {"id":1, "name":"Latest Subscription Videos", "isDefault":"false"}, {"id":2, "name":"My History", "isDefault":"false"}]}',5);

insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'videos.recommended.enabled','false',5);

insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'search.private.enabled','true',5);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'search.live.enabled','true',5);

#######
#Vimeo#
#######
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'videos.enabled','true',6);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'favorite.enabled','false',6);

insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'videos.categories','{"categories":[{"id":3, "name":"My Feeds", "isDefault":"true"}, {"id":1, "name":"Latest Subscription Videos", "isDefault":"false"}]}',6);

insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'videos.recommended.enabled','false',6);

insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'search.private.enabled','true',6);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'search.live.enabled','true',6);
#########
#Netflix#
#########
	
######
#Yelp#
######	
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'favorite.enabled','true',8);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'search.live.enabled','true',8);

########
#Tumblr#
########
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'messages.enabled','true',9);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.enabled','true',9);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'contacts.enabled','false',9);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'favorite.enabled','false',9);

insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.categories','{"categories":[{"id":3, "name":"My Feeds", "isDefault":"true"}]}',9);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.rate.display','false',9);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.comment.display','false',9);

insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'messages.post.contribute','false',9);

insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.contribute','true',9);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.image','true',9);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.url','true',9);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.text','true',9);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.audio','true',9);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.video','true',9);

insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.recommended.enabled','false',9);

insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'search.live.enabled','true',9);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'search.private.enabled','true',9);

########
#Reddit#
########
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.enabled','true',10);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'favorite.enabled','false',10);

insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.categories','{"categories":[{"id":9, "name":"Hot Posts", "isDefault":"true"}]}',10);

insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.rate.display','true',10);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.rate.type','2',10);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.rate.contribute','true',10);

insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.comment.display','false',10);

insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.contribute','true',10);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.image','false',10);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.url','true',10);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.text','true',10);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.audio','false',10);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.video','false',10);

insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.recommended.enabled','false',10);

insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'search.live.enabled','true',10);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'search.private.enabled','true',10);
