package carrental;

public class CarReservationCanceled extends AbstractEvent {

    private Long id;
    private String resrvNo;
    private String bookerCustNo;
    private String resrvCncleDt;
    private String bookerMpno;
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
    public String getBookerCustNo() {
        return bookerCustNo;
    }

    public void setBookerCustNo(String bookerCustNo) {
        this.bookerCustNo = bookerCustNo;
    }
    public String getResrvCncleDt() {
        return resrvCncleDt;
    }

    public void setResrvCncleDt(String resrvCncleDt) {
        this.resrvCncleDt = resrvCncleDt;
    }
    public String getBookerMpno() {
        return bookerMpno;
    }

    public void setBookerMpno(String bookerMpno) {
        this.bookerMpno = bookerMpno;
    }
    public String getProcStatus() {
        return procStatus;
    }

    public void setProcStatus(String procStatus) {
        this.procStatus = procStatus;
    }

}