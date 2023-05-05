package com.cwallet.CHAMPWallet.controller.account;

import com.cwallet.CHAMPWallet.bean.account.PasswordResetForm;
import com.cwallet.CHAMPWallet.bean.account.RegistrationForm;
import com.cwallet.CHAMPWallet.dto.account.UserEntityDTO;
import com.cwallet.CHAMPWallet.exception.account.EmailNotSentException;
import com.cwallet.CHAMPWallet.exception.account.EmailNotUniqueException;
import com.cwallet.CHAMPWallet.exception.account.UserNameNotUniqueException;
import com.cwallet.CHAMPWallet.security.SecurityUtil;
import com.cwallet.CHAMPWallet.service.account.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class UserController {
    private UserService userService;
    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/register")
    public String getRegistrationForm(Model model) {
        model.addAttribute("registrationForm", new RegistrationForm());
        return "users-registration";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("registrationForm") RegistrationForm registrationForm,
                               BindingResult result, Model model) {
        System.out.println(registrationForm);
        if(result.hasErrors()){
            model.addAttribute("registrationForm", registrationForm);
            return "users-registration";
        }
        if(!registrationForm.getPassword().equals(registrationForm.getConfirmPassword())) {
            model.addAttribute("registrationForm", registrationForm);
            model.addAttribute("passwordError", "Password must match");
            return "users-registration";
        }
        UserEntityDTO newUser = UserEntityDTO.builder()
                .username(registrationForm.getUsername())
                .email(registrationForm.getEmail())
                .password(registrationForm.getPassword())
                .build();
        try {
            userService.save(newUser);
        } catch (UserNameNotUniqueException e) {
            model.addAttribute("registrationFrom", registrationForm);
            model.addAttribute("usernameError", "username already exist");
            return "users-registration";
        } catch (EmailNotUniqueException e) {
            model.addAttribute("registrationFrom", registrationForm);
            model.addAttribute("emailError", "Email Already exist");
            return "users-registration";
        } catch (EmailNotSentException e) {
            return "redirect:/login?emailnotsent=email not sent";
        }
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String getLoginForm(Model model) {
        model.addAttribute("loginForm", new RegistrationForm());
        return "users-login";
    }

    @GetMapping("/users/home")
    public String home(Model model) {
        String user = SecurityUtil.getSessionUser();
        model.addAttribute("user", user);
        return "home";
    }

    @GetMapping("/request-verification")
    public String getRequestPasswordresetForm(Model model) {
        model.addAttribute("passwordResetForm", new PasswordResetForm());
        return "password-reset";
    }

    @PostMapping("/request-verification")
    public String requestVerification(@Valid @ModelAttribute("passwordResetForm") PasswordResetForm passwordResetForm,
                                      BindingResult result, Model model) {
        if(result.hasErrors()){
            model.addAttribute("passwordResetForm", passwordResetForm);
            return "password-reset";
        }
        userService.requestPasswordReset(passwordResetForm.getEmail());
        return "redirect:/login?resetpassword";

    }
}
