import { describe, it, expect } from 'vitest';
import themeReducer, { toggleTheme } from './themeSlice';

describe('themeSlice', () => {
  it('returns initial state with darkTheme false', () => {
    const state = themeReducer(undefined, { type: 'unknown' });
    expect(state).toEqual({ darkTheme: false });
  });

  it('toggles darkTheme to true', () => {
    const state = themeReducer({ darkTheme: false }, toggleTheme());
    expect(state.darkTheme).toBe(true);
  });

  it('toggles darkTheme back to false', () => {
    const state = themeReducer({ darkTheme: true }, toggleTheme());
    expect(state.darkTheme).toBe(false);
  });
});
