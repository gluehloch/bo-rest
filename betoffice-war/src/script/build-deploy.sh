#!/bin/bash

# Another possible maven property: ${maven.build.timestamp}
timestamp=$(date +%s)
scp ./target/betoffice-war-${project.version}.war boprod.tdkb2:~/download/betoffice-war-${project.version}.war

ssh boprod.tdkb2 <<'ENDSSH'
    rm -f ~/webapps/betoffice-war.war
    sleep 15
    cp ~/download/betoffice-war-${project.version}.war ~/webapps/betoffice-war.war
ENDSSH
exit 0;
