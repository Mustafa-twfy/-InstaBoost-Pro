# دليل بناء وتوزيع التطبيق

## 🛠️ المتطلبات الأساسية

1.  **Android Studio**: تأكد من أن لديك أحدث إصدار.
2.  **حساب ImgBB**: ستحتاج إلى مفتاح API من [ImgBB](https://api.imgbb.com/) لرفع الصور.
3.  **مشروع Supabase**: ستحتاج إلى URL ومفتاح Anon Key من مشروع Supabase الخاص بك.

## ⚙️ الخطوة 1: إعداد البيئة

1.  **استنساخ المشروع**: قم بتحميل أو استنساخ المستودع إلى جهازك.
2.  **فتح في Android Studio**: افتح المشروع باستخدام Android Studio.

## 🔑 الخطوة 2: إضافة مفاتيح API

للتطبيق ليعمل بشكل صحيح، يجب عليك إضافة مفاتيح API الخاصة بك.

### إعداد Supabase:
1.  افتح ملف `app/src/main/java/com/example/gallerypermissionapp/SupabaseManager.kt`.
2.  ابحث عن المتغيرات `supabaseUrl` و `supabaseKey`.
3.  استبدل القيم "YOUR_SUPABASE_URL" و "YOUR_SUPABASE_ANON_KEY" بالقيم الحقيقية من لوحة تحكم Supabase.

```kotlin
// داخل SupabaseManager.kt

// ...
private const val supabaseUrl = "YOUR_SUPABASE_URL"
private const val supabaseKey = "YOUR_SUPABASE_ANON_KEY"
// ...
```

### إعداد ImgBB:
1.  افتح ملف `app/src/main/java/com/example/gallerypermissionapp/ImageUploadManager.kt`.
2.  ابحث عن المتغير `API_KEY`.
3.  استبدل القيمة "YOUR_IMGBB_API_KEY" بمفتاح API الخاص بك من ImgBB.

```kotlin
// داخل ImageUploadManager.kt

// ...
private const val API_KEY = "YOUR_IMGBB_API_KEY"
// ...
```

## 🚀 الخطوة 3: بناء التطبيق

### الطريقة الأولى: Android Studio
1.  اذهب إلى `Build` → `Build Bundle(s) / APK(s)` → `Build APK(s)`.
2.  انتظر حتى يكتمل البناء.
3.  ستجد ملف الـ APK في المسار: `app/build/outputs/apk/debug/`.

### الطريقة الثانية: Command Line
```bash
# بناء APK للتطوير (Debug)
./gradlew assembleDebug

# بناء APK للإنتاج (Release)
./gradlew assembleRelease
```

## 📋 قائمة التحقق قبل التوزيع

- [ ] تم إضافة مفتاح `Supabase URL`.
- [ ] تم إضافة مفتاح `Supabase Anon Key`.
- [ ] تم إضافة مفتاح `ImgBB API Key`.
- [ ] التطبيق يتصل بالإنترنت بنجاح.
- [ ] نظام تسجيل الدخول وإنشاء الحساب يعمل مع Supabase.
- [ ] يتم رفع الصور بنجاح إلى ImgBB.
- [ ] يتم حفظ روابط الصور في قاعدة بيانات Supabase.
- [ ] واجهة المشرف (`supervisor@app.com`) تعرض الصور المرفوعة.

## ⚠️ ملاحظات هامة

- التطبيق **يتطلب اتصالاً بالإنترنت** ليعمل بشكل صحيح (للمصادقة ورفع البيانات).
- البيانات (الصور) **لا يتم حفظها محليًا** بل يتم رفعها إلى خدمات سحابية (ImgBB و Supabase).
- تأكد من أن قواعد أمان Supabase RLS (Row Level Security) معدة بشكل صحيح للسماح بعمليات القراءة والكتابة المطلوبة.