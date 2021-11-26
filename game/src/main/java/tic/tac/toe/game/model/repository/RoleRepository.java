package tic.tac.toe.game.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tic.tac.toe.game.model.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
