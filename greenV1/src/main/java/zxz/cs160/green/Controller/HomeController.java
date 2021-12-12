package zxz.cs160.green.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import zxz.cs160.green.Model.Reservation;
import zxz.cs160.green.Service.ReservationService;

import java.util.List;

@Controller
public class HomeController {
    @Autowired
    private ReservationService reservationService;

    @GetMapping("/")
    public String listAvailable(Model model){
        List<Reservation> reservations = reservationService.findAllAvailableReservation();
        model.addAttribute("reservations", reservations);
        return "index";
    }


}
