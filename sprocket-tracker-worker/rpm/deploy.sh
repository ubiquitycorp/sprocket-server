service=sprocket-tracker-worker
if (( $(ps -ef | grep -v grep | grep $service | wc -l) == 0 ))
then
/etc/init.d/$service start
fi