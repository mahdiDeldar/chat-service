package com.chubock.chatservice.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    private static final String HEADER_STRING = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    private static final String SCOPES_CLAIM_NAME = "scopes";
    private static final String AUTH_TOKEN_SCOPE = "auth_scope";
    private static final String AUTHORITIES_CLAIM_NAME = "authorities";

    private static final String SECRET = "NLS0wmgVxJI3g3LrRxVSfEkyAVO5u5";

    public JWTAuthorizationFilter(AuthenticationManager authManager) {
        super(authManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {
        String header = req.getHeader(HEADER_STRING);

        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
            chain.doFilter(req, res);
            return;
        }

        String token = header.replace(TOKEN_PREFIX, "");

        UsernamePasswordAuthenticationToken authentication = getAuthentication(token);

        if (authentication != null)
            SecurityContextHolder.getContext().setAuthentication(authentication);

        chain.doFilter(req, res);
    }

    public UsernamePasswordAuthenticationToken getAuthentication(String token) {

        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(SECRET.getBytes()))
                .build()
                .verify(token);

        if (!Arrays.asList(decodedJWT.getClaim(SCOPES_CLAIM_NAME).asArray(String.class)).contains(AUTH_TOKEN_SCOPE))
            return null;

        String user = decodedJWT
                .getSubject();

        List<GrantedAuthority> authorities = new ArrayList<>();

        //noinspection unchecked
        decodedJWT.getClaim(AUTHORITIES_CLAIM_NAME)
                .as(List.class)
                .forEach(authority -> authorities.add(new SimpleGrantedAuthority(authority.toString())));

        if (user != null)
            return new JWTUsernamePasswordAuthenticationToken(user, null, authorities, token);

        return null;
    }

}
