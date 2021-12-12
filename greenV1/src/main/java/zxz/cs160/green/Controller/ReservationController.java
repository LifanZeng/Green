package zxz.cs160.green.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import zxz.cs160.green.Model.Reservation;
import zxz.cs160.green.Model.User;
import zxz.cs160.green.Repository.UserRepository;
import zxz.cs160.green.Service.ReservationService;

import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

@Controller
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private UserRepository userRepository;

    /**
     * get the list of reservations
     * @param model
     * @return
     */
    @GetMapping("/reservations")
    public String list(Model model){
        List<Reservation> reservations = reservationService.findAllReservation();
        model.addAttribute("reservations", reservations);
        return "reservations";
    }

//    /**
//     * get the list of available reservations, for homepage
//     * @param model
//     * @return
//     */
//    @GetMapping("/index")
//    public String listAvailable(Model model){
//        List<Reservation> reservations = reservationService.findAllAvailableReservation();
//        model.addAttribute("reservations", reservations);
//        return "index";
//    }

    /**
     * get the list of reservations as provider
     * @param model
     * @return
     */
    @GetMapping("/reservations/p")

    public String listAsProvider(Model model){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> user = this.userRepository.findByUserName(username);
        List<Reservation> reservations = reservationService.findAllBySQLAsProvider(user.get().getId());
        model.addAttribute("reservations", reservations);
        return "reservations";
    }

    /**
     * get the list of reservations as customer
     * @param model
     * @return
     */
    @GetMapping("/reservations/c")
    public String listAsCustomer(Model model){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> user = this.userRepository.findByUserName(username);
        List<Reservation> reservations = reservationService.findAllBySQLAsCustomer(user.get().getId());
        model.addAttribute("reservations", reservations);
        return "reservations_shared";
    }




    /**
     * get a reservation's detail
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/reservations/{id}")
    public String detail(@PathVariable long id, Model model){
        //    Book bo=bookService.findOne(id);
//        Reservation reservation= reservationService.findAllReservation().get(0);

        Reservation reservation= reservationService.findOneBySQL(id).get(0);
        if (reservation == null){               //?
            System.out.println("bo is null");
            reservation = new Reservation();
        }
        model.addAttribute("reservation", reservation); // (String, Object)
        return "reservation"; //a name of a template
    }




    /*
    jump to input.html
     */
    @GetMapping("/reservations/input")
    public String inputPage(Model model){
        model.addAttribute("reservation", new Reservation());
        return "input";
    }


    /**
     * 提交一个书单，接收post请求   （submit）            ????????????????????可能有问题
     * @param
     * @return
     */
    @PostMapping("/reservations")
    public String post(@RequestParam long id, @RequestParam String startcity, @RequestParam String endcity,
                       @RequestParam Date startday, @RequestParam String starttime, @RequestParam String note, @RequestParam boolean available, Model model) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        java.util.Date date = null;
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> user = this.userRepository.findByUserName(username);
        long userId = user.get().getId();
        try {
             date = formatter.parse(starttime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Reservation reservation=new Reservation(id, userId, userId, startcity, endcity,           //id
                startday, new Time(date.getTime()), note, available);
        Reservation reservation1=reservationService.save(reservation);

        return "redirect:/reservations/p";
    }

    //跳转到更新页面
    @GetMapping("/reservations/{id}/input")
    public String inputEditPage(@PathVariable long id, Model model){
        Reservation reservation = reservationService.findOneBySQL(id).get(0);
        model.addAttribute("reservation", reservation);   ////??
        return "input";
    }



    //跳转到更新页面2
    @GetMapping("/reservations/{id}/update")
    public String updatePage(@PathVariable long id, Model model){
        Reservation reservation = reservationService.findOneBySQL(id).get(0);
        model.addAttribute("reservation", reservation);   ////??
        return "update";
    }

    /**
     * update2
     */
    @PostMapping("/update")
    public String update(@RequestParam long id, @RequestParam long provider_id, @RequestParam long customer_id, @RequestParam String startcity, @RequestParam String endcity,
                         @RequestParam Date startday, @RequestParam String starttime, @RequestParam String note, @RequestParam boolean available, Model model) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        java.util.Date date = null;
        try {
            date = formatter.parse(starttime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int reservation1=reservationService.updateBySQL(id, provider_id, customer_id, startcity, endcity,           //id
                startday, new Time(date.getTime()), note, available);
        return "redirect:/reservations/p";
    }

    //To make a reservation  11/27/2021
    @GetMapping("/reservation/{id}/mk_r1")
    public String makeReservation(@PathVariable long id, Model model) {
        Reservation reservation = reservationService.findOneBySQL(id).get(0);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> user0 = this.userRepository.findByUserName(username);
        reservation.setCustomer_id(user0.get().getId());
        int reservation3 = reservationService.makeReservation(reservation.getId(), user0.get().getId());///////
        model.addAttribute("reservation", reservation);
        return "m_reservation";//
    }


    /**
     * Start to Search by Start-City and End-City     11/26/2021
     */
    @GetMapping("/StartToSearch")
    public String testpage(){
        return "search_reservation";
    }

    /**
     * Search by Start-City and End-City     11/26/2021
     * @param model
     * @return
     */
    @GetMapping("/reservations/search")
    public String searchByStartcityAndEndcity(String startcity, String endcity, Model model) {
        List<Reservation> reservations = reservationService.searchByStartcityAndEndcity(startcity, endcity);
        model.addAttribute("reservations", reservations);
        return "reservations_search";
    }


    //To cancel a reservation  11/27/2021
    @GetMapping("/reservation/{id}/cancel")
    public String CancelReservation(@PathVariable long id, Model model) {
        Reservation reservation = reservationService.findOneBySQL(id).get(0);
        model.addAttribute("reservation", reservation);
        return "c_reservation";//
    }
    @GetMapping("/reservation/{id}/cancel2")
    public String CancelReservation2(@PathVariable long id, Model model) {
        Reservation reservation = reservationService.findOneBySQL(id).get(0);
        int reservation4 = reservationService.cancelReservation(id, reservation.getProvider_id());///////
        model.addAttribute("reservation", reservation);
        return "m_reservation";//
    }
}