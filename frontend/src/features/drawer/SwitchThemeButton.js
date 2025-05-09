import DarkIcon from "@mui/icons-material/DarkMode";
import LightIcon from "@mui/icons-material/LightMode";
import { Box, IconButton } from "@mui/material";
import React from "react";
import { useDispatch, useSelector } from "react-redux";

import { toggleTheme } from "../theme/themeSlice";

export const SwitchThemeButton = () => {
  const dispatch = useDispatch();
  const theme = useSelector((state) => state.theme);

  return (
    <Box>
      <IconButton color="inherit" onClick={() => dispatch(toggleTheme())}>
        {theme.darkTheme ? <LightIcon /> : <DarkIcon />}
      </IconButton>
    </Box>
  );
};
