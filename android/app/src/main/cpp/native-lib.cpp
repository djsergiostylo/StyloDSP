#include <jni.h>
#include <android/log.h>
#include <cmath>
#include <memory>
#include <mutex>

#include <oboe/Oboe.h>

extern "C" {
void* stylo_gain_create(float gain_linear);
void stylo_gain_destroy(void* handle);
void stylo_gain_process(const void* handle, float* samples, size_t sample_count);
}

namespace {
constexpr char TAG[] = "StyloDSP";

class AudioEngine final : public oboe::AudioStreamDataCallback,
                          public oboe::AudioStreamErrorCallback {
public:
    ~AudioEngine() override { stop(); }

    oboe::Result start() {
        std::lock_guard<std::mutex> lock(mutex_);
        if (stream_) {
            return oboe::Result::OK;
        }

        gain_ = stylo_gain_create(1.0f);
        if (!gain_) {
            return oboe::Result::ErrorInternal;
        }

        oboe::AudioStreamBuilder builder;
        builder.setDirection(oboe::Direction::Output)
            ->setPerformanceMode(oboe::PerformanceMode::LowLatency)
            ->setSharingMode(oboe::SharingMode::Shared)
            ->setFormat(oboe::AudioFormat::Float)
            ->setChannelCount(2)
            ->setDataCallback(this)
            ->setErrorCallback(this);

        oboe::Result result = builder.openStream(stream_);
        if (result != oboe::Result::OK || !stream_) {
            __android_log_print(ANDROID_LOG_ERROR, TAG, "openStream failed: %s", oboe::convertToText(result));
            cleanupLocked();
            return result;
        }

        sample_rate_ = static_cast<float>(stream_->getSampleRate());
        if (sample_rate_ <= 0.0f) {
            sample_rate_ = 48000.0f;
        }
        phase_ = 0.0f;

        result = stream_->requestStart();
        if (result != oboe::Result::OK) {
            __android_log_print(ANDROID_LOG_ERROR, TAG, "requestStart failed: %s", oboe::convertToText(result));
            cleanupLocked();
            return result;
        }

        return oboe::Result::OK;
    }

    void stop() {
        std::lock_guard<std::mutex> lock(mutex_);
        cleanupLocked();
    }

    oboe::DataCallbackResult onAudioReady(
        oboe::AudioStream* /*audioStream*/, void* audioData, int32_t numFrames) override {
        if (!audioData || numFrames <= 0 || !gain_) {
            return oboe::DataCallbackResult::Continue;
        }

        auto* output = static_cast<float*>(audioData);
        constexpr float frequency = 440.0f;
        constexpr float amplitude = 0.05f;
        const int32_t channelCount = 2;
        const float phaseIncrement = 2.0f * static_cast<float>(M_PI) * frequency / sample_rate_;

        for (int32_t frame = 0; frame < numFrames; ++frame) {
            const float sample = amplitude * std::sin(phase_);
            phase_ += phaseIncrement;
            if (phase_ >= 2.0f * static_cast<float>(M_PI)) {
                phase_ -= 2.0f * static_cast<float>(M_PI);
            }
            output[frame * channelCount] = sample;
            output[frame * channelCount + 1] = sample;
        }

        stylo_gain_process(gain_, output, static_cast<size_t>(numFrames) * channelCount);
        return oboe::DataCallbackResult::Continue;
    }

    void onErrorAfterClose(oboe::AudioStream* /*audioStream*/, oboe::Result error) override {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "audio error after close: %s", oboe::convertToText(error));
    }

private:
    void cleanupLocked() {
        if (stream_) {
            (void)stream_->requestStop();
            (void)stream_->close();
            stream_.reset();
        }
        if (gain_) {
            stylo_gain_destroy(gain_);
            gain_ = nullptr;
        }
    }

    std::mutex mutex_;
    std::shared_ptr<oboe::AudioStream> stream_;
    void* gain_ = nullptr;
    float sample_rate_ = 48000.0f;
    float phase_ = 0.0f;
};

AudioEngine g_engine;

} // namespace

extern "C" JNIEXPORT jint JNICALL
Java_com_stylo_dsp_MainActivity_nativeStart(JNIEnv*, jobject) {
    const oboe::Result result = g_engine.start();
    return result == oboe::Result::OK ? 0 : static_cast<jint>(result);
}

extern "C" JNIEXPORT void JNICALL
Java_com_stylo_dsp_MainActivity_nativeStop(JNIEnv*, jobject) {
    g_engine.stop();
}
