/*
 * KaliDroid - Master Native Engine Header
 * Include this single header to access all KaliDroid native subsystems.
 *
 * Developer : Rotlqe | https://github.com/Rotlqe | s.pi@outlook.sa
 * License   : GPL-3.0
 *
 * Subsystems:
 *   - PTY manager        (terminal/pty_manager.h)
 *   - Escape code parser (terminal/escape_parser.h)
 *   - VT100 emulator     (terminal/vt100.h)
 *   - I/O ring buffer    (terminal/io_buffer.h)
 *   - Process runner     (terminal/process_runner.h)
 *   - Command tokenizer  (shell/command_tokenizer.h)
 *   - History manager    (shell/history_manager.h)
 *   - Alias engine       (shell/alias_engine.h)
 *   - Env manager        (shell/env_manager.h)
 *   - APT wrapper        (package/apt_wrapper.h)
 *   - dpkg helper        (package/dpkg_helper.h)
 *   - Repo manager       (package/repo_manager.h)
 *   - SHA-256            (crypto/sha256.h)
 *   - MD5                (crypto/md5.h)
 *   - PGP verify         (crypto/pgp_verify.h)
 *   - HTTP client        (net/http_client.h)
 *   - Progress tracker   (net/progress_tracker.h)
 *   - FS utils           (fs/fs_utils.h)
 *   - Path resolver      (fs/path_resolver.h)
 *   - Permission helper  (fs/permission_helper.h)
 */

#ifndef KALIDROID_H
#define KALIDROID_H

#ifdef __cplusplus
extern "C" {
#endif

/* Version */
#define KALIDROID_VERSION_MAJOR 1
#define KALIDROID_VERSION_MINOR 0
#define KALIDROID_VERSION_PATCH 0
#define KALIDROID_VERSION_STR   "1.0.0"

/* Package manager configuration */
#define KALIDROID_KALI_MIRROR      "http://http.kali.org/kali"
#define KALIDROID_KALI_DIST        "kali-rolling"
#define KALIDROID_KALI_COMPONENTS  "main contrib non-free non-free-firmware"

/* Default paths (relative to app data dir) */
#define KALIDROID_ROOTFS_SUBDIR    "kali-rootfs"
#define KALIDROID_BIN_SUBDIR       "bin"
#define KALIDROID_TMP_SUBDIR       "tmp"
#define KALIDROID_HOME_SUBDIR      "home"
#define KALIDROID_DOWNLOADS_SUBDIR "downloads"

/* PTY defaults */
#define KALIDROID_DEFAULT_COLS     80
#define KALIDROID_DEFAULT_ROWS     24
#define KALIDROID_MAX_SESSIONS     8

/* Buffer sizes */
#define KALIDROID_PTY_READ_BUF     (64 * 1024)
#define KALIDROID_IO_BUF_SIZE      (256 * 1024)
#define KALIDROID_HTTP_BUF_SIZE    (8 * 1024 * 1024)

/* Terminal subsystem */
#include "../terminal/pty_manager.h"
#include "../terminal/escape_parser.h"
#include "../terminal/vt100.h"
#include "../terminal/io_buffer.h"
#include "../terminal/process_runner.h"

/* Shell subsystem */
#include "../shell/command_tokenizer.h"
#include "../shell/history_manager.h"
#include "../shell/alias_engine.h"
#include "../shell/env_manager.h"

/* Package subsystem */
#include "../package/apt_wrapper.h"
#include "../package/dpkg_helper.h"
#include "../package/repo_manager.h"

/* Crypto subsystem */
#include "../crypto/sha256.h"
#include "../crypto/md5.h"
#include "../crypto/pgp_verify.h"

/* Network subsystem */
#include "../net/http_client.h"
#include "../net/progress_tracker.h"

/* Filesystem subsystem */
#include "../fs/fs_utils.h"
#include "../fs/path_resolver.h"
#include "../fs/permission_helper.h"

/* -------------------------------------------------------------------------
 * kalidroid_init
 * One-shot initializer. Call from JNI_OnLoad or the Java Application class.
 * proot_bin  : absolute host path to the proot binary
 * rootfs     : absolute host path to the Kali rootfs directory
 * home_dir   : absolute host path to the app home directory
 * arch       : Kali/Debian architecture string (arm64, armhf, amd64, i386)
 * mirror     : Kali APT mirror URL
 * Returns 0 on success, -1 on failure.
 * ------------------------------------------------------------------------- */
static inline int kalidroid_init(const char *proot_bin,
                                  const char *rootfs,
                                  const char *home_dir,
                                  const char *arch,
                                  const char *mirror) {
    int r = 0;

    /* PTY manager */
    r |= pty_manager_init();

    /* Shell subsystems */
    env_init();
    alias_init();

    /* Package subsystem */
    apt_init(proot_bin, rootfs, mirror ? mirror : KALIDROID_KALI_MIRROR);
    dpkg_init(proot_bin, rootfs);
    repo_manager_init(proot_bin, rootfs, arch ? arch : "arm64");

    /* Path resolver */
    path_resolver_init(rootfs, home_dir, "/sdcard");

    return r;
}

/* -------------------------------------------------------------------------
 * kalidroid_destroy
 * Clean shutdown. Call on app exit.
 * ------------------------------------------------------------------------- */
static inline void kalidroid_destroy(void) {
    pty_manager_destroy();
    alias_destroy();
    env_destroy();
}

#ifdef __cplusplus
}
#endif

#endif /* KALIDROID_H */
