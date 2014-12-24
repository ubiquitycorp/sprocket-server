service=sprocket-datasync-master-worker
if [ `/usr/bin/pgrep -f ${service}.jar | wc -l` -eq 0 ]
then
/etc/init.d/$service start
else
/etc/init.d/$service restart
fi
