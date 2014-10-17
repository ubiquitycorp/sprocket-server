use sprocket;
#intersts tables
delete from activity_interests;
#delete from external_interest;
#delete i from interest as i inner join interest as i2 on i2.interest_id = i.parent_id where i2.parent_id is not null;
#delete from interest where parent_id is not null;
#delete from interest ;
#recommended
delete from recommended_activity;
delete from recommended_video;
delete from engaged_item;
delete from group_membership;
#activities,messages and vedios 
delete from activity ;
delete from message;
delete from conversation_contact;
delete from conversation;
delete from video_content;
delete from event;
delete from contact;
#place
delete from user_location;
#delete from place where parent_id is not null;
#identities and users
delete from external_identity;
delete from native_identity;
delete from identity;
delete from user;