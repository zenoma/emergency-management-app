import React, { useState } from "react";
import { DateRangePicker, DateTimePicker } from "@mui/x-data-pickers";
import AdapterDateFns from "@mui/lab/AdapterDateFns";
import LocalizationProvider from "@mui/lab/LocalizationProvider";

function DateTimeRangePicker() {
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
        label="Start Date and Time"
        value={startDateTime}
        onChange={handleStartDateTimeChange}
        renderInput={(params) => <TextField {...params} />}
      />
      <DateTimePicker
        label="End Date and Time"
        value={endDateTime}
        onChange={handleEndDateTimeChange}
        renderInput={(params) => <TextField {...params} />}
      />
    </LocalizationProvider>
  );
}

export default DateTimeRangePicker;
