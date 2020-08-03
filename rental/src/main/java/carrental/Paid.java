package carrental;

public class Paid extends AbstractEvent {

    private Long id;
    private String resrvNo;
    private String paymtNo;
    private String paymtDt;
    private Long paymtAmt;
    private String procStatus;
    private String payerCustNoNA;
    private String carNo;
    private String rentalDt;
    private String returnDt;
    private Long rentalAmt;

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
    public String getPaymtNo() {
        return paymtNo;
    }

    public void setPaymtNo(String paymtNo) {
        this.paymtNo = paymtNo;
    }
    public String getPaymtDt() {
        return paymtDt;
    }

    public void setPaymtDt(String paymtDt) {
        this.paymtDt = paymtDt;
    }
    public Long getPaymtAmt() {
        return paymtAmt;
    }

    public void setPaymtAmt(Long paymtAmt) {
        this.paymtAmt = paymtAmt;
    }
    public String getProcStatus() {
        return procStatus;
    }

    public void setProcStatus(String procStatus) {
        this.procStatus = procStatus;
    }
    public String getPayerCustNoNa() {
        return payerCustNoNA;
    }

    public void setPayerCustNoNa(String payerCustNoNA) {
        this.payerCustNoNA = payerCustNoNA;
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
}