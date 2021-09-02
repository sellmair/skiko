#include "interop.hh"
#include "SkString.h"
#include "skia.h"

extern "C" JNIEXPORT jlong JNICALL Java_org_jetbrains_skia_ManagedString__1nGetFinalizer
  (JNIEnv* env, jclass jclass) {
    return (jlong)org_jetbrains_skia_ManagedString__1nGetFinalizer();
}

extern "C" JNIEXPORT jlong JNICALL Java_org_jetbrains_skia_ManagedString__1nMake
  (JNIEnv* env, jclass jclass, jstring textStr) {
    return (jlong)org_jetbrains_skia_ManagedString__1nMake(skString(env, textStr));
}

extern "C" JNIEXPORT jobject JNICALL Java_org_jetbrains_skia_ManagedString__1nToString
  (JNIEnv* env, jclass jclass, jlong ptr) {
    char* cstr = org_jetbrains_skia_ManagedString__1nToString(ptr); 
    return javaString(env, cstr);
}

extern "C" JNIEXPORT void JNICALL Java_org_jetbrains_skia_ManagedString__1nInsert
  (JNIEnv* env, jclass jclass, jlong ptr, jint offset, jstring s) {
    org_jetbrains_skia_ManagedString__1nInsert(ptr, offset, skString(env, s));
}

extern "C" JNIEXPORT void JNICALL Java_org_jetbrains_skia_ManagedString__1nAppend
  (JNIEnv* env, jclass jclass, jlong ptr, jstring s) {
    org_jetbrains_skia_ManagedString__1nAppend(ptr, skString(env, s));
}

extern "C" JNIEXPORT void JNICALL Java_org_jetbrains_skia_ManagedString__1nRemoveSuffix
  (JNIEnv* env, jclass jclass, jlong ptr, jint from) {
    org_jetbrains_skia_ManagedString__1nRemoveSuffix(ptr, from);
}

extern "C" JNIEXPORT void JNICALL Java_org_jetbrains_skia_ManagedString__1nRemove
  (JNIEnv* env, jclass jclass, jlong ptr, jint from, jint len) {
      org_jetbrains_skia_ManagedString__1nRemove(ptr, from, len);
}
