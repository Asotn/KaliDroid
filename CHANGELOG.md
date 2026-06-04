# KaliDroid Changelog

All notable changes to KaliDroid are documented here.

Developer: Rotlqe | https://github.com/Rotlqe | s.pi@outlook.sa

---

## [1.0.0] - 2024

### Added
- Real PTY-backed terminal using native C/C++ engine
- Full Kali Linux proot environment (no root required)
- apt-get / apt / sudo support via proot
- Background download progress notifications
- `./files -0 & permission` command for instant storage permission grant
- Command history (up/down arrow) backed by native C history manager
- Shell aliases (ll, la, apt, install, search, update, etc.)
- VT100/VT220/xterm-256color escape code parser
- SHA-256 and MD5 package verification
- Settings: font size, Kali mirror URL, keep screen on
- About screen with GitHub and email links
- F-Droid metadata and build reproducibility
- GitHub Actions CI for automated APK builds
- GPL-3.0 license
- Support for arm64-v8a, armeabi-v7a, x86_64, x86 ABIs
- Notification channels: download progress, bootstrap, session
- Pure black terminal UI — no extra colors, no ads, no trackers
- Static terminal prompt icon (no emoji)

### Architecture
- Java layer: Activities, Services, PackageEngine, TerminalSession
- Native layer: PTY manager, VT100 emulator, escape parser, I/O ring buffer
- Shell layer: history manager, alias engine, env manager, command tokenizer
- Package layer: apt wrapper, dpkg helper, repo manager
- Crypto layer: SHA-256, MD5, PGP verification
- Net layer: HTTP client with progress, progress tracker
- FS layer: fs utils, path resolver, permission helper
- JNI bridges: jni_bridge.cpp, terminal_jni.cpp, package_jni.cpp, fs_jni.cpp
