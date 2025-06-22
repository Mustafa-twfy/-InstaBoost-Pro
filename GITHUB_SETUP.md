# 🚀 دليل رفع المشروع إلى GitHub

## الخطوات المطلوبة لرفع المشروع:

### 1. إنشاء مستودع جديد على GitHub

1. اذهب إلى [GitHub.com](https://github.com)
2. انقر على زر **"New"** أو **"+"** ثم **"New repository"**
3. أدخل اسم المستودع: `InstaBoost-Pro`
4. اختر **"Public"** (عام)
5. **لا** تضع علامة على "Add a README file"
6. انقر **"Create repository"**

### 2. تهيئة Git في المشروع المحلي

افتح PowerShell أو Command Prompt في مجلد المشروع واكتب:

```bash
# تهيئة Git
git init

# إضافة جميع الملفات
git add .

# عمل commit أولي
git commit -m "🚀 الإصدار الأول من InstaBoost Pro"

# إضافة المستودع البعيد (استبدل YOUR_USERNAME باسم المستخدم)
git remote add origin https://github.com/YOUR_USERNAME/InstaBoost-Pro.git

# رفع الكود
git branch -M main
git push -u origin main
```

### 3. تفعيل GitHub Actions

بعد رفع المشروع:

1. اذهب إلى تبويب **"Actions"** في GitHub
2. ستجد workflow جاهز
3. انقر **"Run workflow"** لبدء البناء الأول

### 4. تحميل APK

بعد اكتمال البناء:

1. اذهب إلى تبويب **"Actions"**
2. انقر على آخر workflow مكتمل
3. انزل إلى **"Artifacts"**
4. انقر على **"InstaBoost-Pro-Debug"** للتحميل

## 🔄 التحديثات المستقبلية

لرفع تحديثات جديدة:

```bash
# إضافة التغييرات
git add .

# عمل commit
git commit -m "✨ تحديث جديد: [وصف التحديث]"

# رفع التحديثات
git push origin main
```

سيتم البناء تلقائياً وإنشاء release جديد.

## 📱 الوصول السريع للـ APK

### من Releases:
1. اذهب إلى تبويب **"Releases"**
2. انقر على أحدث إصدار
3. انزل إلى **"Assets"**
4. انقر على **"app-debug.apk"**

### من Actions:
1. اذهب إلى تبويب **"Actions"**
2. انقر على آخر workflow مكتمل
3. انزل إلى **"Artifacts"**
4. انقر على **"InstaBoost-Pro-Debug"**

## 🛠️ استكشاف الأخطاء

### مشكلة: Git غير مثبت
```bash
# تحميل Git من:
# https://git-scm.com/downloads
```

### مشكلة: خطأ في الرفع
```bash
# تأكد من صحة رابط المستودع
git remote -v

# إعادة تعيين الرابط إذا لزم الأمر
git remote set-url origin https://github.com/YOUR_USERNAME/InstaBoost-Pro.git
```

### مشكلة: فشل في البناء
- تحقق من تبويب **"Actions"** لرؤية الأخطاء
- تأكد من أن جميع الملفات مرفوعة
- تحقق من صحة ملف `build.gradle`

## 📋 نصائح مهمة

1. **استخدم أسماء واضحة للـ commits**
2. **تحقق من Actions بعد كل push**
3. **احتفظ بنسخة احتياطية من APK**
4. **راجع الأخطاء في Actions إذا فشل البناء**

## 🎯 المزايا

- ✅ **بناء تلقائي** عند كل تحديث
- ✅ **Releases منظمة** مع وصف مفصل
- ✅ **Artifacts محفوظة** لمدة 30 يوم
- ✅ **تعليقات تلقائية** على Pull Requests
- ✅ **Cache محسن** للبناء السريع

---

**بعد اتباع هذه الخطوات، ستحصل على APK جاهز للتثبيت! 🚀** 