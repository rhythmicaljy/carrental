package carrental2;

import carrental2.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class PolicyHandler{

    @Autowired
    CarManagementRepository carManagementRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void onStringEventListener(@Payload String eventString){
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverCarRented_CarUpdate(@Payload CarRented carRented){

        if(carRented.isMe()){

            /*
            Locale currentLocale = new Locale("KOREAN", "KOREA");
            String pattern = "yyyyMMdd";
            SimpleDateFormat formatter = new SimpleDateFormat(pattern, currentLocale);
            String currDt = formatter.format(cal.getTime());
            */
            CarManagement carManagement = new CarManagement();
            carManagement.setCarNo(carRented.getCarNo());
            //carManagement.setCarRegDt(currDt);
            carManagement.setProcStatus("CAR_RENTED");

            carManagementRepository.save(carManagement);

            System.out.println("##### listener [CAR_RENTED] CarUpdate : " + carRented.toJson());
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverCarRentalCanceled_CarUpdate(@Payload CarRentalCanceled carRentalCanceled){

        if(carRentalCanceled.isMe()){

            CarManagement carManagement = new CarManagement();
            carManagement.setCarNo(carRentalCanceled.getCarNo());
            carManagement.setProcStatus("CAR_RENTAL_CANCELED");

            carManagementRepository.save(carManagement);

            System.out.println("##### listener [CAR_RENTAL_CANCELED] CarUpdate : " + carRentalCanceled.toJson());
        }
    }

}
