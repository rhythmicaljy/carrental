package carrental;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface CarManagementRepository extends PagingAndSortingRepository<CarManagement, Long>{

    List<CarManagement> findByCarNo(String carNo);

}