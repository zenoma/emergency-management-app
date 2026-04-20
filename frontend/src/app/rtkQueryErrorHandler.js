import { isRejected } from "@reduxjs/toolkit";
import { toast } from "react-toastify";

export const rtkQueryErrorLogger = (api) => (next) => (action) => {
  // Log basic lifecycle events for RTK Query actions so we can see when
  // requests to the backend start and succeed (only in development).
  if (action && action.meta && action.meta.requestId) {
    const status = action.meta.requestStatus; // 'pending' | 'fulfilled' | 'rejected'
    // endpointName is usually available under action.meta.arg.endpointName for RTK Query
    const endpoint = action.meta.arg && action.meta.arg.endpointName ? action.meta.arg.endpointName : action.type;
    if (import.meta.env.VITE_REACT_APP_MODE === "development") {
      if (status === 'pending') {
        console.info(`[API] ${endpoint} started (requestId=${action.meta.requestId})`);
      } else if (status === 'fulfilled') {
        console.info(`[API] ${endpoint} succeeded (requestId=${action.meta.requestId})`);
      } else if (status === 'rejected') {
        // Log a concise failure summary; detailed handling (toasts, extra logs)
        // is performed later by the existing rejection handling.
        console.error(`[API] ${endpoint} failed (requestId=${action.meta.requestId})`, action.error || action.payload || action);
      }
    }
  }

  if (isRejected(action) && action.payload && action.error && action.error.message) {
    const { status, data } = action.payload;

    if (import.meta.env.VITE_REACT_APP_MODE === "development") {
      console.error(`Request ${action.meta.requestId} rejected with payload:`, action.payload);
    }

    if (status === 500) {
      console.error(action);
      toast.error("500: Internal Server Error");
    } else if (status === 404) {
      // Suppress noisy toasts for missing assignment after deletion.
      // The backend returns 404 for GET /assignments/{id} when the assignment
      // was just deleted; showing a toast in that expected flow is confusing.
      const msg = data && data.errorMessage ? data.errorMessage : '';
      if (msg.includes('Assignment not found') || msg.includes('No existe Assignment')) {
        if (import.meta.env.VITE_REACT_APP_MODE === "development") {
          console.debug('Ignored 404:', msg);
        }
      } else {
        toast.error('404: Not found');
      }
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
