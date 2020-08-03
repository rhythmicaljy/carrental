package carrental2;

public class CarUpdated extends AbstractEvent {

    private Long id;
    private String carNo;
    private String procStatus;
    private Long rentalAmt;

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
    public String getProcStatus() {
        return procStatus;
    }

    public void setProcStatus(String procStatus) {
        this.procStatus = procStatus;
    }
    public Long getRentalBasicAmt() {
        return rentalAmt;
    }

    public void setRentalBasicAmt(Long rentalAmt) {
        this.rentalAmt = rentalAmt;
    }
}