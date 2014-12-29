sleep 5
echo "This installation has ${tomcat.processes} tomcat processes"
EXTRA_TOMCAT=`expr ${tomcat.processes} - 1`
if [ -f /etc/sysconfig/tomcat-local ]
then
 . /etc/sysconfig/tomcat-local
fi

kill -9 `pgrep -f tomcat` 2>&1 > /dev/null
service tomcat7 stop 2>&1 > /dev/null

if [ ! "$EXTRA_TOMCAT" ]
then
 EXTRA_TOMCAT=0
fi

while [ $EXTRA_TOMCAT -gt 0 ]
do
 service tomcat7-${EXTRA_TOMCAT} stop 2>&1 > /dev/null
 chkconfig tomcat7-${EXTRA_TOMCAT} off 2>&1 > /dev/null
 chkconfig --del tomcat7-${EXTRA_TOMCAT} 2>&1 > /dev/null
 EXTRA_TOMCAT=`expr ${EXTRA_TOMCAT} - 1`
done
rm -rvf /var/lib/tomcat7/webapps/sprocket-api
exit 0
