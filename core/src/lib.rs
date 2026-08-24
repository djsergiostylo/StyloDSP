#![allow(dead_code)]

#[no_mangle]
pub extern "C" fn stylo_apply_gain(sample: f32, gain_db: f32) -> f32 {
    let gain = 10.0_f32.powf(gain_db / 20.0);
    sample * gain
}
