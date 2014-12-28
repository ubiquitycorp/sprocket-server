sleep 5
service tomcat7 stop
service tomcat7-1 stop
service tomcat7-2 stop
service tomcat7-3 stop
rm -rvf /var/lib/tomcat7/webapps/sprocket-api
