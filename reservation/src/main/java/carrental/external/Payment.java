package carrental.external;

public class Payment {

    private Long id;
    private String paymtNo;
    private String paymtDt;
    private String paymtCncleDt;
    private Long rentalAmt;
    private Long paymtAmt;
    private String resrvNo;
    private String procStatus;
    private String payerCustNoNa;
    private String carNo;
    private String rentalDt;
    private String returnDt;


    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getPaymtNo() {
        return paymtNo;
    }
    public void setPaymtNo(String paymtNo) {
        this.paymtNo = paymtNo;
    }
    public String getPayerCustNoNa() {
        return payerCustNoNa;
    }
    public void setPayerCustNoNa(String payerCustNoNa) {
        this.payerCustNoNa = payerCustNoNa;
    }


    public String getPaymtDt() {
        return paymtDt;
    }
    public void setPaymtDt(String paymtDt) {
        this.paymtDt = paymtDt;
    }
    public String getPaymtCncleDt() {
        return paymtCncleDt;
    }
    public void setPaymtCncleDt(String paymtCncleDt) {
        this.paymtCncleDt = paymtCncleDt;
    }


    public Long getRentalAmtAmt() {
        return rentalAmt;
    }
    public void setRentalAmt(Long rentalAmt) {
        this.rentalAmt = rentalAmt;
    }


    public Long getPaymtAmt() {
        return paymtAmt;
    }
    public void setPaymtAmt(Long paymtAmt) {
        this.paymtAmt = paymtAmt;
    }
    public String getResrvNo() {
        return resrvNo;
    }
    public void setResrvNo(String resrvNo) {
        this.resrvNo = resrvNo;
    }
    public String getProcStatus() {
        return procStatus;
    }
    public void setProcStatus(String procStatus) {
        this.procStatus = procStatus;
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
}
