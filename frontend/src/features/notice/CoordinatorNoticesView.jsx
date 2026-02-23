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
import dayjs from "dayjs";

var URL = import.meta.env.VITE_REACT_APP_BACKEND_URL;


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
  // sort notices newest first so the grid shows recent items at the top
  const sortedData = data ? [...data].sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt)) : [];


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
        toast.error(error?.data?.errorMessage || t("generic-error"))
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
        toast.error(error?.data?.errorMessage || t("generic-error"))
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
      flex: 2,
      minWidth: 220,
      renderCell: (params) => (
        <Typography variant="subtitle1" gutterBottom>
          {params.value}
        </Typography>
      ),
    },
    {
      field: 'status',
      headerName: t("notice-status"),
      flex: 0.6,
      minWidth: 90,
      renderCell: (params) => (
        <Typography variant="body2" color="text.secondary" sx={{ color: getStatusColor(params.row.status) }}>
          {params.row.status}
        </Typography>
      ),
    },
    {
      field: 'email',
      headerName: t("email"),
      flex: 1.2,
      minWidth: 160,
      valueGetter: (params) => params.row.userDto ? params.row.userDto.email : '',
      renderCell: (params) => {
        return params.row.userDto ? (
          <Typography variant="body2" color="text.secondary">
            {params.row.userDto.email}
          </Typography>
        ) : null;
      },
    },
    {
      field: 'quadrantId',
      headerName: t("notice-quadrant-id"),
      flex: 0.6,
      minWidth: 90,
      renderCell: (params) => (
        <Typography variant="body2" color="text.secondary">
          {params.value || '-'}
        </Typography>
      ),
    },
    {
      field: 'quadrantName',
      headerName: t("notice-quadrant"),
      flex: 1,
      minWidth: 140,
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
      flex: 0.9,
      minWidth: 150,
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
      flex: 0.8,
      minWidth: 120,
      renderCell: (params) => {
        if (params.row.imageDtoList && params.row.imageDtoList[0]) {
          return (
            <img
              src={`${URL}/images/${params.row.id}/${params.row.imageDtoList[0].name}`}
              alt={params.row.name}
              style={{ minWidth: 100, minHeight: 10, cursor: "pointer" }}
              onClick={() => handleDialogOpen(params.row.id, params.row.imageDtoList[0].name)}
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
      flex: 1,
      minWidth: 180,
      renderCell: (params) => (
        <Box onClick={(e) => e.stopPropagation()}
          sx={{ width: "100%", height: "100%", display: "flex", alignItems: "center", justifyContent: "center", gap: 1 }} >
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
        width: "100%",
        boxSizing: "border-box",
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
        <div style={{ height: 'calc(100vh - 200px)', width: '100%' }}>
          <DataGrid
            components={{ Toolbar: CustomToolbar, loadingOverlay: CircularProgress }}
            rows={sortedData}
            columns={columns}
            loading={isLoading}
            initialState={{ sorting: { sortModel: [{ field: 'createdAt', sort: 'desc' }] } }}
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
