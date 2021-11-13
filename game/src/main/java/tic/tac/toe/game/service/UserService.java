package tic.tac.toe.game.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import tic.tac.toe.game.entity.Role;
import tic.tac.toe.game.entity.User;
import tic.tac.toe.game.query.QueryRankTableUnit15x15;
import tic.tac.toe.game.query.QueryRankTableUnit3x3;
import tic.tac.toe.game.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    BCryptPasswordEncoder encoder;

    public UserService(){}

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByMail(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return user;
    }

    public boolean registerUser(User user) {
        User userFromDBSameMail = userRepository.findByMail(user.getUsername());
        User userFromDBSameNickName = userRepository.findByNickname(user.getNickname());

        if (userFromDBSameMail != null || userFromDBSameNickName != null) {
            return false;
        }

        user.setRoles(Collections.singleton(new Role(1L, "ROLE_USER")));
        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.save(user);
        return true;
    }

    public User findUserById(Long userId) {
        Optional<User> userFromDb = userRepository.findById(userId);
        return userFromDb.orElse(new User());
    }

    public void saveUser(User user){
        userRepository.save(user);
    }

    public void updateRow(User user){
        userRepository.save(user);
    }

    public List<QueryRankTableUnit3x3> get3x3RankList(){return userRepository.selectRankTableInfo3x3();}
    public List<QueryRankTableUnit15x15> get15x15RankList(){return userRepository.selectRankTableInfo15x15();}
}
