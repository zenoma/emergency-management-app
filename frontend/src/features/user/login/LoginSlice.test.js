import { describe, it, expect, beforeEach } from 'vitest';
import loginReducer, { validLogin, logout, selectUser, selectToken } from './LoginSlice';

const mockUser = { id: 1, name: 'Test', email: 'test@example.com', userRole: 'COORDINATOR' };
const mockToken = 'abc123';

describe('LoginSlice', () => {
  beforeEach(() => {
    localStorage.clear();
  });

  it('returns initial state', () => {
    const state = loginReducer(undefined, { type: 'unknown' });
    expect(state).toEqual({ user: '', token: '' });
  });

  it('handles validLogin', () => {
    const action = validLogin({ user: mockUser, token: mockToken });
    const state = loginReducer(undefined, action);
    expect(state.user).toEqual(mockUser);
    expect(state.token).toBe(mockToken);
    expect(localStorage.getItem('token')).toBe(mockToken);
  });

  it('handles logout', () => {
    const initial = { user: mockUser, token: mockToken };
    localStorage.setItem('token', mockToken);
    const state = loginReducer(initial, logout());
    expect(state.user).toBe('');
    expect(state.token).toBe('');
    expect(localStorage.getItem('token')).toBe('');
  });

  it('selectUser returns user from state', () => {
    const state = { login: { user: mockUser, token: mockToken } };
    expect(selectUser(state)).toEqual(mockUser);
  });

  it('selectToken returns token from state', () => {
    const state = { login: { user: mockUser, token: mockToken } };
    expect(selectToken(state)).toBe(mockToken);
  });
});
