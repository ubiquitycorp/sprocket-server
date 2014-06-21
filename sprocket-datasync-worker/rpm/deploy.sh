service=sprocket-datasync-worker
if (( $(ps -ef | grep -v grep | grep $service | wc -l) == 0 ))
then
/etc/init.d/$service start
else
/etc/init.d/$service restart
fi