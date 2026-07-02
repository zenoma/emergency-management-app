import React from "react";
import { Alert, Box, Button, Typography } from "@mui/material";
import { withTranslation } from "react-i18next";

class ErrorBoundary extends React.Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false };
  }

  static getDerivedStateFromError() {
    return { hasError: true };
  }

  componentDidCatch(error, errorInfo) {
    console.error("ErrorBoundary caught an error:", error, errorInfo);
  }

  handleReset = () => {
    this.setState({ hasError: false });
  };

  render() {
    const { t } = this.props;

    if (this.state.hasError) {
      return (
        <Box
          sx={{
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
            justifyContent: "center",
            minHeight: "50vh",
            padding: 3,
          }}
        >
          <Alert severity="error" sx={{ mb: 2, maxWidth: 500 }}>
            <Typography variant="h6" gutterBottom>
              {this.props.title || t("error-boundary-title")}
            </Typography>
            <Typography variant="body2">
              {this.props.message || t("error-boundary-message")}
            </Typography>
          </Alert>
          <Button variant="contained" onClick={this.handleReset}>
            {this.props.retryLabel || t("error-boundary-retry")}
          </Button>
        </Box>
      );
    }

    return this.props.children;
  }
}

export default withTranslation()(ErrorBoundary);
