package carrental2;

public class CarReservationCanceled extends AbstractEvent {

    private Long id;
    private String resrvNo;
    private String resrvCncleDt;
    private String custNo;
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
    public String getResrvCncleDt() {
        return resrvCncleDt;
    }

    public void setResrvCncleDt(String resrvCncleDt) {
        this.resrvCncleDt = resrvCncleDt;
    }
    public String getCustNo() {
        return custNo;
    }

    public void setCustNo(String custNo) {
        this.custNo = custNo;
    }
    public String getProcStatus() {
        return procStatus;
    }

    public void setProcStatus(String procStatus) {
        this.procStatus = procStatus;
    }
}