package carrental;

import org.springframework.beans.BeanUtils;

import javax.persistence.*;

@Entity
@Table(name="Payment_table")
public class Payment {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String paymtNo;
    private String payerCustNoNa;
    private String paymtDt;
    private String paymtCncleDt;
    private Long paymtAmt;
    private String resrvNo;
    private String procStatus;
    private String carNo;
    private String rentalDt;
    private String returnDt;
    private Long rentalAmt;

    @PrePersist
    public void onPrePersist(){
/*        Paid paid = new Paid();
        BeanUtils.copyProperties(this, paid);
        paid.publishAfterCommit();*/


        /*
        // 부하 발생
        try {
            Thread.currentThread().sleep((long) (400 + Math.random() * 220));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        */
    }

    @PostUpdate
    public void onPostUpdate(){

        if ("PAID".equals(this.getProcStatus())) {
            Paid paid = new Paid();
            BeanUtils.copyProperties(this, paid);
            paid.publishAfterCommit();
        } else {
            PaymentCanceled paymentCanceled = new PaymentCanceled();
            BeanUtils.copyProperties(this, paymentCanceled);
            paymentCanceled.publishAfterCommit();
        }



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
    public Long getRentalAmt() {
        return rentalAmt;
    }

    public void setRentalAmt(Long rentalAmt) {
        this.rentalAmt = rentalAmt;
    }




}
