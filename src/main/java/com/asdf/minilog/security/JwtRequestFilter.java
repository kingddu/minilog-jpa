package com.asdf.minilog.security;

import io.jsonwebtoken.ExpiredJwtException;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(
            JwtRequestFilter.class
    );

    @Autowired private UserDetailsService jwtUserDetailsService;

    @Autowired private JwtUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(
            jakarta.servlet.http.HttpServletRequest request,
            jakarta.servlet.http.HttpServletResponse response,
            jakarta.servlet.FilterChain filterChain)
        throws jakarta.servlet.ServletException, IOException {

        //스웨거는 제외
        String uri = request.getRequestURI();
        if (uri.startsWith("/swagger-ui")
                || uri.startsWith("/v3/api-docs")
                || uri.startsWith("/api/v2/auth")) {
            filterChain.doFilter(request, response);
            return;
        }


        String requestTokenHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;

// 주석 부분은 원래 소스
/*
        if(requestTokenHeader != null && requestTokenHeader.startsWith(
                "Bearer ")) {
            jwt = requestTokenHeader.substring(7);
            try {
                username = jwtTokenUtil.getUsernameFromToken(jwt);
            }catch(IllegalArgumentException e) {
                logger.error("Unable to get JWT", e);
            } catch(ExpiredJwtException e){
                logger.warn("JWT has expired", e);
            }
        } else {
            logger.warn("JWT does not begin with Bearer String");
        }
*/

// 🔥 Authorization 헤더 없으면 그냥 통과
        if (requestTokenHeader == null || !requestTokenHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = requestTokenHeader.substring(7);

        try {
            username = jwtTokenUtil.getUsernameFromToken(jwt);
        } catch (IllegalArgumentException e) {
            logger.error("Unable to get JWT", e);
            filterChain.doFilter(request, response);
            return;
        } catch (ExpiredJwtException e) {
            logger.warn("JWT has expired", e);
            filterChain.doFilter(request, response);
            return;
        }


      if (username != null && SecurityContextHolder.getContext().
              getAuthentication() == null) {
          UserDetails userDetails = this.jwtUserDetailsService.
                  loadUserByUsername(username);

          if(jwtTokenUtil.validateToken(jwt, userDetails)){
              UsernamePasswordAuthenticationToken
                   usernamePasswordAuthenticationToken =
                      new UsernamePasswordAuthenticationToken(
                              userDetails, null, userDetails.getAuthorities());

              usernamePasswordAuthenticationToken.setDetails(
                      new WebAuthenticationDetailsSource().buildDetails(request));

              SecurityContextHolder.getContext().setAuthentication(
                      usernamePasswordAuthenticationToken);
          }else {
              logger.warn("JWT is not valid");
          }
      }
      filterChain.doFilter(request, response);

    }
}
