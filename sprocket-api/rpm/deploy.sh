sleep 5
EXTRA_TOMCAT=`expr ${tomcat.processes} - 1`
EXTRA_TOMCAT=3
service tomcat7 start

if [ ! "$EXTRA_TOMCAT" ]
then
 EXTRA_TOMCAT=0
fi

while [ $EXTRA_TOMCAT -gt 0 ]
do
 cp -p /etc/rc.d/init.d/tomcat7 /etc/rc.d/init.d/tomcat7-${EXTRA_TOMCAT}
 chkconfig --add tomcat7-${EXTRA_TOMCAT}
 chkconfig tomcat7-${EXTRA_TOMCAT} on
 service tomcat7-${EXTRA_TOMCAT} start
 EXTRA_TOMCAT=`expr ${EXTRA_TOMCAT} - 1`
done

