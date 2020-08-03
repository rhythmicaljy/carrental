package carrental;

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
    private String bookerCustNoNa;
    private String resrvDt;
    private String resrvCncleDt;
    private String carNo;
    private String rentalDt;
    private String returnDt;
    private Long rentalAmt;
    private String bookerMpnoNa;
    private String procStatus;

    @PostPersist
    public void onPostPersist(){
        CarReserved carReserved = new CarReserved();
        BeanUtils.copyProperties(this, carReserved);
        carReserved.publishAfterCommit();

        //Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.

        carrental.external.Payment payment = new carrental.external.Payment();
        // mappings goes here
        payment.setId(carReserved.getId());
        payment.setResrvNo(carReserved.getResrvNo());
        payment.setPaymtNo(carReserved.getResrvNo());
        payment.setPaymtDt(carReserved.getResrvDt());
        payment.setRentalAmt(carReserved.getRentalAmt());
        payment.setPaymtAmt(carReserved.getRentalAmt());
        payment.setProcStatus("RESERVED");
        payment.setCarNo(carReserved.getCarNo());
        payment.setRentalDt(carReserved.getRentalDt());
        payment.setReturnDt(carReserved.getReturnDt());





        System.out.println("##### listener carReservationCanceled.getResrvNo [RESERVED] : " + carReserved.getResrvNo());

        ReservationApplication.applicationContext.getBean(carrental.external.PaymentService.class)
            .payment(payment);


    }

    @PostUpdate
    public void onPostUpdate(){
        CarReservationCanceled carReservationCanceled = new CarReservationCanceled();
        BeanUtils.copyProperties(this, carReservationCanceled);
        carReservationCanceled.publishAfterCommit();

        //Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.

        carrental.external.Payment payment = new carrental.external.Payment();
        // mappings goes here
        payment.setId(carReservationCanceled.getId());
        payment.setResrvNo(carReservationCanceled.getResrvNo());
        payment.setPaymtNo(carReservationCanceled.getResrvNo());
        payment.setPaymtCncleDt(carReservationCanceled.getResrvCncleDt());
        payment.setPaymtAmt(carReservationCanceled.getRentalAmt());
        payment.setProcStatus("RESERVATION_CANCELLED");
        payment.setCarNo(carReservationCanceled.getCarNo());

        System.out.println("##### listener carReservationCanceled.getResrvNo [RESERVATION_CANCELLED] : " + carReservationCanceled.getResrvNo());

        ReservationApplication.applicationContext.getBean(carrental.external.PaymentService.class)
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
    public String getBookerCustNoNa() {
        return bookerCustNoNa;
    }

    public void setBookerCustNoNa(String bookerCustNoNa) {
        this.bookerCustNoNa = bookerCustNoNa;
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
    public Long getRentalAmt() {
        return rentalAmt;
    }

    public void setRentalAmt(Long rentalAmt) {
        this.rentalAmt = rentalAmt;
    }
    public String getBookerMpnoNa() {
        return bookerMpnoNa;
    }

    public void setBookerMpnoNa(String bookerMpnoNa) {
        this.bookerMpnoNa = bookerMpnoNa;
    }
    public String getProcStatus() {
        return procStatus;
    }

    public void setProcStatus(String procStatus) {
        this.procStatus = procStatus;
    }




}
