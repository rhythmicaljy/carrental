package carrental2;

public class CarRentalCanceled extends AbstractEvent {

    private Long id;
    private String carNo;
    private String resrvNo;
    private String rentalCncleDt;
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
    public String getResrvNo() {
        return resrvNo;
    }

    public void setResrvNo(String resrvNo) {
        this.resrvNo = resrvNo;
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
}