# Android Ilovani Imzolash Kalitlari Haqida Ma'lumot (Signing Keys Guide)

Sizning ilovangiz (`com.ustara_barber.shop.myapp`) uchun maxsus yangi **`ustara-app-key.jks`** kalitini generatsiya qildim va uni loyihaga qo'shdim.

Siz so'ragan barcha ma'lumotlar va Google Play Console uchun tayyor buyruqlar quyida batafsil keltirilgan:

---

## 1. Yangi Kalit (Keystore) Ma'lumotlari:

*   **Kalit fayli nomi (Keystore File):** `ustara-app-key.jks` (Loyihaning asosiy papkasida joylashgan. Uni chap tarafdagi fayllar ro'yxatidan yuklab olishingiz mumkin).
*   **Keystore Paroli (Store Password):** `ustarababer123`
*   **Kalit Taxallusi (Key Alias):** `ustara_alias`
*   **Kalit Paroli (Key Password):** `ustarababer123`
*   **SHA-256 barmoq izi (Fingerprint):** `EC:0F:67:F0:6F:4C:CE:44:4A:C9:CB:33:2A:65:85:56:5D:4D:1C:E4:28:7C:E8:82:20:D7:B6:C5:48:21:8A:F8`

---

## 2. Ilovada qayerga qo'shildi?

Loyihaning **`app/build.gradle.kts`** fayli ichidagi `signingConfigs` qismi ushbu yangi kalit bilan avtomatik tarzda sozlandi:

```kotlin
  signingConfigs {
    create("release") {
      val keystorePath = System.getenv("KEYSTORE_PATH") ?: "${rootDir}/ustara-app-key.jks"
      storeFile = file(keystorePath)
      storePassword = System.getenv("STORE_PASSWORD") ?: "ustarababer123"
      keyAlias = "ustara_alias"
      keyPassword = System.getenv("KEY_PASSWORD") ?: "ustarababer123"
    }
  }
```

Bu degani, siz loyihani build qilib `.aab` (Android App Bundle) yuklash faylini chiqarganingizda, u avtomatik ravishda shu yangi kalit bilan imzolanadi!

---

## 3. Google Play Console uchun PEPK Java buyrug'i (Siz so'ragan buyruq)

Google Play Console sizdan kalitni eksport qilib yuklashni so'ragan sahifadagi buyruqni siz uchun to'liq tayyorlab beraman. Uni o'z kompyuteringiz terminalida (CMD yoki Terminal) ishga tushirishingiz mumkin:

### Tayyorgarlik Bosqichlari:
1. Google Play Console sahifasidan **`pepk.jar`** faylini va **`encryption_public_key.pem`** faylini yuklab oling.
2. AI Studio-dan loyihaning ildiz papkasidagi **`ustara-app-key.jks`** faylini kompyuteringizga yuklab oling.
3. Ushbu uchta faylni (`pepk.jar`, `encryption_public_key.pem` va `ustara-app-key.jks`) bitta papkaga joylashtiring.
4. O'sha papkada terminalni ochib, quyidagi buyruqni nusxalab (copy) ishga tushiring:

```bash
java -jar pepk.jar \
  --keystore=ustara-app-key.jks \
  --alias=ustara_alias \
  --output=output.zip \
  --include-cert \
  --rsa-aes-encryption \
  --encryption-key-path=encryption_public_key.pem
```

### Buyruqni ishga tushirganda so'raladigan parollar:
1. **`Enter password for keystore 'ustara-app-key.jks':`** so'ralganda `ustarababer123` deb yozing va Enter bosing. *(Diqqat: Parol yozayotganda ekranda harflar yoki yulduzchalar ko'rinmaydi, lekin u baribir yoziladi! Shunchaki to'g'ri yozib Enter bosing)*.
2. **`Enter password for key 'ustara_alias':`** so'ralganda ham `ustarababer123` deb yozing va Enter bosing.

Natijada papkada **`output.zip`** degan fayl hosil bo'ladi. Ushbu faylni Google Play Console-ga (siz skrinshotda ko'rsatgan oynaga) yuklaysiz!

---

## 4. Firebase yoki boshqa servislar uchun qaysi SHA-256 ishlatiladi?

Ilovangiz Google Play-da nashr etilgandan keyin, Firebase, Telegram API yoki Google Login kabi tashqi xizmatlar to'g'ri ishlashi uchun **ikkala SHA-256 kodini ham** ushbu xizmatlar sozlamalariga kiritib qo'ying:

1. **Yangi Yuklash Kaliti (Biz yaratgan `ustara-app-key.jks`):**
   `EC:0F:67:F0:6F:4C:CE:44:4A:C9:CB:33:2A:65:85:56:5D:4D:1C:E4:28:7C:E8:82:20:D7:B6:C5:48:21:8A:F8`
2. **Google Play App Signing Key (Google Play o'zida saqlaydigan kalit):**
   `8B:02:85:DF:96:B3:B6:B9:D7:A2:73:3F:15:65:F1:4D:59:27:65:70:A5:FB:8D:2F:26:D0:5C:74:EB:A7:0C:63`
