package carrental;

import carrental.config.kafka.KafkaProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MyPageViewHandler {

    @Autowired
    private MyPageRepository myPageRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whenCarReserved_then_CREATE_1(@Payload CarReserved carReserved) {
        try {
            if (carReserved.isMe()) {
                // view 객체 생성
                List<MyPage> myPageList = myPageRepository.findByResrvNo(carReserved.getResrvNo());
                MyPage myPage = new MyPage();
                myPage.setResrvNo(carReserved.getResrvNo());
                if (myPageList.size() > 0) {
                    myPage = myPageList.get(0);
                }
                // view 객체에 이벤트의 Value 를 set 함
                myPage.setRentalAmt(carReserved.getRentalAmt());
                myPage.setProcStatus(carReserved.getProcStatus());
                myPage.setRentalDt(carReserved.getRentalDt());
                myPage.setReturnDt(carReserved.getReturnDt());
                myPage.setCarNo(carReserved.getCarNo());
                // view 레파지 토리에 save
                myPageRepository.save(myPage);
            } else {
                System.out.println("##### listener whenCarReserved_then_CREATE_1 [VIEW] : " + carReserved.toJson());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenPaid_then_UPDATE_1(@Payload Paid paid) {
        try {
            if (paid.isMe()) {
                // view 객체 조회
                List<MyPage> myPageList = myPageRepository.findByResrvNo(paid.getResrvNo());
                MyPage myPage = new MyPage();
                myPage.setResrvNo(paid.getResrvNo());
                if (myPageList.size() > 0) {
                    myPage = myPageList.get(0);
                }
                myPage.setRentalAmt(paid.getPaymtAmt());
                myPage.setProcStatus(paid.getProcStatus());
                myPageRepository.save(myPage);
                System.out.println("##### listener whenPaid_then_UPDATE_1 [VIEW] : " + paid.toJson());            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenCarRented_then_UPDATE_2(@Payload CarRented carRented) {
        try {
            if (carRented.isMe()) {
                // view 객체 조회
                List<MyPage> myPageList = myPageRepository.findByResrvNo(carRented.getResrvNo());
                if (myPageList.size() > 0) {
                    MyPage myPage = myPageList.get(0);
                    myPage.setProcStatus(carRented.getProcStatus());
                    myPageRepository.save(myPage);
                }
            } else {
                System.out.println("##### listener whenCarRented_then_UPDATE_2 [VIEW] : " + carRented.toJson());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whenCarReservationCanceled_then_UPDATE_3(@Payload CarReservationCanceled carReservationCanceled) {
        try {
            if (carReservationCanceled.isMe()) {
                // view 객체 조회
                List<MyPage> myPageList = myPageRepository.findByResrvNo(carReservationCanceled.getResrvNo());
                if (myPageList.size() > 0) {
                    MyPage myPage = myPageList.get(0);
                    myPage.setProcStatus(carReservationCanceled.getProcStatus());
                    myPageRepository.save(myPage);
                }
            } else {
                System.out.println("##### listener whenCarReservationCanceled_then_UPDATE_3 [VIEW] : " + carReservationCanceled.toJson());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenPaymentCanceled_then_UPDATE_4(@Payload PaymentCanceled paymentCanceled) {
        try {
            if (paymentCanceled.isMe()) {
                // view 객체 조회
                List<MyPage> myPageList = myPageRepository.findByResrvNo(paymentCanceled.getResrvNo());
                if (myPageList.size() > 0) {
                    MyPage myPage = myPageList.get(0);
                    myPage.setRentalAmt(paymentCanceled.getPaymtAmt());
                    myPage.setProcStatus(paymentCanceled.getProcStatus());
                    myPageRepository.save(myPage);
                }
            } else {
                System.out.println("##### listener whenPaymentCanceled_then_UPDATE_4 [VIEW] : " + paymentCanceled.toJson());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenCarRentalCanceled_then_UPDATE_5(@Payload CarRentalCanceled carRentalCanceled) {
        try {
            if (carRentalCanceled.isMe()) {
                // view 객체 조회
                List<MyPage> myPageList = myPageRepository.findByResrvNo(carRentalCanceled.getResrvNo());
                if (myPageList.size() > 0) {
                    MyPage myPage = myPageList.get(0);
                    myPage.setProcStatus(carRentalCanceled.getProcStatus());
                    myPageRepository.save(myPage);
                }

            } else {
                System.out.println("##### listener whenCarRentalCanceled_then_UPDATE_5 [VIEW] : " + carRentalCanceled.toJson());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}