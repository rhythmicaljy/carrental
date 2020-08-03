package carrental2;

public class Paid extends AbstractEvent {

    private Long id;
    private String resrvNo;
    private String paymtNo;
    private String paymtDt;
    private Long paymtAmt;
    private String procStatus;

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
}