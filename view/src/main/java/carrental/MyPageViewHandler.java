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
public class MyPageViewHandler {


    @Autowired
    private MyPageRepository myPageRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whenCarReserved_then_CREATE_1 (@Payload CarReserved carReserved) {
        try {
            if (carReserved.isMe()) {
                // view 객체 생성
                MyPage myPage = new MyPage();
                // view 객체에 이벤트의 Value 를 set 함
                myPage.setResrvNo(carReserved.getResrvNo());
                myPage.setRentalAmt(carReserved.getRentalAmt());
                myPage.setProcStatus(carReserved.getProcStatus());
                myPage.setRentalDt(carReserved.getRentalDt());
                myPage.setReturnDt(carReserved.getReturnDt());
                myPage.setCarNo(carReserved.getCarNo());
                // view 레파지 토리에 save
                myPageRepository.save(myPage);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whenPaid_then_UPDATE_1(@Payload Paid paid) {
        try {
            if (paid.isMe()) {
                // view 객체 조회
                List<MyPage> myPageList = myPageRepository.findByResrvNo(paid.getResrvNo());
                for(MyPage myPage : myPageList){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    myPage.setResrvNo(paid.getResrvNo());
                    myPage.setProcStatus(paid.getProcStatus());
                    // view 레파지 토리에 save
                    myPageRepository.save(myPage);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenCarRented_then_UPDATE_2(@Payload CarRented carRented) {
        try {
            if (carRented.isMe()) {
                // view 객체 조회
                List<MyPage> myPageList = myPageRepository.findByResrvNo(carRented.getResrvNo());
                for(MyPage myPage : myPageList){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    myPage.setResrvNo(carRented.getResrvNo());
                    myPage.setProcStatus(carRented.getProcStatus());
                    // view 레파지 토리에 save
                    myPageRepository.save(myPage);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenCarReservationCanceled_then_UPDATE_3(@Payload CarReservationCanceled carReservationCanceled) {
        try {
            if (carReservationCanceled.isMe()) {
                // view 객체 조회
                List<MyPage> myPageList = myPageRepository.findByResrvNo(carReservationCanceled.getResrvNo());
                for(MyPage myPage : myPageList){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    myPage.setResrvNo(carReservationCanceled.getResrvNo());
                    myPage.setProcStatus(carReservationCanceled.getProcStatus());
                    // view 레파지 토리에 save
                    myPageRepository.save(myPage);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenPaymentCanceled_then_UPDATE_4(@Payload PaymentCanceled paymentCanceled) {
        try {
            if (paymentCanceled.isMe()) {
                // view 객체 조회
                List<MyPage> myPageList = myPageRepository.findByResrvNo(paymentCanceled.getResrvNo());
                for(MyPage myPage : myPageList){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    myPage.setResrvNo(paymentCanceled.getResrvNo());
                    myPage.setProcStatus(paymentCanceled.getProcStatus());
                    // view 레파지 토리에 save
                    myPageRepository.save(myPage);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenCarRentalCanceled_then_UPDATE_5(@Payload CarRentalCanceled carRentalCanceled) {
        try {
            if (carRentalCanceled.isMe()) {
                // view 객체 조회
                List<MyPage> myPageList = myPageRepository.findByResrvNo(carRentalCanceled.getResrvNo());
                for(MyPage myPage : myPageList){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    myPage.setResrvNo(carRentalCanceled.getResrvNo());
                    myPage.setProcStatus(carRentalCanceled.getProcStatus());
                    // view 레파지 토리에 save
                    myPageRepository.save(myPage);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}