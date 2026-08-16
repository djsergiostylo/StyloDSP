//! STYLO DSP Core
//!
//! Platform-independent DSP primitives. Host adapters (Web, Android, VST3)
//! must own audio I/O and must not leak host APIs into this crate.

/// Simple gain processor using `f32` as the default realtime sample type.
///
/// The processor is allocation-free after construction and processes an
/// existing buffer in place.
#[derive(Debug, Clone, Copy)]
pub struct Gain {
    gain_linear: f32,
}

impl Gain {
    /// Creates a gain processor from a linear gain value.
    pub const fn new(gain_linear: f32) -> Self {
        Self { gain_linear }
    }

    /// Creates a gain processor from decibels.
    pub fn from_db(db: f32) -> Self {
        Self::new(db_to_linear(db))
    }

    /// Updates the gain. This operation is intended for configuration or a
    /// host-provided parameter snapshot, not for allocation or blocking work.
    pub fn set_linear(&mut self, gain_linear: f32) {
        self.gain_linear = gain_linear;
    }

    /// Returns the current linear gain.
    pub const fn gain_linear(&self) -> f32 {
        self.gain_linear
    }

    /// Processes an existing PCM buffer in place. No allocation occurs here.
    pub fn process(&self, buffer: &mut [f32]) {
        for sample in buffer.iter_mut() {
            *sample *= self.gain_linear;
        }
    }
}

/// Converts dB to linear amplitude.
pub fn db_to_linear(db: f32) -> f32 {
    10.0_f32.powf(db / 20.0)
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn gain_zero_db_is_unity() {
        let gain = Gain::from_db(0.0);
        assert!((gain.gain_linear() - 1.0).abs() < 1e-6);
    }

    #[test]
    fn gain_processes_expected_samples() {
        let gain = Gain::new(2.0);
        let mut buffer = [0.25_f32, -0.5, 1.0, 0.0];
        gain.process(&mut buffer);
        assert_eq!(buffer, [0.5, -1.0, 2.0, 0.0]);
    }

    #[test]
    fn gain_does_not_create_non_finite_values_for_finite_normal_input() {
        let gain = Gain::from_db(6.0);
        let mut buffer = [0.0_f32, 0.25, -0.25, 1.0];
        gain.process(&mut buffer);
        assert!(buffer.iter().all(|sample| sample.is_finite()));
    }
}
