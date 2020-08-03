package carrental;

public class CarReserved extends AbstractEvent {

    private Long id;
    private String resrvNo;
    private String resrvDt;
    private String carNo;
    private String rentalDt;
    private String returnDt;
    private Long rentalAmt;
    private String procStatus;
    private String bookerMpnoNA;
    private String bookerCustNoNA;

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
    public String getResrvDt() {
        return resrvDt;
    }

    public void setResrvDt(String resrvDt) {
        this.resrvDt = resrvDt;
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
    public String getProcStatus() {
        return procStatus;
    }

    public void setProcStatus(String procStatus) {
        this.procStatus = procStatus;
    }
    public String getBookerMpnoNa() {
        return bookerMpnoNA;
    }

    public void setBookerMpnoNa(String bookerMpnoNA) {
        this.bookerMpnoNA = bookerMpnoNA;
    }
    public String getBookerCustNoNa() {
        return bookerCustNoNA;
    }

    public void setBookerCustNoNa(String bookerCustNoNA) {
        this.bookerCustNoNA = bookerCustNoNA;
    }
}