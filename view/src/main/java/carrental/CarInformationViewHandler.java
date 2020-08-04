package carrental;

import carrental.config.kafka.KafkaProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class CarInformationViewHandler {


    @Autowired
    private CarInformationRepository carInformationRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whenCarRegistered_then_CREATE_1 (@Payload CarRegistered carRegistered) {
        try {
            if (carRegistered.isMe()) {
                // view 객체 생성
                CarInformation carInformation = new CarInformation();
                // view 객체에 이벤트의 Value 를 set 함
                if (!(carRegistered.getCarNo()==null)) {
                    List<CarInformation> carlist = carInformationRepository.findByCarNo(carRegistered.getCarNo());
                    carInformation.setCarNo(carRegistered.getCarNo());
                    if (carlist.size()>0) {
                        carInformation = carlist.get(0);
                    }
                    carInformation.setProcStatus(carRegistered.getProcStatus());
                    carInformation.setRentalAmt(carRegistered.getRentalAmt());
                    // view 레파지 토리에 save
                    carInformationRepository.save(carInformation);
                    System.out.println("##### listener whenCarRegistered_then_CREATE_1 [VIEW] : " + carInformation.getCarNo());
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenCarUpdated_then_UPDATE_1(@Payload CarUpdated carUpdated) {
        try {
            if (carUpdated.isMe()) {
                // view 객체 조회
                List<CarInformation> carInformationList = carInformationRepository.findByCarNo(carUpdated.getCarNo());
                for(CarInformation carInformation : carInformationList){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    carInformation.setProcStatus(carUpdated.getProcStatus());
                    carInformation.setRentalAmt(carUpdated.getRentalAmt());

                    System.out.println("##### listener setRentalAmt : " + carUpdated.getRentalAmt());
                    System.out.println("##### listener whenCarUpdated_then_UPDATE_1 [VIEW] : " + carInformation.getCarNo());
                    // view 레파지 토리에 save
                    carInformationRepository.save(carInformation);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenCarDeleted_then_UPDATE_2(@Payload CarDeleted carDeleted) {
        try {
            if (carDeleted.isMe()) {
                // view 객체 조회
                List<CarInformation> carInformationList = carInformationRepository.findByCarNo(carDeleted.getCarNo());
                for(CarInformation carInformation : carInformationList){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    carInformation.setProcStatus(carDeleted.getProcStatus());
                    System.out.println("##### listener whenCarDeleted_then_UPDATE_2 [VIEW] : " + carInformation.getCarNo());
                    // view 레파지 토리에 save
                    carInformationRepository.save(carInformation);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}