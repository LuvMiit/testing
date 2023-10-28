package org.niias.asrb.kn;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.niias.asrb.kn.model.UserKnImpl;
import org.niias.asrb.kn.model.UserToken;
import org.niias.asrb.kn.repository.UserRepository;
import org.niias.asrb.kn.service.UserService;
import org.niias.asrb.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService us;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic().disable();
        http.csrf().disable();


        http.addFilterBefore(new AbstractAuthenticationProcessingFilter(new AntPathRequestMatcher("/authback", "GET")) {
            {
                setAuthenticationManager(authenticationManager());
                setSessionAuthenticationStrategy(new SessionFixationProtectionStrategy());
                setAuthenticationSuccessHandler(new AuthenticationSuccessHandler() {
                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse res, Authentication auth) {
                        //UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) auth;
                        res.setCharacterEncoding("UTF-8");
                        res.setStatus(HttpServletResponse.SC_OK);
                        try {
                            PrintWriter writer = res.getWriter();
                        //        res.sendRedirect(req.getContextPath()  + "/signin/?s="+new ObjectMapper().writeValueAsString(getUserInfo(((UserToken) auth))));
                            writer.write(new ObjectMapper().writeValueAsString(getUserInfo(((UserToken) auth))));
                            writer.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });
                setAuthenticationFailureHandler(new AuthenticationFailureHandler() {
                    @Override
                    public void onAuthenticationFailure(HttpServletRequest req, HttpServletResponse res, AuthenticationException e) throws IOException {
                        req.getSession().setAttribute("AUTH_ERROR", e);
                        //journalService.createAndSaveLogin(ActionType.LOGIN_FAILED, e.getMessage(), req, e);
                        res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                    }
                });
            }

            @Override
            public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
                try {
                    String key = request.getParameter("key");
                    final byte[] json = Base64.getDecoder().decode(key);
                    final Map map = new ObjectMapper().readValue(json, Map.class);
                    final String username = (String) map.get("l");
                    final String hash = (String) map.get("h");
                    final Long expireDate = (Long) map.get("e");
                    if (new Date().after(new Date(expireDate)))
                        throw new AuthenticationServiceException("Key expired");

                    return this.getAuthenticationManager().authenticate(new UserKey(username, hash, expireDate));

                } catch (Exception ex) {
                    //todo; add logger
                    throw new AuthenticationServiceException("Authentication error: " + ex.getMessage(), ex);
                }
            }
        }, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(new UsernamePasswordAuthenticationFilter() {

            @Override
            protected String obtainUsername(HttpServletRequest request) {
                return request.getParameter("login");
            }

            private final AntPathRequestMatcher matcher = new AntPathRequestMatcher("/login/", "POST");

            {
                setPostOnly(false);
                setAuthenticationManager(authenticationManager());
                setSessionAuthenticationStrategy(new SessionFixationProtectionStrategy());
                setAuthenticationSuccessHandler(new AuthenticationSuccessHandler() {
                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse res, Authentication auth) {
                        //UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) auth;
                        res.setCharacterEncoding("UTF-8");
                        res.setStatus(HttpServletResponse.SC_OK);
                        try {
                            PrintWriter writer = res.getWriter();
                            writer.write(new ObjectMapper().writeValueAsString(getUserInfo(((UserToken) auth))));
                            writer.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }



                        //Action action = journalService.createAndSaveLogin(ActionType.LOGIN_OK, token.getName(), req, null);
                        //req.getSession().setAttribute("AUTH_ACTION", action.getId());
                    }
                });
                setAuthenticationFailureHandler(new AuthenticationFailureHandler() {
                    @Override
                    public void onAuthenticationFailure(HttpServletRequest req, HttpServletResponse res, AuthenticationException e) throws IOException {
                        req.getSession().setAttribute("AUTH_ERROR", e);
                        //journalService.createAndSaveLogin(ActionType.LOGIN_FAILED, e.getMessage(), req, e);
                        res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                    }
                });
            }

            final String obtainRailway(HttpServletRequest request) {
                return request.getParameter("railway");
            }

            @Override
            public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
                if (!request.getMethod().equals("POST")) {
                    throw new AuthenticationServiceException(
                            "Authentication method not supported: " + request.getMethod());
                }

                String username = obtainUsername(request);
                String password = obtainPassword(request);
                String railway = obtainRailway(request);

                if (username == null) {
                    username = "";
                }

                if (password == null) {
                    password = "";
                }

                if (railway == null) {
                    railway = "0";
                }

                username = username.trim();
                password = password.trim();

                UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);

                // Allow subclasses to set the "details" property
                setDetails(request, authRequest);

                return this.getAuthenticationManager().authenticate(authRequest);
            }

            @Override
            protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
                return matcher.matches(request);
            }
        }, UsernamePasswordAuthenticationFilter.class);


        http
                .authorizeRequests()
                //.antMatchers("/**/*.js", "/**/*.css", "/**.woff2","/**.ico", "/", "/login", "/logout", "/Welcome", "/dev/**").permitAll()
                .antMatchers("/dev/**").permitAll()
                //.antMatchers("/api**/**").authenticated()
                .anyRequest().permitAll()
                .and().logout().invalidateHttpSession(true).deleteCookies("JSESSIONID");
    }

    /**
     * объединяем два вида информации о юзерах, общую для АСРБ и специфичную для КН
     *
     * @param auth
     * @return
     */
    private Map<String, Object> getUserInfo(UserToken auth) {
        Map<String, Object> ret = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> userInfo = objectMapper.convertValue(auth.getUser(), HashMap.class);
        Map<String, Object> knUserInfo = objectMapper.convertValue(new UserKnImpl(auth.getUser()), HashMap.class);
        ret.putAll(userInfo);
        ret.putAll(knUserInfo);
        return ret;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        return new AuthenticationProvider() {

            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                UsernamePasswordAuthenticationToken authToken = (UsernamePasswordAuthenticationToken) authentication;
                User user = userRepository.findFirstByLogin(authToken.getName());

                if (user == null) {
                    throw new UsernameNotFoundException(authToken.getName());
                }

                boolean isPassNotCorrect = (authToken instanceof UserKey)
                        ? !new BCryptPasswordEncoder().matches(user.getPassword() + ((UserKey) authToken).expireDate + user.getId(), (String) authToken.getCredentials())
                        : !new BCryptPasswordEncoder().matches((String) authToken.getCredentials(), user.getPassword());
                if (user.isBlocked() || isPassNotCorrect)
                    throw new BadCredentialsException(authToken.getName());

                us.populateVertical(user);
                return new UserToken(user);
            }

            @Override
            public boolean supports(Class<?> authentication) {
                return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
            }
        };
    }

    static class UserKey extends UsernamePasswordAuthenticationToken  {

        private final Long expireDate;

        public UserKey(String  principal, String  credentials, Long expireDate) {
            super(principal, credentials);
            this.expireDate = expireDate;
        }
    }
}
