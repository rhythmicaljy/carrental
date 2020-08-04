package carrental;

import carrental.config.kafka.KafkaProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Service
public class PolicyHandler {

    @Autowired
    CarManagementRepository carManagementRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void onStringEventListener(@Payload String eventString) {

    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverCarRented_CarUpdate(@Payload CarRented carRented) {
        // 현재일자
        Locale currentLocale = new Locale("KOREAN", "KOREA");
        String pattern = "yyyyMMdd";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern, currentLocale);
        Date cal = new Date();
        String currDt = formatter.format(cal.getTime());

        List<CarManagement> carList = carManagementRepository.findByCarNo(carRented.getCarNo());
        CarManagement carManagement = new CarManagement();
        carManagement.setCarNo(carRented.getCarNo());
        if (carList.size() > 0) {
            carManagement = carList.get(0);
        }
        carManagement.setCarRegDt(currDt);

        if (carRented.isMe()) {
            if ("CAR_RENTED".equals(carRented.getProcStatus())) {
                carManagement.setProcStatus("CAR_RENTED");
                carManagementRepository.save(carManagement);
                System.out.println("##### listenerCarUpdate [CAR_RENTED] : " + carRented.toJson());
            } else if ("RESERVED".equals(carRented.getProcStatus())) {
                carManagement.setProcStatus("RESERVED");
                carManagementRepository.save(carManagement);
                System.out.println("##### listenerCarUpdate [RESERVED] : " + carRented.toJson());
            }
        }

    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverCarRentalCanceled_CarUpdate(@Payload CarRentalCanceled carRentalCanceled) {

        if (carRentalCanceled.isMe()) {

            CarManagement carManagement = new CarManagement();
            carManagement.setCarNo(carRentalCanceled.getCarNo());
            List<CarManagement> carList = carManagementRepository.findByCarNo(carRentalCanceled.getCarNo());
            if (carList.size() > 0) {
                carManagement = carList.get(0);
            }
            carManagement.setProcStatus("CAR_RENTAL_CANCELED");

            carManagementRepository.save(carManagement);

            System.out.println("##### listener CarUpdate [CAR_RENTAL_CANCELED] : " + carRentalCanceled.toJson());
        }
    }

}
