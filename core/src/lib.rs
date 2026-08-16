#![deny(unsafe_op_in_unsafe_fn)]

use std::slice;
use std::sync::atomic::{AtomicU32, Ordering};

#[repr(C)]
pub struct StyloGain {
    gain_bits: AtomicU32,
}

impl StyloGain {
    pub const fn new(gain_linear: f32) -> Self {
        Self { gain_bits: AtomicU32::new(gain_linear.to_bits()) }
    }

    #[inline]
    pub fn set_gain(&self, gain_linear: f32) {
        self.gain_bits.store(gain_linear.to_bits(), Ordering::Relaxed);
    }

    #[inline]
    pub fn gain(&self) -> f32 {
        f32::from_bits(self.gain_bits.load(Ordering::Relaxed))
    }

    #[inline]
    pub fn process(&self, samples: &mut [f32]) {
        let gain = self.gain();
        for sample in samples {
            *sample *= gain;
        }
    }
}

#[unsafe(no_mangle)]
pub extern "C" fn stylo_gain_create(gain_linear: f32) -> *mut StyloGain {
    Box::into_raw(Box::new(StyloGain::new(gain_linear)))
}

#[unsafe(no_mangle)]
pub unsafe extern "C" fn stylo_gain_destroy(handle: *mut StyloGain) {
    if !handle.is_null() {
        // SAFETY: the pointer must have been returned by stylo_gain_create and
        // is consumed exactly once here.
        unsafe { drop(Box::from_raw(handle)); }
    }
}

#[unsafe(no_mangle)]
pub unsafe extern "C" fn stylo_gain_set(handle: *const StyloGain, gain_linear: f32) {
    if handle.is_null() || !gain_linear.is_finite() || gain_linear < 0.0 {
        return;
    }

    // SAFETY: caller supplies a valid handle from stylo_gain_create.
    let gain = unsafe { &*handle };
    gain.set_gain(gain_linear);
}

#[unsafe(no_mangle)]
pub unsafe extern "C" fn stylo_gain_process(
    handle: *const StyloGain,
    samples: *mut f32,
    sample_count: usize,
) {
    if handle.is_null() || samples.is_null() || sample_count == 0 {
        return;
    }

    // SAFETY: caller supplies a valid handle from stylo_gain_create and a
    // writable PCM buffer containing sample_count f32 values.
    let gain = unsafe { &*handle };
    // SAFETY: caller guarantees the buffer is valid for sample_count samples.
    let buffer = unsafe { slice::from_raw_parts_mut(samples, sample_count) };
    gain.process(buffer);
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn gain_processes_samples() {
        let gain = StyloGain::new(2.0);
        let mut data = [0.25, -0.5, 1.0, 0.0];
        gain.process(&mut data);
        assert_eq!(data, [0.5, -1.0, 2.0, 0.0]);
    }

    #[test]
    fn gain_parameter_updates_without_rebuilding_processor() {
        let gain = StyloGain::new(1.0);
        gain.set_gain(0.5);
        let mut data = [1.0, -2.0];
        gain.process(&mut data);
        assert_eq!(data, [0.5, -1.0]);
    }
}
