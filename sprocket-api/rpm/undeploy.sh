sleep 5
EXTRA_TOMCAT=`expr ${tomcat.processes} - 1`
EXTRA_TOMCAT=3
service tomcat7 stop
if [ ! "$EXTRA_TOMCAT" ]
then
 EXTRA_TOMCAT=0
fi

while [ $EXTRA_TOMCAT -gt 0 ]
do
 service tomcat7-${EXTRA_TOMCAT} stop
 chkconfig tomcat7-${EXTRA_TOMCAT} off
 chkconfig --del tomcat7-${EXTRA_TOMCAT} 2>&1 > /dev/null
 EXTRA_TOMCAT=`expr ${EXTRA_TOMCAT} - 1`
done
rm -rvf /var/lib/tomcat7/webapps/sprocket-api
