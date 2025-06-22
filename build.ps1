# بناء تطبيق InstaBoost Pro
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   بناء تطبيق InstaBoost Pro" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# التحقق من وجود Java
try {
    $javaVersion = java -version 2>&1
    Write-Host "✅ Java مثبت بنجاح" -ForegroundColor Green
    Write-Host "إصدار Java: $($javaVersion[0])" -ForegroundColor Gray
} catch {
    Write-Host "❌ خطأ: Java غير مثبت أو غير موجود في PATH" -ForegroundColor Red
    Write-Host ""
    Write-Host "يرجى تثبيت Java JDK 17 من:" -ForegroundColor Yellow
    Write-Host "https://adoptium.net/temurin/releases/" -ForegroundColor Blue
    Write-Host ""
    Write-Host "أو تعيين JAVA_HOME في PowerShell:" -ForegroundColor Yellow
    Write-Host '$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"' -ForegroundColor Gray
    Write-Host '$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"' -ForegroundColor Gray
    Write-Host ""
    Read-Host "اضغط Enter للخروج"
    exit 1
}

Write-Host ""

# التحقق من وجود gradlew
if (-not (Test-Path "gradlew.bat")) {
    Write-Host "❌ خطأ: ملف gradlew.bat غير موجود" -ForegroundColor Red
    Write-Host "تأكد من أنك في مجلد المشروع الصحيح" -ForegroundColor Yellow
    Read-Host "اضغط Enter للخروج"
    exit 1
}

Write-Host "✅ ملف gradlew موجود" -ForegroundColor Green
Write-Host ""

Write-Host "🚀 بدء عملية البناء..." -ForegroundColor Yellow
Write-Host ""

# بناء التطبيق
try {
    & .\gradlew.bat assembleDebug
    $buildSuccess = $LASTEXITCODE -eq 0
} catch {
    $buildSuccess = $false
    Write-Host "❌ خطأ في عملية البناء: $($_.Exception.Message)" -ForegroundColor Red
}

if ($buildSuccess) {
    Write-Host ""
    Write-Host "✅ تم بناء التطبيق بنجاح!" -ForegroundColor Green
    Write-Host ""
    Write-Host "📱 موقع ملف APK:" -ForegroundColor Cyan
    Write-Host "app\build\outputs\apk\debug\app-debug.apk" -ForegroundColor Gray
    Write-Host ""
    
    # التحقق من وجود ملف APK
    $apkPath = "app\build\outputs\apk\debug\app-debug.apk"
    if (Test-Path $apkPath) {
        Write-Host "✅ ملف APK موجود" -ForegroundColor Green
        $fileInfo = Get-Item $apkPath
        Write-Host "حجم الملف: $([math]::Round($fileInfo.Length / 1MB, 2)) MB" -ForegroundColor Gray
        Write-Host "تاريخ الإنشاء: $($fileInfo.CreationTime)" -ForegroundColor Gray
    } else {
        Write-Host "❌ ملف APK غير موجود" -ForegroundColor Red
    }
} else {
    Write-Host ""
    Write-Host "❌ فشل في بناء التطبيق" -ForegroundColor Red
    Write-Host "تحقق من الأخطاء أعلاه" -ForegroundColor Yellow
}

Write-Host ""
Read-Host "اضغط Enter للخروج" 