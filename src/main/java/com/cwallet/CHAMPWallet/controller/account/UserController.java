package com.cwallet.CHAMPWallet.controller.account;

import com.cwallet.CHAMPWallet.bean.account.EmailForm;
import com.cwallet.CHAMPWallet.bean.account.PasswordResetForm;
import com.cwallet.CHAMPWallet.bean.account.RegistrationForm;
import com.cwallet.CHAMPWallet.dto.account.UserEntityDTO;
import com.cwallet.CHAMPWallet.dto.account.VerificationDTO;
import com.cwallet.CHAMPWallet.exception.account.*;
import com.cwallet.CHAMPWallet.models.account.Verification;
import com.cwallet.CHAMPWallet.security.SecurityUtil;
import com.cwallet.CHAMPWallet.service.account.UserService;
import com.cwallet.CHAMPWallet.service.account.VerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.jws.WebParam;
import javax.mail.SendFailedException;
import javax.validation.Valid;

@Controller
public class UserController {
    private UserService userService;
    @Autowired
    private VerificationService verificationService;
    @Autowired
    private SecurityUtil securityUtil;
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
        model.addAttribute("walletBalance", securityUtil.getLoggedInUser().getWallet().getBalance());
        return "home";
    }

    @GetMapping("/password-reset")
    public String getRequestPasswordresetForm(Model model) {
        model.addAttribute("passwordResetForm", new EmailForm());
        return "password-reset";
    }

    @PostMapping("/password-reset")
    public String requestPasswordReset(@Valid @ModelAttribute("passwordResetForm") EmailForm emailForm,
                                      BindingResult result, Model model) {
        if(result.hasErrors()){
            model.addAttribute("passwordResetForm", emailForm);
            return "password-reset";
        }
        try {
            userService.requestPasswordReset(emailForm.getEmail());
        } catch (NoSuchAccountException e) {
            model.addAttribute("errorMessage", "No such account exist");
            model.addAttribute("passwordResetForm", emailForm);
            return "password-reset";
        } catch (SendFailedException e) {
            model.addAttribute("errorMessage", "Failed to send reset link");
            model.addAttribute("passwordResetForm", emailForm);
            return "password-reset";
        }
        return "redirect:/login?resetpassword";
    }

    @GetMapping("/request-verification")
    public String getRequestVerification(Model model) {
        model.addAttribute("emailForm", new EmailForm());
        return "request-verification";
    }

    @PostMapping("/request-verification")
    public String requestVerification(@Valid @ModelAttribute("emailForm") EmailForm emailForm,
                                      BindingResult result, Model model) {
        if(result.hasErrors()){
            model.addAttribute("emailForm", emailForm);
            return "request-verification";
        }
        try {
            userService.requestVerificationCode(emailForm.getEmail());
        } catch (NoSuchAccountException e) {
            model.addAttribute("errorMessage", "No such account exist");
            model.addAttribute("emailForm", emailForm);
            return "request-verification";
        } catch (EmailNotSentException e) {
            model.addAttribute("errorMessage", "Failed to send reset link");
            model.addAttribute("emailForm", emailForm);
            return "request-verification";
        } catch (AccountAlreadyActivatedException e) {
            model.addAttribute("errorMessage", "Account already activated");
            model.addAttribute("emailForm", emailForm);
            return "request-verification";
        }
        return "redirect:/login?requestverification";
    }

    @GetMapping("/activate-account")
    public String activateAccount(@RequestParam String activation, @RequestParam String account, Model model) {
        if(activation == null || account == null) {
            model.addAttribute("errorMessage", "Invalid activation link");
            return "activation";
        }

        try {
            boolean verificationSuccess = verificationService.validateAccount(activation, Long.valueOf(account));
            if(verificationSuccess) {
                return "redirect:/login?activationsuccess=your account has been activated successfully, you can now login";
            }
            model.addAttribute("errorMessage", "Invalid activation link(false)");
            return "activation";
        } catch (NoSuchAccountException e) {
            model.addAttribute("errorMessage", "Invalid activation link");
            return "activation";
        } catch (AccountAlreadyActivatedException e) {
            model.addAttribute("errorMessage", "Your account is already activated");
            return "activation";
        }
    }
}
