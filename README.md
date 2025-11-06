# 📝 Todolist (Android - Java)

**Todolist** adalah aplikasi Android sederhana yang sedang dikembangkan menggunakan **Java** dan **Android Studio**.  
Tujuan utama proyek ini adalah membangun aplikasi **ToDo List** dengan fitur **CRUD (Create, Read, Update, Delete)**, dukungan **multibahasa**, serta **tampilan dinamis** berbasis Material Design.

---

## 🚧 Status Pengembangan

> Versi saat ini masih dalam tahap awal (prototype splash screen dan struktur dasar).

| Fitur | Status |
|-------|---------|
| Splash Screen dengan bendera & teks “Halo” sesuai bahasa perangkat | ✅ Selesai |
| Deteksi otomatis bahasa & negara (🇨🇦 🇫🇷 🇮🇹 🇩🇪 🇺🇸) | ✅ Selesai |
| Navigasi dari splash ke halaman utama | ✅ Selesai |
| Halaman utama (daftar tugas) | 🔄 Dalam Pengembangan |
| CRUD Tugas (Tambah, Edit, Hapus, Tandai Selesai) | ⏳ Belum |
| Penyimpanan Lokal (Room / SQLite) | ⏳ Belum |
| Tema Gelap & Terang | ⏳ Belum |
| Notifikasi Pengingat | ⏳ Belum |

---

## 🌍 Dukungan Bahasa & Negara

| Bahasa | Negara | Teks Sapaan | Bendera |
|--------|---------|--------------|----------|
| Inggris | Amerika Serikat | Hello | 🇺🇸 |
| Inggris | Kanada | Hello | 🇨🇦 |
| Prancis | Prancis | Bonjour | 🇫🇷 |
| Prancis | Kanada | Bonjour | 🇨🇦 |
| Italia | Italia | Ciao | 🇮🇹 |
| Jerman | Jerman | Hallo | 🇩🇪 |

---

## 🧱 Struktur Proyek
```
app/
├─ java/com/example/todolist/
│ ├─ SplashActivity.java
│ └─ MainActivity.java
│
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
│ ├─ activity_splash.xml
│ └─ activity_main.xml
```


---

## ⚙️ Spesifikasi Teknis

| Komponen | Keterangan |
|-----------|------------|
| Bahasa Pemrograman | Java |
| IDE | Android Studio |
| Minimum SDK | 24 |
| Target SDK | 34 |
| UI Framework | Material Design Components |
| Emulator Disarankan | Pixel 6 (API 30) |

---

## 🎨 Desain & Dokumentasi UI/UX

| Jenis | Deskripsi | Link |
|-------|------------|------|
| 🧭 **Storyboard** | Alur logika interaksi antar layar (navigasi pengguna) | [Lihat di Figma](https://www.figma.com/design/zwnAIHrr9yiHcJ57vAHFcR/Mockup?node-id=43-2&p=f&t=iNnDf95PpVGLppqW-0) |
| 🎨 **Mockup** | Tampilan visual awal sebelum implementasi di Android Studio | [Lihat di Figma](https://www.figma.com/design/zwnAIHrr9yiHcJ57vAHFcR/Mockup?node-id=100-96&p=f&t=iNnDf95PpVGLppqW-0) |
| 🧱 **UI (User Interface)** | Tampilan antarmuka pengguna (hasil nyata di aplikasi) | [Lihat di Figma](https://www.figma.com/design/zwnAIHrr9yiHcJ57vAHFcR/Mockup?node-id=37-129&p=f&t=iNnDf95PpVGLppqW-0) |
| 🧩 **UX (User Experience)** | Alur pengalaman dan interaksi pengguna | [Lihat di Figma](https://www.figma.com/design/zwnAIHrr9yiHcJ57vAHFcR/Mockup?node-id=71-2&p=f&t=iNnDf95PpVGLppqW-0) |

---

## 🔮 Rencana Pengembangan Berikutnya

- [ ] Halaman utama dengan daftar tugas (RecyclerView Grid 2 kolom)
- [ ] Fitur Tambah / Ubah / Hapus tugas
- [ ] Penyimpanan menggunakan **Room Database**
- [ ] Filter tugas berdasarkan status (aktif / selesai)
- [ ] Animasi transisi antar halaman
- [ ] Penyesuaian bahasa otomatis di seluruh aplikasi
- [ ] Mode **gelap & terang** dinamis
- [ ] Notifikasi pengingat harian

