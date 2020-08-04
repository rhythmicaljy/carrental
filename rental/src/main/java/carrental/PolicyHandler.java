package carrental;

import carrental.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PolicyHandler{

    @Autowired
    CarRentalRepository carRentalRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void onStringEventListener(@Payload String eventString){

    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverPaid_CarRental(@Payload Paid paid){

        if(paid.isMe() && "PAID".equals(paid.getProcStatus())){

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

        if(paymentCanceled.isMe() && "PAYMENT_CANCELED".equals(paymentCanceled.getProcStatus()) ){

            Optional<CarRental> carRentalO = carRentalRepository.findById(paymentCanceled.getId());
            CarRental carRental = new CarRental();
            carRental.setId(paymentCanceled.getId());
            carRental.setPaymtNo(paymentCanceled.getPaymtNo());
            carRental.setResrvNo(paymentCanceled.getResrvNo());
            carRental.setCarNo(paymentCanceled.getCarNo());
            if (carRentalO.isPresent()){
                carRental = carRentalO.get();
            }
            carRental.setRentalCncleDt(paymentCanceled.getPaymtCncleDt());
            carRental.setProcStatus("PAYMENT_CANCELED");

            carRentalRepository.save(carRental);

            System.out.println("##### listener CarRentalCancellation [PAYMENT_CANCELED] : " + paymentCanceled.toJson());
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverCarReserved_CarRental(@Payload CarReserved carReserved){

        if(carReserved.isMe() && "RESERVED".equals(carReserved.getProcStatus())  ){
            CarRental carRental = new CarRental();
            carRental.setId(carReserved.getId());
            carRental.setResrvNo(carReserved.getResrvNo());

            carRental.setCarNo(carReserved.getCarNo());
            carRental.setRentalDt(carReserved.getRentalDt());
            carRental.setReturnDt(carReserved.getRentalDt());
            carRental.setProcStatus(carReserved.getProcStatus());

            carRentalRepository.save(carRental);

            System.out.println("##### listener CarRental [Reserved] : " + carReserved.toJson());
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverCarReservationCanceled_CarRentalCancellation(@Payload CarReservationCanceled carReservationCanceled){

        if(carReservationCanceled.isMe()   && "RESERVATION_CANCELED".equals(carReservationCanceled.getProcStatus())    ){
            CarRental carRental = new CarRental();
            carRental.setId(carReservationCanceled.getId());
            carRental.setResrvNo(carReservationCanceled.getResrvNo());

 /*           carRental.setCarNo(carReservationCanceled.getCarNo());
            carRental.setRentalDt(carReservationCanceled.getRentalDt());
            carRental.setReturnDt(carReservationCanceled.getRentalDt());*/
            carRental.setProcStatus(carReservationCanceled.getProcStatus());

            carRentalRepository.save(carRental);

            System.out.println("##### listener CarRental [Reservation Canceled] : " + carReservationCanceled.toJson());
        }
    }

}
