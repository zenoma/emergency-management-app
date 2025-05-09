import proj4 from "proj4";

export const transformCoordinates = (longitude, latitude) => {
  var firstProjection = proj4("EPSG:4326");
  var secondProjection = "+proj=utm +zone=29 +ellps=GRS80 +towgs84=0,0,0,0,0,0,0 +units=m +no_defs";

  var result = proj4(firstProjection, secondProjection).forward([longitude, latitude]);
  return { longitude: result[0], latitude: result[1] };
};
export const untransformCoordinates = (longitude, latitude) => {
  var firstProjection = "+proj=utm +zone=29 +ellps=GRS80 +towgs84=0,0,0,0,0,0,0 +units=m +no_defs";
  var secondProjection = proj4("EPSG:4326");

  var result = proj4(firstProjection, secondProjection).forward([longitude, latitude]);
  return { longitude: result[0], latitude: result[1] };
};
