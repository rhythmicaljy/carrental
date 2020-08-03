package carrental;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;

@Entity
@Table(name="CarRental_table")
public class CarRental {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String rentalNoNa;
    private String rentalDvsnNa;
    private String brwerCustNoNa;
    private String carNo;
    private String rentalDt;
    private String returnDt;
    private String paymtNo;
    private String rentalCncleDt;
    private String procStatus;
    private String resrvNo;

    @PostPersist
    public void onPostPersist(){
        CarRented carRented = new CarRented();
        BeanUtils.copyProperties(this, carRented);
        carRented.publishAfterCommit();


    }

    @PostUpdate
    public void onPostUpdate(){
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
    public String getRentalNoNa() {
        return rentalNoNa;
    }

    public void setRentalNoNa(String rentalNoNa) {
        this.rentalNoNa = rentalNoNa;
    }
    public String getRentalDvsnNa() {
        return rentalDvsnNa;
    }

    public void setRentalDvsnNa(String rentalDvsnNa) {
        this.rentalDvsnNa = rentalDvsnNa;
    }
    public String getBrwerCustNoNa() {
        return brwerCustNoNa;
    }

    public void setBrwerCustNoNa(String brwerCustNoNa) {
        this.brwerCustNoNa = brwerCustNoNa;
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
    public String getResrvNo() {
        return resrvNo;
    }

    public void setResrvNo(String resrvNo) {
        this.resrvNo = resrvNo;
    }


}
