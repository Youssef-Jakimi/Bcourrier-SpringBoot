package com.courrier.Bcourrier.Repositories;

import com.courrier.Bcourrier.Entities.Courrier;
import com.courrier.Bcourrier.Entities.ServiceIntern;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResponsableSVCRepository extends JpaRepository<Courrier, Long> {

    List<Courrier> findTop3ByServiceOrderByDateRegistreDesc(ServiceIntern service);
}
