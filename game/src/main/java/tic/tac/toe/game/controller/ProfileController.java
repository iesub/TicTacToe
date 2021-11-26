package tic.tac.toe.game.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import tic.tac.toe.game.model.service.GameSessionService;
import tic.tac.toe.game.model.service.UserService;

@Controller
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private GameSessionService sessionService;

    @GetMapping("/profile/{userId}")
    public String showProfile(Model model, @PathVariable("userId") Long userId){
        model.addAttribute("currenUser", userService.findUserById(userId));
        model.addAttribute("sessionsHistory", sessionService.getGameSessionsById(userId));
        return "profile";
    }
}
