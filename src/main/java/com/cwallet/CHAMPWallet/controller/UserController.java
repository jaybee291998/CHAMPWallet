package com.cwallet.CHAMPWallet.controller;

import com.cwallet.CHAMPWallet.bean.PasswordResetForm;
import com.cwallet.CHAMPWallet.bean.RegistrationForm;
import com.cwallet.CHAMPWallet.dto.UserEntityDTO;
import com.cwallet.CHAMPWallet.exception.EmailNotUniqueException;
import com.cwallet.CHAMPWallet.exception.UserNameNotUniqueException;
import com.cwallet.CHAMPWallet.models.UserEntity;
import com.cwallet.CHAMPWallet.security.SecurityUtil;
import com.cwallet.CHAMPWallet.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.jws.WebParam;
import javax.persistence.GeneratedValue;
import javax.validation.Valid;
import java.awt.image.BufferedImage;

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
