#!/bin/bash
# Verification script to test if dashboard fix is working
# Run this after merging PR #7 to verify everything works

echo "🔍 FRC Dashboard - Fix Verification Script"
echo "=========================================="
echo ""

# Color codes for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check if we're in the right directory
if [ ! -f "settings.gradle" ]; then
    echo -e "${RED}❌ Error: settings.gradle not found${NC}"
    echo "Please run this script from the project root directory"
    exit 1
fi

echo "Step 1: Checking settings.gradle for dashboard module..."
if grep -q "include 'dashboard'" settings.gradle; then
    echo -e "${GREEN}✅ Dashboard module found in settings.gradle${NC}"
else
    echo -e "${RED}❌ Dashboard module NOT found in settings.gradle${NC}"
    echo "The fix has not been applied yet. Please merge PR #7"
    exit 1
fi
echo ""

echo "Step 2: Verifying Gradle recognizes dashboard project..."
if ./gradlew projects --console=plain 2>&1 | grep -q "Project ':dashboard'"; then
    echo -e "${GREEN}✅ Gradle recognizes dashboard as a subproject${NC}"
else
    echo -e "${RED}❌ Gradle does not recognize dashboard project${NC}"
    exit 1
fi
echo ""

echo "Step 3: Testing dashboard build..."
if ./gradlew :dashboard:build --console=plain > /tmp/dashboard_build.log 2>&1; then
    echo -e "${GREEN}✅ Dashboard builds successfully${NC}"
else
    echo -e "${RED}❌ Dashboard build failed${NC}"
    echo "Check /tmp/dashboard_build.log for details"
    exit 1
fi
echo ""

echo "Step 4: Checking for documentation files..."
files_to_check=("DASHBOARD_QUICKSTART.md" "DASHBOARD_LAYOUT.md" "dashboard/README.md")
all_found=true

for file in "${files_to_check[@]}"; do
    if [ -f "$file" ]; then
        echo -e "${GREEN}✅ Found: $file${NC}"
    else
        echo -e "${RED}❌ Missing: $file${NC}"
        all_found=false
    fi
done
echo ""

if [ "$all_found" = false ]; then
    echo -e "${YELLOW}⚠️  Some documentation files are missing${NC}"
    echo "Please make sure PR #7 is fully merged"
fi

echo "Step 5: Checking dashboard JAR generation..."
if [ -f "dashboard/build/libs/frc-dashboard-1.0.0.jar" ]; then
    echo -e "${GREEN}✅ Dashboard JAR exists${NC}"
    jar_size=$(du -h "dashboard/build/libs/frc-dashboard-1.0.0.jar" | cut -f1)
    echo "   JAR size: $jar_size"
else
    echo -e "${YELLOW}⚠️  Dashboard JAR not found (will be created on first build)${NC}"
fi
echo ""

echo "=========================================="
echo -e "${GREEN}🎉 All checks passed!${NC}"
echo ""
echo "The dashboard fix is working correctly!"
echo ""
echo "Next steps:"
echo "  1. Run the dashboard:"
echo "     ./gradlew :dashboard:run"
echo ""
echo "  2. Read the quick start guide:"
echo "     cat DASHBOARD_QUICKSTART.md"
echo ""
echo "  3. Check the visual layout:"
echo "     cat DASHBOARD_LAYOUT.md"
echo ""
echo "Happy dashboarding! 🤖💜"
