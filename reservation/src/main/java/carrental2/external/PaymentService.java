
package carrental2.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

//@FeignClient(name="payment", url="http://payment:8080")
@FeignClient(name="payment", url="http://localhost:8083")
public interface PaymentService {

    @RequestMapping(method= RequestMethod.POST, path="/payments")
    public void payment(@RequestBody Payment payment);

    @RequestMapping(method= RequestMethod.POST, path="/payments")
    public void paymentCancellation(@RequestBody Payment payment);
}