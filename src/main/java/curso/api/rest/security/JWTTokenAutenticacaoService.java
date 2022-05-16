package curso.api.rest.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
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
    public void addAuthentication(HttpServletResponse response, String username) throws Exception {
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
}
