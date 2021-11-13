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
    List<User> queue3x3Rank= new ArrayList<>();
    List<User> queue15x15NRank= new ArrayList<>();
    List<User> queue15x15Rank= new ArrayList<>();

    public void addToQueue3x3NoRank(User user){
        queue3x3NRank.add(user);
    }

    public void deleteFrom3x3NoRank(User user){
        queue3x3NRank.remove(user);
    }

    public void addToQueue3x3Rank(User user){
        queue3x3Rank.add(user);
    }

    public void deleteFrom3x3Rank(User user){
        queue3x3Rank.remove(user);
    }

    public void addToQueue15x15NoRank(User user){
        queue15x15NRank.add(user);
    }

    public void deleteFrom15x15NoRank(User user){
        queue15x15NRank.remove(user);
    }

    public void addToQueue15x15Rank(User user){
        queue15x15Rank.add(user);
    }

    public void deleteFrom15x15Rank(User user){
        queue15x15Rank.remove(user);
    }
}
