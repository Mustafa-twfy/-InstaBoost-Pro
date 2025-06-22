@echo off
echo ========================================
echo    رفع المشروع إلى GitHub
echo ========================================
echo.

REM التحقق من وجود Git
git --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ خطأ: Git غير مثبت
    echo.
    echo يرجى تثبيت Git من:
    echo https://git-scm.com/downloads
    echo.
    pause
    exit /b 1
)

echo ✅ Git مثبت بنجاح
echo.

REM التحقق من وجود مجلد .git
if not exist ".git" (
    echo 🔧 تهيئة Git...
    git init
    echo ✅ تم تهيئة Git
    echo.
)

REM إضافة جميع الملفات
echo 📁 إضافة الملفات...
git add .
echo ✅ تم إضافة جميع الملفات
echo.

REM طلب رسالة الـ commit
set /p commit_message="أدخل رسالة الـ commit (أو اضغط Enter للرسالة الافتراضية): "
if "%commit_message%"=="" set commit_message="🚀 تحديث InstaBoost Pro"

REM عمل commit
echo 💾 عمل commit...
git commit -m "%commit_message%"
echo ✅ تم عمل commit
echo.

REM التحقق من وجود remote
git remote -v >nul 2>&1
if %errorlevel% neq 0 (
    echo.
    echo ⚠️  لم يتم العثور على remote repository
    echo.
    echo يرجى إنشاء مستودع على GitHub أولاً:
    echo 1. اذهب إلى https://github.com
    echo 2. انقر على "New repository"
    echo 3. أدخل اسم المستودع: InstaBoost-Pro
    echo 4. اختر Public
    echo 5. انقر Create repository
    echo.
    set /p github_username="أدخل اسم المستخدم في GitHub: "
    set /p repo_name="أدخل اسم المستودع (أو اضغط Enter للاسم الافتراضي): "
    if "%repo_name%"=="" set repo_name=InstaBoost-Pro
    
    echo.
    echo 🔗 إضافة remote repository...
    git remote add origin https://github.com/%github_username%/%repo_name%.git
    echo ✅ تم إضافة remote repository
    echo.
)

REM رفع الكود
echo 🚀 رفع الكود إلى GitHub...
git branch -M main
git push -u origin main

if %errorlevel% equ 0 (
    echo.
    echo ✅ تم رفع المشروع بنجاح!
    echo.
    echo 📋 الخطوات التالية:
    echo 1. اذهب إلى مستودعك على GitHub
    echo 2. انتقل إلى تبويب Actions
    echo 3. انتظر اكتمال البناء
    echo 4. انزل إلى Artifacts
    echo 5. انقر على InstaBoost-Pro-Debug للتحميل
    echo.
    echo 🌐 رابط المستودع:
    git remote get-url origin
) else (
    echo.
    echo ❌ فشل في رفع المشروع
    echo تحقق من:
    echo - صحة بيانات GitHub
    echo - وجود اتصال بالإنترنت
    echo - صحة رابط المستودع
)

echo.
pause 