package carrental2;

import carrental2.config.kafka.KafkaProcessor;
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
                myPage.setResrvDt(carReserved.getResrvDt());
                myPage.setCarNo(carReserved.getCarNo());
                myPage.setProcStatus(carReserved.getProcStatus());
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
                    myPage.setPaymtNo(paid.getPaymtNo());
                    myPage.setPaymtDt(paid.getPaymtDt());
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
                List<MyPage> myPageList = myPageRepository.findByCarNo(carRented.getCarNo());
                for(MyPage myPage : myPageList){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    myPage.setRentalDvsn(carRented.getRentalDvsn());
                    myPage.setProcStatus(carRented.getProcStatus());
                    myPage.setRentalDt(carRented.getRentalDt());
                    myPage.setReturnDt(carRented.getReturnDt());
                    myPage.setPaymtNo(carRented.getPaymtNo());
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
                    myPage.setProcStatus(carReservationCanceled.getProcStatus());
                    myPage.setResrvCncleDt(carReservationCanceled.getResrvCncleDt());
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
                    myPage.setProcStatus(paymentCanceled.getProcStatus());
                    myPage.setPaymtCncleDt(paymentCanceled.getPaymtCncleDt());
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
                List<MyPage> myPageList = myPageRepository.findByCarNo(carRentalCanceled.getCarNo());
                for(MyPage myPage : myPageList){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    myPage.setProcStatus(carRentalCanceled.getProcStatus());
                    myPage.setRentalCncleDt(carRentalCanceled.getRentalCncleDt());
                    // view 레파지 토리에 save
                    myPageRepository.save(myPage);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}