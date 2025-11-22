# Multi-stage build for Jakarta EE application
FROM maven:3.9.6-eclipse-temurin-21 AS builder

# Set working directory
WORKDIR /app

# Copy pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Production stage
FROM tomcat:10.1-jdk21

# Remove default webapps
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy the WAR file from builder stage
COPY --from=builder /app/target/jee-event-manager.war /usr/local/tomcat/webapps/ROOT.war

# Copy context configuration
COPY src/main/webapp/META-INF/context.xml /usr/local/tomcat/conf/Catalina/localhost/ROOT.xml

# Create startup script
RUN echo '#!/bin/bash\n\
set -e\n\
\n\
# Extract WAR file\n\
cd /usr/local/tomcat/webapps\n\
mkdir -p ROOT\n\
cd ROOT\n\
jar -xf ../ROOT.war\n\
cd ..\n\
rm -f ROOT.war\n\
\n\
# Build JDBC URLs (one for XML with &amp;, one for plain text)\n\
JDBC_URL_XML="jdbc:mysql://${MYSQLHOST}:${MYSQLPORT}/${MYSQLDATABASE}?createDatabaseIfNotExist=true\\&amp;useSSL=false\\&amp;serverTimezone=UTC\\&amp;allowPublicKeyRetrieval=true"\n\
JDBC_URL="jdbc:mysql://${MYSQLHOST}:${MYSQLPORT}/${MYSQLDATABASE}?createDatabaseIfNotExist=true\\&useSSL=false\\&serverTimezone=UTC\\&allowPublicKeyRetrieval=true"\n\
\n\
# Update persistence.xml with XML-escaped URL\n\
sed -i "s|PLACEHOLDER_JDBC_URL|${JDBC_URL_XML}|g" /usr/local/tomcat/webapps/ROOT/WEB-INF/classes/META-INF/persistence.xml\n\
sed -i "s|PLACEHOLDER_DB_USER|${MYSQLUSER}|g" /usr/local/tomcat/webapps/ROOT/WEB-INF/classes/META-INF/persistence.xml\n\
sed -i "s|PLACEHOLDER_DB_PASS|${MYSQLPASSWORD}|g" /usr/local/tomcat/webapps/ROOT/WEB-INF/classes/META-INF/persistence.xml\n\
\n\
# Update context.xml with XML-escaped URL\n\
sed -i "s|PLACEHOLDER_JDBC_URL|${JDBC_URL_XML}|g" /usr/local/tomcat/conf/Catalina/localhost/ROOT.xml\n\
sed -i "s|PLACEHOLDER_DB_USER|${MYSQLUSER}|g" /usr/local/tomcat/conf/Catalina/localhost/ROOT.xml\n\
sed -i "s|PLACEHOLDER_DB_PASS|${MYSQLPASSWORD}|g" /usr/local/tomcat/conf/Catalina/localhost/ROOT.xml\n\
\n\
# Start Tomcat\n\
exec catalina.sh run\n\
' > /usr/local/tomcat/bin/start.sh && chmod +x /usr/local/tomcat/bin/start.sh

# Set environment variables
ENV CATALINA_OPTS="-Xms512m -Xmx1024m"

# Expose port
EXPOSE 8080

# Start Tomcat with database configuration
CMD ["/usr/local/tomcat/bin/start.sh"]
