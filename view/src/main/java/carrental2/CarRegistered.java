package carrental2;

public class CarRegistered extends AbstractEvent {

    private Long id;
    private String carNo;
    private Long rentalAmt;
    private String carRegDt;
    private String procStatus;

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
    public Long getRentalAmt() {
        return rentalAmt;
    }

    public void setRentalAmt(Long rentalAmt) {
        this.rentalAmt = rentalAmt;
    }
    public String getCarRegDt() {
        return carRegDt;
    }

    public void setCarRegDt(String carRegDt) {
        this.carRegDt = carRegDt;
    }
    public String getProcStatus() {
        return procStatus;
    }

    public void setProcStatus(String procStatus) {
        this.procStatus = procStatus;
    }
}