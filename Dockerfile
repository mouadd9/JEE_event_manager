# Use Tomcat 10 with JDK 21
FROM tomcat:10.1-jdk21

# Remove default Tomcat webapps
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy the WAR file to Tomcat webapps as ROOT.war (so it runs on /)
COPY target/jee-event-manager.war /usr/local/tomcat/webapps/ROOT.war

# Expose port 8080 (Tomcat default)
EXPOSE 8080

# Set environment variables for JVM (optional tuning)
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Start Tomcat
CMD ["catalina.sh", "run"]
