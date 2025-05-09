import { createTheme } from "@mui/material/styles";

const baseTheme = createTheme({
  typography: {
    fontFamily: "'Work Sans', sans-serif",
    fontSize: 14,
    fontFamilySecondary: "'Roboto Condensed', sans-serif",
  },
});

const darkTheme = createTheme({
  ...baseTheme,
  palette: {
    mode: "dark",
    primary: {
      main: "#3EB489",
    },
    secondary: {
      main: "#B43E69",
    },
  },
});

const lightTheme = createTheme({
  ...baseTheme,
  palette: {
    mode: "light",
    primary: {
      main: "#3EB489",
    },
    secondary: {
      main: "#B43E69",
    },
    background: {
      default: "#FAFAFA",
    },
  },
});

export { darkTheme, lightTheme };
