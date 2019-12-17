#include <jni.h>
#include <string>

extern "C" {
extern int bspatch_main(int arg, char *argv[]);
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_kangjj_ndk_bsdiff_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
extern "C"
JNIEXPORT void JNICALL
Java_com_kangjj_ndk_bsdiff_MainActivity_doPatchNative(JNIEnv *env, jobject instance,
                                                      jstring oldApk_, jstring newApk_,
                                                      jstring patch_) {
    const char *oldApk = env->GetStringUTFChars(oldApk_, 0);
    const char *newApk = env->GetStringUTFChars(newApk_, 0);
    const char *patch = env->GetStringUTFChars(patch_, 0);

    char *argv[4] = {"bspatch", const_cast<char *>(oldApk), const_cast<char *>(newApk),
                     const_cast<char *>(patch)};
    bspatch_main(4, argv);

    env->ReleaseStringUTFChars(oldApk_, oldApk);
    env->ReleaseStringUTFChars(newApk_, newApk);
    env->ReleaseStringUTFChars(patch_, patch);
}