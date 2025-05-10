package es.udc.fireproject.backend.model.entities.notice;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

  List<Notice> findByUserId(Long userId);

}
