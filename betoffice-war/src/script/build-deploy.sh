#!/bin/bash
timestamp=$(date +%s)
scp ./betoffice-war/target/betoffice-war-${project.version}.war boprod.tdkb2:~/download/betoffice-war-${project.version}-${maven.build.timestamp}.war

ssh boprod.tdkb2 <<'ENDSSH'
    cd ~/webapps
    cp betoffice-war.war betoffice-war.war-${maven.build.timestamp}
    rm -f betoffice-war.war
    sleep 10
    cp ~/download/betoffice-war-${project.version}-${maven.build.timestamp}.war ~/webapps/betoffice-war.war
ENDSSH
exit 0;
