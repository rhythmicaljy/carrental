package carrental;

public class CarReservationCanceled extends AbstractEvent {

    private Long id;
    private String resrvNo;
    private String resrvCncleDt;
    private String procStatus;
    private String bookerCustNoNA;
    private String bookerMpnoNA;

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
    public String getProcStatus() {
        return procStatus;
    }

    public void setProcStatus(String procStatus) {
        this.procStatus = procStatus;
    }
    public String getBookerCustNoNa() {
        return bookerCustNoNA;
    }

    public void setBookerCustNoNa(String bookerCustNoNA) {
        this.bookerCustNoNA = bookerCustNoNA;
    }
    public String getBookerMpnoNa() {
        return bookerMpnoNA;
    }

    public void setBookerMpnoNa(String bookerMpnoNA) {
        this.bookerMpnoNA = bookerMpnoNA;
    }
}