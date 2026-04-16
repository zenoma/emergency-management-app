import LocalFireDepartmentIcon from '@mui/icons-material/LocalFireDepartment';
import WaterIcon from '@mui/icons-material/Water';
import TerrainIcon from '@mui/icons-material/Terrain';
import DirectionsCarIcon from '@mui/icons-material/DirectionsCar';
import MedicalServicesIcon from '@mui/icons-material/MedicalServices';
import ScienceIcon from '@mui/icons-material/Science';
import FactoryIcon from '@mui/icons-material/Factory';
import StormIcon from '@mui/icons-material/Storm';
import HelpOutlineIcon from '@mui/icons-material/HelpOutline';
import { Box, Typography } from '@mui/material';

// Map known emergency type names (or partial matches) to icons.
const mapNameToIcon = (name) => {
  if (!name) return HelpOutlineIcon;
  const n = name.toLowerCase();
  if (n.includes('incend') || n.includes('fire')) return LocalFireDepartmentIcon;
  if (n.includes('inund') || n.includes('flood') || n.includes('water')) return WaterIcon;
  if (n.includes('derrum') || n.includes('desprend') || n.includes('land')) return TerrainIcon;
  if (n.includes('accident') || n.includes('vial') || n.includes('car')) return DirectionsCarIcon;
  if (n.includes('sanit') || n.includes('salud') || n.includes('medical')) return MedicalServicesIcon;
  if (n.includes('quím') || n.includes('quim') || n.includes('chemical')) return ScienceIcon;
  if (n.includes('industrial') || n.includes('factory') || n.includes('industr')) return FactoryIcon;
  if (n.includes('temporal') || n.includes('meteor') || n.includes('storm')) return StormIcon;
  return HelpOutlineIcon;
};

export default function EmergencyTypeIcon({ name, sx, showLabel = false }) {
  const IconComp = mapNameToIcon(name);
  return (
    <Box sx={{ display: 'inline-flex', alignItems: 'center', gap: 1, ...sx }}>
      <IconComp fontSize="small" />
      {showLabel && <Typography variant="body2">{name}</Typography>}
    </Box>
  );
}
