package tic.tac.toe.game.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "user")
public class User implements UserDetails {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = Long.valueOf(0);
    @Column(name = "mail")
    String mail;
    @Column(name = "username")
    String nickname;
    @Column(name = "password")
    String password;
    @Transient
    private String passwordConfirm;
    @Column(name = "rank_score_3x3")
    private int rankScore3x3 = 0;
    @Column(name = "games_won_3x3_rank")
    private int gamesWonRank3x3 = 0;
    @Column(name = "games_won_3x3_no_rank")
    private int gamesWonNoRank3x3 = 0;
    @Column(name = "rank_score_15x15")
    private int rankScore15x15 = 0;
    @Column(name = "games_won_15x15_rank")
    private int gamesWonRank15x15 = 0;
    @Column(name = "games_won_15x15_no_rank")
    private int gamesWonNoRank15x15 = 0;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_has_user_role",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "user_role_id", referencedColumnName = "id"))
    private Set<Role> roles;
    @OneToMany(mappedBy = "sender")
    private Set<ChatMessage> messagesSent;
    @OneToMany(mappedBy = "gameWinner")
    private Set<GameSession> wonSessions;
    @Column(name = "activated")
    boolean isActive = false;

    public String getUsername(){
        return mail;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    public boolean isAccountNonLocked() {
        return true;
    }

    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }

}
