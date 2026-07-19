# Android Ilovani Imzolash Kalitlari Haqida Ma'lumot (Signing Keys Guide)

Sizning ilovangiz (`com.ustara_barber.shop.myapp`) uchun mutlaqo yangi va xavfsiz **`ustara-barber-key.jks`** kalitini generatsiya qildim va uni loyihaga qo'shdim.

Siz so'ragan barcha ma'lumotlar va Google Play Console uchun tayyor buyruqlar quyida batafsil keltirilgan:

---

## 1. Yangi Kalit (Keystore) Ma'lumotlari:

*   **Kalit fayli nomi (Keystore File):** `ustara-barber-key.jks` (Loyihaning asosiy papkasida joylashgan. Uni chap tarafdagi fayllar ro'yxatidan yuklab olishingiz mumkin).
*   **Keystore Paroli (Store Password):** `ustarabarber99`
*   **Kalit Taxallusi (Key Alias):** `ustara_barber_alias`
*   **Kalit Paroli (Key Password):** `ustarabarber99`
*   **SHA-1 barmoq izi (Fingerprint):** `E5:D8:F0:98:EA:18:A4:9E:BF:CF:03:0A:79:86:76:F9:C0:57:45:C6`
*   **SHA-256 barmoq izi (Fingerprint):** `BA:E3:41:07:2C:A6:C6:F3:71:48:D1:48:B7:71:AC:4E:D6:53:97:C6:AD:57:1D:96:73:1A:51:24:E8:A3:1D:A8`

---

## 2. Ilovada qayerga qo'shildi?

Loyihaning **`app/build.gradle.kts`** fayli ichidagi `signingConfigs` qismi ushbu yangi kalit bilan avtomatik tarzda sozlandi:

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

Bu degani, siz loyihani build qilib `.aab` (Android App Bundle) yuklash faylini chiqarganingizda, u avtomatik ravishda shu yangi kalit bilan imzolanadi!

---

## 3. Google Play Console uchun PEPK Java buyrug'i (Siz so'ragan buyruq)

Google Play Console sizdan kalitni eksport qilib yuklashni so'ragan sahifadagi buyruqni siz uchun to'liq tayyorlab beraman. Uni o'z kompyuteringiz terminalida (CMD yoki Terminal) ishga tushirishingiz mumkin:

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

Natijada papkada **`output.zip`** degan fayl hosil bo'ladi. Ushbu faylni Google Play Console-ga (siz skrinshotda ko'rsatgan oynaga) yuklaysiz!

---

## 4. Firebase yoki boshqa servislar uchun qaysi SHA-256 ishlatiladi?

Ilovangiz Google Play-da nashr etilgandan keyin, Firebase, Telegram API yoki Google Login kabi tashqi xizmatlar to'g'ri ishlashi uchun **ikkala SHA-256 kodini ham** ushbu xizmatlar sozlamalariga kiritib qo'ying:

1. **Yangi Yuklash Kaliti (Biz yaratgan `ustara-barber-key.jks`):**
   `BA:E3:41:07:2C:A6:C6:F3:71:48:D1:48:B7:71:AC:4E:D6:53:97:C6:AD:57:1D:96:73:1A:51:24:E8:A3:1D:A8`
2. **Google Play App Signing Key (Google Play o'zida saqlaydigan kalit):**
   `8B:02:85:DF:96:B3:B6:B9:D7:A2:73:3F:15:65:F1:4D:59:27:65:70:A5:FB:8D:2F:26:D0:5C:74:EB:A7:0C:63`
