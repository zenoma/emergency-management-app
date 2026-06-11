import { describe, it, expect } from 'vitest';
import formatDate from './formatDate';

describe('formatDate', () => {
  it('returns "-" for null', () => {
    expect(formatDate(null)).toBe('-');
  });

  it('returns "-" for undefined', () => {
    expect(formatDate(undefined)).toBe('-');
  });

  it('formats a valid date string', () => {
    const result = formatDate('2025-06-15T10:30:00Z', 'es');
    expect(result).toMatch(/\d{1,2}\/\d{1,2}\/\d{4}, \d{2}:\d{2}:\d{2}/);
  });

  it('formats a Date object', () => {
    const d = new Date(2025, 0, 1, 9, 5, 30);
    const result = formatDate(d, 'es');
    expect(result).toMatch(/1\/1\/2025, 09:05:30/);
  });

  it('returns raw value for invalid date', () => {
    expect(formatDate('not-a-date')).toBe('not-a-date');
  });
});
