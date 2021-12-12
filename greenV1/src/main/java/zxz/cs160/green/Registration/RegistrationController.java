package zxz.cs160.green.Registration;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(path = "/signup")
@AllArgsConstructor
public class RegistrationController {

    private RegistrationService registrationService;

    @GetMapping
    public String register() {
        return "signup";
    }

    @PostMapping
    public String register(Registration request) {
        registrationService.register(request);
        return "login";
    }

    @GetMapping(path = "confirm")
    public String confirm(@RequestParam("token") String token) {
        return registrationService.confirmToken(token);
    }

}
