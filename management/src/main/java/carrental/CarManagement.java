package carrental;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;

@Entity
@Table(name="CarManagement_table")
public class CarManagement {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String carNo;
    private String carRegDt;
    private String carDelDt;
    private Long rentalAmt;
    private String procStatus;
    private String carStatusNa;

    @PostPersist
    public void onPostPersist(){
        CarRegistered carRegistered = new CarRegistered();
        BeanUtils.copyProperties(this, carRegistered);
        carRegistered.publishAfterCommit();


    }

    @PostUpdate
    public void onPostUpdate(){
        CarDeleted carDeleted = new CarDeleted();
        BeanUtils.copyProperties(this, carDeleted);
        carDeleted.publishAfterCommit();


        CarUpdated carUpdated = new CarUpdated();
        BeanUtils.copyProperties(this, carUpdated);
        carUpdated.publishAfterCommit();


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
    public String getCarRegDt() {
        return carRegDt;
    }

    public void setCarRegDt(String carRegDt) {
        this.carRegDt = carRegDt;
    }
    public String getCarDelDt() {
        return carDelDt;
    }

    public void setCarDelDt(String carDelDt) {
        this.carDelDt = carDelDt;
    }
    public Long getRentalAmt() {
        return rentalAmt;
    }

    public void setRentalAmt(Long rentalAmt) {
        this.rentalAmt = rentalAmt;
    }
    public String getProcStatus() {
        return procStatus;
    }

    public void setProcStatus(String procStatus) {
        this.procStatus = procStatus;
    }
    public String getCarStatusNa() {
        return carStatusNa;
    }

    public void setCarStatusNa(String carStatusNa) {
        this.carStatusNa = carStatusNa;
    }




}
