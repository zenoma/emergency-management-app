package es.udc.emergencyproject.backend.integration.services;

import es.udc.emergencyproject.backend.IntegrationTest;
import es.udc.emergencyproject.backend.model.entities.emergency.Emergency;
import es.udc.emergencyproject.backend.model.entities.emergency.EmergencyIndex;
import es.udc.emergencyproject.backend.model.entities.emergency.EmergencyType;
import es.udc.emergencyproject.backend.model.entities.organization.Organization;
import es.udc.emergencyproject.backend.model.entities.organization.OrganizationType;
import es.udc.emergencyproject.backend.model.exceptions.AlreadyExistException;
import es.udc.emergencyproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.emergencyproject.backend.model.services.emergency.EmergencyManagementService;
import es.udc.emergencyproject.backend.model.services.emergency.recommendation.AssignmentRecommendation;
import es.udc.emergencyproject.backend.model.services.emergency.recommendation.EmergencyRecommendationService;
import es.udc.emergencyproject.backend.model.services.personal.PersonalManagementFacade;
import es.udc.emergencyproject.backend.model.services.resources.ResourceManagementFacade;
import es.udc.emergencyproject.backend.utils.OrganizationOM;
import es.udc.emergencyproject.backend.utils.OrganizationTypeOM;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

@RequiredArgsConstructor
class EmergencyRecommendationServiceImplTest extends IntegrationTest {

  private static final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 25829);

  private final EmergencyRecommendationService emergencyRecommendationService;
  private final EmergencyManagementService emergencyManagementService;
  private final PersonalManagementFacade personalManagementFacade;
  private final ResourceManagementFacade resourceManagementFacade;

  private Organization createOrganizationWithLocation(String name, double x, double y) {
    OrganizationType type = personalManagementFacade.createOrganizationType(
        OrganizationTypeOM.withDefaultValues().getName());
    Organization org = OrganizationOM.withDefaultValues();
    org.setOrganizationType(type);
    org.setName(name);
    org.setCode(name.substring(0, 4).toUpperCase());
    org.setLocation(geometryFactory.createPoint(new Coordinate(x, y)));
    return personalManagementFacade.createOrganization(org);
  }

  @Test
  void givenPointEmergencyWithAvailableResources_whenRecommend_thenReturnsRecommendations()
      throws InstanceNotFoundException, AlreadyExistException {
    Organization org = createOrganizationWithLocation("Rec Centro", 539500, 4724000);
    resourceManagementFacade.createTeam("REC-TEAM-01", org.getId());
    resourceManagementFacade.createVehicle("REC001", "Camioneta", org.getId());

    List<EmergencyType> types = emergencyManagementService.findAllEmergencyTypes();
    Emergency emergency = emergencyManagementService.createEmergency("Incendio forestal en zona norte",
        types.get(0).getId(), EmergencyIndex.UNO);
    Point location = geometryFactory.createPoint(new Coordinate(539500, 4724000));
    emergencyManagementService.linkEmergencyToPoint(emergency.getId(), location);

    List<AssignmentRecommendation> recommendations = emergencyRecommendationService.recommendForEmergency(
        emergency.getId());
    Assertions.assertFalse(recommendations.isEmpty());
    Assertions.assertTrue(recommendations.stream().anyMatch(r -> r.getResourceType().name().equals("TEAM")));
  }

  @Test
  void givenEmergencyWithQuadrant_whenRecommendWithQuadrantId_thenReturnsRecommendations()
      throws InstanceNotFoundException, AlreadyExistException {
    Organization org = createOrganizationWithLocation("Rec Brigada", 594900, 4687400);
    resourceManagementFacade.createTeam("REC-TEAM-02", org.getId());

    List<EmergencyType> types = emergencyManagementService.findAllEmergencyTypes();
    Emergency emergency = emergencyManagementService.createEmergency("Derrumbe en carretera",
        types.get(2).getId(), EmergencyIndex.UNO);
    emergencyManagementService.linkEmergencyToQuadrants(emergency.getId(), List.of(1));

    List<AssignmentRecommendation> recommendations = emergencyRecommendationService.recommendForEmergency(
        emergency.getId(), 1);
    Assertions.assertNotNull(recommendations);
  }

  @Test
  void givenNoAvailableResources_whenRecommend_thenReturnsEmpty()
      throws InstanceNotFoundException {
    List<EmergencyType> types = emergencyManagementService.findAllEmergencyTypes();
    Emergency emergency = emergencyManagementService.createEmergency("Incendio forestal",
        types.get(0).getId(), EmergencyIndex.UNO);
    Point location = geometryFactory.createPoint(new Coordinate(539500, 4724000));
    emergencyManagementService.linkEmergencyToPoint(emergency.getId(), location);

    List<AssignmentRecommendation> recommendations = emergencyRecommendationService.recommendForEmergency(
        emergency.getId());
    Assertions.assertTrue(recommendations.isEmpty());
  }

  @Test
  void givenAvailableResources_whenRecommend_thenRecommendationsIncludeBothTypes()
      throws InstanceNotFoundException, AlreadyExistException {
    Organization org = createOrganizationWithLocation("Rec Dual", 539500, 4724000);
    resourceManagementFacade.createTeam("REC-TEAM-03", org.getId());
    resourceManagementFacade.createVehicle("REC002", "Furgoneta", org.getId());

    List<EmergencyType> types = emergencyManagementService.findAllEmergencyTypes();
    Emergency emergency = emergencyManagementService.createEmergency("Incendio forestal en montana",
        types.get(0).getId(), EmergencyIndex.UNO);
    Point location = geometryFactory.createPoint(new Coordinate(539500, 4724000));
    emergencyManagementService.linkEmergencyToPoint(emergency.getId(), location);

    List<AssignmentRecommendation> recommendations = emergencyRecommendationService.recommendForEmergency(
        emergency.getId());
    Assertions.assertFalse(recommendations.isEmpty());
  }

  @Test
  void givenEmergencyWithNoLocation_whenRecommend_thenReturnsEmpty()
      throws InstanceNotFoundException {
    List<EmergencyType> types = emergencyManagementService.findAllEmergencyTypes();
    Emergency emergency = emergencyManagementService.createEmergency("Derrumbe", types.get(2).getId(),
        EmergencyIndex.UNO);

    List<AssignmentRecommendation> recommendations = emergencyRecommendationService.recommendForEmergency(
        emergency.getId());
    Assertions.assertTrue(recommendations.isEmpty());
  }

  @Test
  void givenInvalidEmergencyId_whenRecommend_thenThrows() {
    Assertions.assertThrows(InstanceNotFoundException.class,
        () -> emergencyRecommendationService.recommendForEmergency(-1L));
  }
}
