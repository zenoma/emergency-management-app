import {
  Box,
  FormControl,
  InputLabel,
  MenuItem,
  Select,
  Typography,
} from "@mui/material";
import { useState } from "react";
import { useTranslation } from "react-i18next";
import enFlag from "../../assets/images/en.svg";
import esFlag from "../../assets/images/es.svg";
import glFlag from "../../assets/images/gl.svg";
import i18n from "../../i18n";

export const SwitchLanguajeDropdown = () => {
  const [language, setLanguage] = useState(i18n.language.substring(0,2));


  const handleChange = (event) => {
    setLanguage(event.target.value);
    i18n.changeLanguage(event.target.value);
  };

  const { t } = useTranslation();

  return (
    <Box sx={{ padding: "30px" }}>
      <FormControl fullWidth>
        <InputLabel id="demo-simple-select-label">{t("language")}</InputLabel>
        <Select
          id="language-selector"
          value={language}
          label="Languaje"
          onChange={handleChange}
        >
          <MenuItem value={"en"}>
            <Box sx={{ maxWidth: "20px", display: "flex" }}>
              <img alt="English Flag" src={enFlag} />
              <Typography sx={{ margin: "5px" }}>English</Typography>
            </Box>
          </MenuItem>
          <MenuItem value={"es"}>
            <Box sx={{ maxWidth: "20px", display: "flex" }}>
              <img alt="Bandera Española" src={esFlag} />
              <Typography sx={{ margin: "5px" }}>Español</Typography>
            </Box>
          </MenuItem>
          <MenuItem value={"gl"}>
            <Box sx={{ maxWidth: "20px", display: "flex" }}>
              <img alt="Bandera Gallega" src={glFlag} />
              <Typography sx={{ margin: "5px" }}>Galego</Typography>
            </Box>
          </MenuItem>
        </Select>
      </FormControl>
    </Box>
  );
};

export default function BasicSelect() {}
