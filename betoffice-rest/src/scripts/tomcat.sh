#!/bin/bash
#
# tomcat
#
# chkconfig:
# description:  Start up the Tomcat servlet engine.

RETVAL=$?
JAVA_HOME="/opt/devtools/java/jdk1.8.0_91"
CATALINA_HOME="/opt/devtools/tomcat/tomcat9-PROD"
JAVA_OPTS="-Xms64M -Xmx128M"

export JAVA_HOME
export CATALINA_HOME
export JAVA_OPTS

case "$1" in
 start)
        if [ -f $CATALINA_HOME/bin/startup.sh ];
          then
            echo $"Starting Tomcat"
            /bin/su tomcat $CATALINA_HOME/bin/startup.sh
        fi
        ;;
 stop)
        if [ -f $CATALINA_HOME/bin/shutdown.sh ];
          then
            echo $"Stopping Tomcat"
            /bin/su tomcat $CATALINA_HOME/bin/shutdown.sh
        fi
        ;;
 *)
        echo $"Usage: $0 {start|stop}"
        exit 1
        ;;
esac

exit $RETVAL
