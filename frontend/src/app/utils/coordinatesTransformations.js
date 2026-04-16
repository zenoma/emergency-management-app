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
  // Input is expected as projected coordinates: [easting (x), northing (y)].
  // However, some data sources may have the two values swapped. Apply a small
  // heuristic: if x looks like a northing (> 1e6) and y looks like an easting (< 1e6),
  // try swapping the inputs.
  let input = [x, y];
  let swapped = false;
  if (Math.abs(x) > 1000000 && Math.abs(y) < 1000000) {
    // Likely the values are (northing, easting) instead of (easting, northing)
    input = [y, x];
    swapped = true;
  }

  const result = proj4(from, to, input);
  const longitude = result[0];
  const latitude = result[1];
  // Debug to help validate correct axis/order during development
  console.debug('untransformCoordinates', { input: { x, y }, usedInput: { x: input[0], y: input[1] }, swapped, output: { longitude, latitude } });
  return { longitude, latitude };
};
