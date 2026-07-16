# Android Ilovani Imzolash Kalitlari Haqida Ma'lumot (Signing Keys Guide)

Sizning ilovangiz (`com.ustara_barber.shop.myapp`) uchun berilgan kalitlar va ulardan foydalanish bo'yicha muhim qo'llanma:

## 1. Ilova qayerda imzolanadi? (Qayerga qo'yilgan?)

Ilovani imzolash sozlamalari loyihangizning **`app/build.gradle.kts`** fayli ichida quyidagi qismda sozlangan:

```kotlin
  signingConfigs {
    create("release") {
      val keystorePath = System.getenv("KEYSTORE_PATH") ?: "${rootDir}/my-upload-key.jks"
      storeFile = file(keystorePath)
      storePassword = System.getenv("STORE_PASSWORD") ?: "password123"
      keyAlias = "upload"
      keyPassword = System.getenv("KEY_PASSWORD") ?: "password123"
    }
  }
```

Ushbu kodga ko'ra, ilovaning ishlab chiqarish (Release) versiyasi loyiha ildiz qatlamidagi **`my-upload-key.jks`** fayli yordamida imzolanadi. 

*   **Yuklash kaliti (Upload Key):** `my-upload-key.jks`
*   **Parol (Keystore & Key Password):** `password123`
*   **Taxallusi (Alias):** `upload`

---

## 2. Nima uchun SHA-256 kodini (`8B:02:85...`) to'g'ridan-to'g'ri kodga yozib bo'lmaydi?

Android tizimida ilova SHA-256 kodini matn sifatida kodning biror joyiga shunchaki nusxalab (copy-paste) imzolab bo'lmaydi:
1.  **SHA-256** - bu imzolash fayli (`.jks` yoki `.keystore`) ichidagi sertifikatning matematik hisoblangan **"barmoq izi" (fingerprint)** dir.
2.  Ilova faqatgina haqiqiy maxfiy kalit fayli (`.jks`) orqali kriptografik usulda imzolanadi.
3.  Ushbu kalit faylisiz uning SHA-256 barmoq iziga ega bo'lgan imzoni soxtalashtirib yoki generatsiya qilib bo'lmaydi (SHA-256 ning kriptografik xavfsizligi tufayli).

---

## 3. Google Play App Signing (Ilovani imzolash tizimi) qanday ishlaydi?

Siz aytgan **`8B:02:85:DF:96:B3:B6:B9:D7:A2:73:3F:15:65:F1:4D:59:27:65:70:A5:FB:8D:2F:26:D0:5C:74:EB:A7:0C:63`** kalit kodi:
*   Bu **Google Play App Signing Key** (Google Play Ilovani Imzolash Kaliti) hisoblanadi.
*   Ushbu kalit **faqatgina Google Play bulutida saqlanadi**. Google Play uni xavfsizlik nuqtai nazaridan ishlab chiquvchilarga (hech kimga) yuklab olish uchun bermaydi. Shuning uchun bu kalitning maxfiy `.jks` faylini kompyuterga yuklab olib, ilovani u bilan to'g'ridan-to'g'ri imzolashning ilojisi yo'q.

### Ilovani Google Play-ga yuklash zanjiri:
1.  Siz AI Studio-da ilovani **Yuklash kaliti (Upload Key - `my-upload-key.jks`, SHA-256: `7E:00:77...`)** bilan imzolaysiz va `.aab` formatida Google Play Console-ga yuklaysiz.
2.  Google Play Console siz yuklagan fayldagi yuklash kalitini tekshiradi va u to'g'ri bo'lsa, uni qabul qiladi.
3.  Qabul qilgandan so'ng, Google Play siz yuklagan yuklash imzosini olib tashlaydi va uning o'rniga o'zida saqlanayotgan **Haqiqiy Imzolash Kaliti (`8B:02:85...`)** bilan qaytadan imzolab, foydalanuvchilarga tarqatadi.
4.  Natijada, foydalanuvchilar telefonga o'rnatganda ilova aynan **`8B:02:85...`** kaliti bilan imzolangan bo'ladi.

Siz hozir yuklash kaliti (`my-upload-key.jks`) bilan imzolangan `.aab` faylini bemalol Google Play-ga yuklashingiz mumkin. Google barchasini o'zi avtomatik ravishda to'g'rilab imzolaydi.

---

## 4. Agar boshqa servislar (Firebase, Telegram, Google Sign-In) uchun SHA-256 kerak bo'lsa?

Agar sizga Firebase, Google Login yoki boshqa API servislar uchun SHA-256 talab qilinsa, u yerga **ikkala kalitning ham** SHA-256 kodini kiritishingiz shart:
1.  **Yuklash kaliti SHA-256 si:** `7E:00:77:9A:C1:49:D1:3A:1D:27:AD:42:0C:64:DB:31:FA:66:27:D2:38:34:78:7F:10:43:7F:76:95:30:77:BF` (Mahalliy sinovlar va yuklash jarayonida ishlashi uchun).
2.  **Google Play App Signing SHA-256 si:** `8B:02:85:DF:96:B3:B6:B9:D7:A2:73:3F:15:65:F1:4D:59:27:65:70:A5:FB:8D:2F:26:D0:5C:74:EB:A7:0C:63` (Google Play Store-dan yuklab olinganda ishlashi uchun).

---

## 5. PEPK Java buyrug'ini ishga tushirish (Google Play kalitini yuklash)

Google Play Console sizdan kalitni shifrlab yuklashni so'raganda **PEPK (`pepk.jar`)** vositasidan foydalaniladi. Siz yozgan buyruqni loyihamizdagi **`my-upload-key.jks`** kalitiga moslashtirilgan ko'rinishi quyidagicha:

### Tayyorgarlik Bosqichlari:
1. Google Play Console sahifasidan **`pepk.jar`** va **`encryption_public_key.pem`** fayllarini yuklab oling.
2. Ushbu fayllarni loyihangizning asosiy (root) jildiga (chap tarafdagi fayllar ro'yxatiga) yuklang (Drag-and-Drop yoki Upload orqali).
3. Quyidagi buyruqni terminalda ishga tushiring:

```bash
java -jar pepk.jar \
  --keystore=my-upload-key.jks \
  --alias=upload \
  --output=output.zip \
  --include-cert \
  --rsa-aes-encryption \
  --encryption-key-path=encryption_public_key.pem
```

### Buyruq ishga tushganda so'raladigan parollar:
* **Enter password for keystore 'my-upload-key.jks':** `password123` deb yozing va Enter bosing (yozganda ekranda harflar ko'rinmaydi).
* **Enter password for key 'upload':** `password123` deb yozing va Enter bosing.

Natijada loyihada **`output.zip`** fayli paydo bo'ladi. O'sha faylni yuklab olib Google Play Console-ga topshirasiz!
