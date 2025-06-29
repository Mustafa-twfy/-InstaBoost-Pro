-- إنشاء جدول الصور في Supabase
-- قم بتشغيل هذا الكود في SQL Editor في Supabase Dashboard

-- إنشاء جدول الصور
CREATE TABLE IF NOT EXISTS images (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    image_url TEXT NOT NULL,
    uploaded_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    user_email TEXT,
    file_name TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- إنشاء فهرس للبحث السريع
CREATE INDEX IF NOT EXISTS idx_images_uploaded_at ON images(uploaded_at DESC);
CREATE INDEX IF NOT EXISTS idx_images_user_email ON images(user_email);

-- تمكين RLS (Row Level Security)
ALTER TABLE images ENABLE ROW LEVEL SECURITY;

-- إنشاء سياسات الأمان
-- السماح للجميع بقراءة الصور
CREATE POLICY "Allow public read access" ON images
    FOR SELECT USING (true);

-- السماح للمستخدمين المسجلين بإضافة صور
CREATE POLICY "Allow authenticated users to insert" ON images
    FOR INSERT WITH CHECK (auth.role() = 'authenticated');

-- السماح للمستخدمين بتحديث صورهم فقط
CREATE POLICY "Allow users to update their own images" ON images
    FOR UPDATE USING (auth.email() = user_email);

-- السماح للمستخدمين بحذف صورهم فقط
CREATE POLICY "Allow users to delete their own images" ON images
    FOR DELETE USING (auth.email() = user_email);

-- إضافة تعليقات على الجدول
COMMENT ON TABLE images IS 'جدول لتخزين روابط الصور المرفوعة';
COMMENT ON COLUMN images.id IS 'المعرف الفريد للصورة';
COMMENT ON COLUMN images.image_url IS 'رابط الصورة على Supabase Storage';
COMMENT ON COLUMN images.uploaded_at IS 'تاريخ ووقت رفع الصورة';
COMMENT ON COLUMN images.user_email IS 'بريد المستخدم الذي رفع الصورة';
COMMENT ON COLUMN images.file_name IS 'اسم الملف المحفوظ';
COMMENT ON COLUMN images.created_at IS 'تاريخ إنشاء السجل';

-- إنشاء Storage Bucket للصور
-- قم بتنفيذ هذا في Storage > Create new bucket
-- اسم البكت: images
-- Public bucket: true
-- File size limit: 50MB
-- Allowed MIME types: image/*

-- سياسات Storage للأمان
-- السماح للجميع بقراءة الصور
CREATE POLICY "Allow public read access to images" ON storage.objects
    FOR SELECT USING (bucket_id = 'images');

-- السماح للمستخدمين المسجلين برفع الصور
CREATE POLICY "Allow authenticated users to upload images" ON storage.objects
    FOR INSERT WITH CHECK (bucket_id = 'images' AND auth.role() = 'authenticated');

-- السماح للمستخدمين بحذف صورهم فقط
CREATE POLICY "Allow users to delete their own images" ON storage.objects
    FOR DELETE USING (bucket_id = 'images' AND auth.email() = (storage.foldername(name))[1]); 