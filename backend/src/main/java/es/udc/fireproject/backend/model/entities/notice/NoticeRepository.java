package es.udc.fireproject.backend.model.entities.notice;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

  List<Notice> findByUserId(Long userId);

  @Query("select distinct n from Notice n left join fetch n.imageList")
  List<Notice> findAllWithImages();

  @Query("select distinct n from Notice n left join fetch n.imageList where n.user.id = :userId")
  List<Notice> findByUserIdWithImages(@Param("userId") Long userId);

}
