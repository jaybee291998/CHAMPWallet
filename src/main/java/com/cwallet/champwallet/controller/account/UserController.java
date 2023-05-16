package com.cwallet.champwallet.controller.account;

import com.cwallet.champwallet.bean.account.EmailForm;
import com.cwallet.champwallet.bean.account.PasswordResetForm;
import com.cwallet.champwallet.bean.account.RegistrationForm;
import com.cwallet.champwallet.dto.account.UserEntityDTO;
import com.cwallet.champwallet.exception.account.*;
import com.cwallet.champwallet.security.SecurityUtil;
import com.cwallet.champwallet.service.account.UserService;
import com.cwallet.champwallet.service.account.VerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

//import javax.jws.WebParam;
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
        if(securityUtil.getLoggedInUser() != null) {
            return "redirect:/users/home";
        }
        model.addAttribute("registrationForm", new RegistrationForm());
        return "users-registration";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("registrationForm") RegistrationForm registrationForm,
                               BindingResult result, Model model) {
        if(securityUtil.getLoggedInUser() != null) {
            return "redirect:/users/home";
        }
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

    @GetMapping("/")
    public String adam() {
        if(securityUtil.getLoggedInUser() != null) {
            return "redirect:/users/home";
        } else {
            return "landing";
        }
    }
    @GetMapping("/login")
    public String getLoginForm(Model model) {
        if(securityUtil.getLoggedInUser() != null) {
            return "redirect:/users/home";
        }
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
        if(securityUtil.getLoggedInUser() != null) {
            return "redirect:/users/home";
        }
        model.addAttribute("passwordResetForm", new EmailForm());
        return "request-password-reset";
    }

    @PostMapping("/password-reset")
    public String requestPasswordReset(@Valid @ModelAttribute("passwordResetForm") EmailForm emailForm,
                                      BindingResult result, Model model) {
        if(securityUtil.getLoggedInUser() != null) {
            return "redirect:/users/home";
        }
        if(result.hasErrors()){
            model.addAttribute("passwordResetForm", emailForm);
            return "request-password-reset";
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
        if(securityUtil.getLoggedInUser() != null) {
            return "redirect:/users/home";
        }
        model.addAttribute("emailForm", new EmailForm());
        return "request-verification";
    }

    @PostMapping("/request-verification")
    public String requestVerification(@Valid @ModelAttribute("emailForm") EmailForm emailForm,
                                      BindingResult result, Model model) {
        if(securityUtil.getLoggedInUser() != null) {
            return "redirect:/users/home";
        }
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
        if(securityUtil.getLoggedInUser() != null) {
            return "redirect:/users/home";
        }
        if((activation == null || activation.equals("")) || (account == null || account.equals(""))) {
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
        } catch (VerificationAlreadyUsedException e) {
            model.addAttribute("errorMessage", "verification already used");
            return "activation";
        }
    }

    @GetMapping("/reset-password")
    public String getResetPasswordForm(@RequestParam String activation, @RequestParam String account, Model model) {
        if(securityUtil.getLoggedInUser() != null) {
            return "redirect:/users/home";
        }
        if((activation == null || activation.equals("")) || (account == null || account.equals(""))) {
            model.addAttribute("errorMessage", "Invalid reset link");
            return "password-reset";
        }
        PasswordResetForm resetForm = PasswordResetForm.builder()
                .activationCode(activation)
                .accountID(Long.valueOf(account))
                .build();

        model.addAttribute("passwordResetForm", resetForm);
        return "password-reset";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@Valid @ModelAttribute("passwordResetForm") PasswordResetForm resetForm, BindingResult bindingResult, Model model) {
        if(securityUtil.getLoggedInUser() != null) {
            return "redirect:/users/home";
        }
        if(bindingResult.hasErrors()) {
            model.addAttribute("passwordResetForm", resetForm);
            return "redirect:/reset-password?invalidlink=invalid reset link";
        }

        if(resetForm.getActivationCode() == null || resetForm.getActivationCode().equals("")) {
            model.addAttribute("passwordResetForm", resetForm);
            model.addAttribute("errorMessage", "No activation code given");
            return "password-reset";
        }
        try {
            boolean isSuccess = verificationService.resetPassword(resetForm.getActivationCode(), resetForm.getAccountID(), resetForm.getPassword());
            if(isSuccess) {
                return "redirect:/login?resetpasswordsuccess=Password reset was successfully";
            }
            model.addAttribute("errorMessage", "Invalid activation link");
            return "password-reset";
        } catch (NoSuchAccountException e) {
            model.addAttribute("errorMessage", "No such account");
            return "password-reset";
        } catch (VerificationAlreadyUsedException e) {
            model.addAttribute("errorMessage", "The verification link is already used");
            return "password-reset";
        }
    }
}
