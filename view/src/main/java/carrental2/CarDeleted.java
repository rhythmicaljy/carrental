package carrental2;

public class CarDeleted extends AbstractEvent {

    private Long id;
    private String carNo;
    private String carDelDt;
    private String procStatus;

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
    public String getCarDelDt() {
        return carDelDt;
    }

    public void setCarDelDt(String carDelDt) {
        this.carDelDt = carDelDt;
    }
    public String getProcStatus() {
        return procStatus;
    }

    public void setProcStatus(String procStatus) {
        this.procStatus = procStatus;
    }
}