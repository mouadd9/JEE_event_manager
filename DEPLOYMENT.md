# Deployment Guide - Railway.app

## 🚀 Quick Deployment Steps

### 1️⃣ Prepare Your Code (DONE ✅)
- ✅ Dockerfile created
- ✅ Environment variables externalized
- ✅ railway.toml configured

### 2️⃣ Build the Application Locally
```bash
./mvnw clean package
```
This creates `target/jee-event-manager.war`

### 3️⃣ Sign Up & Setup Railway

1. **Go to:** https://railway.app
2. **Sign up** with your GitHub account
3. **Click "New Project"**
4. **Select "Deploy from GitHub repo"**
5. **Choose:** `mouadd9/JEE_event_manager`
6. **Select branch:** `deploy`

### 4️⃣ Add MySQL Database

1. In your Railway project, click **"New"** → **"Database"** → **"Add MySQL"**
2. Railway will automatically create:
   - `MYSQL_URL`
   - `MYSQL_USER`
   - `MYSQL_PASSWORD`
   - `MYSQL_DATABASE`

### 5️⃣ Configure Environment Variables

Go to your app service → **"Variables"** tab → Add these:

```
DB_URL=<copy from Railway MySQL - use MYSQL_URL but change format>
DB_USER=<copy MYSQL_USER>
DB_PASSWORD=<copy MYSQL_PASSWORD>
EMAIL_USERNAME=youssef2003plus@gmail.com
EMAIL_PASSWORD=wyanhkxrkdqpacuu
EMAIL_FROM=youssef2003plus@gmail.com
```

**Important:** Railway's `MYSQL_URL` format needs conversion:
- Railway gives: `mysql://user:pass@host:port/database`
- You need: `jdbc:mysql://host:port/database?useSSL=false&serverTimezone=UTC`

Example conversion:
```
Railway: mysql://root:password123@containers-us-west-123.railway.app:5432/railway
Your DB_URL: jdbc:mysql://containers-us-west-123.railway.app:5432/railway?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
```

### 6️⃣ Deploy!

Railway will automatically:
1. Clone your repo
2. Run `./mvnw clean package` (build WAR)
3. Build Docker image using Dockerfile
4. Start Tomcat container
5. Your app will be live at: `https://your-app.railway.app`

### 7️⃣ Access Your Application

Once deployed, Railway gives you a URL like:
```
https://jee-event-manager-production-xxxx.up.railway.app
```

Your app routes:
- Home: `https://your-url.railway.app/`
- Login: `https://your-url.railway.app/login`
- Register: `https://your-url.railway.app/register`
- Catalogue: `https://your-url.railway.app/catalogue`

---

## 🔄 Redeployment Workflow

When you or teammates make code changes:

1. **Push to `deploy` branch:**
   ```bash
   git add .
   git commit -m "Your changes"
   git push origin deploy
   ```

2. **Railway automatically:**
   - Detects the push
   - Rebuilds the Docker image
   - Deploys new version
   - Zero manual steps needed!

---

## 📊 Railway Free Tier Limits

- ✅ 500 hours/month execution time
- ✅ $5 credit/month
- ✅ 1GB RAM
- ✅ Good for small-medium projects
- ✅ Automatic HTTPS
- ✅ Custom domain support (optional)

---

## 🐛 Troubleshooting

### Build fails?
- Check Railway logs: Click your service → "Deployments" → Click latest deployment → "View Logs"
- Common issue: Missing `target/jee-event-manager.war` → Run `./mvnw clean package` locally first

### Database connection fails?
- Verify `DB_URL`, `DB_USER`, `DB_PASSWORD` are set correctly
- Check Railway MySQL service is running
- Ensure URL format is correct (see step 5)

### Email not sending?
- Verify `EMAIL_USERNAME` and `EMAIL_PASSWORD` are set
- Check Gmail App Password is correct (no spaces)

### App crashes on startup?
- Check Railway logs for Java exceptions
- Verify all environment variables are set
- Check Hibernate schema creation (tables should auto-create)

---

## 🎯 Next Steps After Deployment

1. **Test all features:**
   - Registration with email verification
   - Login
   - Password reset
   - Event creation (as organizer)
   - Event viewing (as participant)

2. **Monitor:**
   - Railway dashboard shows CPU, memory, logs
   - Set up alerts if needed

3. **Team workflow:**
   - Teammates work on feature branches
   - Create PRs to merge into `admin`
   - You merge `admin` → `deploy` when ready to release
   - Railway auto-deploys on push to `deploy`

---

## 📞 Support

If you encounter issues:
- Railway docs: https://docs.railway.app
- Check deployment logs in Railway dashboard
- Verify environment variables are set correctly

---

**Ready to deploy? Follow steps 2-7 above!** 🚀
