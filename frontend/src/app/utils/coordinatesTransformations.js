import proj4 from "proj4";

// Convert from geographic (lon, lat) in EPSG:4326 to projected UTM (x, y)
export const transformCoordinates = (longitude, latitude) => {
  const from = "EPSG:4326";
  const to = "+proj=utm +zone=29 +ellps=GRS80 +towgs84=0,0,0,0,0,0,0 +units=m +no_defs";

  // proj4 expects [x, y] input; for geographic coordinates that's [lon, lat]
  const result = proj4(from, to, [longitude, latitude]);
  return { longitude: result[0], latitude: result[1] };
};

// Convert from projected UTM (x, y) to geographic coordinates (lon, lat) EPSG:4326
export const untransformCoordinates = (x, y) => {
  const from = "+proj=utm +zone=29 +ellps=GRS80 +towgs84=0,0,0,0,0,0,0 +units=m +no_defs";
  const to = "EPSG:4326";
  let input = [x, y];
  let swapped = false;
  if (Math.abs(x) > 1000000 && Math.abs(y) < 1000000) {
    input = [y, x];
    swapped = true;
  }

  const result = proj4(from, to, input);
  const longitude = result[0];
  const latitude = result[1];
  return { longitude, latitude };
};
