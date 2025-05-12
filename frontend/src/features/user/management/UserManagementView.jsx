import { Paper, Typography } from '@mui/material';
import React from 'react';
import { useTranslation } from 'react-i18next';
import UserDataGrid from "../UserDataGrid";

export default function UserManagementView() {
    const { t } = useTranslation();


    return (
        <Paper
            sx={{
                color: "primary.light",
                padding: 2,
                minWidth: "900px",
                margin: 1,
            }}
            variant="outlined"
        >
            <Typography
                variant="h4"
                margin={1}
                sx={{ fontWeight: "bold", color: "primary.light" }}
            >
                {t("user-management")}
            </Typography>
            <UserDataGrid />
        </Paper>
    )
}
