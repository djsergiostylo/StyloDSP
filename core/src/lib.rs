#![deny(unsafe_op_in_unsafe_fn)]

use std::slice;

#[repr(C)]
pub struct StyloGain {
    gain_linear: f32,
}

impl StyloGain {
    pub const fn new(gain_linear: f32) -> Self {
        Self { gain_linear }
    }

    pub fn process(&self, samples: &mut [f32]) {
        for sample in samples {
            *sample *= self.gain_linear;
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
}
