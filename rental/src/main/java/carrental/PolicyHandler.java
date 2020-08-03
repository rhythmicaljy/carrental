package carrental;

import carrental.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class PolicyHandler{

    @Autowired
    CarRentalRepository carRentalRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void onStringEventListener(@Payload String eventString){

    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverPaid_CarRental(@Payload Paid paid){

        if(paid.isMe()){

            CarRental carRental = new CarRental();
            carRental.setId(paid.getId());
            carRental.setResrvNo(paid.getResrvNo());
            carRental.setPaymtNo(paid.getPaymtNo());
            carRental.setCarNo(paid.getCarNo());
            carRental.setRentalDt(paid.getRentalDt());
            carRental.setReturnDt(paid.getRentalDt());
            carRental.setProcStatus(paid.getProcStatus());

            carRentalRepository.save(carRental);

            System.out.println("##### listener CarRental [PAID] : " + paid.toJson());
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverPaymentCanceled_CarRentalCancellation(@Payload PaymentCanceled paymentCanceled){

        if(paymentCanceled.isMe()){

            CarRental carRental = new CarRental();
            carRental.setPaymtNo(paymentCanceled.getPaymtNo());
            carRental.setRentalCncleDt(paymentCanceled.getPaymtCncleDt());
            carRental.setResrvNo(paymentCanceled.getResrvNo());
            carRental.setCarNo(paymentCanceled.getCarNo());
            carRental.setProcStatus("PAYMENT_CANCELED");

            carRentalRepository.save(carRental);

            System.out.println("##### listener CarRentalCancellation [PAYMENT_CANCELED] : " + paymentCanceled.toJson());
        }
    }

}
