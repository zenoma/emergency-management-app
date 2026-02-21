import { isRejected } from "@reduxjs/toolkit";
import { toast } from "react-toastify";

export const rtkQueryErrorLogger = (api) => (next) => (action) => {
  if (isRejected(action) && action.payload && action.error && action.error.message) {
    const { status, data } = action.payload;

    if (import.meta.env.VITE_REACT_APP_MODE === "development") {
      console.error(`Request ${action.meta.requestId} rejected with payload:`, action.payload);
    }

    if (status === 500) {
      console.error(action);
      toast.error("500: Internal Server Error");
    } else if (data) {
      if (data.fieldErrors && data.fieldErrors.length > 0) {
        data.fieldErrors.forEach((fieldError) => {
          const errorMessage = `${fieldError.fieldName}: ${fieldError.message}`;
          console.error(errorMessage);
          toast.error(errorMessage);
        });
      } else if (data.errorMessage) {
        const errorMessage = data.errorMessage;
        console.error(errorMessage);
        toast.error(errorMessage);
      }
    }
  }

  return next(action);
};
