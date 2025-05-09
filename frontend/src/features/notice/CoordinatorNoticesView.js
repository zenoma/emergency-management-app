import { useState } from "react";
import { useSelector } from "react-redux";

import { Box, Button, CircularProgress, Dialog, DialogContent, Paper, Typography } from "@mui/material";

import { selectToken } from "../user/login/LoginSlice";

import CloseIcon from '@mui/icons-material/Close';
import DeleteIcon from '@mui/icons-material/Delete';
import DoneIcon from '@mui/icons-material/Done';

import { DataGrid, GridToolbarColumnsButton, GridToolbarContainer, GridToolbarDensitySelector, GridToolbarFilterButton, esES } from '@mui/x-data-grid';
import { useTranslation } from "react-i18next";
import { toast } from "react-toastify";
import { useDeleteNoticeMutation, useGetNoticesQuery, useUpdateNoticeMutation } from "../../api/noticeApi";

var URL = process.env.REACT_APP_BACKEND_URL;


export default function CoordinatorNoticesView() {
  const [list, setList] = useState("");
  const { t } = useTranslation();
  const { i18n } = useTranslation("home");
  const locale = i18n.language;


  var localeText;

  if (i18n.language === "es") {
    localeText = esES.components.MuiDataGrid.defaultProps.localeText;
  }


  const token = useSelector(selectToken);

  const payload = {
    token: token,
    locale: locale,
    id: ''
  };

  const { data, error, isLoading, refetch } = useGetNoticesQuery(payload, { refetchOnMountOrArgChange: true });


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

  const [updateNotice] = useUpdateNoticeMutation(payload);

  const handleUpdateClick = (e, row, status) => {
    e.stopPropagation();
    const payload = {
      token: token,
      locale: locale,
      id: row.id,
      status: status
    };

    updateNotice(payload)
      .unwrap()
      .then((payload) => {
        refetch();
      })
      .catch((error) =>
        toast.error(error.data.globalError)
      );
  }

  const [deleteNotice] = useDeleteNoticeMutation(payload);


  const handleDeleteClick = (e, row) => {
    e.stopPropagation();
    const payload = {
      token: token,
      locale: locale,
      id: row.id,
    };

    deleteNotice(payload)
      .unwrap()
      .then((payload) => {
        refetch();
      })
      .catch((error) =>
        toast.error(error.data.globalError)
      );
  }

  if ((data === "") & (list === "")) {
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
      width: 300,
      renderCell: (params) => (
        <Typography variant="subtitle1" gutterBottom>
          {params.value}
        </Typography>
      ),
    },
    {
      field: 'status',
      headerName: t("notice-status"),
      width: 100,
      renderCell: (params) => (
        <Typography variant="body2" color="text.secondary" sx={{ color: getStatusColor(params.row.status) }}>
          {params.row.status}
        </Typography>
      ),
    },
    {
      field: 'email',
      headerName: t("email"),
      width: 200,
      renderCell: (params) => {
        return params.row.user ? (
          <Typography variant="body2" color="text.secondary">
            {params.row.user.email}
          </Typography>
        ) : null;
      },
    },
    {
      field: 'image',
      headerName: t("notice-image"),
      width: 150,
      renderCell: (params) => {
        if (params.row.images[0]) {
          return (
            <img
              src={`${URL}/images/${params.row.id}/${params.row.images[0].name}`}
              alt={params.row.name}
              style={{ minWidth: 100, minHeight: 10, cursor: "pointer" }}
              onClick={() => handleDialogOpen(params.row.id, params.row.images[0].name)}
            />
          );
        } else {
          return null;
        }
      }
    },
    {
      field: "notice-options",
      headerName: t("notice-options"),
      width: 200,
      renderCell: (params) => (
        <Box onClick={(e) => e.stopPropagation()}
          sx={{ width: "100%", height: "100%", display: "flex", alignItems: "center", justifyContent: "center" }} >
          <Button
            sx={{ borderRadius: "20px" }}
            variant="contained"
            color="primary"
            disabled={params.row.status !== "PENDING"}
            onClick={(e) => handleUpdateClick(e, params.row, "ACCEPTED")}
          >
            <DoneIcon />
          </Button>
          <Button
            sx={{ borderRadius: "20px" }}
            variant="contained"
            color="error"
            disabled={params.row.status !== "PENDING"}
            onClick={(e) => handleUpdateClick(e, params.row, "REJECTED")}          >
            <CloseIcon />
          </Button>
          <Button
            sx={{ borderRadius: "20px" }}
            color="error"
            disabled={params.row.status !== "PENDING"}
            onClick={(e) => handleDeleteClick(e, params.row)}
          >
            <DeleteIcon />
          </Button>
        </Box >
      ),
    },
  ];

  const statusFilterModel = {
    items: [{ columnField: "status", operatorValue: 'equals', value: 'PENDING', label: 'Pending' }]
  };

  const [filterModel, setFilterModel] = useState(statusFilterModel);

  const handleFilterModelChange = (model) => {
    setFilterModel(model);
  };

  function CustomToolbar() {
    return (
      <GridToolbarContainer>
        <GridToolbarColumnsButton />
        <GridToolbarFilterButton />
        <GridToolbarDensitySelector />
      </GridToolbarContainer>
    );
  }
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
        {t("notices")}
      </Typography>
      {error ? (
        t("generic-error")
      ) : isLoading ? (
        <CircularProgress />
      ) : data ? (
        <div style={{ height: 550, width: '100%' }}>
          <DataGrid
            components={{ Toolbar: CustomToolbar, loadingOverlay: CircularProgress }}
            rows={data}
            columns={columns}
            loading={isLoading}
            disableSelectionOnClick
            disableColumnMenu
            filterModel={filterModel}
            onFilterModelChange={handleFilterModelChange}
            localeText={localeText}
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
