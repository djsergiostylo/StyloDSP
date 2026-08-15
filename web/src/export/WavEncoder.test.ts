import { describe, expect, it } from 'vitest';
import { encodeWav } from './WavEncoder';

describe('encodeWav', () => {
  it('creates a RIFF/WAVE blob', async () => {
    const context = new OfflineAudioContext(2, 16, 48000);
    const buffer = context.createBuffer(2, 16, 48000);
    buffer.getChannelData(0).fill(0.25);
    buffer.getChannelData(1).fill(-0.25);

    const blob = encodeWav(buffer, { bitDepth: 24 });
    const bytes = new Uint8Array(await blob.arrayBuffer());

    expect(blob.type).toBe('audio/wav');
    expect(new TextDecoder().decode(bytes.slice(0, 4))).toBe('RIFF');
    expect(new TextDecoder().decode(bytes.slice(8, 12))).toBe('WAVE');
    expect(new DataView(bytes.buffer).getUint16(34, true)).toBe(24);
  });
});
