#!/bin/bash

echo "========================================"
echo "JEE Event Manager - Railway Deployment"
echo "========================================"
echo ""

echo "Step 1: Login to Railway"
echo "This will open your browser for authentication..."
railway login
if [ $? -ne 0 ]; then
    echo "Login failed! Please try again."
    exit 1
fi
echo ""

echo "Step 2: Initialize Railway Project"
railway init
if [ $? -ne 0 ]; then
    echo "Project initialization failed!"
    exit 1
fi
echo ""

echo "Step 3: Add MySQL Database"
railway add -d mysql
if [ $? -ne 0 ]; then
    echo "Database provisioning failed!"
    exit 1
fi
echo ""

echo "Step 4: Deploy Application"
echo "This will build and deploy your application..."
railway up
if [ $? -ne 0 ]; then
    echo "Deployment failed!"
    exit 1
fi
echo ""

echo "Step 5: Generate Public URL"
railway domain
echo ""

echo "========================================"
echo "Deployment Complete!"
echo "========================================"
echo ""
echo "To view your application:"
echo "railway open"
echo ""
echo "To view logs:"
echo "railway logs"
echo ""
