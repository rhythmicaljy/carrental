package carrental;

public class CarUpdated extends AbstractEvent {

    private Long id;
    private String carNo;
    private Long rentalAmt;
    private String procStatus;
    private String carStatusNA;

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
    public String getProcStatus() {
        return procStatus;
    }

    public void setProcStatus(String procStatus) {
        this.procStatus = procStatus;
    }
    public String getCarStatusNa() {
        return carStatusNA;
    }

    public void setCarStatusNa(String carStatusNA) {
        this.carStatusNA = carStatusNA;
    }
}