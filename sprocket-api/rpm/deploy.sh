sleep 5
echo "This installation has ${tomcat.processes} tomcat processes"
EXTRA_TOMCAT=`expr ${tomcat.processes} - 1`
if [ -f /etc/sysconfig/tomcat-local ]
then
 . /etc/sysconfig/tomcat-local
fi

service tomcat7 start

if [ ! "$EXTRA_TOMCAT" ]
then
 EXTRA_TOMCAT=0
fi

while [ $EXTRA_TOMCAT -gt 0 ]
do
 cp -p /etc/rc.d/init.d/tomcat7 /etc/rc.d/init.d/tomcat7-${EXTRA_TOMCAT}
 chkconfig --del tomcat7-${EXTRA_TOMCAT} 2>&1 > /dev/null
 chkconfig --add tomcat7-${EXTRA_TOMCAT}
 chkconfig tomcat7-${EXTRA_TOMCAT} on
 service tomcat7-${EXTRA_TOMCAT} start
 EXTRA_TOMCAT=`expr ${EXTRA_TOMCAT} - 1`
done
exit 0
