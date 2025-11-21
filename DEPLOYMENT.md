# Deployment Guide - JEE Event Manager

## Prerequisites
- Git installed
- Railway account (free tier)
- Railway CLI

## Deployment Steps

### 1. Install Railway CLI

**Windows (PowerShell):**
```powershell
powershell -c "irm https://railway.app/install.ps1 | iex"
```

**macOS/Linux:**
```bash
curl -fsSL https://railway.app/install.sh | sh
```

Or using npm:
```bash
npm install -g @railway/cli
```

### 2. Login to Railway
```bash
railway login
```
This will open your browser to authenticate.

### 3. Initialize Railway Project
From the project directory:
```bash
cd JEE_event_manager
railway init
```

Choose "Create new project" and give it a name (e.g., "jee-event-manager").

### 4. Add MySQL Database
```bash
railway add --database mysql
```

This will provision a MySQL database and automatically set the `DATABASE_URL` environment variable.

### 5. Deploy the Application
```bash
railway up
```

This command will:
- Build your Docker image
- Push it to Railway
- Deploy your application
- Connect it to the MySQL database

### 6. Generate Domain
```bash
railway domain
```

This will give you a public URL to access your application.

### 7. View Logs
```bash
railway logs
```

### 8. Open in Browser
```bash
railway open
```

## Environment Variables

Railway will automatically set `DATABASE_URL`. If you need to add more:

```bash
railway variables set VARIABLE_NAME=value
```

## Local Testing with Docker

Before deploying, you can test locally:

```bash
# Build and run with Docker Compose
docker-compose up --build

# Access at http://localhost:8080
```

## Troubleshooting

### Check deployment status
```bash
railway status
```

### View environment variables
```bash
railway variables
```

### Redeploy
```bash
railway up --detach
```

### Access Railway dashboard
```bash
railway open
```

## Important Notes

1. **Database Initialization**: The first deployment will take longer as it creates the database schema.
2. **Free Tier Limits**: Railway provides $5 free credit per month.
3. **Cold Starts**: Free tier apps may sleep after inactivity.
4. **Email Service**: Update email credentials in Railway environment variables if needed.

## Post-Deployment Setup

### Initialize Admin User
Once deployed, you'll need to run the admin setup SQL:

```bash
# Connect to Railway MySQL
railway connect mysql

# Then run the SQL from admin_setup.sql
```

Or use the Railway web interface to execute the SQL scripts.

## Cost Optimization

To stay within free tier:
- Use Railway's $5/month free credit
- Monitor usage in the dashboard
- Consider using sleep mode for non-production apps

## Support

For issues:
- Check logs: `railway logs`
- View status: `railway status`
- Railway docs: https://docs.railway.app
