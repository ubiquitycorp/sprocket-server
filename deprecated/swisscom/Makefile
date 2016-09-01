PUPPETSERVER=ubiquity-provision.ubiquitybc.net
CUSTOMER=swisscom

all:	sprocket.json
sprocket.json:	sprocket.json.in
	[ "${CLOUDNAME}" ] && [ "${PUPPETSERVER}" ] && cat sprocket.json.in | sed "s/CLOUDNAME/${CLOUDNAME}/g" | \
	sed "s/PUPPETSERVER/${PUPPETSERVER}/g" | sed "s/CUSTOMER/${CUSTOMER}/g" > sprocket.json ||  echo "Please make sure to set CLOUDNAME"

clean:	
	rm sprocket.json
