# رفع المشروع إلى GitHub
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   رفع المشروع إلى GitHub" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# التحقق من وجود Git
try {
    $gitVersion = git --version
    Write-Host "✅ Git مثبت بنجاح" -ForegroundColor Green
    Write-Host "إصدار Git: $gitVersion" -ForegroundColor Gray
} catch {
    Write-Host "❌ خطأ: Git غير مثبت" -ForegroundColor Red
    Write-Host ""
    Write-Host "يرجى تثبيت Git من:" -ForegroundColor Yellow
    Write-Host "https://git-scm.com/downloads" -ForegroundColor Blue
    Write-Host ""
    Read-Host "اضغط Enter للخروج"
    exit 1
}

Write-Host ""

# التحقق من وجود مجلد .git
if (-not (Test-Path ".git")) {
    Write-Host "🔧 تهيئة Git..." -ForegroundColor Yellow
    git init
    Write-Host "✅ تم تهيئة Git" -ForegroundColor Green
    Write-Host ""
}

# إضافة جميع الملفات
Write-Host "📁 إضافة الملفات..." -ForegroundColor Yellow
git add .
Write-Host "✅ تم إضافة جميع الملفات" -ForegroundColor Green
Write-Host ""

# طلب رسالة الـ commit
$commitMessage = Read-Host "أدخل رسالة الـ commit (أو اضغط Enter للرسالة الافتراضية)"
if ([string]::IsNullOrWhiteSpace($commitMessage)) {
    $commitMessage = "🚀 تحديث InstaBoost Pro"
}

# عمل commit
Write-Host "💾 عمل commit..." -ForegroundColor Yellow
git commit -m $commitMessage
Write-Host "✅ تم عمل commit" -ForegroundColor Green
Write-Host ""

# التحقق من وجود remote
try {
    $remoteUrl = git remote get-url origin 2>$null
    if ($LASTEXITCODE -ne 0) {
        throw "No remote found"
    }
    Write-Host "✅ تم العثور على remote repository" -ForegroundColor Green
    Write-Host "الرابط: $remoteUrl" -ForegroundColor Gray
} catch {
    Write-Host ""
    Write-Host "⚠️  لم يتم العثور على remote repository" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "يرجى إنشاء مستودع على GitHub أولاً:" -ForegroundColor Cyan
    Write-Host "1. اذهب إلى https://github.com" -ForegroundColor White
    Write-Host "2. انقر على 'New repository'" -ForegroundColor White
    Write-Host "3. أدخل اسم المستودع: InstaBoost-Pro" -ForegroundColor White
    Write-Host "4. اختر Public" -ForegroundColor White
    Write-Host "5. انقر Create repository" -ForegroundColor White
    Write-Host ""
    
    $githubUsername = Read-Host "أدخل اسم المستخدم في GitHub"
    $repoName = Read-Host "أدخل اسم المستودع (أو اضغط Enter للاسم الافتراضي)"
    if ([string]::IsNullOrWhiteSpace($repoName)) {
        $repoName = "InstaBoost-Pro"
    }
    
    Write-Host ""
    Write-Host "🔗 إضافة remote repository..." -ForegroundColor Yellow
    git remote add origin "https://github.com/$githubUsername/$repoName.git"
    Write-Host "✅ تم إضافة remote repository" -ForegroundColor Green
    Write-Host ""
}

# رفع الكود
Write-Host "🚀 رفع الكود إلى GitHub..." -ForegroundColor Yellow
git branch -M main
git push -u origin main

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "✅ تم رفع المشروع بنجاح!" -ForegroundColor Green
    Write-Host ""
    Write-Host "📋 الخطوات التالية:" -ForegroundColor Cyan
    Write-Host "1. اذهب إلى مستودعك على GitHub" -ForegroundColor White
    Write-Host "2. انتقل إلى تبويب Actions" -ForegroundColor White
    Write-Host "3. انتظر اكتمال البناء" -ForegroundColor White
    Write-Host "4. انزل إلى Artifacts" -ForegroundColor White
    Write-Host "5. انقر على InstaBoost-Pro-Debug للتحميل" -ForegroundColor White
    Write-Host ""
    
    $remoteUrl = git remote get-url origin
    Write-Host "🌐 رابط المستودع:" -ForegroundColor Cyan
    Write-Host $remoteUrl -ForegroundColor Blue
} else {
    Write-Host ""
    Write-Host "❌ فشل في رفع المشروع" -ForegroundColor Red
    Write-Host "تحقق من:" -ForegroundColor Yellow
    Write-Host "- صحة بيانات GitHub" -ForegroundColor White
    Write-Host "- وجود اتصال بالإنترنت" -ForegroundColor White
    Write-Host "- صحة رابط المستودع" -ForegroundColor White
}

Write-Host ""
Read-Host "اضغط Enter للخروج" 