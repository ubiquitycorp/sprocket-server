use sprocket;
SET FOREIGN_KEY_CHECKS=0;
#intersts tables
delete from activity_interests;
#delete from external_interest;
#delete i from interest as i inner join interest as i2 on i2.interest_id = i.parent_id where i2.parent_id is not null;
#delete from interest where parent_id is not null;
#delete from interest ;
delete from favorite_item;
#recommended
delete from recommended_activity;
delete from recommended_video;
delete from group_membership;
#activities,messages and vedios 
delete from activity ;
delete from comment;
#################Drop those tables because of having large data ##########################
drop table message;
drop table conversation_contact;
drop table conversation;
##########################################################################################
delete from video_content;
delete from event;
delete from user_contact;
#place
delete from user_location;
#delete from place where parent_id is not null;
#identities and users

delete from contact;
delete from external_identity;
delete from native_identity;
delete from identity;
delete from user_location;
delete from user;
SET FOREIGN_KEY_CHECKS=1;