#!/bin/bash

# Wait for MySQL to be ready
echo "Waiting for MySQL to be ready..."
MAX_RETRIES=30
RETRY_COUNT=0

until mysql -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" -p"$DB_PASS" -e "SELECT 1" &>/dev/null; do
    RETRY_COUNT=$((RETRY_COUNT + 1))
    if [ $RETRY_COUNT -ge $MAX_RETRIES ]; then
        echo "Failed to connect to MySQL after $MAX_RETRIES attempts"
        exit 1
    fi
    echo "MySQL is unavailable - sleeping (attempt $RETRY_COUNT/$MAX_RETRIES)"
    sleep 2
done

echo "MySQL is ready!"

# Check if database needs initialization
TABLES_COUNT=$(mysql -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" -p"$DB_PASS" "$DB_NAME" -e "SHOW TABLES;" 2>/dev/null | wc -l)

if [ "$TABLES_COUNT" -le 1 ]; then
    echo "Database is empty. Initializing with event_managerVf.sql..."
    mysql -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" -p"$DB_PASS" "$DB_NAME" < /usr/local/tomcat/event_managerVf.sql

    if [ $? -eq 0 ]; then
        echo "Database initialized successfully!"
    else
        echo "Failed to initialize database"
        exit 1
    fi
else
    echo "Database already initialized (found $TABLES_COUNT tables). Skipping initialization."
fi
