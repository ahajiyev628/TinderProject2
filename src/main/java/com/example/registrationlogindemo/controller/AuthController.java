package com.example.registrationlogindemo.controller;

import com.example.registrationlogindemo.dto.UserDto;
import com.example.registrationlogindemo.entity.User;
import com.example.registrationlogindemo.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Controller
public class AuthController {

    private UserService userService;

//    @GetMapping("index")
//    public String home(){
//        return "login";
//    }

    @GetMapping("login")
    public String loginForm() {
        // System.out.println(userService.findByEmail("allahverdihajiyev@gmail.com").isPresent());
        return "login";
    }

    @GetMapping("mainpage")
    public String mainpage() {
        return "mainpage";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(email);
        System.out.println(user.getEmail());
        if (userService.findByEmail(email)!=null && BCrypt.checkpw(password, user.getPassword())) {
            session.setAttribute("loggedInUser", user);
            System.out.println("User " + user.getEmail() + " authenticated successfully.");
            return "redirect:/main-page";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid email or password.");
            return "redirect:/login?error=true";
        }
    }

//    @GetMapping("/login")
//    public String login(@RequestParam String email, @RequestParam String password, HttpSession session) {
//        if (userService.authenticate(email, password)) {
//            session.setAttribute("loggedInUser", email);
//            return "redirect:/main-page";
//        } else {
//            return "redirect:/login?error";
//        }
//    }

    @GetMapping("register")
    public String showRegistrationForm(Model model){
        UserDto user = new UserDto();
        model.addAttribute("user", user);
        return "register";
    }

    // handler method to handle register user form submit request
    @PostMapping("/register/save")
    public String registration(@Valid @ModelAttribute("user") UserDto user,
                               BindingResult result,
                               Model model){
        User existing = userService.findByEmail(user.getEmail());
        if (existing!=null) {
            result.rejectValue("email", null, "There is already an account registered with that email");
        }
        if (result.hasErrors()) {
            model.addAttribute("user", user);
            return "register";
        }
        userService.saveUser(user);
        return "redirect:/login";
    }

    @GetMapping("/users")
    public String listRegisteredUsers(Model model){
        List<UserDto> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "users";
    }
}
