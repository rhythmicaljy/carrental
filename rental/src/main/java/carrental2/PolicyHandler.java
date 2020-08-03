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
    CarRentalRepository carRentalRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void onStringEventListener(@Payload String eventString){

    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverPaid_CarRental(@Payload Paid paid){

        if(paid.isMe()){

            CarRental carRental = new CarRental();
            carRental.setPaymtNo(paid.getPaymtNo());
            carRental.setResrvNo(paid.getResrvNo());
            carRental.setProcStatus("PAID");

            carRentalRepository.save(carRental);

            System.out.println("##### listener [PAID] CarRental : " + paid.toJson());
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverPaymentCanceled_CarRentalCancellation(@Payload PaymentCanceled paymentCanceled){

        if(paymentCanceled.isMe()){

            CarRental carRental = new CarRental();
            carRental.setPaymtNo(paymentCanceled.getPaymtNo());
            carRental.setRentalCncleDt(paymentCanceled.getPaymtCncleDt());
            carRental.setResrvNo(paymentCanceled.getResrvNo());
            carRental.setProcStatus("PAYMENT_CANCELED");

            carRentalRepository.save(carRental);

            System.out.println("##### listener [PAYMENT_CANCELED] CarRentalCancellation : " + paymentCanceled.toJson());
        }
    }

}
