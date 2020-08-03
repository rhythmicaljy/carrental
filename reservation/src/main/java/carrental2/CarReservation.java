package carrental2;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;

@Entity
@Table(name="CarReservation_table")
public class CarReservation {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String resrvNo;
    private String custNo;
    private String resrvDt;
    private String resrvCncleDt;
    private String carNo;
    private String rentalDt;
    private String returnDt;
    private String procStatus;
    private String rentalDvsn;
    private String paymtNo;
    private Long rentalAmt;

    @PostPersist
    public void onPostPersist(){
        CarReserved carReserved = new CarReserved();
        BeanUtils.copyProperties(this, carReserved);
        carReserved.publishAfterCommit();

        //Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.

        carrental2.external.Payment payment = new carrental2.external.Payment();
        // mappings goes here
        payment.setId(carReserved.getId());
        payment.setResrvNo(carReserved.getResrvNo());
        payment.setPaymtNo(carReserved.getPaymtNo());
        payment.setPaymtAmt(carReserved.getRentalAmt());
        payment.setPaymtDt(carReserved.getResrvDt());
        payment.setProcStatus("RESERVED");

        System.out.println("##### listener [RESERVED] carReserved.getResrvNo : " + carReserved.getResrvNo());

        ReservationApplication.applicationContext.getBean(carrental2.external.PaymentService.class)
            .payment(payment);


    }

    @PostUpdate
    public void onPostUpdate(){
        CarReservationCanceled carReservationCanceled = new CarReservationCanceled();
        BeanUtils.copyProperties(this, carReservationCanceled);
        carReservationCanceled.publishAfterCommit();

        //Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.

        carrental2.external.Payment payment = new carrental2.external.Payment();
        // mappings goes here
        payment.setId(carReservationCanceled.getId());
        payment.setResrvNo(carReservationCanceled.getResrvNo());
        payment.setPaymtCncleDt(carReservationCanceled.getResrvCncleDt());
        payment.setProcStatus("RESERVATION_CANCELLED");

        System.out.println("##### listener [RESERVATION_CANCELLED] carReservationCanceled.getResrvNo : " + carReservationCanceled.getResrvNo());

        ReservationApplication.applicationContext.getBean(carrental2.external.PaymentService.class)
            .paymentCancellation(payment);


    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getResrvNo() {
        return resrvNo;
    }

    public void setResrvNo(String resrvNo) {
        this.resrvNo = resrvNo;
    }
    public String getCustNo() {
        return custNo;
    }

    public void setCustNo(String custNo) {
        this.custNo = custNo;
    }
    public String getResrvDt() {
        return resrvDt;
    }

    public void setResrvDt(String resrvDt) {
        this.resrvDt = resrvDt;
    }
    public String getResrvCncleDt() {
        return resrvCncleDt;
    }

    public void setResrvCncleDt(String resrvCncleDt) {
        this.resrvCncleDt = resrvCncleDt;
    }
    public String getCarNo() {
        return carNo;
    }

    public void setCarNo(String carNo) {
        this.carNo = carNo;
    }
    public String getRentalDt() {
        return rentalDt;
    }

    public void setRentalDt(String rentalDt) {
        this.rentalDt = rentalDt;
    }
    public String getReturnDt() {
        return returnDt;
    }

    public void setReturnDt(String returnDt) {
        this.returnDt = returnDt;
    }
    public String getProcStatus() {
        return procStatus;
    }

    public void setProcStatus(String procStatus) {
        this.procStatus = procStatus;
    }
    public String getRentalDvsn() {
        return rentalDvsn;
    }

    public void setRentalDvsn(String rentalDvsn) {
        this.rentalDvsn = rentalDvsn;
    }
    public String getPaymtNo() {
        return paymtNo;
    }

    public void setPaymtNo(String paymtNo) {
        this.paymtNo = paymtNo;
    }
    public Long getRentalAmt() {
        return rentalAmt;
    }

    public void setRentalAmt(Long rentalAmt) {
        this.rentalAmt = rentalAmt;
    }




}
