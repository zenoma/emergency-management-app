import { describe, it, expect, vi, beforeAll } from 'vitest';
import { render, waitFor } from '@testing-library/react';
import { Provider } from 'react-redux';
import { store } from './app/store';
import App from './App';

// Silence i18n debug output in tests
import i18n from './i18n';
beforeAll(() => {
  i18n.changeLanguage('es');
  // Mock navigator.geolocation (not available in jsdom)
  Object.defineProperty(navigator, 'geolocation', {
    value: { getCurrentPosition: vi.fn() },
    configurable: true,
  });
});

vi.mock('./api/userApi', () => ({
  useLoginFromTokenMutation: () => [
    vi.fn().mockReturnValue({ unwrap: () => Promise.reject(new Error('Rejected')) }),
  ],
}));

describe('App', () => {
  it('renders without crashing', async () => {
    const { container } = render(
      <Provider store={store}>
        <App />
      </Provider>
    );
    await waitFor(() => {
      expect(container.querySelector('.App')).toBeInTheDocument();
    });
  });
});
