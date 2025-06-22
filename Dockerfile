FROM openjdk:17-jdk

# تثبيت أدوات Android SDK
RUN apt-get update && apt-get install -y \
    wget \
    unzip \
    curl \
    && rm -rf /var/lib/apt/lists/*

# تعيين متغيرات البيئة
ENV ANDROID_HOME=/opt/android-sdk
ENV PATH=$PATH:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools

# تحميل Android SDK
RUN mkdir -p $ANDROID_HOME && cd $ANDROID_HOME && \
    wget -q https://dl.google.com/android/repository/commandlinetools-linux-8512546_latest.zip && \
    unzip commandlinetools-linux-8512546_latest.zip && \
    rm commandlinetools-linux-8512546_latest.zip

# تثبيت Android SDK components
RUN yes | $ANDROID_HOME/cmdline-tools/bin/sdkmanager --licenses
RUN $ANDROID_HOME/cmdline-tools/bin/sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"

# تعيين مجلد العمل
WORKDIR /app

# نسخ ملفات المشروع
COPY . .

# منح صلاحيات التنفيذ لـ gradlew
RUN chmod +x gradlew

# بناء التطبيق
RUN ./gradlew assembleDebug

# مجلد الإخراج
VOLUME /app/app/build/outputs/apk/debug

# الأمر الافتراضي
CMD ["ls", "-la", "app/build/outputs/apk/debug/"] 