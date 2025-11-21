#!/bin/bash

# Parse Railway MySQL DATABASE_URL
# Format: mysql://user:password@host:port/database
if [ -n "$DATABASE_URL" ]; then
    echo "Configuring database from DATABASE_URL..."

    # Extract components from DATABASE_URL
    DB_USER=$(echo $DATABASE_URL | sed -n 's/.*:\/\/\([^:]*\):.*/\1/p')
    DB_PASS=$(echo $DATABASE_URL | sed -n 's/.*:\/\/[^:]*:\([^@]*\)@.*/\1/p')
    DB_HOST=$(echo $DATABASE_URL | sed -n 's/.*@\([^:]*\):.*/\1/p')
    DB_PORT=$(echo $DATABASE_URL | sed -n 's/.*:\([0-9]*\)\/.*/\1/p')
    DB_NAME=$(echo $DATABASE_URL | sed -n 's/.*\/\([^?]*\).*/\1/p')

    JDBC_URL="jdbc:mysql://${DB_HOST}:${DB_PORT}/${DB_NAME}?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"

    echo "Database configuration:"
    echo "  Host: $DB_HOST"
    echo "  Port: $DB_PORT"
    echo "  Database: $DB_NAME"
    echo "  User: $DB_USER"

    # Export variables for init-db.sh
    export DB_HOST DB_PORT DB_NAME DB_USER DB_PASS

    # Initialize database with event_managerVf.sql
    /usr/local/tomcat/bin/init-db.sh

    # Update persistence.xml
    sed -i "s|jdbc:mysql://localhost:3306/event_manager[^\"]*|$JDBC_URL|g" \
        /usr/local/tomcat/webapps/ROOT/WEB-INF/classes/META-INF/persistence.xml
    sed -i "s|<property name=\"jakarta.persistence.jdbc.user\" value=\"[^\"]*\"/>|<property name=\"jakarta.persistence.jdbc.user\" value=\"$DB_USER\"/>|g" \
        /usr/local/tomcat/webapps/ROOT/WEB-INF/classes/META-INF/persistence.xml
    sed -i "s|<property name=\"jakarta.persistence.jdbc.password\" value=\"[^\"]*\"/>|<property name=\"jakarta.persistence.jdbc.password\" value=\"$DB_PASS\"/>|g" \
        /usr/local/tomcat/webapps/ROOT/WEB-INF/classes/META-INF/persistence.xml

    # Update context.xml
    sed -i "s|url=\"jdbc:mysql://localhost:3306/event_manager[^\"]*\"|url=\"$JDBC_URL\"|g" \
        /usr/local/tomcat/conf/Catalina/localhost/ROOT.xml
    sed -i "s|username=\"[^\"]*\"|username=\"$DB_USER\"|g" \
        /usr/local/tomcat/conf/Catalina/localhost/ROOT.xml
    sed -i "s|password=\"[^\"]*\"|password=\"$DB_PASS\"|g" \
        /usr/local/tomcat/conf/Catalina/localhost/ROOT.xml

    echo "Database configuration updated successfully!"
else
    echo "No DATABASE_URL found, using default configuration"
fi

# Start Tomcat
exec catalina.sh run
