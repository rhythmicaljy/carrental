package carrental2;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;

@Entity
@Table(name="CarRental_table")
public class CarRental {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String carNo;
    private String rentalDvsn;
    private String rentalDt;
    private String returnDt;
    private String paymtNo;
    private String rentalCncleDt;
    private String procStatus;
    private Long rentalAmt;
    private String resrvNo;

    @PrePersist
    public void onPrePersist(){
        CarRented carRented = new CarRented();
        BeanUtils.copyProperties(this, carRented);
        carRented.publishAfterCommit();


    }

    @PreUpdate
    public void onPreUpdate(){
        CarRentalCanceled carRentalCanceled = new CarRentalCanceled();
        BeanUtils.copyProperties(this, carRentalCanceled);
        carRentalCanceled.publishAfterCommit();


    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getCarNo() {
        return carNo;
    }

    public void setCarNo(String carNo) {
        this.carNo = carNo;
    }
    public String getRentalDvsn() {
        return rentalDvsn;
    }

    public void setRentalDvsn(String rentalDvsn) {
        this.rentalDvsn = rentalDvsn;
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
    public String getPaymtNo() {
        return paymtNo;
    }

    public void setPaymtNo(String paymtNo) {
        this.paymtNo = paymtNo;
    }
    public String getRentalCncleDt() {
        return rentalCncleDt;
    }

    public void setRentalCncleDt(String rentalCncleDt) {
        this.rentalCncleDt = rentalCncleDt;
    }
    public String getProcStatus() {
        return procStatus;
    }

    public void setProcStatus(String procStatus) {
        this.procStatus = procStatus;
    }
    public Long getRentalAmt() {
        return rentalAmt;
    }

    public void setRentalAmt(Long rentalAmt) {
        this.rentalAmt = rentalAmt;
    }
    public String getResrvNo() {
        return resrvNo;
    }

    public void setResrvNo(String resrvNo) {
        this.resrvNo = resrvNo;
    }




}
