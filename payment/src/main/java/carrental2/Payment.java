package carrental2;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;

@Entity
@Table(name="Payment_table")
public class Payment {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String paymtNo;
    private String paymtDt;
    private String paymtCncleDt;
    private Long paymtAmt;
    private String resrvNo;
    private String procStatus;

    @PrePersist
    public void onPrePersist(){
        Paid paid = new Paid();
        BeanUtils.copyProperties(this, paid);
        paid.publishAfterCommit();


    }

    @PreUpdate
    public void onPreUpdate(){
        PaymentCanceled paymentCanceled = new PaymentCanceled();
        BeanUtils.copyProperties(this, paymentCanceled);
        paymentCanceled.publishAfterCommit();


    }


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




}
