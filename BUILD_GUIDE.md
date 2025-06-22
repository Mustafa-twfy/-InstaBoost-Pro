# دليل بناء التطبيق بدون Android Studio

## الطريقة الأولى: استخدام خدمات البناء عبر الإنترنت

### 1. GitHub Actions (الأسهل)
1. ارفع المشروع إلى GitHub
2. سأنشئ لك workflow تلقائي للبناء
3. سيتم بناء APK تلقائياً

### 2. خدمات البناء السحابية
- **Appetize.io** - بناء مجاني
- **Bitrise** - بناء مجاني للمشاريع المفتوحة
- **CircleCI** - بناء مجاني

## الطريقة الثانية: تثبيت Java وبناء محلياً

### 1. تثبيت Java JDK
```bash
# تحميل OpenJDK 17 من:
# https://adoptium.net/temurin/releases/
# أو
# https://www.oracle.com/java/technologies/downloads/
```

### 2. تعيين JAVA_HOME
```bash
# في Windows
set JAVA_HOME=C:\Program Files\Java\jdk-17
set PATH=%JAVA_HOME%\bin;%PATH%

# في PowerShell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
```

### 3. بناء التطبيق
```bash
.\gradlew assembleDebug
```

## الطريقة الثالثة: استخدام Docker

### 1. إنشاء Dockerfile
```dockerfile
FROM openjdk:17-jdk

WORKDIR /app
COPY . .

RUN ./gradlew assembleDebug

# APK سيكون في app/build/outputs/apk/debug/
```

### 2. بناء باستخدام Docker
```bash
docker build -t android-build .
docker run --rm -v ${PWD}:/app android-build
```

## الطريقة الرابعة: استخدام Android SDK فقط

### 1. تحميل Android SDK Command Line Tools
- من: https://developer.android.com/studio#command-tools

### 2. استخدام AAPT و D8 مباشرة
```bash
# تجميع الموارد
aapt2 compile --dir res -o compiled_resources.zip

# تجميع الكود
d8 --output . compiled_resources.zip *.class
```

## الطريقة الخامسة: استخدام Gradle Wrapper مع Java محلي

### 1. تثبيت Java
```bash
# تحميل وتثبيت Java JDK 17
```

### 2. بناء مباشر
```bash
# في مجلد المشروع
.\gradlew.bat assembleDebug
```

## موقع APK بعد البناء

بعد البناء الناجح، ستجد ملف APK في:
```
app/build/outputs/apk/debug/app-debug.apk
```

## نصائح مهمة

1. **تأكد من تثبيت Java JDK 17 أو أحدث**
2. **تحقق من متغير JAVA_HOME**
3. **استخدم PowerShell أو Command Prompt**
4. **تأكد من وجود اتصال بالإنترنت للتحميلات**

## الطريقة الأسرع (موصى بها)

1. ارفع المشروع إلى GitHub
2. سأنشئ لك GitHub Actions workflow
3. سيتم البناء تلقائياً عند كل push
4. ستجد APK في Releases 