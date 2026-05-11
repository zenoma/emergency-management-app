package es.udc.emergencyproject.backend.model.services.notifications;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import es.udc.emergencyproject.backend.model.entities.assignment.Assignment;
import es.udc.emergencyproject.backend.model.entities.assignment.AssignmentStatus;
import es.udc.emergencyproject.backend.model.entities.resource.ResourceType;
import es.udc.emergencyproject.backend.model.entities.user.UserRepository;
import es.udc.emergencyproject.backend.model.entities.user.UserRole;
import jakarta.annotation.PostConstruct;
import java.io.FileInputStream;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssignmentNotificationService {

  private final UserRepository userRepository;

  @Value("${firebase.service-account-path:}")
  private String serviceAccountPath;

  private volatile boolean firebaseReady;

  @PostConstruct
  void init() {
    String path = serviceAccountPath;
    if (path == null || path.isBlank()) {
      path = System.getenv("FIREBASE_SERVICE_ACCOUNT_PATH");
    }

    if (path == null || path.isBlank()) {
      log.warn("Firebase service account path not configured; assignment notifications disabled");
      return;
    }

    try (FileInputStream serviceAccount = new FileInputStream(path)) {
      if (FirebaseApp.getApps().isEmpty()) {
        FirebaseOptions options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .build();
        FirebaseApp.initializeApp(options);
      }
      firebaseReady = true;
    } catch (Exception e) {
      log.warn("Unable to initialize Firebase Admin for assignment notifications", e);
    }
  }

  public void notifyTeamAssignmentCreated(Assignment assignment) {
    if (!firebaseReady || assignment == null || assignment.getResource() == null) {
      return;
    }

    if (assignment.getResource().getResourceType() != ResourceType.TEAM) {
      return;
    }

    Long teamId = assignment.getResource().getId();
    if (teamId == null) {
      return;
    }

    List<String> tokens = userRepository
        .findTeamUsersWithMobileDeviceByTeamIdAndUserRoleIn(teamId,
            List.of(UserRole.MANAGER, UserRole.COORDINATOR))
        .stream()
        .map(user -> user.getMobileDevice() != null ? user.getMobileDevice().getFcmToken() : null)
        .filter(token -> token != null && !token.isBlank())
        .distinct()
        .collect(Collectors.toList());

    if (tokens.isEmpty()) {
      return;
    }

    try {
      MulticastMessage message = MulticastMessage.builder()
          .addAllTokens(tokens)
          .putData("route", "myassignments/" + teamId)
          .putData("teamId", teamId.toString())
          .putData("assignmentId", Objects.toString(assignment.getId(), ""))
          .setNotification(Notification.builder()
              .setTitle("Nueva asignación")
              .setBody("Tienes una nueva asignación pendiente")
              .build())
          .build();

      BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(message);
      log.debug("Assignment notification sent: {} success, {} failure", response.getSuccessCount(),
          response.getFailureCount());
    } catch (Exception e) {
      log.warn("Failed to send assignment notification", e);
    }
  }

  public void notifyTeamAssignmentStatusChanged(Assignment assignment, AssignmentStatus status) {
    if (!firebaseReady || assignment == null || assignment.getResource() == null || status == null) {
      return;
    }

    if (assignment.getResource().getResourceType() != ResourceType.TEAM) {
      return;
    }

    Long teamId = assignment.getResource().getId();
    if (teamId == null) {
      return;
    }

    String title;
    String body;
    String route;
    List<String> tokens;
    if (status == AssignmentStatus.ACCEPTED) {
      title = "Asignaci\u00f3n aceptada";
      body = "Una asignaci\u00f3n ha cambiado a estado aceptado";
      route = "myassignments/" + teamId;
      tokens = userRepository
          .findTeamUsersWithMobileDeviceByTeamIdAndUserRoleIn(teamId,
              List.of(UserRole.MANAGER, UserRole.COORDINATOR))
          .stream()
          .map(user -> user.getMobileDevice() != null ? user.getMobileDevice().getFcmToken() : null)
          .filter(token -> token != null && !token.isBlank())
          .distinct()
          .collect(Collectors.toList());
    } else if (status == AssignmentStatus.COMPLETED) {
      title = "Asignaci\u00f3n completada";
      body = "Una asignaci\u00f3n ha cambiado a estado completado";
      route = "myassignments/" + teamId;
      tokens = userRepository
          .findTeamUsersWithMobileDeviceByTeamId(teamId)
          .stream()
          .map(user -> user.getMobileDevice() != null ? user.getMobileDevice().getFcmToken() : null)
          .filter(token -> token != null && !token.isBlank())
          .distinct()
          .collect(Collectors.toList());
    } else {
      return;
    }

    if (tokens.isEmpty()) {
      return;
    }

    try {
      MulticastMessage message = MulticastMessage.builder()
          .addAllTokens(tokens)
          .putData("route", route)
          .putData("teamId", teamId.toString())
          .putData("assignmentId", Objects.toString(assignment.getId(), ""))
          .putData("status", status.name())
          .setNotification(Notification.builder()
              .setTitle(title)
              .setBody(body)
              .build())
          .build();

      BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(message);
      log.info("Assignment status notification sent: {} success, {} failure", response.getSuccessCount(),
          response.getFailureCount());
    } catch (Exception e) {
      log.warn("Failed to send assignment status notification", e);
    }
  }
}
