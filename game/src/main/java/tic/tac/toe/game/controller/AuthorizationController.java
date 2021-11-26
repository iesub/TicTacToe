package tic.tac.toe.game.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import tic.tac.toe.game.model.entity.User;
import tic.tac.toe.game.MailService;
import tic.tac.toe.game.model.service.UserService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class AuthorizationController {

    @Autowired
    private UserService userService;

    @Autowired
    private MailService mailService;

    @RequestMapping("/login")
    public String login() {
        return "login";
    }

    @RequestMapping("/login-error")
    public String loginError(Model model) {
        model.addAttribute("Error", true);
        return "login";
    }

    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("userForm", new User());

        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(@ModelAttribute("userForm") User userForm, BindingResult bindingResult, Model model) {

        String emailPattern =
                "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" +
                        "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(userForm.getUsername());

        if (bindingResult.hasErrors()) {
            return "registration";
        }
        if(!matcher.matches()){
            model.addAttribute("Error", "Неверный формат электронной почты");
            return "registration";
        }
        if (!userForm.getPassword().equals(userForm.getPasswordConfirm())){
            model.addAttribute("Error", "Пароли не совпадают");
            return "registration";
        }
        if (!userService.registerUser(userForm)){
            model.addAttribute("Error", "Пользователь с такой электронной почтой ил никнеймом уже существует");
            return "registration";
        }

        User userFromBD = userService.loadUserByUsername(userForm.getUsername());
        mailService.sendActivationURL(userFromBD.getUsername(), userFromBD.getId());
        return "registration-mail-notification";
    }

    @GetMapping("/registration/activate/{userId}")
    public String activation(@PathVariable("userId") Long userId) {
        User user = userService.findUserById(userId);
        user.setActive(true);
        userService.updateRow(user);
        return "redirect:/login";
    }
}
