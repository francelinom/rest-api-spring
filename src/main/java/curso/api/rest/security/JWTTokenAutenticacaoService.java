package curso.api.rest.security;

import curso.api.rest.ApplicationContextLoad;
import curso.api.rest.model.Usuario;
import curso.api.rest.repository.UsuarioRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@Service
@Component
public class JWTTokenAutenticacaoService {

    private static final long EXPIRATION_TIME = 172800000;
    private static final String SECRET = "SenhaExtremamenteSecreta";
    private static final String TOKEN_PREFIX = "Bearer";
    private static final String HEADER_STRING = "Authorization";

    /*
    Gerando token de autenticado e adicionando ao cabeçalho e reposta Http
     */
    public void addAuthentication(HttpServletResponse response, String username) throws IOException {
        /*
        Montagem do token
         */
        String JWT = Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SECRET).compact();

        String token = TOKEN_PREFIX + " " + JWT;

        response.addHeader(HEADER_STRING, token);

        response.getWriter().write("{\"Authorization\": \""+token+"\"}");

    }

    /*
    Retorna usuário autenticado ou null
     */
    public Authentication getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(HEADER_STRING);
        String tokenLimpo = token.replace(TOKEN_PREFIX, "").trim();

        if (token != null) {
            String user = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(tokenLimpo)
                    .getBody().getSubject();
            if (user != null) {
                Usuario usuario = ApplicationContextLoad.getApplicationContext()
                        .getBean(UsuarioRepository.class).findUserByLogin(user);

                if (usuario != null) {
                    if (tokenLimpo.equalsIgnoreCase(usuario.getToken())) {
                        return new UsernamePasswordAuthenticationToken(
                                usuario.getUsername(),
                                usuario.getPassword(),
                                usuario.getAuthorities()
                        );
                    }
                }
            }
        }
        return null;
    }
}
