export interface WavEncodeOptions {
  bitDepth?: 16 | 24 | 32;
}

export function encodeWav(buffer: AudioBuffer, options: WavEncodeOptions = {}): Blob {
  const bitDepth = options.bitDepth ?? 24;
  const channels = buffer.numberOfChannels;
  const sampleRate = buffer.sampleRate;
  const bytesPerSample = bitDepth / 8;
  const blockAlign = channels * bytesPerSample;
  const dataSize = buffer.length * blockAlign;
  const headerSize = 44;
  const output = new ArrayBuffer(headerSize + dataSize);
  const view = new DataView(output);

  writeAscii(view, 0, 'RIFF');
  view.setUint32(4, 36 + dataSize, true);
  writeAscii(view, 8, 'WAVE');
  writeAscii(view, 12, 'fmt ');
  view.setUint32(16, 16, true);
  view.setUint16(20, 1, true);
  view.setUint16(22, channels, true);
  view.setUint32(24, sampleRate, true);
  view.setUint32(28, sampleRate * blockAlign, true);
  view.setUint16(32, blockAlign, true);
  view.setUint16(34, bitDepth, true);
  writeAscii(view, 36, 'data');
  view.setUint32(40, dataSize, true);

  const channelData = Array.from({ length: channels }, (_, ch) => buffer.getChannelData(ch));
  let offset = headerSize;
  for (let frame = 0; frame < buffer.length; frame++) {
    for (let ch = 0; ch < channels; ch++) {
      const sample = Math.max(-1, Math.min(1, channelData[ch][frame]));
      if (bitDepth === 32) {
        view.setInt32(offset, Math.round(sample * 0x7fffffff), true);
        offset += 4;
      } else if (bitDepth === 24) {
        const value = Math.round(sample * 0x7fffff);
        view.setUint8(offset, value & 0xff);
        view.setUint8(offset + 1, (value >> 8) & 0xff);
        view.setUint8(offset + 2, (value >> 16) & 0xff);
        offset += 3;
      } else {
        view.setInt16(offset, Math.round(sample * 0x7fff), true);
        offset += 2;
      }
    }
  }

  return new Blob([output], { type: 'audio/wav' });
}

function writeAscii(view: DataView, offset: number, value: string): void {
  for (let i = 0; i < value.length; i++) view.setUint8(offset + i, value.charCodeAt(i));
}
