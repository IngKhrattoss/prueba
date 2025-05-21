package encalma.backendcalma3.Repository;



import encalma.backendcalma3.Entity.promotion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface promotionRepository  extends JpaRepository<promotion, Long> {
    List<promotion> findAll();
}
