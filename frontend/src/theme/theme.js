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
  },
});

export { darkTheme, lightTheme };
