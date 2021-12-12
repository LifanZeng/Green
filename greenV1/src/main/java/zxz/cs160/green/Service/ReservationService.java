package zxz.cs160.green.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import zxz.cs160.green.Email.EmailSender;
import zxz.cs160.green.Model.Reservation;
import zxz.cs160.green.Model.User;
import zxz.cs160.green.Repository.ReservationRepository;
import zxz.cs160.green.Repository.UserRepository;

import javax.transaction.Transactional;
import java.sql.Date;
import java.sql.Time;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    //    private final MyUserDetailsService myUserDetailsService;
//    private final EmailValidator emailValidator;
    @Autowired
    private EmailSender emailSender;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationRepository reservationRepository;
    //list all reservation, old
    public List<Reservation> findAllReservation(){
        return reservationRepository.findAllBySQL();
    }

    //list all available reservations, for Homepage
    public List<Reservation> findAllAvailableReservation(){
        return reservationRepository.findAllAvailableBySQL();
    }

    //Find All reservations as Provider
    public List<Reservation> findAllBySQLAsProvider(long provider_id){
        return reservationRepository.findAllBySQLAsProvider(provider_id);
    }

    //Find All reservations as customer
    public List<Reservation> findAllBySQLAsCustomer(long customer_id){
        return reservationRepository.findAllBySQLAsCustomer(customer_id);
    }

    //add new; update
    public Reservation save(Reservation reservation){
        return reservationRepository.save(reservation);
    }

//    //find one
//    public Reservation findOne(long id){
//        return reservationRepository.findById(id).get();
//    }

    //find one by id via SQL
    @Transactional
    public List<Reservation> findOneBySQL(long id){
        return reservationRepository.findOneBySQL(id);
    }

    //update
    @Transactional
    public int updateBySQL(long id, long provider_id, long customer_id, String startcity, String endcity, Date startday, Time starttime, String note, boolean available){
        return reservationRepository.updateBySQL(id, provider_id, customer_id, startcity, endcity, startday, starttime, note, available);
    }

    //To make a reservation  11/24/2021
    @Transactional
    public int makeReservation(long id, long customer_id) {
        User customer = userRepository.findOneUserByIdViaSQL(customer_id);      //拿到需要发邮件的用户邮件地址
        Reservation reservation = reservationService.findOneBySQL(id).get(0);   //拿到当前行程的信息

        if(!reservation.isAvailable()){
            throw new IllegalStateException("This reservation is occupied!");
        }else if (customer_id==reservation.getProvider_id()){
            throw new IllegalStateException("You can't join your own trip!");
        }

        long provider_id = reservation.getProvider_id();                        //拿到行程发起者的信息
        User provider = userRepository.findOneUserByIdViaSQL(provider_id);      //拿到行程发起者的信息

        int stats = reservationRepository.makeReservation(id, customer_id);

        emailSender.send(
                customer.getEmail(),
                buildEmailMakeC(customer.getFirstname(), provider, reservation), "Trip Confirmation");

        emailSender.send(
                provider.getEmail(),
                buildEmailMakeP(provider.getFirstname(), customer, reservation), "Booking Noticed");

        return stats;
    }

    //Search by Start-City and End-City     11/26/2021
    public List<Reservation> searchByStartcityAndEndcity(String startcity, String endcity) {
        return reservationRepository.searchByStartcityAndEndcity(startcity, endcity);
    }

    //To cancel a reservation  11/27/2021
    @Transactional
    public int cancelReservation(long id, long provider_id) {
        Reservation reservation = reservationService.findOneBySQL(id).get(0);   //拿到当前行程的信息
        User customer = userRepository.findOneUserByIdViaSQL(reservation.getCustomer_id());      //拿到乘客邮件地址
        User provider = userRepository.findOneUserByIdViaSQL(reservation.getProvider_id());      //拿到发起人邮件地址
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> currentUser = this.userRepository.findByUserName(username);               //当前登录用户
        if(reservation.isAvailable()){
            throw new IllegalStateException("This trip has not been booked yet!");
        }else if(currentUser.get().getId()!= reservation.getCustomer_id()){
            throw new IllegalStateException("You can't cancel other people's booking!");
        }
        int stats = reservationRepository.cancelReservation(id, provider_id);

        emailSender.send(
                customer.getEmail(),
                buildEmailCancelC(customer.getFirstname(), reservation), "Trip Cancellation");

        emailSender.send(
                provider.getEmail(),
                buildEmailCancelP(provider.getFirstname(), reservation), "Cancellation Noticed");

        return stats;
    }

    private String buildEmailMakeC(String cName, User provider, Reservation reservation) {
        return "<!DOCTYPE html>\n" +
                "\n" +
                "<html lang=\"en\" xmlns:o=\"urn:schemas-microsoft-com:office:office\" xmlns:v=\"urn:schemas-microsoft-com:vml\">\n" +
                "<head>\n" +
                "<title></title>\n" +
                "<meta charset=\"utf-8\"/>\n" +
                "<meta content=\"width=device-width, initial-scale=1.0\" name=\"viewport\"/>\n" +
                "<!--[if mso]><xml><o:OfficeDocumentSettings><o:PixelsPerInch>96</o:PixelsPerInch><o:AllowPNG/></o:OfficeDocumentSettings></xml><![endif]-->\n" +
                "<!--[if !mso]><!-->\n" +
                "<link href=\"https://fonts.googleapis.com/css?family=Open+Sans\" rel=\"stylesheet\" type=\"text/css\"/>\n" +
                "<link href=\"https://fonts.googleapis.com/css?family=Lato\" rel=\"stylesheet\" >\n" +
                "<!--<![endif]-->\n" +
                "<style>\n" +
                "\t\t* {\n" +
                "\t\t\tbox-sizing: border-box;\n" +
                "\t\t}\n" +
                "\n" +
                "\t\tbody {\n" +
                "\t\t\tmargin: 0;\n" +
                "\t\t\tpadding: 0;\n" +
                "\t\t}\n" +
                "\n" +
                "\t\ta[x-apple-data-detectors] {\n" +
                "\t\t\tcolor: inherit !important;\n" +
                "\t\t\ttext-decoration: inherit !important;\n" +
                "\t\t}\n" +
                "\n" +
                "\t\t#MessageViewBody a {\n" +
                "\t\t\tcolor: inherit;\n" +
                "\t\t\ttext-decoration: none;\n" +
                "\t\t}\n" +
                "\n" +
                "\t\tp {\n" +
                "\t\t\tline-height: inherit\n" +
                "\t\t}\n" +
                "\n" +
                "\t\t@media (max-width:620px) {\n" +
                "\t\t\t.icons-inner {\n" +
                "\t\t\t\ttext-align: center;\n" +
                "\t\t\t}\n" +
                "\n" +
                "\t\t\t.icons-inner td {\n" +
                "\t\t\t\tmargin: 0 auto;\n" +
                "\t\t\t}\n" +
                "\n" +
                "\t\t\t.row-content {\n" +
                "\t\t\t\twidth: 100% !important;\n" +
                "\t\t\t}\n" +
                "\n" +
                "\t\t\t.stack .column {\n" +
                "\t\t\t\twidth: 100%;\n" +
                "\t\t\t\tdisplay: block;\n" +
                "\t\t\t}\n" +
                "\t\t}\n" +
                "\t</style>\n" +
                "</head>\n" +
                "<body style=\"background-color: #FFFFFF; margin: 0; padding: 0; -webkit-text-size-adjust: none; text-size-adjust: none;\">\n" +
                "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"nl-container\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #FFFFFF;\" width=\"100%\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td>\n" +
                "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"row row-1\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #f7f6f5;\" width=\"100%\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td>\n" +
                "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"row row-2\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #f7f6f5;\" width=\"100%\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td>\n" +
                "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"row-content stack\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #fff; color: #000000; width: 600px;\" width=\"600\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td class=\"column\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; font-weight: 400; text-align: left; vertical-align: top; padding-top: 0px; padding-bottom: 0px; border-top: 0px; border-right: 0px; border-bottom: 0px; border-left: 0px;\" width=\"100%\">\n" +
                "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"heading_block\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\" width=\"100%\">\n" +
                "<tr>\n" +
                "<td style=\"text-align:center;width:100%;padding-top:35px;\">\n" +
                "<h1 style=\"margin: 0; color: #072b52; direction: ltr; font-family: 'Lora', Georgia, serif; font-size: 50px; font-weight: normal; letter-spacing: 1px; line-height: 120%; text-align: center; margin-top: 0; margin-bottom: 0;\"><strong>You Are All Set.</strong></h1>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "    \n" +
                "    \n" +
                "    \n" +
                "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"row row-3\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #f7f6f5;\" width=\"100%\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td>\n" +
                "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"row-content stack\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #fff; color: #000000; width: 600px;\" width=\"600\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td class=\"column\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; font-weight: 400; text-align: left; vertical-align: top; padding-top: 0px; padding-bottom: 0px; border-top: 0px; border-right: 0px; border-bottom: 0px; border-left: 0px;\" width=\"100%\">\n" +
                "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"text_block\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; word-break: break-word;\" width=\"100%\">\n" +
                "<tr>\n" +
                "<td style=\"padding-bottom:40px;padding-left:15px;padding-right:15px;padding-top:40px;\">\n" +
                "<div style=\"font-family: Tahoma, Verdana, sans-serif\">\n" +
                "<div style=\"font-size: 12px; font-family: 'Lato', Tahoma, Verdana, Segoe, sans-serif; mso-line-height-alt: 18px; color: #222222; line-height: 1.5;\">\n" +
                "    <p style=\"margin: 0; font-size: 16px; text-align: left; mso-line-height-alt: 24px;\"><span style=\"font-size:16px;\"><strong>Hey, "+cName+"</strong></span></p>\n" +
                "<p style=\"margin: 0; font-size: 16px; text-align: left; mso-line-height-alt: 24px;\"><span style=\"font-size:16px;\">We have your upcoming trip details here: </span></p>\n" +
                "<!--这里写trip的信息-->    \n" +
                "<p style=\"margin: 0; font-size: 16px; text-align: left; mso-line-height-alt: 18px;\"> </p>\n" +
                "<p style=\"margin: 0; font-size: 16px; text-align: left; mso-line-height-alt: 24px;\"><strong>Depart City: </strong><span style=\"font-size:16px;\">"+reservation.getStartcity()+"</span></p>\n" +
                "<p style=\"margin: 0; font-size: 16px; text-align: left; mso-line-height-alt: 24px;\"><strong>Destination: </strong><span style=\"font-size:16px;\">"+reservation.getEndcity()+"</span></p>\n" +
                "<p style=\"margin: 0; font-size: 16px; text-align: left; mso-line-height-alt: 24px;\"><strong>Depart Day: </strong><span style=\"font-size:16px;\">"+reservation.getStartday()+"</span></p>\n" +
                "<p style=\"margin: 0; font-size: 16px; text-align: left; mso-line-height-alt: 24px;\"><strong>Depart Time: </strong><span style=\"font-size:16px;\">"+reservation.getStarttime()+"</span></p>\n" +
                "<p style=\"margin: 0; font-size: 16px; text-align: left; mso-line-height-alt: 24px;\"><strong>Special Instruction: </strong><span style=\"font-size:16px;\">"+reservation.getNote()+"</span></p>\n" +
                "\n" +
                "<!--这里写trip provider的信息-->   \n" +
                "<p style=\"margin: 0; font-size: 16px; text-align: left; mso-line-height-alt: 18px;\"> </p>\n" +
                "<p style=\"margin: 0; font-size: 16px; text-align: left; mso-line-height-alt: 24px;\">The trip listed above is provided by <span style=\"font-size:16px;\"><strong>"+provider.getFirstname()+"</strong></span></p>\n" +
                "<p style=\"margin: 0; font-size: 16px; text-align: left; mso-line-height-alt: 24px;\"><strong>Car Maker: </strong><span style=\"font-size:16px;\">"+provider.getCarbrand()+"</span></p>\n" +
                "<p style=\"margin: 0; font-size: 16px; text-align: left; mso-line-height-alt: 24px;\"><strong>Car Model: </strong><span style=\"font-size:16px;\">"+provider.getCarmodel()+"</span></p>\n" +
                "<p style=\"margin: 0; font-size: 16px; text-align: left; mso-line-height-alt: 24px;\"><strong>Contact Host: </strong><span style=\"font-size:16px;\">"+provider.getEmail()+"</span></p>\n" +
                "<p style=\"margin: 0; font-size: 16px; text-align: left; mso-line-height-alt: 24px;\"><span style=\"font-size:16px;\">Hope you enjoy your trip, and we hope to see you again soon.</span></p>\n" +
                "\n" +
                "</div>\n" +
                "</div>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "     \n" +
                "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"row row-4\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #f7f6f5;\" width=\"100%\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td>\n" +
                "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"row-content stack\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #fff; color: #000000; width: 600px;\" width=\"600\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td class=\"column\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; font-weight: 400; text-align: left; vertical-align: top; padding-top: 0px; padding-bottom: 0px; border-top: 0px; border-right: 0px; border-bottom: 0px; border-left: 0px;\" width=\"100%\">\n" +
                "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"heading_block\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\" width=\"100%\">\n" +
                "<tr>\n" +
                "<td style=\"padding-bottom:35px;padding-left:5px;padding-right:5px;padding-top:30px;text-align:center;width:100%;\">\n" +
                "<h2 style=\"margin: 0; color: #072b52; direction: ltr; font-size: 19px; font-weight: normal; letter-spacing: 1px; line-height: 120%; text-align: center; margin-top: 0; margin-bottom: 0;\">Contact Us if You Have Any Question: <strong>(800)629-3971</strong></h2>\n" +
                "<h1 style=\"margin: 0; color: #072b52; direction: ltr; font-size: 19px; font-weight: normal; letter-spacing: 1px; line-height: 120%; text-align: center; margin-top: 0; margin-bottom: 0;\"> <strong>&</strong></h1>\n" +
                "<h2 style=\"margin: 0; color: #072b52; direction: ltr; font-size: 19px; font-weight: normal; letter-spacing: 1px; line-height: 120%; text-align: center; margin-top: 0; margin-bottom: 0;\">Keep Sharing with Us by Tagging <strong>#GreenTrips</strong></h2>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"row row-5\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #f7f6f5;\" width=\"100%\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td>\n" +
                "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"row-content stack\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #fff; color: #000000; width: 600px;\" width=\"600\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td class=\"column\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; font-weight: 400; text-align: left; vertical-align: top; padding-top: 5px; padding-bottom: 5px; border-top: 0px; border-right: 0px; border-bottom: 0px; border-left: 0px;\" width=\"100%\">\n" +
                "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"html_block\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\" width=\"100%\">\n" +
                "<tr>\n" +
                "<td>\n" +
                "<div align=\"center\" style=\"font-family:Arial, Helvetica Neue, Helvetica, sans-serif;\"><div style=\"height:30px;\"> </div></div>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</table>\n" +
                "\n" +
                "    \n" +
                "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"html_block\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\" width=\"100%\">\n" +
                "<tr>\n" +
                "<td>\n" +
                "<div align=\"center\" style=\"font-family:Arial, Helvetica Neue, Helvetica, sans-serif;\"><div style=\"height:30px;\"> </div></div>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</table>\n" +
                "\n" +
                "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"html_block\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\" width=\"100%\">\n" +
                "<tr>\n" +
                "<td>\n" +
                "<div align=\"center\" style=\"font-family:Arial, Helvetica Neue, Helvetica, sans-serif;\"><div style=\"height:30px;\"> </div></div>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"row row-6\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #f7f6f5;\" width=\"100%\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td>\n" +
                "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"row-content stack\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #a8d072; color: #000000; width: 600px;\" width=\"600\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td class=\"column\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; font-weight: 400; text-align: left; vertical-align: top; padding-top: 5px; padding-bottom: 5px; border-top: 0px; border-right: 0px; border-bottom: 0px; border-left: 0px;\" width=\"100%\">\n" +
                "<table border=\"0\" cellpadding=\"10\" cellspacing=\"0\" class=\"text_block\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; word-break: break-word;\" width=\"100%\">\n" +
                "<tr>\n" +
                "<td>\n" +
                "<div style=\"font-family: Tahoma, Verdana, sans-serif\">\n" +
                "<div style=\"font-size: 12px; font-family: 'Lato', Tahoma, Verdana, Segoe, sans-serif; mso-line-height-alt: 14.399999999999999px; color: #f7f6f5; line-height: 1.2;\">\n" +
                "<p style=\"margin: 0; mso-line-height-alt: 14.399999999999999px;\"> </p>\n" +
                "<p style=\"margin: 0; text-align: center;\"><a href=\"http://www.example.com/\" rel=\"noopener\" style=\"text-decoration: underline; color: #f7f6f5;\" target=\"_blank\" title=\"http://www.example.com/\">Terms &amp; Conditions</a></p>\n" +
                "<p style=\"margin: 0; text-align: center;\">Your access to, and all of your use of the Website, Products, and/or Services must be lawful and must be in compliance with these Terms, and any other agreement between you and Usabilla.</p>\n" +
                "<p style=\"margin: 0; font-size: 12px; text-align: center;\"><span style=\"color:#c0c0c0;\"><br/><br/></span></p>\n" +
                "<p style=\"margin: 0; text-align: center;\">© Copyright 2021. All Rights Reserved.</p>\n" +
                "<p style=\"margin: 0; text-align: center;\"><a href=\"http://www.example.com/\" rel=\"noopener\" style=\"color: #f7f6f5;\" target=\"_blank\" title=\"http://www.example.com\">Manage Preferences</a> | <a href=\"http://www.example.com/\" rel=\"noopener\" style=\"color: #f7f6f5;\" target=\"_blank\" title=\"http://www.example.com\">Unsubscribe</a></p>\n" +
                "<p style=\"margin: 0; font-size: 12px; text-align: center;\"><span style=\"color:#c0c0c0;\"> </span></p>\n" +
                "</div>\n" +
                "</div>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"row row-7\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\" width=\"100%\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td>\n" +
                "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"row-content stack\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; color: #000000; width: 600px;\" width=\"600\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td class=\"column\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; font-weight: 400; text-align: left; vertical-align: top; padding-top: 5px; padding-bottom: 5px; border-top: 0px; border-right: 0px; border-bottom: 0px; border-left: 0px;\" width=\"100%\">\n" +
                "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"icons_block\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\" width=\"100%\">\n" +
                "<tr>\n" +
                "<td style=\"color:#9d9d9d;font-family:inherit;font-size:15px;padding-bottom:5px;padding-top:5px;text-align:center;\">\n" +
                "<table cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\" width=\"100%\">\n" +
                "<tr>\n" +
                "<td style=\"text-align:center;\">\n" +
                "<!--[if vml]><table align=\"left\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"display:inline-block;padding-left:0px;padding-right:0px;mso-table-lspace: 0pt;mso-table-rspace: 0pt;\"><![endif]-->\n" +
                "<!--[if !vml]><!-->\n" +
                "<table cellpadding=\"0\" cellspacing=\"0\" class=\"icons-inner\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; display: inline-block; margin-right: -4px; padding-left: 0px; padding-right: 0px;\">\n" +
                "<!--<![endif]-->\n" +
                "<tr>\n" +
                "<td style=\"text-align:center;padding-top:5px;padding-bottom:5px;padding-left:5px;padding-right:6px;\"></td>\n" +
                "<td style=\"font-family:Arial, Helvetica Neue, Helvetica, sans-serif;font-size:15px;color:#9d9d9d;vertical-align:middle;text-align:center;\"><a href=\"https://www.designedwithbee.com/\" style=\"color:#9d9d9d;text-decoration:none;\">Designed with Green Team</a></td>\n" +
                "</tr>\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "    \n" +
                "    \n" +
                "    \n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</table><!-- End -->\n" +
                "</body>\n" +
                "</html>";
    }

    private String buildEmailCancelC(String cName, Reservation reservation) {
        return "<!DOCTYPE html>\n" +
                "\n" +
                "<html lang=\"en\" xmlns:o=\"urn:schemas-microsoft-com:office:office\" xmlns:v=\"urn:schemas-microsoft-com:vml\">\n" +
                "<head>\n" +
                "<title></title>\n" +
                "<meta charset=\"utf-8\"/>\n" +
                "<meta content=\"width=device-width, initial-scale=1.0\" name=\"viewport\"/>\n" +
                "<!--[if mso]><xml><o:OfficeDocumentSettings><o:PixelsPerInch>96</o:PixelsPerInch><o:AllowPNG/></o:OfficeDocumentSettings></xml><![endif]-->\n" +
                "<!--[if !mso]><!-->\n" +
                "<link href=\"https://fonts.googleapis.com/css?family=Open+Sans\" rel=\"stylesheet\" type=\"text/css\"/>\n" +
                "<link href=\"https://fonts.googleapis.com/css?family=Lato\" rel=\"stylesheet\" >\n" +
                "<!--<![endif]-->\n" +
                "<style>\n" +
                "\t\t* {\n" +
                "\t\t\tbox-sizing: border-box;\n" +
                "\t\t}\n" +
                "\n" +
                "\t\tbody {\n" +
                "\t\t\tmargin: 0;\n" +
                "\t\t\tpadding: 0;\n" +
                "\t\t}\n" +
                "\n" +
                "\t\ta[x-apple-data-detectors] {\n" +
                "\t\t\tcolor: inherit !important;\n" +
                "\t\t\ttext-decoration: inherit !important;\n" +
                "\t\t}\n" +
                "\n" +
                "\t\t#MessageViewBody a {\n" +
                "\t\t\tcolor: inherit;\n" +
                "\t\t\ttext-decoration: none;\n" +
                "\t\t}\n" +
                "\n" +
                "\t\tp {\n" +
                "\t\t\tline-height: inherit\n" +
                "\t\t}\n" +
                "\n" +
                "\t\t@media (max-width:620px) {\n" +
                "\t\t\t.icons-inner {\n" +
                "\t\t\t\ttext-align: center;\n" +
                "\t\t\t}\n" +
                "\n" +
                "\t\t\t.icons-inner td {\n" +
                "\t\t\t\tmargin: 0 auto;\n" +
                "\t\t\t}\n" +
                "\n" +
                "\t\t\t.row-content {\n" +
                "\t\t\t\twidth: 100% !important;\n" +
                "\t\t\t}\n" +
                "\n" +
                "\t\t\t.stack .column {\n" +
                "\t\t\t\twidth: 100%;\n" +
                "\t\t\t\tdisplay: block;\n" +
                "\t\t\t}\n" +
                "\t\t}\n" +
                "\t</style>\n" +
                "</head>\n" +
                "<body style=\"background-color: #FFFFFF; margin: 0; padding: 0; -webkit-text-size-adjust: none; text-size-adjust: none;\">\n" +
                "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"nl-container\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #FFFFFF;\" width=\"100%\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td>\n" +
                "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"row row-1\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #f7f6f5;\" width=\"100%\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td>\n" +
                "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"row row-2\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #f7f6f5;\" width=\"100%\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td>\n" +
                "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"row-content stack\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #fff; color: #000000; width: 600px;\" width=\"600\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td class=\"column\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; font-weight: 400; text-align: left; vertical-align: top; padding-top: 0px; padding-bottom: 0px; border-top: 0px; border-right: 0px; border-bottom: 0px; border-left: 0px;\" width=\"100%\">\n" +
                "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"heading_block\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\" width=\"100%\">\n" +
                "<tr>\n" +
                "<td style=\"text-align:center;width:100%;padding-top:35px;\">\n" +
                "<h1 style=\"margin: 0; color: #072b52; direction: ltr; font-family: 'Lora', Georgia, serif; font-size: 45px; font-weight: normal; letter-spacing: 1px; line-height: 120%; text-align: center; margin-top: 0; margin-bottom: 0;\"><strong>Cancellation Confirmed.</strong></h1>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "    \n" +
                "    \n" +
                "    \n" +
                "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"row row-3\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #f7f6f5;\" width=\"100%\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td>\n" +
                "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"row-content stack\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #fff; color: #000000; width: 600px;\" width=\"600\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td class=\"column\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; font-weight: 400; text-align: left; vertical-align: top; padding-top: 0px; padding-bottom: 0px; border-top: 0px; border-right: 0px; border-bottom: 0px; border-left: 0px;\" width=\"100%\">\n" +
                "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"text_block\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; word-break: break-word;\" width=\"100%\">\n" +
                "<tr>\n" +
                "<td style=\"padding-bottom:40px;padding-left:15px;padding-right:15px;padding-top:40px;\">\n" +
                "<div style=\"font-family: Tahoma, Verdana, sans-serif\">\n" +
                "<div style=\"font-size: 12px; font-family: 'Lato', Tahoma, Verdana, Segoe, sans-serif; mso-line-height-alt: 18px; color: #222222; line-height: 1.5;\">\n" +
                "    <p style=\"margin: 0; font-size: 16px; text-align: left; mso-line-height-alt: 24px;\"><span style=\"font-size:16px;\"><strong>Hey, "+cName+"</strong></span></p>\n" +
                "<p style=\"margin: 0; font-size: 16px; text-align: left; mso-line-height-alt: 24px;\"><span style=\"font-size:16px;\">The following trip has been cancelled.</span></p>\n" +
                "<!--这里写trip的信息-->    \n" +
                "<p style=\"margin: 0; font-size: 16px; text-align: left; mso-line-height-alt: 18px;\"> </p>\n" +
                "<p style=\"margin: 0; font-size: 16px; text-align: left; mso-line-height-alt: 24px;\"><strong>Depart City: </strong><span style=\"font-size:16px;\">"+reservation.getStartcity()+"</span></p>\n" +
                "<p style=\"margin: 0; font-size: 16px; text-align: left; mso-line-height-alt: 24px;\"><strong>Destination: </strong><span style=\"font-size:16px;\">"+reservation.getEndcity()+"</span></p>\n" +
                "<p style=\"margin: 0; font-size: 16px; text-align: left; mso-line-height-alt: 24px;\"><strong>Depart Day: </strong><span style=\"font-size:16px;\">"+reservation.getStartday()+"</span></p>\n" +
                "<p style=\"margin: 0; font-size: 16px; text-align: left; mso-line-height-alt: 24px;\"><strong>Depart Time: </strong><span style=\"font-size:16px;\">"+reservation.getStarttime()+"</span></p>\n" +
                "<p style=\"margin: 0; font-size: 16px; text-align: left; mso-line-height-alt: 24px;\"><strong>Special Instruction: </strong><span style=\"font-size:16px;\">"+reservation.getNote()+"</span></p>\n" +
                "<p style=\"margin: 0; font-size: 16px; text-align: left; mso-line-height-alt: 18px;\"> </p>\n" +
                "\n" +
                "    <p style=\"margin: 0; font-size: 16px; text-align: left; mso-line-height-alt: 24px;\"><span style=\"font-size:16px;\">If you would like to explore more, please visit our <a href=\"http://localhost:8080\">site</a>.</span></p>\n" +
                " \n" +
                "\n" +
                "</div>\n" +
                "</div>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "     \n" +
                "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"row row-4\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #f7f6f5;\" width=\"100%\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td>\n" +
                "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"row-content stack\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #fff; color: #000000; width: 600px;\" width=\"600\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td class=\"column\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; font-weight: 400; text-align: left; vertical-align: top; padding-top: 0px; padding-bottom: 0px; border-top: 0px; border-right: 0px; border-bottom: 0px; border-left: 0px;\" width=\"100%\">\n" +
                "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"heading_block\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\" width=\"100%\">\n" +
                "<tr>\n" +
                "<td style=\"padding-bottom:35px;padding-left:5px;padding-right:5px;padding-top:30px;text-align:center;width:100%;\">\n" +
                "<h2 style=\"margin: 0; color: #072b52; direction: ltr; font-size: 19px; font-weight: normal; letter-spacing: 1px; line-height: 120%; text-align: center; margin-top: 0; margin-bottom: 0;\">Contact Us if You Have Any Question: <strong>(800)629-3971</strong></h2>\n" +
                "<h1 style=\"margin: 0; color: #072b52; direction: ltr; font-size: 19px; font-weight: normal; letter-spacing: 1px; line-height: 120%; text-align: center; margin-top: 0; margin-bottom: 0;\"> <strong>&</strong></h1>\n" +
                "<h2 style=\"margin: 0; color: #072b52; direction: ltr; font-size: 19px; font-weight: normal; letter-spacing: 1px; line-height: 120%; text-align: center; margin-top: 0; margin-bottom: 0;\">Keep Sharing with Us by Tagging <strong>#GreenTrips</strong></h2>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"row row-5\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #f7f6f5;\" width=\"100%\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td>\n" +
                "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"row-content stack\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #fff; color: #000000; width: 600px;\" width=\"600\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td class=\"column\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; font-weight: 400; text-align: left; vertical-align: top; padding-top: 5px; padding-bottom: 5px; border-top: 0px; border-right: 0px; border-bottom: 0px; border-left: 0px;\" width=\"100%\">\n" +
                "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"html_block\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\" width=\"100%\">\n" +
                "<tr>\n" +
                "<td>\n" +
                "<div align=\"center\" style=\"font-family:Arial, Helvetica Neue, Helvetica, sans-serif;\"><div style=\"height:30px;\"> </div></div>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</table>\n" +
                "\n" +
                "    \n" +
                "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"html_block\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\" width=\"100%\">\n" +
                "<tr>\n" +
                "<td>\n" +
                "<div align=\"center\" style=\"font-family:Arial, Helvetica Neue, Helvetica, sans-serif;\"><div style=\"height:30px;\"> </div></div>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</table>\n" +
                "\n" +
                "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"html_block\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\" width=\"100%\">\n" +
                "<tr>\n" +
                "<td>\n" +
                "<div align=\"center\" style=\"font-family:Arial, Helvetica Neue, Helvetica, sans-serif;\"><div style=\"height:30px;\"> </div></div>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"row row-6\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #f7f6f5;\" width=\"100%\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td>\n" +
                "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"row-content stack\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #a8d072; color: #000000; width: 600px;\" width=\"600\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td class=\"column\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; font-weight: 400; text-align: left; vertical-align: top; padding-top: 5px; padding-bottom: 5px; border-top: 0px; border-right: 0px; border-bottom: 0px; border-left: 0px;\" width=\"100%\">\n" +
                "<table border=\"0\" cellpadding=\"10\" cellspacing=\"0\" class=\"text_block\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; word-break: break-word;\" width=\"100%\">\n" +
                "<tr>\n" +
                "<td>\n" +
                "<div style=\"font-family: Tahoma, Verdana, sans-serif\">\n" +
                "<div style=\"font-size: 12px; font-family: 'Lato', Tahoma, Verdana, Segoe, sans-serif; mso-line-height-alt: 14.399999999999999px; color: #f7f6f5; line-height: 1.2;\">\n" +
                "<p style=\"margin: 0; mso-line-height-alt: 14.399999999999999px;\"> </p>\n" +
                "<p style=\"margin: 0; text-align: center;\"><a href=\"http://www.example.com/\" rel=\"noopener\" style=\"text-decoration: underline; color: #f7f6f5;\" target=\"_blank\" title=\"http://www.example.com/\">Terms &amp; Conditions</a></p>\n" +
                "<p style=\"margin: 0; text-align: center;\">Your access to, and all of your use of the Website, Products, and/or Services must be lawful and must be in compliance with these Terms, and any other agreement between you and Usabilla.</p>\n" +
                "<p style=\"margin: 0; font-size: 12px; text-align: center;\"><span style=\"color:#c0c0c0;\"><br/><br/></span></p>\n" +
                "<p style=\"margin: 0; text-align: center;\">© Copyright 2021. All Rights Reserved.</p>\n" +
                "<p style=\"margin: 0; text-align: center;\"><a href=\"http://www.example.com/\" rel=\"noopener\" style=\"color: #f7f6f5;\" target=\"_blank\" title=\"http://www.example.com\">Manage Preferences</a> | <a href=\"http://www.example.com/\" rel=\"noopener\" style=\"color: #f7f6f5;\" target=\"_blank\" title=\"http://www.example.com\">Unsubscribe</a></p>\n" +
                "<p style=\"margin: 0; font-size: 12px; text-align: center;\"><span style=\"color:#c0c0c0;\"> </span></p>\n" +
                "</div>\n" +
                "</div>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"row row-7\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\" width=\"100%\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td>\n" +
                "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"row-content stack\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; color: #000000; width: 600px;\" width=\"600\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td class=\"column\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; font-weight: 400; text-align: left; vertical-align: top; padding-top: 5px; padding-bottom: 5px; border-top: 0px; border-right: 0px; border-bottom: 0px; border-left: 0px;\" width=\"100%\">\n" +
                "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"icons_block\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\" width=\"100%\">\n" +
                "<tr>\n" +
                "<td style=\"color:#9d9d9d;font-family:inherit;font-size:15px;padding-bottom:5px;padding-top:5px;text-align:center;\">\n" +
                "<table cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\" width=\"100%\">\n" +
                "<tr>\n" +
                "<td style=\"text-align:center;\">\n" +
                "<!--[if vml]><table align=\"left\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"display:inline-block;padding-left:0px;padding-right:0px;mso-table-lspace: 0pt;mso-table-rspace: 0pt;\"><![endif]-->\n" +
                "<!--[if !vml]><!-->\n" +
                "<table cellpadding=\"0\" cellspacing=\"0\" class=\"icons-inner\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; display: inline-block; margin-right: -4px; padding-left: 0px; padding-right: 0px;\">\n" +
                "<!--<![endif]-->\n" +
                "<tr>\n" +
                "<td style=\"text-align:center;padding-top:5px;padding-bottom:5px;padding-left:5px;padding-right:6px;\"></td>\n" +
                "<td style=\"font-family:Arial, Helvetica Neue, Helvetica, sans-serif;font-size:15px;color:#9d9d9d;vertical-align:middle;letter-spacing:undefined;text-align:center;\"><a href=\"https://www.designedwithbee.com/\" style=\"color:#9d9d9d;text-decoration:none;\">Designed with Green Team</a></td>\n" +
                "</tr>\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "    \n" +
                "    \n" +
                "    \n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</table><!-- End -->\n" +
                "</body>\n" +
                "</html>";
    }

    private String buildEmailMakeP(String pName,User customer, Reservation reservation) {
        return "<!DOCTYPE html>\n" +
                "\n" +
                "<html lang=\"en\" xmlns:o=\"urn:schemas-microsoft-com:office:office\" xmlns:v=\"urn:schemas-microsoft-com:vml\">\n" +
                "<head>\n" +
                "<title></title>\n" +
                "<meta charset=\"utf-8\"/>\n" +
                "<meta content=\"width=device-width, initial-scale=1.0\" name=\"viewport\"/>\n" +
                "<!--[if mso]><xml><o:OfficeDocumentSettings><o:PixelsPerInch>96</o:PixelsPerInch><o:AllowPNG/></o:OfficeDocumentSettings></xml><![endif]-->\n" +
                "<!--[if !mso]><!-->\n" +
                "<link href=\"https://fonts.googleapis.com/css?family=Open+Sans\" rel=\"stylesheet\" type=\"text/css\"/>\n" +
                "<link href=\"https://fonts.googleapis.com/css?family=Lato\" rel=\"stylesheet\" >\n" +
                "<!--<![endif]-->\n" +
                "<style>\n" +
                "\t\t* {\n" +
                "\t\t\tbox-sizing: border-box;\n" +
                "\t\t}\n" +
                "\n" +
                "\t\tbody {\n" +
                "\t\t\tmargin: 0;\n" +
                "\t\t\tpadding: 0;\n" +
                "\t\t}\n" +
                "\n" +
                "\t\ta[x-apple-data-detectors] {\n" +
                "\t\t\tcolor: inherit !important;\n" +
                "\t\t\ttext-decoration: inherit !important;\n" +
                "\t\t}\n" +
                "\n" +
                "\t\t#MessageViewBody a {\n" +
                "\t\t\tcolor: inherit;\n" +
                "\t\t\ttext-decoration: none;\n" +
                "\t\t}\n" +
                "\n" +
                "\t\tp {\n" +
                "\t\t\tline-height: inherit\n" +
                "\t\t}\n" +
                "\n" +
                "\t\t@media (max-width:620px) {\n" +
                "\t\t\t.icons-inner {\n" +
                "\t\t\t\ttext-align: center;\n" +
                "\t\t\t}\n" +
                "\n" +
                "\t\t\t.icons-inner td {\n" +
                "\t\t\t\tmargin: 0 auto;\n" +
                "\t\t\t}\n" +
                "\n" +
                "\t\t\t.row-content {\n" +
                "\t\t\t\twidth: 100% !important;\n" +
                "\t\t\t}\n" +
                "\n" +
                "\t\t\t.stack .column {\n" +
                "\t\t\t\twidth: 100%;\n" +
                "\t\t\t\tdisplay: block;\n" +
                "\t\t\t}\n" +
                "\t\t}\n" +
                "\t</style>\n" +
                "</head>\n" +
                "<body style=\"background-color: #FFFFFF; margin: 0; padding: 0; -webkit-text-size-adjust: none; text-size-adjust: none;\">\n" +
                "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"nl-container\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #FFFFFF;\" width=\"100%\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td>\n" +
                "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"row row-1\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #f7f6f5;\" width=\"100%\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td>\n" +
                "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"row row-2\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #f7f6f5;\" width=\"100%\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td>\n" +
                "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"row-content stack\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #fff; color: #000000; width: 600px;\" width=\"600\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td class=\"column\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; font-weight: 400; text-align: left; vertical-align: top; padding-top: 0px; padding-bottom: 0px; border-top: 0px; border-right: 0px; border-bottom: 0px; border-left: 0px;\" width=\"100%\">\n" +
                "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"heading_block\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\" width=\"100%\">\n" +
                "<tr>\n" +
                "<td style=\"text-align:center;width:100%;padding-top:35px;\">\n" +
                "<h1 style=\"margin: 0; color: #072b52; direction: ltr; font-family: 'Lora', Georgia, serif; font-size: 50px; font-weight: normal; letter-spacing: 1px; line-height: 120%; text-align: center; margin-top: 0; margin-bottom: 0;\"><strong>Booking Noticed.</strong></h1>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "    \n" +
                "    \n" +
                "    \n" +
                "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"row row-3\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #f7f6f5;\" width=\"100%\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td>\n" +
                "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"row-content stack\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #fff; color: #000000; width: 600px;\" width=\"600\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td class=\"column\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; font-weight: 400; text-align: left; vertical-align: top; padding-top: 0px; padding-bottom: 0px; border-top: 0px; border-right: 0px; border-bottom: 0px; border-left: 0px;\" width=\"100%\">\n" +
                "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"text_block\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; word-break: break-word;\" width=\"100%\">\n" +
                "<tr>\n" +
                "<td style=\"padding-bottom:40px;padding-left:15px;padding-right:15px;padding-top:40px;\">\n" +
                "<div style=\"font-family: Tahoma, Verdana, sans-serif\">\n" +
                "<div style=\"font-size: 12px; font-family: 'Lato', Tahoma, Verdana, Segoe, sans-serif; mso-line-height-alt: 18px; color: #222222; line-height: 1.5;\">\n" +
                "    <p style=\"margin: 0; font-size: 16px; text-align: left; mso-line-height-alt: 24px;\"><span style=\"font-size:16px;\"><strong>Hey, "+pName+"</strong></span></p>\n" +
                "<p style=\"margin: 0; font-size: 16px; text-align: left; mso-line-height-alt: 24px;\"><span style=\"font-size:16px;\">We have your hosted trip details here: </span></p>\n" +
                "<!--这里写trip的信息-->    \n" +
                "<p style=\"margin: 0; font-size: 16px; text-align: left; mso-line-height-alt: 18px;\"> </p>\n" +
                "<p style=\"margin: 0; font-size: 16px; text-align: left; mso-line-height-alt: 24px;\"><strong>Depart City: </strong><span style=\"font-size:16px;\">"+reservation.getStartcity()+"</span></p>\n" +
                "<p style=\"margin: 0; font-size: 16px; text-align: left; mso-line-height-alt: 24px;\"><strong>Destination: </strong><span style=\"font-size:16px;\">"+reservation.getEndcity()+"</span></p>\n" +
                "<p style=\"margin: 0; font-size: 16px; text-align: left; mso-line-height-alt: 24px;\"><strong>Depart Day: </strong><span style=\"font-size:16px;\">"+reservation.getStartday()+"</span></p>\n" +
                "<p style=\"margin: 0; font-size: 16px; text-align: left; mso-line-height-alt: 24px;\"><strong>Depart Time: </strong><span style=\"font-size:16px;\">"+reservation.getStarttime()+"</span></p>\n" +
                "<p style=\"margin: 0; font-size: 16px; text-align: left; mso-line-height-alt: 24px;\"><strong>Special Instruction: </strong><span style=\"font-size:16px;\">"+reservation.getNote()+"</span></p>\n" +
                "\n" +
                "<!--这里写trip provider的信息-->   \n" +
                "<p style=\"margin: 0; font-size: 16px; text-align: left; mso-line-height-alt: 18px;\"> </p>\n" +
                "<p style=\"margin: 0; font-size: 16px; text-align: left; mso-line-height-alt: 24px;\">The trip listed above is booked by <span style=\"font-size:16px;\"><strong>"+customer.getFirstname()+"</strong></span></p>\n" +
                "<p style=\"margin: 0; font-size: 16px; text-align: left; mso-line-height-alt: 24px;\"><strong>Contact Passenger: </strong><span style=\"font-size:16px;\">"+customer.getEmail()+"</span></p>\n" +
                "<p style=\"margin: 0; font-size: 16px; text-align: left; mso-line-height-alt: 18px;\"> </p>\n" +
                "<p style=\"margin: 0; font-size: 16px; text-align: left; mso-line-height-alt: 24px;\"><span style=\"font-size:16px;\">Hope you enjoy your trip, and we hope to see you again soon.</span></p>\n" +
                "\n" +
                "</div>\n" +
                "</div>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "     \n" +
                "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"row row-4\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #f7f6f5;\" width=\"100%\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td>\n" +
                "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"row-content stack\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #fff; color: #000000; width: 600px;\" width=\"600\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td class=\"column\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; font-weight: 400; text-align: left; vertical-align: top; padding-top: 0px; padding-bottom: 0px; border-top: 0px; border-right: 0px; border-bottom: 0px; border-left: 0px;\" width=\"100%\">\n" +
                "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"heading_block\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\" width=\"100%\">\n" +
                "<tr>\n" +
                "<td style=\"padding-bottom:35px;padding-left:5px;padding-right:5px;padding-top:30px;text-align:center;width:100%;\">\n" +
                "<h2 style=\"margin: 0; color: #072b52; direction: ltr; font-size: 19px; font-weight: normal; letter-spacing: 1px; line-height: 120%; text-align: center; margin-top: 0; margin-bottom: 0;\">Contact Us if You Have Any Question: <strong>(800)629-3971</strong></h2>\n" +
                "<h1 style=\"margin: 0; color: #072b52; direction: ltr; font-size: 19px; font-weight: normal; letter-spacing: 1px; line-height: 120%; text-align: center; margin-top: 0; margin-bottom: 0;\"> <strong>&</strong></h1>\n" +
                "<h2 style=\"margin: 0; color: #072b52; direction: ltr; font-size: 19px; font-weight: normal; letter-spacing: 1px; line-height: 120%; text-align: center; margin-top: 0; margin-bottom: 0;\">Keep sharing with us by tagging <strong>#GreenTrips</strong></h2>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"row row-5\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #f7f6f5;\" width=\"100%\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td>\n" +
                "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"row-content stack\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #fff; color: #000000; width: 600px;\" width=\"600\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td class=\"column\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; font-weight: 400; text-align: left; vertical-align: top; padding-top: 5px; padding-bottom: 5px; border-top: 0px; border-right: 0px; border-bottom: 0px; border-left: 0px;\" width=\"100%\">\n" +
                "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"html_block\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\" width=\"100%\">\n" +
                "<tr>\n" +
                "<td>\n" +
                "<div align=\"center\" style=\"font-family:Arial, Helvetica Neue, Helvetica, sans-serif;\"><div style=\"height:30px;\"> </div></div>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</table>\n" +
                "\n" +
                "    \n" +
                "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"html_block\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\" width=\"100%\">\n" +
                "<tr>\n" +
                "<td>\n" +
                "<div align=\"center\" style=\"font-family:Arial, Helvetica Neue, Helvetica, sans-serif;\"><div style=\"height:30px;\"> </div></div>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</table>\n" +
                "\n" +
                "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"html_block\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\" width=\"100%\">\n" +
                "<tr>\n" +
                "<td>\n" +
                "<div align=\"center\" style=\"font-family:Arial, Helvetica Neue, Helvetica, sans-serif;\"><div style=\"height:30px;\"> </div></div>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"row row-6\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #f7f6f5;\" width=\"100%\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td>\n" +
                "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"row-content stack\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #a8d072; color: #000000; width: 600px;\" width=\"600\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td class=\"column\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; font-weight: 400; text-align: left; vertical-align: top; padding-top: 5px; padding-bottom: 5px; border-top: 0px; border-right: 0px; border-bottom: 0px; border-left: 0px;\" width=\"100%\">\n" +
                "<table border=\"0\" cellpadding=\"10\" cellspacing=\"0\" class=\"text_block\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; word-break: break-word;\" width=\"100%\">\n" +
                "<tr>\n" +
                "<td>\n" +
                "<div style=\"font-family: Tahoma, Verdana, sans-serif\">\n" +
                "<div style=\"font-size: 12px; font-family: 'Lato', Tahoma, Verdana, Segoe, sans-serif; mso-line-height-alt: 14.399999999999999px; color: #f7f6f5; line-height: 1.2;\">\n" +
                "<p style=\"margin: 0; mso-line-height-alt: 14.399999999999999px;\"> </p>\n" +
                "<p style=\"margin: 0; text-align: center;\"><a href=\"http://www.example.com/\" rel=\"noopener\" style=\"text-decoration: underline; color: #f7f6f5;\" target=\"_blank\" title=\"http://www.example.com/\">Terms &amp; Conditions</a></p>\n" +
                "<p style=\"margin: 0; text-align: center;\">Your access to, and all of your use of the Website, Products, and/or Services must be lawful and must be in compliance with these Terms, and any other agreement between you and Usabilla.</p>\n" +
                "<p style=\"margin: 0; font-size: 12px; text-align: center;\"><span style=\"color:#c0c0c0;\"><br/><br/></span></p>\n" +
                "<p style=\"margin: 0; text-align: center;\">© Copyright 2021. All Rights Reserved.</p>\n" +
                "<p style=\"margin: 0; text-align: center;\"><a href=\"http://www.example.com/\" rel=\"noopener\" style=\"color: #f7f6f5;\" target=\"_blank\" title=\"http://www.example.com\">Manage Preferences</a> | <a href=\"http://www.example.com/\" rel=\"noopener\" style=\"color: #f7f6f5;\" target=\"_blank\" title=\"http://www.example.com\">Unsubscribe</a></p>\n" +
                "<p style=\"margin: 0; font-size: 12px; text-align: center;\"><span style=\"color:#c0c0c0;\"> </span></p>\n" +
                "</div>\n" +
                "</div>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"row row-7\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\" width=\"100%\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td>\n" +
                "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"row-content stack\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; color: #000000; width: 600px;\" width=\"600\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td class=\"column\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; font-weight: 400; text-align: left; vertical-align: top; padding-top: 5px; padding-bottom: 5px; border-top: 0px; border-right: 0px; border-bottom: 0px; border-left: 0px;\" width=\"100%\">\n" +
                "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"icons_block\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\" width=\"100%\">\n" +
                "<tr>\n" +
                "<td style=\"color:#9d9d9d;font-family:inherit;font-size:15px;padding-bottom:5px;padding-top:5px;text-align:center;\">\n" +
                "<table cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\" width=\"100%\">\n" +
                "<tr>\n" +
                "<td style=\"text-align:center;\">\n" +
                "<!--[if vml]><table align=\"left\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"display:inline-block;padding-left:0px;padding-right:0px;mso-table-lspace: 0pt;mso-table-rspace: 0pt;\"><![endif]-->\n" +
                "<!--[if !vml]><!-->\n" +
                "<table cellpadding=\"0\" cellspacing=\"0\" class=\"icons-inner\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; display: inline-block; margin-right: -4px; padding-left: 0px; padding-right: 0px;\">\n" +
                "<!--<![endif]-->\n" +
                "<tr>\n" +
                "<td style=\"text-align:center;padding-top:5px;padding-bottom:5px;padding-left:5px;padding-right:6px;\"></td>\n" +
                "<td style=\"font-family:Arial, Helvetica Neue, Helvetica, sans-serif;font-size:15px;color:#9d9d9d;vertical-align:middle;text-align:center;\"><a href=\"https://www.designedwithbee.com/\" style=\"color:#9d9d9d;text-decoration:none;\">Designed with Green Team</a></td>\n" +
                "</tr>\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "    \n" +
                "    \n" +
                "    \n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</table><!-- End -->\n" +
                "</body>\n" +
                "</html>";
    }
    private String buildEmailCancelP(String pName, Reservation reservation) {
        return "<!DOCTYPE html>\n" +
                "\n" +
                "<html lang=\"en\" xmlns:o=\"urn:schemas-microsoft-com:office:office\" xmlns:v=\"urn:schemas-microsoft-com:vml\">\n" +
                "<head>\n" +
                "<title></title>\n" +
                "<meta charset=\"utf-8\"/>\n" +
                "<meta content=\"width=device-width, initial-scale=1.0\" name=\"viewport\"/>\n" +
                "<!--[if mso]><xml><o:OfficeDocumentSettings><o:PixelsPerInch>96</o:PixelsPerInch><o:AllowPNG/></o:OfficeDocumentSettings></xml><![endif]-->\n" +
                "<!--[if !mso]><!-->\n" +
                "<link href=\"https://fonts.googleapis.com/css?family=Open+Sans\" rel=\"stylesheet\" type=\"text/css\"/>\n" +
                "<link href=\"https://fonts.googleapis.com/css?family=Lato\" rel=\"stylesheet\" >\n" +
                "<!--<![endif]-->\n" +
                "<style>\n" +
                "\t\t* {\n" +
                "\t\t\tbox-sizing: border-box;\n" +
                "\t\t}\n" +
                "\n" +
                "\t\tbody {\n" +
                "\t\t\tmargin: 0;\n" +
                "\t\t\tpadding: 0;\n" +
                "\t\t}\n" +
                "\n" +
                "\t\ta[x-apple-data-detectors] {\n" +
                "\t\t\tcolor: inherit !important;\n" +
                "\t\t\ttext-decoration: inherit !important;\n" +
                "\t\t}\n" +
                "\n" +
                "\t\t#MessageViewBody a {\n" +
                "\t\t\tcolor: inherit;\n" +
                "\t\t\ttext-decoration: none;\n" +
                "\t\t}\n" +
                "\n" +
                "\t\tp {\n" +
                "\t\t\tline-height: inherit\n" +
                "\t\t}\n" +
                "\n" +
                "\t\t@media (max-width:620px) {\n" +
                "\t\t\t.icons-inner {\n" +
                "\t\t\t\ttext-align: center;\n" +
                "\t\t\t}\n" +
                "\n" +
                "\t\t\t.icons-inner td {\n" +
                "\t\t\t\tmargin: 0 auto;\n" +
                "\t\t\t}\n" +
                "\n" +
                "\t\t\t.row-content {\n" +
                "\t\t\t\twidth: 100% !important;\n" +
                "\t\t\t}\n" +
                "\n" +
                "\t\t\t.stack .column {\n" +
                "\t\t\t\twidth: 100%;\n" +
                "\t\t\t\tdisplay: block;\n" +
                "\t\t\t}\n" +
                "\t\t}\n" +
                "\t</style>\n" +
                "</head>\n" +
                "<body style=\"background-color: #FFFFFF; margin: 0; padding: 0; -webkit-text-size-adjust: none; text-size-adjust: none;\">\n" +
                "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"nl-container\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #FFFFFF;\" width=\"100%\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td>\n" +
                "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"row row-1\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #f7f6f5;\" width=\"100%\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td>\n" +
                "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"row row-2\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #f7f6f5;\" width=\"100%\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td>\n" +
                "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"row-content stack\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #fff; color: #000000; width: 600px;\" width=\"600\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td class=\"column\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; font-weight: 400; text-align: left; vertical-align: top; padding-top: 0px; padding-bottom: 0px; border-top: 0px; border-right: 0px; border-bottom: 0px; border-left: 0px;\" width=\"100%\">\n" +
                "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"heading_block\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\" width=\"100%\">\n" +
                "<tr>\n" +
                "<td style=\"text-align:center;width:100%;padding-top:35px;\">\n" +
                "<h1 style=\"margin: 0; color: #072b52; direction: ltr; font-family: 'Lora', Georgia, serif; font-size: 45px; font-weight: normal; letter-spacing: 1px; line-height: 120%; text-align: center; margin-top: 0; margin-bottom: 0;\"><strong>Cancellation Noticed.</strong></h1>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "    \n" +
                "    \n" +
                "    \n" +
                "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"row row-3\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #f7f6f5;\" width=\"100%\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td>\n" +
                "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"row-content stack\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #fff; color: #000000; width: 600px;\" width=\"600\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td class=\"column\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; font-weight: 400; text-align: left; vertical-align: top; padding-top: 0px; padding-bottom: 0px; border-top: 0px; border-right: 0px; border-bottom: 0px; border-left: 0px;\" width=\"100%\">\n" +
                "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"text_block\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; word-break: break-word;\" width=\"100%\">\n" +
                "<tr>\n" +
                "<td style=\"padding-bottom:40px;padding-left:15px;padding-right:15px;padding-top:40px;\">\n" +
                "<div style=\"font-family: Tahoma, Verdana, sans-serif\">\n" +
                "<div style=\"font-size: 12px; font-family: 'Lato', Tahoma, Verdana, Segoe, sans-serif; mso-line-height-alt: 18px; color: #222222; line-height: 1.5;\">\n" +
                "    <p style=\"margin: 0; font-size: 16px; text-align: left; mso-line-height-alt: 24px;\"><span style=\"font-size:16px;\"><strong>Hey, "+pName+"</strong></span></p>\n" +
                "<p style=\"margin: 0; font-size: 16px; text-align: left; mso-line-height-alt: 24px;\"><span style=\"font-size:16px;\">Now we have your hosted trip listed below available again.</span></p>\n" +
                "<!--这里写trip的信息-->    \n" +
                "<p style=\"margin: 0; font-size: 16px; text-align: left; mso-line-height-alt: 18px;\"> </p>\n" +
                "<p style=\"margin: 0; font-size: 16px; text-align: left; mso-line-height-alt: 24px;\"><strong>Depart City: </strong><span style=\"font-size:16px;\">"+reservation.getStartcity()+"</span></p>\n" +
                "<p style=\"margin: 0; font-size: 16px; text-align: left; mso-line-height-alt: 24px;\"><strong>Destination: </strong><span style=\"font-size:16px;\">"+reservation.getEndcity()+"</span></p>\n" +
                "<p style=\"margin: 0; font-size: 16px; text-align: left; mso-line-height-alt: 24px;\"><strong>Depart Day: </strong><span style=\"font-size:16px;\">"+reservation.getStartday()+"</span></p>\n" +
                "<p style=\"margin: 0; font-size: 16px; text-align: left; mso-line-height-alt: 24px;\"><strong>Depart Time: </strong><span style=\"font-size:16px;\">"+reservation.getStarttime()+"</span></p>\n" +
                "<p style=\"margin: 0; font-size: 16px; text-align: left; mso-line-height-alt: 24px;\"><strong>Special Instruction: </strong><span style=\"font-size:16px;\">"+reservation.getNote()+"</span></p>\n" +
                "<p style=\"margin: 0; font-size: 16px; text-align: left; mso-line-height-alt: 18px;\"> </p>\n" +
                "    <p style=\"margin: 0; font-size: 16px; text-align: left; mso-line-height-alt: 24px;\"><span style=\"font-size:16px;\">If you want to check out more details, please visit our <a href=\"http://localhost:8080\">site</a>.</span></p>\n" +
                " \n" +
                "\n" +
                "</div>\n" +
                "</div>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "     \n" +
                "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"row row-4\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #f7f6f5;\" width=\"100%\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td>\n" +
                "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"row-content stack\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #fff; color: #000000; width: 600px;\" width=\"600\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td class=\"column\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; font-weight: 400; text-align: left; vertical-align: top; padding-top: 0px; padding-bottom: 0px; border-top: 0px; border-right: 0px; border-bottom: 0px; border-left: 0px;\" width=\"100%\">\n" +
                "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"heading_block\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\" width=\"100%\">\n" +
                "<tr>\n" +
                "<td style=\"padding-bottom:35px;padding-left:5px;padding-right:5px;padding-top:30px;text-align:center;width:100%;\">\n" +
                "<h2 style=\"margin: 0; color: #072b52; direction: ltr; font-size: 19px; font-weight: normal; letter-spacing: 1px; line-height: 120%; text-align: center; margin-top: 0; margin-bottom: 0;\">Contact Us if You Have Any Question: <strong>(800)629-3971</strong></h2>\n" +
                "<h1 style=\"margin: 0; color: #072b52; direction: ltr; font-size: 19px; font-weight: normal; letter-spacing: 1px; line-height: 120%; text-align: center; margin-top: 0; margin-bottom: 0;\"> <strong>&</strong></h1>\n" +
                "<h2 style=\"margin: 0; color: #072b52; direction: ltr;font-size: 19px; font-weight: normal; letter-spacing: 1px; line-height: 120%; text-align: center; margin-top: 0; margin-bottom: 0;\">Keep sharing with us by tagging <strong>#GreenTrips</strong></h2>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"row row-5\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #f7f6f5;\" width=\"100%\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td>\n" +
                "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"row-content stack\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #fff; color: #000000; width: 600px;\" width=\"600\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td class=\"column\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; font-weight: 400; text-align: left; vertical-align: top; padding-top: 5px; padding-bottom: 5px; border-top: 0px; border-right: 0px; border-bottom: 0px; border-left: 0px;\" width=\"100%\">\n" +
                "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"html_block\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\" width=\"100%\">\n" +
                "<tr>\n" +
                "<td>\n" +
                "<div align=\"center\" style=\"font-family:Arial, Helvetica Neue, Helvetica, sans-serif;\"><div style=\"height:30px;\"> </div></div>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</table>\n" +
                "\n" +
                "    \n" +
                "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"html_block\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\" width=\"100%\">\n" +
                "<tr>\n" +
                "<td>\n" +
                "<div align=\"center\" style=\"font-family:Arial, Helvetica Neue, Helvetica, sans-serif;\"><div style=\"height:30px;\"> </div></div>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</table>\n" +
                "\n" +
                "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"html_block\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\" width=\"100%\">\n" +
                "<tr>\n" +
                "<td>\n" +
                "<div align=\"center\" style=\"font-family:Arial, Helvetica Neue, Helvetica, sans-serif;\"><div style=\"height:30px;\"> </div></div>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"row row-6\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #f7f6f5;\" width=\"100%\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td>\n" +
                "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"row-content stack\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #a8d072; color: #000000; width: 600px;\" width=\"600\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td class=\"column\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; font-weight: 400; text-align: left; vertical-align: top; padding-top: 5px; padding-bottom: 5px; border-top: 0px; border-right: 0px; border-bottom: 0px; border-left: 0px;\" width=\"100%\">\n" +
                "<table border=\"0\" cellpadding=\"10\" cellspacing=\"0\" class=\"text_block\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; word-break: break-word;\" width=\"100%\">\n" +
                "<tr>\n" +
                "<td>\n" +
                "<div style=\"font-family: Tahoma, Verdana, sans-serif\">\n" +
                "<div style=\"font-size: 12px; font-family: 'Lato', Tahoma, Verdana, Segoe, sans-serif; mso-line-height-alt: 14.399999999999999px; color: #f7f6f5; line-height: 1.2;\">\n" +
                "<p style=\"margin: 0; mso-line-height-alt: 14.399999999999999px;\"> </p>\n" +
                "<p style=\"margin: 0; text-align: center;\"><a href=\"http://www.example.com/\" rel=\"noopener\" style=\"text-decoration: underline; color: #f7f6f5;\" target=\"_blank\" title=\"http://www.example.com/\">Terms &amp; Conditions</a></p>\n" +
                "<p style=\"margin: 0; text-align: center;\">Your access to, and all of your use of the Website, Products, and/or Services must be lawful and must be in compliance with these Terms, and any other agreement between you and Usabilla.</p>\n" +
                "<p style=\"margin: 0; font-size: 12px; text-align: center;\"><span style=\"color:#c0c0c0;\"><br/><br/></span></p>\n" +
                "<p style=\"margin: 0; text-align: center;\">© Copyright 2021. All Rights Reserved.</p>\n" +
                "<p style=\"margin: 0; text-align: center;\"><a href=\"http://www.example.com/\" rel=\"noopener\" style=\"color: #f7f6f5;\" target=\"_blank\" title=\"http://www.example.com\">Manage Preferences</a> | <a href=\"http://www.example.com/\" rel=\"noopener\" style=\"color: #f7f6f5;\" target=\"_blank\" title=\"http://www.example.com\">Unsubscribe</a></p>\n" +
                "<p style=\"margin: 0; font-size: 12px; text-align: center;\"><span style=\"color:#c0c0c0;\"> </span></p>\n" +
                "</div>\n" +
                "</div>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"row row-7\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\" width=\"100%\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td>\n" +
                "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"row-content stack\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; color: #000000; width: 600px;\" width=\"600\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td class=\"column\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; font-weight: 400; text-align: left; vertical-align: top; padding-top: 5px; padding-bottom: 5px; border-top: 0px; border-right: 0px; border-bottom: 0px; border-left: 0px;\" width=\"100%\">\n" +
                "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"icons_block\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\" width=\"100%\">\n" +
                "<tr>\n" +
                "<td style=\"color:#9d9d9d;font-family:inherit;font-size:15px;padding-bottom:5px;padding-top:5px;text-align:center;\">\n" +
                "<table cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\" width=\"100%\">\n" +
                "<tr>\n" +
                "<td style=\"text-align:center;\">\n" +
                "<!--[if vml]><table align=\"left\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"display:inline-block;padding-left:0px;padding-right:0px;mso-table-lspace: 0pt;mso-table-rspace: 0pt;\"><![endif]-->\n" +
                "<!--[if !vml]><!-->\n" +
                "<table cellpadding=\"0\" cellspacing=\"0\" class=\"icons-inner\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; display: inline-block; margin-right: -4px; padding-left: 0px; padding-right: 0px;\">\n" +
                "<!--<![endif]-->\n" +
                "<tr>\n" +
                "<td style=\"text-align:center;padding-top:5px;padding-bottom:5px;padding-left:5px;padding-right:6px;\"></td>\n" +
                "<td style=\"font-family:Arial, Helvetica Neue, Helvetica, sans-serif;font-size:15px;color:#9d9d9d;vertical-align:middle;text-align:center;\"><a href=\"https://www.designedwithbee.com/\" style=\"color:#9d9d9d;text-decoration:none;\">Designed with Green Team</a></td>\n" +
                "</tr>\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "    \n" +
                "    \n" +
                "    \n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</table><!-- End -->\n" +
                "</body>\n" +
                "</html>";
    }
}
