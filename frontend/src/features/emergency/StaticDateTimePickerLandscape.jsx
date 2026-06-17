import React, { useState } from "react";
import { DateRangePicker, DateTimePicker } from "@mui/x-data-pickers";
import AdapterDateFns from "@mui/lab/AdapterDateFns";
import LocalizationProvider from "@mui/lab/LocalizationProvider";
import { useTranslation } from "react-i18next";

function DateTimeRangePicker() {
  const { t } = useTranslation();
  const [startDateTime, setStartDateTime] = useState(null);
  const [endDateTime, setEndDateTime] = useState(null);

  const handleStartDateTimeChange = (date) => {
    setStartDateTime(date);
  };

  const handleEndDateTimeChange = (date) => {
    setEndDateTime(date);
  };

  return (
    <LocalizationProvider dateAdapter={AdapterDateFns}>
      <DateTimePicker
        label={t("start-date-time")}
        value={startDateTime}
        onChange={handleStartDateTimeChange}
        renderInput={(params) => <TextField {...params} />}
      />
      <DateTimePicker
        label={t("end-date-time")}
        value={endDateTime}
        onChange={handleEndDateTimeChange}
        renderInput={(params) => <TextField {...params} />}
      />
    </LocalizationProvider>
  );
}

export default DateTimeRangePicker;
