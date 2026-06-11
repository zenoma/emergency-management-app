import { describe, it, expect } from 'vitest';
import { transformCoordinates, untransformCoordinates } from './coordinatesTransformations';

describe('transformCoordinates', () => {
  it('transforms geographic coordinates to UTM', () => {
    // Santiago de Compostela approx: 42.88°N, -8.54°E
    const result = transformCoordinates(-8.54, 42.88);
    expect(result).toHaveProperty('longitude');
    expect(result).toHaveProperty('latitude');
    expect(typeof result.longitude).toBe('number');
    expect(typeof result.latitude).toBe('number');
  });

  it('round-trip: untransform(transform(lon, lat)) ≈ original', () => {
    const originalLon = -8.54;
    const originalLat = 42.88;
    const utm = transformCoordinates(originalLon, originalLat);
    const back = untransformCoordinates(utm.longitude, utm.latitude);
    expect(back.longitude).toBeCloseTo(originalLon, 4);
    expect(back.latitude).toBeCloseTo(originalLat, 4);
  });
});

describe('untransformCoordinates', () => {
  it('converts UTM coordinates back to geographic', () => {
    const result = untransformCoordinates(500000, 4748000);
    expect(result).toHaveProperty('longitude');
    expect(result).toHaveProperty('latitude');
  });

  it('handles swapped axis when x > 1000000 and y < 1000000', () => {
    const result = untransformCoordinates(4748000, 500000);
    expect(result).toHaveProperty('longitude');
    expect(result).toHaveProperty('latitude');
  });
});
