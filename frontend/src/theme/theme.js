import { createTheme } from "@mui/material/styles";

const baseTheme = {
  typography: {
    fontFamily: "'Work Sans', sans-serif",
    fontSize: 14,
    h5: {
      fontSize: "1.35rem",
      fontWeight: 600,
    },
    h4: {
      fontWeight: 700,
    },
  },
  shape: {
    borderRadius: 14,
  },
  components: {
    MuiPaper: {
      styleOverrides: {
        root: {
          borderRadius: 14,
        },
      },
    },
    MuiCard: {
      styleOverrides: {
        root: {
          borderRadius: 20,
        },
      },
    },
  },
};

const darkTheme = createTheme({
  ...baseTheme,
  palette: {
    mode: "dark",
    // Brand colors inspired by the provided art: deep navy background + warm orange pin
    primary: { main: "#0B3A66", contrastText: "#FFFFFF" }, // deep navy
    secondary: { main: "#FF6A00", contrastText: "#FFFFFF" }, // warm orange accent
    background: {
      // deep, slightly teal-leaning navy for app background
      default: "#071E2B",
      // cards/paper slightly lighter than the background
      paper: "#0B3450",
    },
    text: {
      primary: "#FFFFFF",
      secondary: "rgba(255,255,255,0.8)",
    },
    // Status colors for chips / labels used across the app
    status: {
      accepted: { main: '#2e7d32', light: '#e8f5e9', dark: '#1b5e20', contrastText: '#FFFFFF' },
      pending: { main: '#ff9800', light: '#fff8e1', dark: '#ff6a00', contrastText: '#0f1115' },
      busy: { main: '#d32f2f', light: '#ffcdd2', dark: '#b71c1c', contrastText: '#FFFFFF' },
      available: { main: '#4caf50', light: '#e8f5e9', dark: '#2e7d32', contrastText: '#FFFFFF' },
      completed: { main: '#607d8b', light: '#eceff1', dark: '#455a64', contrastText: '#FFFFFF' },
      default: { main: '#9e9e9e', light: '#f5f5f5', dark: '#616161', contrastText: '#0f1115' },
    },
  },
});

const lightTheme = createTheme({
  ...baseTheme,
  palette: {
    mode: "light",
    // Keep the brand primary navy for recognizability on light surfaces
    primary: { main: "#0B3A66", contrastText: "#FFFFFF" },
    secondary: { main: "#FF6A00", contrastText: "#FFFFFF" },
    background: {
      default: "#F5F7F8",
      paper: "#FFFFFF",
    },
    text: {
      primary: "#0F1115",
      secondary: "rgba(15,17,21,0.7)",
    },
    // Same status palette for light theme (keeps colors consistent)
    status: {
      accepted: { main: '#2e7d32', light: '#e8f5e9', dark: '#1b5e20', contrastText: '#FFFFFF' },
      pending: { main: '#ff9800', light: '#fff8e1', dark: '#ff6a00', contrastText: '#0f1115' },
      busy: { main: '#d32f2f', light: '#ffcdd2', dark: '#b71c1c', contrastText: '#FFFFFF' },
      available: { main: '#4caf50', light: '#e8f5e9', dark: '#2e7d32', contrastText: '#FFFFFF' },
      completed: { main: '#607d8b', light: '#eceff1', dark: '#455a64', contrastText: '#FFFFFF' },
      default: { main: '#9e9e9e', light: '#f5f5f5', dark: '#616161', contrastText: '#0f1115' },
    },
  },
});

export { darkTheme, lightTheme };
