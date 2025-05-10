package es.udc.fireproject.backend;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

@Transactional
@SpringBootTest
public abstract class IntegrationTest {

  @Container
  static PostgreSQLContainer<?> postgis = new PostgreSQLContainer<>(
      DockerImageName.parse("postgis/postgis")
          .asCompatibleSubstituteFor("postgres"))
      .withDatabaseName("firedb")
      .withUsername("fireuser")
      .withPassword("fireuser")
      .withCopyFileToContainer(
          MountableFile.forClasspathResource("db"),
          "/docker-entrypoint-initdb.d/"
      );

  static {
    postgis.start();
  }

  @DynamicPropertySource
  static void overrideProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgis::getJdbcUrl);
    registry.add("spring.datasource.username", postgis::getUsername);
    registry.add("spring.datasource.password", postgis::getPassword);

    registry.add("spring.datasource.hikari.max-lifetime", () -> 300_000);
    registry.add("spring.datasource.hikari.connection-test-query", () -> "SELECT 1");
    registry.add("spring.datasource.hikari.minimum-idle", () -> 1);
    registry.add("spring.datasource.hikari.maximum-pool-size", () -> 5);
  }


}