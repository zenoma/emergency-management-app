import { useState } from "react";
import { useSelector } from "react-redux";

import { CircularProgress, Dialog, DialogContent, Paper, Typography } from "@mui/material";

import { selectToken, selectUser } from "../user/login/LoginSlice";

import { DataGrid } from '@mui/x-data-grid';
import { useTranslation } from "react-i18next";
import { useGetNoticesQuery } from "../../api/noticeApi";


var URL = import.meta.env.REACT_APP_BACKEND_URL;

export default function MyNoticesList() {
  const [list, setList] = useState("");
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

  if (data && data !== list) {
    setList(data);
  }

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
      width: 400,
      renderCell: (params) => (
        <Typography variant="subtitle1" gutterBottom>
          {params.value}
        </Typography>
      ),
    },
    {
      field: 'noticeStatus',
      headerName: t("notice-status"),
      width: 200,
      renderCell: (params) => (
        <Typography variant="body2" color="text.secondary" sx={{ color: getStatusColor(params.row.status) }}>
          {params.row.status}
        </Typography>
      ),
    },
    // {
    //   field: 'image',
    //   headerName: t("notice-image"),
    //   width: 150,
    //   renderCell: (params) => {
    //     if (params.row.images[0]) {
    //       return (
    //         <img
    //           src={`${URL}/images/${params.row.id}/${params.row.images[0].name}`}
    //           alt={params.row.title}
    //           style={{ width: 50, height: 50 }}
    //           onClick={() => handleDialogOpen(params.row.id, params.row.images[0].name)}
    //         />

    //       );
    //     } else {
    //       return null;
    //     }
    //   }
    // },
  ];
  return (
    <Paper
      sx={{
        display: "inline-block",
        padding: "10px",
        minWidth: "1000px",
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
        <div style={{ height: 550, width: '100%' }}>
          <DataGrid
            rows={data}
            columns={columns}
            loading={isLoading}
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
