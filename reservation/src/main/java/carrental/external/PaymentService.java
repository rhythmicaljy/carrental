
package carrental.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

//@FeignClient(name="payment", url="http://payment:8080", fallback = PaymentServiceFallback.class)
@FeignClient(name="payment", url="http://localhost:8083", fallback = PaymentServiceFallback.class)
public interface PaymentService {

    @RequestMapping(method= RequestMethod.POST, path="/payments")
    public void payment(@RequestBody Payment payment);

    //@RequestMapping(method= RequestMethod.PUT, path="/payments") => 404 status Error : JPA Transaction Error
    @RequestMapping(method= RequestMethod.POST, path="/payments")
    public void paymentCancellation(@RequestBody Payment payment);
}