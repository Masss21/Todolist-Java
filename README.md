📝 Todolist (Android - Java)

📱 **Todolist** adalah aplikasi Android sederhana yang sedang dikembangkan menggunakan **Java** dan **Android Studio**.  
Tujuan akhirnya adalah membangun aplikasi **ToDo List** dengan fitur **CRUD (Create, Read, Update, Delete)** yang memiliki dukungan **multibahasa dan tampilan dinamis**.

---

🚧 Status Pengembangan

Versi saat ini masih dalam tahap awal:

- [x] Splash Screen tampil dengan bendera & teks “Halo” sesuai bahasa perangkat  
- [x] Deteksi otomatis bahasa & negara (contoh: 🇨🇦 Kanada, 🇫🇷 Prancis, 🇮🇹 Italia, 🇩🇪 Jerman, 🇺🇸 AS)  
- [x] Navigasi dari splash ke halaman utama  
- [ ] Halaman utama untuk menampilkan daftar tugas  
- [ ] CRUD tugas (Tambah, Edit, Hapus, Tandai Selesai)  
- [ ] Penyimpanan lokal (Room / SQLite)  
- [ ] Tema gelap & terang  
- [ ] Notifikasi pengingat tugas  

---

## 🌍 Dukungan Bahasa & Negara

| Bahasa | Negara | Teks | Bendera |
|--------|---------|------|---------|
| Inggris | Amerika Serikat | Hello | 🇺🇸 |
| Inggris | Kanada | Hello | 🇨🇦 |
| Prancis | Prancis | Bonjour | 🇫🇷 |
| Prancis | Kanada | Bonjour | 🇨🇦 |
| Italia | Italia | Ciao | 🇮🇹 |
| Jerman | Jerman | Hallo | 🇩🇪 |


## 🧱 Project Structure

app/
├─ java/com/example/todolist/
│ ├─ SplashActivity.java
│ └─ MainActivity.java
├─ res/
│ ├─ drawable/
│ │ ├─ flag_id.png
│ │ ├─ flag_us.png
│ │ ├─ flag_ca.png
│ │ ├─ flag_fr.png
│ │ ├─ flag_it.png
│ │ ├─ flag_de.png
│ │ └─ splash_logo.png
│ ├─ drawable-en-rUS/
│ ├─ drawable-en-rCA/
│ ├─ drawable-fr-rFR/
│ ├─ drawable-fr-rCA/
│ ├─ drawable-it-rIT/
│ ├─ drawable-de-rDE/
│ ├─ values/
│ ├─ values-it/
│ ├─ values-fr/
│ ├─ values-de/
│ └─ layout/
│ ├─ splash_screen.xml
│ └─ activity_main.xml
│ └─ activity_splash.xml


---

## ⚙️ Spesifikasi Teknis

| Komponen | Keterangan |
|-----------|------------|
| Bahasa Pemrograman | Java |
| IDE | Android Studio |
| Minimum SDK | 24 |
| Target SDK | 34 |
| UI Framework | Material Design Components |
| Emulator Disarankan | Pixel 6 API 30 |


==
Storyboard:https://www.figma.com/design/zwnAIHrr9yiHcJ57vAHFcR/Mockup?node-id=43-2&p=f&t=iNnDf95PpVGLppqW-0
Mockup: https://www.figma.com/design/zwnAIHrr9yiHcJ57vAHFcR/Mockup?node-id=100-96&p=f&t=iNnDf95PpVGLppqW-0
UI: https://www.figma.com/design/zwnAIHrr9yiHcJ57vAHFcR/Mockup?node-id=37-129&p=f&t=iNnDf95PpVGLppqW-0
UX:https://www.figma.com/design/zwnAIHrr9yiHcJ57vAHFcR/Mockup?node-id=71-2&p=f&t=iNnDf95PpVGLppqW-0
==

🧩 Rencana Pengembangan Berikutnya
==
 -Halaman utama dengan daftar tugas (RecyclerView)
 -Fungsi tambah / ubah / hapus tugas
 -Penyimpanan dengan Room Database
 -Filter tugas berdasarkan status (aktif / selesai) 
 -Animasi transisi antar halaman
 -Penyesuaian bahasa otomatis di seluruh aplikasi
==
