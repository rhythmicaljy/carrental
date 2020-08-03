//package carrental;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@org.springframework.web.bind.annotation.RestController
//public class RestController {
//
//    @Autowired CarInformationRepository carInformationRepository;
//    @Autowired MyPageRepository myPageRepository;
//
//    @GetMapping("/carInfo/{carNo}")
//    public ResponseEntity<?> getCarInformation(@PathVariable String carNo) {
//        List<CarInformation> carInformationList = carInformationRepository.findByCarNo(carNo);
//        CarInformation carInfo = new CarInformation();
//        carInfo.setCarNo(carNo);
//        for (CarInformation carInformation : carInformationList){
//            carInfo.setId(carInformation.getId());
//            carInfo.setProcStatus(carInformation.getProcStatus());
//            if (!(carInformation.getRentalAmt()==null)) carInfo.setRentalAmt(carInformation.getRentalAmt());
//        }
//        return ResponseEntity.ok(carInfo);
//    }
//
//    @GetMapping("/resvInfo/{resvNo}")
//    public ResponseEntity<?> getResvInformation(@PathVariable String resvNo) {
//        List<MyPage> myPageList = myPageRepository.findByResrvNo(resvNo);
//        MyPage myPage = new MyPage();
//        myPage.setResrvNo(resvNo);
//        for (MyPage myP : myPageList){
//            myPage.setId(myP.getId());
//            if (!(myP.getRentalAmt()==null)) myPage.setRentalAmt(myP.getRentalAmt());
//            if (!(myP.getCarNo()==null)) myPage.setCarNo(myP.getCarNo());
//            if (!(myP.getRentalDt()==null)) myPage.setRentalDt(myP.getRentalDt());
//            if (!(myP.getReturnDt()==null)) myPage.setReturnDt(myP.getReturnDt());
//            if (!(myP.getProcStatus()==null)) myPage.setProcStatus(myP.getProcStatus());
//        }
//        return ResponseEntity.ok(myPage);
//    }
//
//}
