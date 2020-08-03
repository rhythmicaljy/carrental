package carrental;

import carrental.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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

            // 현재일자
            Locale currentLocale = new Locale("KOREAN", "KOREA");
            String pattern = "yyyyMMdd";
            SimpleDateFormat formatter = new SimpleDateFormat(pattern, currentLocale);
            Date cal = new Date();
            String currDt = formatter.format(cal.getTime());

            CarManagement carManagement = new CarManagement();
            carManagement.setCarNo(carRented.getCarNo());
            carManagement.setCarRegDt(currDt);
            carManagement.setProcStatus("CAR_RENTED");

            carManagementRepository.save(carManagement);

            System.out.println("##### listenerCarUpdate [CAR_RENTED] : " + carRented.toJson());
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverCarRentalCanceled_CarUpdate(@Payload CarRentalCanceled carRentalCanceled){

        if(carRentalCanceled.isMe()){

            CarManagement carManagement = new CarManagement();
            carManagement.setCarNo(carRentalCanceled.getCarNo());
            carManagement.setProcStatus("CAR_RENTAL_CANCELED");

            carManagementRepository.save(carManagement);

            System.out.println("##### listener CarUpdate [CAR_RENTAL_CANCELED] : " + carRentalCanceled.toJson());
        }
    }

}
