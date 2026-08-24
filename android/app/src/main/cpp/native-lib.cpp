#include <jni.h>
extern "C" JNIEXPORT jfloat JNICALL
Java_com_stylo_eq_MainActivity_nativeGain(JNIEnv*, jobject, jfloat sample, jfloat gainDb) {
    const float gain = std::pow(10.0f, gainDb / 20.0f);
    return sample * gain;
}
