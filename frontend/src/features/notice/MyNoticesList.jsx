import { useState } from "react";
import { useSelector } from "react-redux";

import { CircularProgress, Dialog, DialogContent, Paper, Typography } from "@mui/material";

import { selectToken, selectUser } from "../user/login/LoginSlice";

import { DataGrid } from '@mui/x-data-grid';
import dayjs from "dayjs";
import { useTranslation } from "react-i18next";
import { useGetNoticesQuery } from "../../api/noticeApi";


var URL = import.meta.env.VITE_REACT_APP_BACKEND_URL;

export default function MyNoticesList() {
  // no local copy of data needed; use query result directly
  const { t } = useTranslation();
  const { i18n } = useTranslation("home");
  const locale = i18n.language;

  const token = useSelector(selectToken);
  const user = useSelector(selectUser);

  const payload = {
    token: token,
    locale: locale,
    id: user.id
  };

  const [dialogOpen, setDialogOpen] = useState(false);
  const [selectedNoticeId, setSelectedNoticeId] = useState(null);
  const [selectedImage, setSelectedImage] = useState(null);

  const handleDialogOpen = (id, image) => {
    setSelectedNoticeId(id);
    setSelectedImage(image);
    setDialogOpen(true);
  };

  const handleDialogClose = () => {
    setSelectedNoticeId(null);
    setSelectedImage(null);
    setDialogOpen(false);
  };

  const { data, error, isLoading } = useGetNoticesQuery(payload, { refetchOnMountOrArgChange: true });
  // ensure notices are displayed most recent first by sorting locally
  const sortedData = data ? [...data].sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt)) : [];

  function getStatusColor(status) {
    switch (status) {
      case "ACCEPTED":
        return 'primary.light';
      case "REJECTED":
        return "error.dark";
      default:
        return "black";
    }
  }

  const columns = [
    {
      field: 'body',
      headerName: t("notice-body"),
      flex: 2,
      minWidth: 150,
      renderCell: (params) => (
        <Typography variant="subtitle1" gutterBottom>
          {params.value}
        </Typography>
      ),
    },
    {
      field: 'noticeStatus',
      headerName: t("notice-status"),
      flex: 1,
      minWidth: 80,
      renderCell: (params) => (
        <Typography variant="body2" color="text.secondary" sx={{ color: getStatusColor(params.row.status) }}>
          {params.row.status}
        </Typography>
      ),
    },
    {
      field: 'quadrantId',
      headerName: t("notice-quadrant-id"),
      flex: 0.8,
      minWidth: 80,
      renderCell: (params) => (
        <Typography variant="body2" color="text.secondary">
          {params.value || '-'}
        </Typography>
      ),
    },
    {
      field: 'quadrantName',
      headerName: t("notice-quadrant"),
      flex: 1.2,
      minWidth: 100,
      renderCell: (params) => (
        <Typography variant="body2" color="text.secondary">
          {params.value || '-'}
        </Typography>
      ),
    },
    {
      field: 'createdAt',
      headerName: t("created-at"),
      type: 'dateTime',
      flex: 1,
      minWidth: 140,
      valueGetter: (params) => (params.value ? new Date(params.value) : null),
      renderCell: (params) => (
        <Typography variant="body2" color="text.secondary">
          {params.value ? dayjs(params.value).format("DD-MM-YYYY HH:mm:ss") : '-'}
        </Typography>
      ),
    },
    {
      field: 'image',
      headerName: t("notice-image"),
      flex: 1,
      minWidth: 80,
      renderCell: (params) => {
        if (params.row.imageDtoList && params.row.imageDtoList[0]) {
          return (
            <img
              src={`${URL}/images/${params.row.id}/${params.row.imageDtoList[0].name}`}
              alt={params.row.name}
              style={{ maxWidth: "100%", minHeight: 10, cursor: "pointer" }}
              onClick={() => handleDialogOpen(params.row.id, params.row.imageDtoList[0].name)}
            />
          );
        } else {
          return null;
        }
      }
    },
  ];
  return (
    <Paper
      sx={{
        padding: "10px",
        width: "100%",
        maxWidth: "100%",
        boxSizing: "border-box",
      }}
      variant="outlined"
    >
      <Typography
        variant="h4"
        margin={1}
        sx={{ fontWeight: "bold", color: "primary.light" }}
      >
        {t("my-notices")}
      </Typography>
      {error ? (
        t("generic-error")
      ) : isLoading ? (
        <CircularProgress />
      ) : data ? (
        <div style={{ height: 'calc(100vh - 200px)', width: '100%' }}>
          <DataGrid
            rows={sortedData}
            columns={columns}
            loading={isLoading}
            initialState={{ sorting: { sortModel: [{ field: 'createdAt', sort: 'desc' }] } }}
            disableSelectionOnClick
            disableColumnMenu
            components={{
              loadingOverlay: CircularProgress,
            }}
          />
        </div>)
        : null}
      <Dialog open={dialogOpen} onClose={handleDialogClose}>
        <DialogContent>
          {selectedImage && <img src={`${URL}/images/${selectedNoticeId}/${selectedImage}`}
            alt="Imagen" style={{ maxWidth: "100%" }} />}
        </DialogContent>
      </Dialog>
    </Paper >

  );
}
