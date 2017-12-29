# docker build -t betoffice/tomcat:1.1.0 .
# docker run --expose 8080 -p 8080:8080 --name botomcat -d betoffice/tomcat:1.1.0

FROM tomcat:8.5.24

COPY betoffice-jweb-1.1.0.war /usr/local/tomcat/webapps
COPY context.xml /usr/local/tomcat/conf
COPY mysql-connector-java-5.1.31.jar /usr/local/tomcat/lib

EXPOSE 8080
