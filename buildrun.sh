#!/bin/bash

clear
REPOSITORY=$1
VERSION=$2
DEV_MODE=$3

if [ -z "$REPOSITORY" ]
then
    REPOSITORY=dtdemos
fi

if [ -z "$VERSION" ]
then
    VERSION=1
fi

if [ -z "$DEV_MODE" ]
then
    DEV_MODE=false
fi

IMAGE=dt-orders-order-service
FULLIMAGE=$REPOSITORY/$IMAGE:$VERSION

echo "Building: $FULLIMAGE"
./mvnw -e clean package -Dmaven.test.skip=true
./writeManifest.sh

docker build -t $FULLIMAGE . --build-arg APP_VERSION=$VERSION

echo ""
echo "========================================================"
echo "Ready to run $FULLIMAGE ?"
echo "========================================================"

read -rsp "Press ctrl-c to abort. Press any key to continue"
echo ""
echo "access app @ http://localhost"
echo "" 
docker run -it -p 80:8080 \
  --env CUSTOMER_SERVICE_DOMAIN=172.17.0.1 \
  --env CUSTOMER_SERVICE_PORT=8181 \
  --env CATALOG_SERVICE_DOMAIN=172.17.0.1 \
  --env CATALOG_SERVICE_PORT=8182 \
  --env DEV_MODE=$DEV_MODE \
  $FULLIMAGE 
