/*
 * VoidTerm - NativeFs (JNI wrapper)
 * Wraps the native fs_utils / path_resolver / permission_helper / sha256
 * layer. Safe — every call is guarded so a missing or unlinked .so never
 * crashes the app.
 *
 * Native method names below MUST match fs_jni.cpp exactly (package +
 * class + method name form the JNI symbol).
 *
 * Developer : Asotn | https://github.com/Asotn | s.pi@outlook.sa
 * License   : GPL-3.0
 */
package com.asotn.voidterm.utils;

import android.util.Log;

import com.asotn.voidterm.engine.NativeTerminal;

public final class NativeFs {

    private static final String TAG = "VoidTerm-NativeFs";

    // -------------------------------------------------------------------
    // Native declarations — names MUST match fs_jni.cpp exactly
    // -------------------------------------------------------------------
    private static native void    nativePathResolverInit(String rootfs, String home, String sdcard);
    private static native String  nativeGuestToHost(String guestPath);
    private static native String  nativePathJoin(String base, String rel);

    private static native boolean nativeExists(String path);
    private static native long    nativeFileSize(String path);
    private static native long    nativeFreeSpace(String path);
    private static native boolean nativeMkdirs(String path);
    private static native boolean nativeDeleteFile(String path);
    private static native boolean nativeDeleteDirRecursive(String path);

    private static native boolean nativeCanRead(String path);
    private static native boolean nativeCanWrite(String path);
    private static native boolean nativeSdcardReadable();
    private static native boolean nativeSdcardWritable();
    private static native String  nativeGetModeString(String path);

    private static native String  nativeSha256File(String path);
    private static native boolean nativeSha256Verify(String path, String expectedHex);

    private NativeFs() { }

    private static boolean ready() {
        return NativeTerminal.isLibraryLoaded();
    }

    // -------------------------------------------------------------------
    // Public safe API
    // -------------------------------------------------------------------
    public static void pathResolverInit(String rootfs, String home, String sdcard) {
        if (!ready()) return;
        try { nativePathResolverInit(rootfs, home, sdcard); }
        catch (Throwable t) { Log.w(TAG, "pathResolverInit: " + t.getMessage()); }
    }

    public static String guestToHost(String guestPath) {
        if (!ready()) return guestPath;
        try { return nativeGuestToHost(guestPath); }
        catch (Throwable t) { return guestPath; }
    }

    public static String pathJoin(String base, String rel) {
        if (!ready()) return base + "/" + rel;
        try { return nativePathJoin(base, rel); }
        catch (Throwable t) { return base + "/" + rel; }
    }

    public static boolean exists(String path) {
        if (!ready()) return new java.io.File(path).exists();
        try { return nativeExists(path); } catch (Throwable t) { return false; }
    }

    public static long fileSize(String path) {
        if (!ready()) return new java.io.File(path).length();
        try { return nativeFileSize(path); } catch (Throwable t) { return -1; }
    }

    public static long freeSpace(String path) {
        if (!ready()) return new java.io.File(path).getFreeSpace();
        try { return nativeFreeSpace(path); } catch (Throwable t) { return -1; }
    }

    public static boolean mkdirs(String path) {
        if (!ready()) return new java.io.File(path).mkdirs();
        try { return nativeMkdirs(path); } catch (Throwable t) { return false; }
    }

    public static boolean deleteFile(String path) {
        if (!ready()) return new java.io.File(path).delete();
        try { return nativeDeleteFile(path); } catch (Throwable t) { return false; }
    }

    public static boolean deleteDirRecursive(String path) {
        if (!ready()) return false;
        try { return nativeDeleteDirRecursive(path); } catch (Throwable t) { return false; }
    }

    public static boolean canRead(String path) {
        if (!ready()) return new java.io.File(path).canRead();
        try { return nativeCanRead(path); } catch (Throwable t) { return false; }
    }

    public static boolean canWrite(String path) {
        if (!ready()) return new java.io.File(path).canWrite();
        try { return nativeCanWrite(path); } catch (Throwable t) { return false; }
    }

    public static boolean sdcardReadable() {
        if (!ready()) return false;
        try { return nativeSdcardReadable(); } catch (Throwable t) { return false; }
    }

    public static boolean sdcardWritable() {
        if (!ready()) return false;
        try { return nativeSdcardWritable(); } catch (Throwable t) { return false; }
    }

    public static String getModeString(String path) {
        if (!ready()) return "----------";
        try { return nativeGetModeString(path); } catch (Throwable t) { return "----------"; }
    }

    public static String sha256File(String path) {
        if (!ready()) return "";
        try { return nativeSha256File(path); } catch (Throwable t) { return ""; }
    }

    public static boolean sha256Verify(String path, String expectedHex) {
        if (!ready()) return false;
        try { return nativeSha256Verify(path, expectedHex); } catch (Throwable t) { return false; }
    }
}
