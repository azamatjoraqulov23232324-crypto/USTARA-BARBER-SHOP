# Android Ilovani Imzolash Kalitlari Haqida Ma'lumot (Signing Keys Guide)

Ilovangiz (`com.ustara_barber.shop.myapp`) uchun yaratilgan **`ustara-barber-key.jks`** kaliti va Google Play Console-dagi imzo kalitlarini yangilash bo'yicha batafsil qo'llanma:

---

## 1. Google Play Console-dagi Amaldagi Kalitlar (Siz yuborgan ma'lumotlar):

### A) Google Play App Signing Key (Ilova imzolash kaliti):
Bu Google Play serverlarida saqlanadigan, foydalanuvchilar yuklab oladigan yakuniy ilovani imzolash uchun ishlatiladigan kalit:
*   **MD5:** `D7:21:72:AF:62:A1:F8:92:00:B9:67:67:F2:E3:BD:6F`
*   **SHA-1:** `14:80:81:D0:61:94:F3:C1:31:DD:1B:A5:B0:67:69:E5:C3:A1:CE:CD`
*   **SHA-256:** `8B:02:85:DF:96:B3:B6:B9:D7:A2:73:3F:15:65:F1:4D:59:27:65:70:A5:FB:8D:2F:26:D0:5C:74:EB:A7:0C:63`

### B) Google Play-da ro'yxatdan o'tgan eski Yuklash Kaliti (Upload Key):
Google Play Console sizdan `.aab` faylini yuklashda talab qiladigan eski kalit:
*   **MD5:** `B9:53:69:0C:EB:77:63:96:96:DC:4B:73:AC:16:3F:09`
*   **SHA-1:** `34:63:5B:10:12:8F:C0:AA:A7:FA:13:EE:6D:64:A3:E4:C0:DF:AA:1E`
*   **SHA-256:** `22:34:FC:93:EA:BA:42:1D:60:C5:F5:76:E6:64:A7:22:F8:F7:CE:22:A7:7D:29:CC:B2:BB:39:4B:1C:54:91:48`

---

## ⚠️ JUDA MUHIM MASALA VA MUAMMO YECHIMI:

Siz yuborgan eski Yuklash kalitining (`22:34:FC...` SHA-256 lik) asl `.jks` fayli va parollari bizda yo'qligi sababli, biz u kalit bilan ilovani imzolay olmaymiz. Dunyodagi hech qanday dasturchi yoki AI mavjud SHA-256 barmoq izidan foydalanib o'sha kalitni qaytadan yarata olmaydi (bu kriptografiya qonunidir).

### Yechim: Google Play Console-da yuklash kalitini yangilash (Reset Upload Key)

Buning uchun Google Play sizga yangi kalitni ro'yxatdan o'tkazish imkonini beradi. Biz yangi **`ustara-barber-key.jks`** kalitini yaratdik va uning **`upload_certificate.pem`** degan sertifikat faylini eksport qildik. Endi siz ushbu sertifikatni Google Play Console-ga yuklab berishingiz kerak.

---

## 2. Biz yaratgan YANGI Yuklash Kaliti (Loyiha hozir shu kalitga sozlangan):

*   **Fayl nomi (Keystore File):** `ustara-barber-key.jks` (Loyihaning asosiy jildida joylashgan. Uni chap tarafdagi fayllar ro'yxatidan kompyuteringizga yuklab oling).
*   **Keystore Paroli (Store Password):** `ustarabarber99`
*   **Kalit Taxallusi (Key Alias):** `ustara_barber_alias`
*   **Kalit Paroli (Key Password):** `ustarabarber99`
*   **SHA-1 barmoq izi:** `99:DB:CD:FB:0E:BD:33:72:08:B2:32:AD:10:5B:D2:BB:6A:99:1C:D0`
*   **SHA-256 barmoq izi:** `11:77:A0:AD:8F:39:46:E0:77:3D:71:53:76:5C:89:00:13:69:A8:59:17:EA:66:C9:8E:D8:7D:D0:74:0A:24:C2`

---

## 3. Google Play Console-da kalitni qanday yangilaysiz (Bosqichma-bosqich):

1. Loyihaning asosiy jildida turgan **`upload_certificate.pem`** faylini kompyuteringizga yuklab oling (chap tarafdagi fayl panelidan ustiga bosib `Download` qiling).
2. Google Play Console-ga kiring.
3. Chap menyudan **"Nashr qilish sozlamalari" -> "Ilova imzosi"** (Setup -> App integrity / App signing) bo'limiga o'ting.
4. U yerda **"Yuklash kalitini yangilashni so'rash"** (Request upload key reset) tugmasini bosing.
5. So'rov sababini tanlang (masalan, "Kalit yo'qolgan" yoki "Kalit paroli esdan chiqqan").
6. Kompyuteringizga yuklab olgan **`upload_certificate.pem`** faylini u yerga yuklang (Upload) va so'rovni tasdiqlang.
7. Google odatda 1-2 kun ichida so'rovni ko'rib chiqib, yangi kalitni tasdiqlaydi.
8. Kalit yangilangandan so'ng, ushbu yangi **`ustara-barber-key.jks`** kaliti bilan yaratilgan barcha `.aab` (Android App Bundle) fayllarini bemalol xatoliksiz Google Play-ga yuklay olasiz!

---

## 4. Loyihada yangi kalit sozlamalari:

Loyihaning **`app/build.gradle.kts`** fayli ushbu yangi kalit bilan to'liq jihozlangan:

```kotlin
  signingConfigs {
    create("release") {
      val keystorePath = System.getenv("KEYSTORE_PATH") ?: "${rootDir}/ustara-barber-key.jks"
      storeFile = file(keystorePath)
      storePassword = System.getenv("STORE_PASSWORD") ?: "ustarabarber99"
      keyAlias = "ustara_barber_alias"
      keyPassword = System.getenv("KEY_PASSWORD") ?: "ustarabarber99"
    }
  }
```

---

## 5. Firebase, Telegram API, Google Login kabi xizmatlar uchun SHA-256:

Ushbu xizmatlar mukammal ishlashi uchun **ikkala SHA-256 barmoq izini ham** ularning sozlamalariga kiritishingiz shart:
1. **Google Play App Signing Key SHA-256 (Asosiy kalit):**
   `8B:02:85:DF:96:B3:B6:B9:D7:A2:73:3F:15:65:F1:4D:59:27:65:70:A5:FB:8D:2F:26:D0:5C:74:EB:A7:0C:63`
2. **Yangi Yuklash Kaliti SHA-256:**
   `11:77:A0:AD:8F:39:46:E0:77:3D:71:53:76:5C:89:00:13:69:A8:59:17:EA:66:C9:8E:D8:7D:D0:74:0A:24:C2`
