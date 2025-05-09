import { configureStore } from "@reduxjs/toolkit";

import loginReducer from "../features/user/login/LoginSlice";
import { baseApi } from "../api/baseApi";
import { weatherApi } from "../api/weatherApi";
import { rtkQueryErrorLogger } from "../app/rtkQueryErrorHandler";
import theme from "../features/theme/themeSlice";

export const store = configureStore({
  reducer: {
    theme: theme,
    login: loginReducer,
    [baseApi.reducerPath]: baseApi.reducer,
    [weatherApi.reducerPath]: weatherApi.reducer,
  },
  middleware: (getDefaultMiddleware) => getDefaultMiddleware().concat(baseApi.middleware).concat(weatherApi.middleware).concat(rtkQueryErrorLogger),
});
