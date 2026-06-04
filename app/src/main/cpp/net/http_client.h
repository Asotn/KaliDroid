/*
 * KaliDroid - HTTP Client Header
 * Developer : Rotlqe | https://github.com/Rotlqe | s.pi@outlook.sa
 */
#ifndef HTTP_CLIENT_H
#define HTTP_CLIENT_H
#include <stddef.h>
typedef void (*http_progress_fn)(long downloaded, long total, int pct, void *userdata);
int http_download(const char *url, const char *dest_path,
                   http_progress_fn progress_fn, void *userdata);
int http_fetch_string(const char *url, char *buf, size_t buf_size);
#endif
