import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import EmergencyTypeIcon from './EmergencyTypeIcon';

describe('EmergencyTypeIcon', () => {
  it('renders without crashing', () => {
    const { container } = render(<EmergencyTypeIcon name="Incendio forestal" />);
    expect(container.firstChild).toBeInTheDocument();
  });

  it('shows the label when showLabel is true', () => {
    render(<EmergencyTypeIcon name="Incendio forestal" showLabel />);
    expect(screen.getByText('Incendio forestal')).toBeInTheDocument();
  });

  it('does not show the label by default', () => {
    render(<EmergencyTypeIcon name="Incendio forestal" />);
    expect(screen.queryByText('Incendio forestal')).not.toBeInTheDocument();
  });

  it('renders fire icon for incendio names', () => {
    const { container } = render(<EmergencyTypeIcon name="Incendio" />);
    const svg = container.querySelector('svg');
    expect(svg).toBeInTheDocument();
  });

  it('renders water icon for inundación names', () => {
    const { container } = render(<EmergencyTypeIcon name="Inundación" />);
    expect(container.querySelector('svg')).toBeInTheDocument();
  });

  it('renders help icon for unknown names', () => {
    const { container } = render(<EmergencyTypeIcon name="UnknownType" />);
    expect(container.querySelector('svg')).toBeInTheDocument();
  });

  it('accepts custom sx styles', () => {
    const { container } = render(<EmergencyTypeIcon name="Test" sx={{ color: 'red' }} />);
    expect(container.firstChild).toBeInTheDocument();
  });
});
