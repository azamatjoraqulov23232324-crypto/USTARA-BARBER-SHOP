# Android Ilovani Imzolash Kalitlari Haqida Ma'lumot (Signing Keys Guide)

Sizning ilovangiz (`com.ustara_barber.shop.myapp`) uchun yaratilgan **`ustara-barber-key.jks`** kaliti va Google Play Console-dagi imzo ma'lumotlari haqida batafsil ma'lumot:

---

## 1. Google Play Console-dagi Asosiy Imzo Kaliti (Siz bergan kalitlar):
Google Play Console-da "Ilova imzosi" (App Signing) bo'limida ko'rsatilgan va Google Play ilovani foydalanuvchilarga tarqatishda ishlatadigan kalit ma'lumotlari:

*   **MD5 barmoq izi:** `D7:21:72:AF:62:A1:F8:92:00:B9:67:67:F2:E3:BD:6F`
*   **SHA-1 barmoq izi:** `14:80:81:D0:61:94:F3:C1:31:DD:1B:A5:B0:67:69:E5:C3:A1:CE:CD`
*   **SHA-256 barmoq izi:** `8B:02:85:DF:96:B3:B6:B9:D7:A2:73:3F:15:65:F1:4D:59:27:65:70:A5:FB:8D:2F:26:D0:5C:74:EB:A7:0C:63`

> ⚠️ **Muhim eslatma:** Ushbu kalit faqat Google Play bulutida saqlanadi. Bizda uning `.jks` fayli bo'lishi mumkin emas, chunki Google uni hech kimga bermaydi. Shuning uchun loyihani imzolashda biz **Yuklash Kaliti** (Upload Key) dan foydalanamiz.

---

## 2. Biz yaratgan va loyihaga ulangan Yuklash Kaliti (Upload Key):
Siz uchun yaratilgan va loyihaga ulangan yangi xavfsiz kalit ma'lumotlari:

*   **Kalit fayli nomi (Keystore File):** `ustara-barber-key.jks` (Loyihaning asosiy jildida joylashgan. Uni chap tarafdagi fayllar ro'yxatidan yuklab olishingiz mumkin).
*   **Keystore Paroli (Store Password):** `ustarabarber99`
*   **Kalit Taxallusi (Key Alias):** `ustara_barber_alias`
*   **Kalit Paroli (Key Password):** `ustarabarber99`
*   **SHA-1 barmoq izi:** `E5:D8:F0:98:EA:18:A4:9E:BF:CF:03:0A:79:86:76:F9:C0:57:45:C6`
*   **SHA-256 barmoq izi:** `BA:E3:41:07:2C:A6:C6:F3:71:48:D1:48:B7:71:AC:4E:D6:53:97:C6:AD:57:1D:96:73:1A:51:24:E8:A3:1D:A8`

---

## 3. Ilovada qayerga qo'shildi?

Loyihaning **`app/build.gradle.kts`** faylidagi `signingConfigs` qismi ushbu yangi kalit bilan avtomatik tarzda sozlandi:

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

Bu degani, siz loyihani build qilib `.aab` (Android App Bundle) yuklash faylini chiqarganingizda, u avtomatik ravishda shu yuklash kaliti bilan imzolanadi!

---

## 4. Google Play Console uchun PEPK Java buyrug'i (Siz so'ragan buyruq)

Google Play Console-da "Kalitni eksport qilish va yuklash" bo'limi uchun tayyor buyruq. Uni o'z kompyuteringiz terminalida (CMD yoki Terminal) ishga tushirishingiz mumkin:

### Tayyorgarlik Bosqichlari:
1. Google Play Console sahifasidan **`pepk.jar`** faylini va **`encryption_public_key.pem`** faylini yuklab oling.
2. AI Studio-dan loyihaning ildiz papkasidagi **`ustara-barber-key.jks`** faylini kompyuteringizga yuklab oling.
3. Ushbu uchta faylni (`pepk.jar`, `encryption_public_key.pem` va `ustara-barber-key.jks`) bitta papkaga joylashtiring.
4. O'sha papkada terminalni ochib, quyidagi buyruqni nusxalab (copy) ishga tushiring:

```bash
java -jar pepk.jar \
  --keystore=ustara-barber-key.jks \
  --alias=ustara_barber_alias \
  --output=output.zip \
  --include-cert \
  --rsa-aes-encryption \
  --encryption-key-path=encryption_public_key.pem
```

### Buyruqni ishga tushirganda so'raladigan parollar:
1. **`Enter password for keystore 'ustara-barber-key.jks':`** so'ralganda `ustarabarber99` deb yozing va Enter bosing. *(Diqqat: Parol yozayotganda ekranda harflar yoki yulduzchalar ko'rinmaydi, lekin u baribir yoziladi! Shunchaki to'g'ri yozib Enter bosing)*.
2. **`Enter password for key 'ustara_barber_alias':`** so'ralganda ham `ustarabarber99` deb yozing va Enter bosing.

Natijada shu papkada **`output.zip`** degan fayl hosil bo'ladi. Ushbu faylni Google Play Console-ga (siz skrinshotda ko'rsatgan oynaga) yuklaysiz!

---

## 5. Firebase yoki boshqa tashqi servislar (masalan SMS, Telegram, Google Login) uchun qaysi SHA-256 ishlatiladi?

Ilovangiz Google Play-da nashr etilgandan keyin, Firebase yoki Google Login kabi tashqi xizmatlar to'g'ri ishlashi uchun **ikkala SHA-256 kodini ham** ushbu xizmatlar sozlamalariga kiritib qo'ying:

1. **Yuklash Kaliti (Biz yaratgan `ustara-barber-key.jks`):**
   `BA:E3:41:07:2C:A6:C6:F3:71:48:D1:48:B7:71:AC:4E:D6:53:97:C6:AD:57:1D:96:73:1A:51:24:E8:A3:1D:A8`
2. **Google Play App Signing Key (Siz Google Console-dan yuborgan kalit):**
   `8B:02:85:DF:96:B3:B6:B9:D7:A2:73:3F:15:65:F1:4D:59:27:65:70:A5:FB:8D:2F:26:D0:5C:74:EB:A7:0C:63`
