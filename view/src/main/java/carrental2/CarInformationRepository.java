package carrental2;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CarInformationRepository extends CrudRepository<CarInformation, Long> {

    List<CarInformation> findByCarNo(String carNo);
    //List<CarInformation> findByCarNo(String carNo);

}