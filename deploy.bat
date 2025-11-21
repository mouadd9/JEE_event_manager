@echo off
echo ========================================
echo JEE Event Manager - Railway Deployment
echo ========================================
echo.

echo Step 1: Login to Railway
echo This will open your browser for authentication...
railway login
if %errorlevel% neq 0 (
    echo Login failed! Please try again.
    pause
    exit /b 1
)
echo.

echo Step 2: Initialize Railway Project
railway init
if %errorlevel% neq 0 (
    echo Project initialization failed!
    pause
    exit /b 1
)
echo.

echo Step 3: Add MySQL Database
railway add -d mysql
if %errorlevel% neq 0 (
    echo Database provisioning failed!
    pause
    exit /b 1
)
echo.

echo Step 4: Deploy Application
echo This will build and deploy your application...
railway up
if %errorlevel% neq 0 (
    echo Deployment failed!
    pause
    exit /b 1
)
echo.

echo Step 5: Generate Public URL
railway domain
echo.

echo ========================================
echo Deployment Complete!
echo ========================================
echo.
echo To view your application:
railway open
echo.
echo To view logs:
echo railway logs
echo.
pause
