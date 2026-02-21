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
    primary: { main: "#3EB489" },
    secondary: { main: "#B43E69" },
    background: {
      default: "#0F1115",
      paper: "rgba(255,255,255,0.04)",
    },
  },
});

const lightTheme = createTheme({
  ...baseTheme,
  palette: {
    mode: "light",
    primary: { main: "#3EB489" },
    secondary: { main: "#B43E69" },
    background: {
      default: "#F5F7F8",
      paper: "#FFFFFF",
    }
  },
});

export { darkTheme, lightTheme };
