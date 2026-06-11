import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import BackButton from './BackButton';

describe('BackButton', () => {
  it('renders a button', () => {
    render(
      <MemoryRouter>
        <BackButton />
      </MemoryRouter>
    );
    expect(screen.getByRole('button')).toBeInTheDocument();
  });

  it('renders an arrow icon', () => {
    render(
      <MemoryRouter>
        <BackButton />
      </MemoryRouter>
    );
    const button = screen.getByRole('button');
    expect(button.querySelector('svg')).toBeInTheDocument();
  });
});
