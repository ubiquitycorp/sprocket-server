sleep 5
service tomcat7 stop
service tomcat7-1 stop
service tomcat7-2 stop
service tomcat7-3 stop
chkconfig tomcat7-1 off
chkconfig tomcat7-2 off
chkconfig tomcat7-3 off
chkconfig --del tomcat7-1
chkconfig --del tomcat7-2
chkconfig --del tomcat7-3
rm -rvf /var/lib/tomcat7/webapps/sprocket-api
