package curso.api.rest.controller;

import curso.api.rest.model.Usuario;
import curso.api.rest.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@CrossOrigin
@Controller
@RequestMapping(value = "/usuario")
public class IndexController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping(value = "/{id}/codigo/{venda}", produces = "application/json")
    public ResponseEntity<Usuario> init(@PathVariable(value = "id") Long id,
                                        @PathVariable(value = "venda") Long venda) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        return new ResponseEntity<Usuario>(usuario.get(), HttpStatus.OK);
    }
    @GetMapping(value = "/{id}", produces = "application/json")
    @Cacheable("cacheuser")
    public ResponseEntity<Usuario> init(@PathVariable(value = "id") Long id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        return new ResponseEntity<Usuario>(usuario.get(), HttpStatus.OK);
    }

    @GetMapping(value = "/", produces = "application/json")
    @Cacheable("cacheusuarios")
    public ResponseEntity<List<Usuario>> usuario() {
        List<Usuario> list = (List<Usuario>) usuarioRepository.findAll();

        return new ResponseEntity<List<Usuario>>(list, HttpStatus.OK);
    }
    @PostMapping(value = "/", produces = "application/json")
    public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usuario) {
        for (int pos = 0; pos < usuario.getTelefones().size(); pos++) {
            usuario.getTelefones().get(pos).setUsuario(usuario);
        }
        String senhacriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
        usuario.setSenha(senhacriptografada);
        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);
    }

    @PutMapping(value = "/", produces = "application/json")
    public ResponseEntity<Usuario> atualizar(@RequestBody Usuario usuario) {
        for (int pos = 0; pos < usuario.getTelefones().size(); pos++) {
            usuario.getTelefones().get(pos).setUsuario(usuario);
        }

        Usuario userTemporario = usuarioRepository.findUserByLogin(usuario.getLogin());

        if (!userTemporario.getSenha().equals(usuario.getSenha())) {
            String senhacriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
            usuario.setSenha(senhacriptografada);
        }

        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}", produces = "application/text")
    public String delete(@PathVariable(value = "id") Long id) {
        usuarioRepository.deleteById(id);
        return "OK";
    }
}