#include <jni.h>
#include <cmath>
#include <memory>
#include <atomic>
#include <oboe/Oboe.h>

extern "C" void stylo_gain_process(float* buffer, size_t len, float gain_linear);

namespace {

class StyloCallback final : public oboe::AudioStreamDataCallback {
public:
    oboe::DataCallbackResult onAudioReady(
            oboe::AudioStream* audioStream,
            void* audioData,
            int32_t numFrames) override {
        auto* output = static_cast<float*>(audioData);
        constexpr float sampleRate = 48000.0f;
        constexpr float frequency = 440.0f;
        constexpr float gain = 0.5f;

        const float phaseStep = 2.0f * static_cast<float>(M_PI) * frequency / sampleRate;
        float phase = phase_.load(std::memory_order_relaxed);

        for (int32_t i = 0; i < numFrames; ++i) {
            output[i] = std::sin(phase);
            phase += phaseStep;
            if (phase >= 2.0f * static_cast<float>(M_PI)) {
                phase -= 2.0f * static_cast<float>(M_PI);
            }
        }

        // The realtime callback performs no allocation, locking or JNI work.
        stylo_gain_process(output, static_cast<size_t>(numFrames), gain);
        phase_.store(phase, std::memory_order_relaxed);
        return oboe::DataCallbackResult::Continue;
    }

private:
    std::atomic<float> phase_{0.0f};
};

std::shared_ptr<oboe::AudioStream> stream;
std::shared_ptr<StyloCallback> callback;

bool openStream(oboe::SharingMode sharingMode) {
    oboe::AudioStreamBuilder builder;
    builder.setDirection(oboe::Direction::Output)
           ->setPerformanceMode(oboe::PerformanceMode::LowLatency)
           ->setSharingMode(sharingMode)
           ->setFormat(oboe::AudioFormat::Float)
           ->setChannelCount(1)
           ->setDataCallback(callback.get());

    oboe::Result result = builder.openStream(stream);
    if (result != oboe::Result::OK) {
        stream.reset();
        return false;
    }

    const int32_t burst = stream->getFramesPerBurst();
    if (burst > 0) {
        stream->setBufferSizeInFrames(burst * 2);
    }

    result = stream->requestStart();
    if (result != oboe::Result::OK) {
        stream->close();
        stream.reset();
        return false;
    }
    return true;
}

} // namespace

extern "C" JNIEXPORT jboolean JNICALL
Java_com_stylo_dsp_NativeAudio_start(JNIEnv*, jobject) {
    if (stream) return JNI_TRUE;
    callback = std::make_shared<StyloCallback>();

    // Prefer exclusive low-latency; fall back to shared if the device cannot provide it.
    if (openStream(oboe::SharingMode::Exclusive)) return JNI_TRUE;
    return openStream(oboe::SharingMode::Shared) ? JNI_TRUE : JNI_FALSE;
}

extern "C" JNIEXPORT void JNICALL
Java_com_stylo_dsp_NativeAudio_stop(JNIEnv*, jobject) {
    if (stream) {
        stream->requestStop();
        stream->close();
        stream.reset();
    }
    callback.reset();
}
