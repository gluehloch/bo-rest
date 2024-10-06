#!/bin/bash
timestamp=$(date +%s)
scp ./target/betoffice-war-${project.version}.war boprod.tdkb2:~/download/betoffice-war-${project.version}-${maven.build.timestamp}.war

ssh boprod.tdkb2 <<'ENDSSH'
    rm -f ~/webapps/betoffice-war.war
    sleep 15
    cp ~/download/betoffice-war-${project.version}-${maven.build.timestamp}.war ~/webapps/betoffice-war.war
ENDSSH
exit 0;
