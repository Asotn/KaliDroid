/*
 * KaliDroid - APT Wrapper
 * Wraps apt-get and apt-cache commands inside the Kali proot environment.
 * Parses output for progress reporting and error detection.
 *
 * Developer : Rotlqe | https://github.com/Rotlqe | s.pi@outlook.sa
 * License   : GPL-3.0
 */

#include "apt_wrapper.h"
#include "../terminal/process_runner.h"
#include <string.h>
#include <stdlib.h>
#include <stdio.h>
#include <android/log.h>

#define LOG_TAG "KaliDroid-APT"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,  LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

static char g_rootfs[512]    = {0};
static char g_proot_bin[512] = {0};
static char g_mirror[512]    = "http://http.kali.org/kali";

// Base apt-get environment
#define APT_ENV "DEBIAN_FRONTEND=noninteractive " \
                "APT_KEY_DONT_WARN_ON_DANGEROUS_USAGE=1 "

// -------------------------------------------------------------------------
// apt_init
// -------------------------------------------------------------------------
void apt_init(const char *proot_bin, const char *rootfs, const char *mirror) {
    if (proot_bin) strncpy(g_proot_bin, proot_bin, sizeof(g_proot_bin) - 1);
    if (rootfs)    strncpy(g_rootfs,    rootfs,    sizeof(g_rootfs)    - 1);
    if (mirror)    strncpy(g_mirror,    mirror,    sizeof(g_mirror)    - 1);
}

// -------------------------------------------------------------------------
// apt_run
// Runs an apt-get command inside proot.
// -------------------------------------------------------------------------
static int apt_run(const char *apt_args, char *out, size_t out_size) {
    char cmd[2048];
    snprintf(cmd, sizeof(cmd), APT_ENV "apt-get %s 2>&1", apt_args);
    LOGI("apt_run: %s", cmd);
    return process_run_proot(g_proot_bin, g_rootfs, cmd, out, out_size);
}

// -------------------------------------------------------------------------
// apt_cache_run
// Runs an apt-cache command inside proot.
// -------------------------------------------------------------------------
static int apt_cache_run(const char *apt_args, char *out, size_t out_size) {
    char cmd[2048];
    snprintf(cmd, sizeof(cmd), "apt-cache %s 2>&1", apt_args);
    return process_run_proot(g_proot_bin, g_rootfs, cmd, out, out_size);
}

// -------------------------------------------------------------------------
// apt_update
// Runs apt-get update. Returns 0 on success.
// -------------------------------------------------------------------------
int apt_update(char *out, size_t out_size) {
    return apt_run("update", out, out_size);
}

// -------------------------------------------------------------------------
// apt_upgrade
// -------------------------------------------------------------------------
int apt_upgrade(int dist_upgrade, char *out, size_t out_size) {
    const char *cmd = dist_upgrade
        ? "dist-upgrade -y --allow-downgrades --fix-broken"
        : "upgrade -y --allow-downgrades --fix-broken";
    return apt_run(cmd, out, out_size);
}

// -------------------------------------------------------------------------
// apt_install
// Installs one or more packages. pkg_list is space-separated.
// -------------------------------------------------------------------------
int apt_install(const char *pkg_list, char *out, size_t out_size) {
    if (!pkg_list || strlen(pkg_list) == 0) return -1;
    char args[2048];
    snprintf(args, sizeof(args),
        "install -y --no-install-recommends --fix-broken %s", pkg_list);
    return apt_run(args, out, out_size);
}

// -------------------------------------------------------------------------
// apt_remove
// -------------------------------------------------------------------------
int apt_remove(const char *pkg_list, int purge, char *out, size_t out_size) {
    if (!pkg_list) return -1;
    char args[2048];
    if (purge) {
        snprintf(args, sizeof(args), "purge -y %s && apt-get autoremove -y", pkg_list);
    } else {
        snprintf(args, sizeof(args), "remove -y %s", pkg_list);
    }
    return apt_run(args, out, out_size);
}

// -------------------------------------------------------------------------
// apt_autoremove
// -------------------------------------------------------------------------
int apt_autoremove(char *out, size_t out_size) {
    return apt_run("autoremove -y", out, out_size);
}

// -------------------------------------------------------------------------
// apt_autoclean
// -------------------------------------------------------------------------
int apt_autoclean(char *out, size_t out_size) {
    return apt_run("autoclean", out, out_size);
}

// -------------------------------------------------------------------------
// apt_fix_broken
// -------------------------------------------------------------------------
int apt_fix_broken(char *out, size_t out_size) {
    return apt_run("install -f -y", out, out_size);
}

// -------------------------------------------------------------------------
// apt_search
// -------------------------------------------------------------------------
int apt_search(const char *query, char *out, size_t out_size) {
    if (!query) return -1;
    char args[512];
    snprintf(args, sizeof(args), "search %s", query);
    return apt_cache_run(args, out, out_size);
}

// -------------------------------------------------------------------------
// apt_show
// -------------------------------------------------------------------------
int apt_show(const char *pkg_name, char *out, size_t out_size) {
    if (!pkg_name) return -1;
    char args[512];
    snprintf(args, sizeof(args), "show %s", pkg_name);
    return apt_cache_run(args, out, out_size);
}

// -------------------------------------------------------------------------
// apt_list_upgradable
// -------------------------------------------------------------------------
int apt_list_upgradable(char *out, size_t out_size) {
    char cmd[256];
    snprintf(cmd, sizeof(cmd), "list --upgradable 2>&1");
    return apt_run(cmd, out, out_size);
}

// -------------------------------------------------------------------------
// apt_download_only
// Downloads a package to the apt cache without installing.
// -------------------------------------------------------------------------
int apt_download_only(const char *pkg_name, char *out, size_t out_size) {
    if (!pkg_name) return -1;
    char args[512];
    snprintf(args, sizeof(args), "install --download-only -y %s", pkg_name);
    return apt_run(args, out, out_size);
}

// -------------------------------------------------------------------------
// apt_get_cache_size
// Returns the size of the apt cache in bytes.
// -------------------------------------------------------------------------
long apt_get_cache_size(void) {
    char out[64] = {0};
    process_run_proot(g_proot_bin, g_rootfs,
        "du -sb /var/cache/apt/archives/ 2>/dev/null | cut -f1",
        out, sizeof(out));
    return atol(out);
}

// -------------------------------------------------------------------------
// apt_write_sources_list
// Writes the Kali sources.list file. Call after changing mirror.
// -------------------------------------------------------------------------
int apt_write_sources_list(const char *mirror, const char *dist,
                            const char *components) {
    char path[512];
    snprintf(path, sizeof(path), "%s/etc/apt/sources.list", g_rootfs);

    char content[1024];
    snprintf(content, sizeof(content),
        "deb %s %s %s\n", mirror, dist, components);

    FILE *f = fopen(path, "w");
    if (!f) return -1;
    fputs(content, f);
    fclose(f);

    LOGI("sources.list updated: %s", content);
    return 0;
}

// -------------------------------------------------------------------------
// apt_set_mirror
// -------------------------------------------------------------------------
void apt_set_mirror(const char *mirror) {
    if (mirror) strncpy(g_mirror, mirror, sizeof(g_mirror) - 1);
}

// -------------------------------------------------------------------------
// apt_get_mirror
// -------------------------------------------------------------------------
const char *apt_get_mirror(void) {
    return g_mirror;
}

// -------------------------------------------------------------------------
// apt_parse_progress_line
// Detects a progress percentage in an apt output line.
// Returns -1 if not a progress line, 0-100 otherwise.
// -------------------------------------------------------------------------
int apt_parse_progress_line(const char *line) {
    if (!line) return -1;

    // Format: "XX% [N files... XXXKB/s Xs]"
    const char *p = line;
    while (*p == ' ') p++;

    int pct = 0;
    int consumed = 0;
    if (sscanf(p, "%d%%%n", &pct, &consumed) == 1 && consumed > 0) {
        if (p[consumed] == ' ' && p[consumed + 1] == '[') {
            return pct;
        }
    }

    // Format: "Get:N http://..."
    if (strncmp(line, "Get:", 4) == 0) return 0;
    if (strncmp(line, "Fetched", 7) == 0) return 100;

    return -1;
}
