package curso.api.rest.controller;

import com.google.gson.Gson;
import curso.api.rest.model.Usuario;
import curso.api.rest.model.UsuarioDTO;
import curso.api.rest.repository.TelefoneRepository;
import curso.api.rest.repository.UsuarioRepository;
import curso.api.rest.service.ImplementacaoUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Optional;

@CrossOrigin
@Controller
@RequestMapping(value = "/usuario")
public class IndexController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TelefoneRepository telefoneRepository;

    @Autowired
    private ImplementacaoUserDetailsService implementacaoUserDetailsService;

    @GetMapping(value = "/{id}/codigo/{venda}", produces = "application/json")
    public ResponseEntity<Usuario> init(@PathVariable(value = "id") Long id,
                                        @PathVariable(value = "venda") Long venda) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        return new ResponseEntity<Usuario>(usuario.get(), HttpStatus.OK);
    }
    @GetMapping(value = "/{id}", produces = "application/json")
    @CacheEvict(value = "cacheuser", allEntries = true)
    @CachePut("cacheuser")
    public ResponseEntity<UsuarioDTO> init(@PathVariable(value = "id") Long id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        return new ResponseEntity<UsuarioDTO>(new UsuarioDTO(usuario.get()), HttpStatus.OK);
    }

    @GetMapping(value = "/", produces = "application/json")
    @CacheEvict(value = "cacheusuarios", allEntries = true)
    @CachePut("cacheusuarios")
    public ResponseEntity<Page<Usuario>> usuario() {
        PageRequest page = PageRequest.of(0, 5, Sort.by("nome"));
        Page<Usuario> list = usuarioRepository.findAll(page);
//        List<Usuario> list = (List<Usuario>) usuarioRepository.findAll();

        return new ResponseEntity<Page<Usuario>>(list, HttpStatus.OK);
    }

    @GetMapping(value = "/page/{pagina}", produces = "application/json")
    @CacheEvict(value = "cacheusuarios", allEntries = true)
    @CachePut("cacheusuarios")
    public ResponseEntity<Page<Usuario>> usuarioPagina(@PathVariable("pagina") int pagina) {
        PageRequest page = PageRequest.of(pagina, 5, Sort.by("nome"));
        Page<Usuario> list = usuarioRepository.findAll(page);

        return new ResponseEntity<Page<Usuario>>(list, HttpStatus.OK);
    }

    @GetMapping(value = "/usuarioPorNome/{nome}", produces = "application/json")
    public ResponseEntity<List<Usuario>> usuarioPorNome(@PathVariable("nome") String nome) throws InterruptedException{
        List<Usuario> list = (List<Usuario>) usuarioRepository.findUserByNome(nome);

        return new ResponseEntity<List<Usuario>>(list, HttpStatus.OK);
    }
    @PostMapping(value = "/", produces = "application/json")
    public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usuario) throws Exception {
        for (int pos = 0; pos < usuario.getTelefones().size(); pos++) {
            usuario.getTelefones().get(pos).setUsuario(usuario);
        }
        /*
        Consumindo API Externa de CEP
         */
        URL url = new URL("https://viacep.com.br/ws/"+usuario.getCep()+"/json/");
        URLConnection connection = url.openConnection();
        InputStream is = connection.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

        String cep = "";
        StringBuilder jsonCep = new StringBuilder();

        while ((cep = br.readLine()) != null) {
            jsonCep.append(cep);
        }

        Usuario userAux = new Gson().fromJson(jsonCep.toString(), Usuario.class);
        usuario.setCep(userAux.getCep());
        usuario.setLogradouro(userAux.getLogradouro());
        usuario.setComplemento(userAux.getComplemento());
        usuario.setBairro(userAux.getBairro());
        usuario.setLocalidade(userAux.getLocalidade());
        usuario.setUf(userAux.getUf());

        String senhacriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
        usuario.setSenha(senhacriptografada);
        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        implementacaoUserDetailsService.insereAcessoPadrao(usuarioSalvo.getId());
        return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);
    }

    @PutMapping(value = "/", produces = "application/json")
    public ResponseEntity<Usuario> atualizar(@RequestBody Usuario usuario) {
        for (int pos = 0; pos < usuario.getTelefones().size(); pos++) {
            usuario.getTelefones().get(pos).setUsuario(usuario);
        }

        Usuario userTemporario = usuarioRepository.findById(usuario.getId()).get();

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

    @DeleteMapping(value = "/removerTelefone/{id}", produces = "application/text")
    public String deleteTelefone(@PathVariable("id") Long id) {
        telefoneRepository.deleteById(id);

        return "ok";
    }
}
