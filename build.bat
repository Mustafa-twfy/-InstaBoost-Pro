@echo off
echo ========================================
echo    بناء تطبيق InstaBoost Pro
echo ========================================
echo.

REM التحقق من وجود Java
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ خطأ: Java غير مثبت أو غير موجود في PATH
    echo.
    echo يرجى تثبيت Java JDK 17 من:
    echo https://adoptium.net/temurin/releases/
    echo.
    echo أو تعيين JAVA_HOME:
    echo set JAVA_HOME=C:\Program Files\Java\jdk-17
    echo set PATH=%%JAVA_HOME%%\bin;%%PATH%%
    echo.
    pause
    exit /b 1
)

echo ✅ Java مثبت بنجاح
echo.

REM التحقق من وجود gradlew
if not exist "gradlew.bat" (
    echo ❌ خطأ: ملف gradlew.bat غير موجود
    echo تأكد من أنك في مجلد المشروع الصحيح
    pause
    exit /b 1
)

echo ✅ ملف gradlew موجود
echo.

echo 🚀 بدء عملية البناء...
echo.

REM بناء التطبيق
gradlew.bat assembleDebug

if %errorlevel% equ 0 (
    echo.
    echo ✅ تم بناء التطبيق بنجاح!
    echo.
    echo 📱 موقع ملف APK:
    echo app\build\outputs\apk\debug\app-debug.apk
    echo.
    echo 📋 للتحقق من وجود الملف:
    if exist "app\build\outputs\apk\debug\app-debug.apk" (
        echo ✅ ملف APK موجود
        dir "app\build\outputs\apk\debug\app-debug.apk"
    ) else (
        echo ❌ ملف APK غير موجود
    )
) else (
    echo.
    echo ❌ فشل في بناء التطبيق
    echo تحقق من الأخطاء أعلاه
)

echo.
pause 