USE sprocket;

insert into `configuration` (`configuration_id`,`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,0,1,1415285840335,'message.service.host','localhost',-1);
insert into `configuration` (`configuration_id`,`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (2,0,1,1415285845693,'message.service.port','5222',-1);
insert into `configuration` (`configuration_id`,`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (3,0,1,1415285845698,'message.service.protocol','xmpp',-1);
insert into `configuration` (`configuration_id`,`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (4,1,1,1415285845703,'http.connnection.timeout','5',-1);
insert into `configuration` (`configuration_id`,`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (5,1,1,1415285845707,'http.transmission.timeout','20',-1);
insert into `configuration` (`configuration_id`,`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (6,1,1,1415285845712,'xmpp.connection.timeout','5',-1);
insert into `configuration` (`configuration_id`,`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (7,1,1,1415285845716,'xmpp.transmission.timeout','20',-1);
#########
#TWITTER#
#########
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'sync.messages ','true',0);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'sync.activities ','true',0);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'sync.videos ','false',0);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'sync.localfeed','false',0);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'sync.favorite','false',0);

insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'messages.post.contribute ','false ',0);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.image','false',0);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.url','false',0);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.text','true',0);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.audio','false',0);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.video','false',0);

insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.rate.display','false',0);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.rate.type','0',0);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.rate.contribute','false',0);

insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.comment.display','false',0);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.comment.contribute','false',0);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.comment.rate.display','false',0);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.comment.rate.type','0',0);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.comment.rate.contribute','false',0);

##########
#FACEBOOK#
##########
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'sync.messages ','true',1);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'sync.activities ','true',1);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'sync.videos ','false',1);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'sync.localfeed','true',1);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'sync.favorite','false',1);

insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'messages.post.contribute ','false ',1);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.image','false',1);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.url','false',1);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.text','false',1);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.audio','false',1);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.video','false',1);

insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.rate.display','false',1);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.rate.type','0',1);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.rate.contribute','false',1);

insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.comment.display','false',1);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.comment.contribute','false',1);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.comment.rate.display','false',1);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.comment.rate.type','0',1);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.comment.rate.contribute','false',1);

#######
#YAHOO#
#######

##########
#LinkedIn#
##########
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'sync.messages ','false',3);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'sync.activities ','true',3);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'sync.videos ','false',3);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'sync.localfeed','false',3);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'sync.favorite','false',3);

insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'messages.post.contribute ','false ',3);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.image','false',3);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.url','false',3);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.text','true',3);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.audio','false',3);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.video','false',3);

insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.rate.display','false',3);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.rate.type','0',3);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.rate.contribute','false',3);

insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.comment.display','false',3);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.comment.contribute','false',3);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.comment.rate.display','false',3);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.comment.rate.type','0',3);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.comment.rate.contribute','false',3);

########
#Google#
########
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'sync.messages ','true',4);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'sync.activities ','false',4);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'sync.videos ','false',4);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'sync.localfeed','false',4);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'sync.favorite','false',4);

insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'messages.post.contribute ','false ',4);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.image','false',4);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.url','false',4);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.text','false',4);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.audio','false',4);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.video','false',4);

insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.rate.display','false',4);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.rate.type','0',4);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.rate.contribute','false',4);

insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.comment.display','false',4);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.comment.contribute','false',4);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.comment.rate.display','false',4);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.comment.rate.type','0',4);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.comment.rate.contribute','false',4);

#########
#YouTube#
#########
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'sync.messages ','false',5);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'sync.activities ','false',5);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'sync.videos ','true',5);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'sync.localfeed','false',5);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'sync.favorite','false',5);

insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'messages.post.contribute ','false ',5);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.image','false',5);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.url','false',5);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.text','false',5);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.audio','false',5);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.video','false',5);

insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.rate.display','false',5);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.rate.type','0',5);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.rate.contribute','false',5);

insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.comment.display','false',5);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.comment.contribute','false',5);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.comment.rate.display','false',5);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.comment.rate.type','0',5);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.comment.rate.contribute','false',5);


#######
#Vimeo#
#######
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'sync.messages ','false',6);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'sync.activities ','false',6);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'sync.videos ','true',6);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'sync.localfeed','false',6);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'sync.favorite','false',6);

insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'messages.post.contribute ','false ',6);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.image','false',6);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.url','false',6);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.text','false',6);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.audio','false',6);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.video','false',6);

insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.rate.display','false',6);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.rate.type','0',6);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.rate.contribute','false',6);

insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.comment.display','false',6);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.comment.contribute','false',6);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.comment.rate.display','false',6);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.comment.rate.type','0',6);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.comment.rate.contribute','false',6);

#########
#Netflix#
#########
	
######
#Yelp#
######	
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'sync.messages ','false',8);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'sync.activities ','false',8);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'sync.videos ','false',8);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'sync.localfeed','false',8);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'sync.favorite','true',8);

insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'messages.post.contribute ','false ',8);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.image','false',8);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.url','false',8);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.text','false',8);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.audio','false',8);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.video','false',8);

insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.rate.display','true',8);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.rate.type','1',8);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.rate.contribute','false',8);

insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.comment.display','false',8);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.comment.contribute','false',8);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.comment.rate.display','false',8);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.comment.rate.type','0',8);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.comment.rate.contribute','false',8);


########
#Tumblr#
########
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'sync.messages ','true',9);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'sync.activities ','true',9);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'sync.videos ','false',9);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'sync.localfeed','false',9);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'sync.favorite','false',9);

insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'messages.post.contribute ','false ',9);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.image','true',9);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.url','true',9);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.text','true',9);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.audio','true',9);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.video','true',9);

insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.rate.display','false',9);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.rate.type','0',9);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.rate.contribute','false',9);

insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.comment.display','false',9);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.comment.contribute','false',9);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.comment.rate.display','false',9);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.comment.rate.type','0',9);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.comment.rate.contribute','false',9);

########
#Reddit#
########
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'sync.messages ','false',10);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'sync.activities ','true',10);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'sync.videos ','false',10);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'sync.localfeed','false',10);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'sync.favorite','false',10);

insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'messages.post.contribute ','false ',10);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.image','false ',10);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.url','true',10);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.text','true',10);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.audio','false',10);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.post.video','false',10);

insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.rate.display','true',10);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.rate.type','2',10);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.rate.contribute','true',10);

insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.comment.display','true',10);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.comment.contribute','true',10);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.comment.rate.display','true',10);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.comment.rate.type','2',10);
insert into `configuration` (`configuration_type`,`is_active`,`last_updated`,`name`,`value`,`external_network`) values  (1,1,1417004543000,'activities.comment.rate.contribute','true',10);
