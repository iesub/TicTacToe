package tic.tac.toe.game.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import tic.tac.toe.game.entity.User;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Service
public class QueueService {
    List<User> queue3x3NRank= new ArrayList<>();

    public void addToQueue3x3NoRank(User user){
        queue3x3NRank.add(user);
    }

    public void deleteFrom3x3NoRank(User user){
        queue3x3NRank.remove(user);
    }
}
