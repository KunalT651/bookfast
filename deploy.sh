#!/bin/bash

echo "🚀 BookFast Deployment Script"
echo "=============================="

# Check if we're in the right directory
if [ ! -d "frontEnd" ] || [ ! -d "backEnd" ]; then
    echo "❌ Error: Please run this script from the BookFast root directory"
    exit 1
fi

echo "📋 Step 1: Preparing for deployment..."

# Update backend configuration for production
echo "🔧 Updating backend configuration..."
sed -i 's|http://localhost:4200|https://your-frontend-url.vercel.app|g' backEnd/src/main/resources/application.properties

echo "📦 Step 2: Building frontend..."
cd frontEnd
npm install
npm run build
cd ..

echo "📦 Step 3: Building backend..."
cd backEnd
./mvnw clean package -DskipTests
cd ..

echo "✅ Build completed!"
echo ""
echo "📋 Next steps:"
echo "1. Push your code to GitHub:"
echo "   git add ."
echo "   git commit -m 'Prepare for deployment'"
echo "   git push origin main"
echo ""
echo "2. Deploy to Vercel:"
echo "   - Go to vercel.com"
echo "   - Import your GitHub repository"
echo "   - Set root directory to 'frontEnd'"
echo ""
echo "3. Deploy to Render:"
echo "   - Go to render.com"
echo "   - Create new Web Service"
echo "   - Set root directory to 'backEnd'"
echo "   - Add PostgreSQL database"
echo ""
echo "4. Update environment variables in both platforms"
echo "5. Update Google OAuth redirect URIs"
echo ""
echo "📖 See DEPLOYMENT.md for detailed instructions"
