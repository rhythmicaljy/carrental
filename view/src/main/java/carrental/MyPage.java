package carrental;

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
        public String getProcStatus() {
            return procStatus;
        }

        public void setProcStatus(String procStatus) {
            this.procStatus = procStatus;
        }

}
