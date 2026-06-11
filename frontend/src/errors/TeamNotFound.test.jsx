import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import TeamNotFoundPage from './TeamNotFound';

describe('TeamNotFoundPage', () => {
  it('renders without crashing', () => {
    render(<TeamNotFoundPage />);
    expect(screen.getByRole('img')).toBeInTheDocument();
  });

  it('renders a heading', () => {
    render(<TeamNotFoundPage />);
    expect(screen.getByRole('heading')).toBeInTheDocument();
  });
});
