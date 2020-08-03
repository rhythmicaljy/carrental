package carrental;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="CarInformation_table")
public class CarInformation {

        @Id
        @GeneratedValue(strategy=GenerationType.AUTO)
        private Long id;
        private String carNo;
        private Long rentalAmt;
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
        public Long getRentalAmt() {
            return rentalAmt;
        }

        public void setRentalAmt(Long rentalAmt) {
            this.rentalAmt = rentalAmt;
        }
        public String getProcStatus() {
            return procStatus;
        }

        public void setProcStatus(String procStatus) {
            this.procStatus = procStatus;
        }

}
