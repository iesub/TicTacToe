package tic.tac.toe.game.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tic.tac.toe.game.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
