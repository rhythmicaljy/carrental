package carrental2;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="MyPage_table")
public class MyPage {

        @Id
        @GeneratedValue(strategy=GenerationType.AUTO)
        private Long id;
        private String resrvNo;
        private String rentalDt;
        private String returnDt;
        private Long rentalAmt;
        private String carNo;
        private String resrvCncleDt;
        private String paymtCncleDt;
        private String rentalCncleDt;
        private String paymtNo;
        private String paymtDtm;
        private String rentalDvsn;
        private String procStatus;
        private String resrvDt;
        private String paymtDt;


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
        public String getCarNo() {
            return carNo;
        }

        public void setCarNo(String carNo) {
            this.carNo = carNo;
        }
        public String getResrvCncleDt() {
            return resrvCncleDt;
        }

        public void setResrvCncleDt(String resrvCncleDt) {
            this.resrvCncleDt = resrvCncleDt;
        }
        public String getPaymtCncleDt() {
            return paymtCncleDt;
        }

        public void setPaymtCncleDt(String paymtCncleDt) {
            this.paymtCncleDt = paymtCncleDt;
        }
        public String getRentalCncleDt() {
            return rentalCncleDt;
        }

        public void setRentalCncleDt(String rentalCncleDt) {
            this.rentalCncleDt = rentalCncleDt;
        }
        public String getPaymtNo() {
            return paymtNo;
        }

        public void setPaymtNo(String paymtNo) {
            this.paymtNo = paymtNo;
        }
        public String getPaymtDtm() {
            return paymtDtm;
        }

        public void setPaymtDtm(String paymtDtm) {
            this.paymtDtm = paymtDtm;
        }
        public String getRentalDvsn() {
            return rentalDvsn;
        }

        public void setRentalDvsn(String rentalDvsn) {
            this.rentalDvsn = rentalDvsn;
        }
        public String getProcStatus() {
            return procStatus;
        }

        public void setProcStatus(String procStatus) {
            this.procStatus = procStatus;
        }
        public String getResrvDt() {
            return resrvDt;
        }

        public void setResrvDt(String resrvDt) {
            this.resrvDt = resrvDt;
        }
        public String getPaymtDt() {
            return paymtDt;
        }

        public void setPaymtDt(String paymtDt) {
            this.paymtDt = paymtDt;
        }

}
