package carrental;

public class PaymentCanceled extends AbstractEvent {

    private Long id;
    private String resrvNo;
    private String paymtNo;
    private String paymtCncleDt;
    private Long paymtAmt;
    private String procStatus;
    private String payerCustNoNA;
    private String carNo;

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
    public String getPaymtCncleDt() {
        return paymtCncleDt;
    }

    public void setPaymtCncleDt(String paymtCncleDt) {
        this.paymtCncleDt = paymtCncleDt;
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
}