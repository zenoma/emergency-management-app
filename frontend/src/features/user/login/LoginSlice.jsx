import { createSlice } from "@reduxjs/toolkit";

const initialState = { user: "", token: "" };

export const loginSlice = createSlice({
  name: "login",
  initialState,
  reducers: {
    validLogin(state, action) {
      state.user = action.payload.user;
      state.token = action.payload.token;
      localStorage.setItem("token", state.token);
    },
    logout(state) {
      state.user = "";
      state.token = "";
      localStorage.setItem("token", "");
    },
  },
  extraReducers: {},
});

export const selectUser = (state) => state.login.user;
export const selectToken = (state) => state.login.token;

export const { validLogin, logout } = loginSlice.actions;

export default loginSlice.reducer;
