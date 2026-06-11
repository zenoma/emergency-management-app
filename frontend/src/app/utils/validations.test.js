import { describe, it, expect } from 'vitest';
import { emailValidation, phoneNumberValidation, dniValidation } from './validations';

describe('emailValidation', () => {
  it('accepts valid emails', () => {
    expect(emailValidation('user@example.com')).toBe(true);
    expect(emailValidation('user.name@example.co.uk')).toBe(true);
  });

  it('rejects invalid emails', () => {
    expect(emailValidation('')).toBe(false);
    expect(emailValidation('not-an-email')).toBe(false);
    expect(emailValidation('@example.com')).toBe(false);
    expect(emailValidation('user@')).toBe(false);
    expect(emailValidation('user@.com')).toBe(false);
  });
});

describe('phoneNumberValidation', () => {
  it('accepts 9-digit phone numbers', () => {
    expect(phoneNumberValidation('123456789')).toBe(true);
    expect(phoneNumberValidation('987654321')).toBe(true);
  });

  it('rejects phone numbers with wrong digit count', () => {
    expect(phoneNumberValidation('')).toBe(false);
    expect(phoneNumberValidation('12345678')).toBe(false);
    expect(phoneNumberValidation('1234567890')).toBe(false);
  });

  it('extracts digits ignoring non-numeric characters', () => {
    expect(phoneNumberValidation('981 234 567')).toBe(true);
    expect(phoneNumberValidation('981-234-567')).toBe(true);
  });
});

describe('dniValidation', () => {
  it('accepts valid DNI with correct letter', () => {
    expect(dniValidation('12345678Z')).toBe(true);
  });

  it('rejects DNI with incorrect letter', () => {
    expect(dniValidation('12345678A')).toBe(false);
  });

  it('rejects malformed DNI', () => {
    expect(dniValidation('')).toBe(undefined);
    expect(dniValidation('1234')).toBe(undefined);
    expect(dniValidation('12345678')).toBe(undefined);
    expect(dniValidation('ABCDEFGHI')).toBe(undefined);
  });
});
