# دليل إعداد Supabase للتطبيق

## الخطوات المطلوبة لإعداد Supabase:

### 1. إنشاء مشروع Supabase
1. اذهب إلى [supabase.com](https://supabase.com)
2. سجل دخول أو أنشئ حساب جديد
3. انقر على "New Project"
4. اختر اسم للمشروع (مثل: instaboost-pro)
5. اختر كلمة مرور قوية لقاعدة البيانات
6. اختر المنطقة الأقرب لك
7. انتظر حتى يتم إنشاء المشروع

### 2. الحصول على مفاتيح API
1. في لوحة التحكم، اذهب إلى Settings > API
2. انسخ:
   - **Project URL** (مثل: https://cmjjgicgyubhvdddvnsb.supabase.co)
   - **anon public** key (مثل: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...)

### 3. إنشاء جدول الصور
1. اذهب إلى SQL Editor في لوحة التحكم
2. انسخ محتوى ملف `supabase_setup.sql`
3. اضغط على "Run" لتنفيذ الكود
4. تأكد من إنشاء الجدول بنجاح

### 4. إعداد Storage للصور
1. اذهب إلى Storage في لوحة التحكم
2. انقر على "Create new bucket"
3. أدخل:
   - **Name**: images
   - **Public bucket**: ✅ مفعل
   - **File size limit**: 50MB
   - **Allowed MIME types**: image/*
4. اضغط على "Create bucket"

### 5. إعداد سياسات Storage
1. اذهب إلى Storage > Policies
2. تأكد من وجود السياسات التالية:
   - **Allow public read access to images**: للقراءة العامة
   - **Allow authenticated users to upload images**: للمستخدمين المسجلين
   - **Allow users to delete their own images**: لحذف الصور

### 6. إعداد Authentication
1. اذهب إلى Authentication > Settings
2. تأكد من تفعيل Email Auth
3. يمكنك تعديل رسائل البريد الإلكتروني إذا أردت

### 7. اختبار الاتصال
1. شغل التطبيق
2. جرب إنشاء حساب جديد
3. جرب تسجيل الدخول
4. جرب رفع صورة
5. تحقق من ظهور الصورة في لوحة المشرف

## ملاحظات مهمة:

- تأكد من أن جميع الأذونات تعمل بشكل صحيح
- إذا واجهت مشاكل في الاتصال، تحقق من مفاتيح API
- يمكنك مراقبة الطلبات في قسم Logs في Supabase
- تأكد من أن جدول `images` تم إنشاؤه بنجاح
- تأكد من أن Storage bucket `images` تم إنشاؤه بنجاح

## استكشاف الأخطاء:

### مشكلة في الاتصال:
- تحقق من مفاتيح API
- تأكد من أن المشروع نشط
- تحقق من إعدادات الشبكة

### مشكلة في رفع الصور:
- تحقق من إنشاء Storage bucket
- تأكد من وجود اتصال بالإنترنت
- تحقق من حجم الصورة (يجب أن تكون أقل من 50MB)
- تحقق من سياسات Storage

### مشكلة في عرض الصور:
- تحقق من إنشاء جدول `images`
- تأكد من حفظ روابط الصور في قاعدة البيانات
- تحقق من سياسات الأمان (RLS)
- تحقق من أن Storage bucket عام (public)

### مشكلة في Authentication:
- تحقق من تفعيل Email Auth
- تأكد من صحة البريد الإلكتروني
- تحقق من قوة كلمة المرور (6 أحرف على الأقل) 