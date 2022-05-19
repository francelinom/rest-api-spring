package curso.api.rest.repository;

import curso.api.rest.model.Usuario;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UsuarioRepository extends CrudRepository<Usuario, Long> {
    @Query("select u from Usuario u where u.login = ?1")
    Usuario findUserByLogin(String login);

    @Query("select u from Usuario u where u.nome like %?1%")
    List<Usuario> findUserByNome(String nome);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "update usuario set token = ?1 where login = ?2")
    void atualizaTokenUser(String token, String login);
}
