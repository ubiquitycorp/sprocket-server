PUPPETSERVER=ubiquity-provision.ubiquitybc.net
WORKINGDIR=`pwd`


all:	sprocket.json
sprocket.json:	sprocket.json.in
	[ "${CLOUDNAME}" ] && [ "${PUPPETSERVER}" ] && cat sprocket.json.in | sed "s/CLOUDNAME/${CLOUDNAME}/g" | \
	sed "s/PUPPETSERVER/${PUPPETSERVER}/g" | sed "s/CUSTOMER/${CUSTOMER}/g" > sprocket.json ||  echo "Please make sure to set CLOUDNAME"
        aws cloudformation create-stack --stack-name ${CLOUDNAME}-${CUSTOMER} --template-body file://$WORKINGDIR/sprocket.json

clean:	
	rm sprocket.json
