package tic.tac.toe.game.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tic.tac.toe.game.entity.User;
import tic.tac.toe.game.query.QueryRankTableUnit15x15;
import tic.tac.toe.game.query.QueryRankTableUnit3x3;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByMail(String mail);
    User findByNickname(String nickname);
    @Query(value="select new tic.tac.toe.game.query.QueryRankTableUnit3x3(c.id, c.nickname, c.rankScore3x3) from User c order by c.rankScore3x3 desc")
    List<QueryRankTableUnit3x3> selectRankTableInfo3x3();
    @Query(value="select new tic.tac.toe.game.query.QueryRankTableUnit15x15(c.id, c.nickname, c.rankScore15x15) from User c order by c.rankScore15x15 desc")
    List<QueryRankTableUnit15x15> selectRankTableInfo15x15();
}
