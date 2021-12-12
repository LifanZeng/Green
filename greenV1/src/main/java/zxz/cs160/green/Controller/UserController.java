package zxz.cs160.green.Controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import zxz.cs160.green.Model.Reservation;
import zxz.cs160.green.Model.User;
import zxz.cs160.green.Repository.UserRepository;
import zxz.cs160.green.Service.ReservationService;
import zxz.cs160.green.Service.UserService;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Controller
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private UserRepository userRepository;

    //find All User
    @GetMapping("/alluser")
    public List<User> findAllUser(){
        return userService.findAllUser();
    }

//    @GetMapping("/user")
//    public String user() {
//        return ("<h1>Welcome User</h1>");
//    }
//
//    @GetMapping("/admin")
//    public String admin() {
//        return ("<h1>Welcome Admin</h1>");
//    }


    //find user detail by userId which is got from index.html 11/20/2021
    @GetMapping("/user/dt")
    public String findOneUserById(Model model){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> user0 = this.userRepository.findByUserName(username);
        //User user = userService.findOneUserByIdViaSQL(user0.get().getId());
        model.addAttribute("user", user0.get());
        return "user";
    }
    //find user detail by userId which is got from index.html 11/22/2021
    @GetMapping("/user/{id}/dt")
    public String findOneUser(@PathVariable long id, Model model){
        User user = userService.findOneUserByIdViaSQL(id);
        model.addAttribute("user", user);
        return "user";
    }

    /**user update 11/22/2021
     * Jump to the website user_update.html and get the data
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/user/{id}/update")
    public String userUpdate(@PathVariable long id, Model model){
        User user = userService.findOneUserByIdViaSQL(id);
        model.addAttribute("user", user);
        return "user_update";
    }

    /**
     * user update 11/21/2021
     * submit for update                           ??????????????????问题出在这里
     */
    @PostMapping("/user/{id}/update2")
    public String userUpdate2(@PathVariable long id, @RequestParam String firstname, @RequestParam String lastname, @RequestParam String cellphone, @RequestParam String address, @RequestParam String carbrand, @RequestParam String carmodel, @RequestParam String platenumber ){
        System.out.println("id= "+id+"; firstname= "+firstname +"; lastname="+lastname+ "; cellphone="+cellphone+ "; address="+address +"; carbrand="+carbrand+ "; carmodel="+carmodel+ "; platenumber= " +platenumber);
        int user1 = this.userRepository.userUpdateByIdViaSQL(id, firstname, lastname, cellphone, address, carbrand, carmodel, platenumber);
        System.out.println("user1="+user1);
        return "redirect:/user/{id}/dt";
    }



}


