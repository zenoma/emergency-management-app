import { isFulfilled, isRejected } from "@reduxjs/toolkit";
import { toast } from "react-toastify";

export const rtkQueryErrorLogger = (api) => (next) => (action) => {
  if (process.env.REACT_APP_MODE === "development" && isFulfilled(action)) {
    console.log(`Request ${action.meta.requestId} succeeded with payload:`, action.payload);
  }

  if (isRejected(action) && action.payload && action.error && action.error.message) {
    const { status, data } = action.payload;

    if (data && data.fieldErrors) {
      data.fieldErrors.forEach((fieldError) => {
        const errorMessage = `${fieldError.fieldName}: ${fieldError.message}`;
        console.error(errorMessage);
        toast.error(errorMessage);
      });
    } else if (data && data.globalError) {
      const errorMessage = data.globalError;
      console.error(errorMessage);
      toast.error(errorMessage);
    } else if (status === "FETCH_ERROR") {
      console.error(action);
      toast.error("500: Internal Server Error");
    }
  }

  return next(action);
};
