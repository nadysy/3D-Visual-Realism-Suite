# 3D Visual Realism Suite

Project Java 17 + Maven + LWJGL 3 + JOML untuk NetBeans 18.

## Menjalankan
1. Ekstrak ZIP.
2. Buka NetBeans 18 -> File -> Open Project -> pilih folder `3D_Visual_Realism_Suite`.
3. Pastikan JDK 17 aktif dan koneksi internet tersedia saat Maven mengunduh dependency.
4. Klik kanan project -> Clean and Build.
5. Jalankan `com.grafkom.viewing3d.Main`.

Alternatif terminal pada folder project: `mvn clean compile exec:java`.

## Kontrol
- B: Phong / Blinn-Phong
- F: Flat / Smooth
- Z: Z-Buffer ON/OFF
- C: Back-Face Culling ON/OFF
- T: Texture ON/OFF
- N: Nearest / Bilinear-Mipmap filtering
- ESC: keluar

## Catatan penting
Project ini dibuat sebagai implementasi mandiri berdasarkan spesifikasi modul Minggu 11 karena project ThreeDViewing Minggu 10 belum disertakan. Implementasi dan kompatibilitas runtime perlu diuji di komputer pengguna. File tekstur checker dibuat sebagai aset contoh. Screenshot hasil pengujian harus diambil setelah aplikasi berhasil dijalankan; jangan mengisi laporan dengan screenshot simulasi.
