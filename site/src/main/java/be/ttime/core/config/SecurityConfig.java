package be.ttime.core.config;

import be.ttime.core.config.security.AuthenticationFailureHandler;
import be.ttime.core.config.security.CustomLoginAuthenticationEntryPoint;
import be.ttime.core.config.security.CustomUrlLogoutSuccessHandler;
import be.ttime.core.persistence.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

@Configuration
@EnableWebSecurity
@ComponentScan(basePackages = "be.ttime.core.config.security")
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private IUserService userService;

    @Autowired
    @Qualifier("authenticationProvider")
    private AuthenticationProvider authenticationProvider;

    @Autowired
    private PersistentTokenRepository tokenRepository;

    @Value("${dataSource.url}")
    private String dataSourceUrl;

    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandler;

    @Autowired
    private CustomLoginAuthenticationEntryPoint loginEntryPoint;

    @Autowired
    private CustomUrlLogoutSuccessHandler logoutSuccessHandler;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        //auth.inMemoryAuthentication().withUser("user").password("pass").roles("ADMIN");
        //auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder());
        auth.authenticationProvider(authenticationProvider).userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf()
                .ignoringAntMatchers("/plugin/**");

        http.authorizeRequests()
                .antMatchers("/admin/login").permitAll()
                .antMatchers("/admin/**").hasAuthority("READ_PRIVILEGE")
                .anyRequest().permitAll()
            .and()
                .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/")
                .failureUrl("/login?error=true")
                //.successHandler(authenticationSuccessHandler)
                .failureHandler(authenticationFailureHandler)
            .and()
                //.exceptionHandling().accessDeniedHandler(accessDeniedHandler)
                .rememberMe()
                .tokenRepository(tokenRepository)
                .tokenValiditySeconds(15 * 24 * 60 * 60) // 15 days
            .and()
                .exceptionHandling().authenticationEntryPoint(loginEntryPoint)
            .and()
                .logout().logoutSuccessHandler(logoutSuccessHandler);


        if(dataSourceUrl.contains(":h2")) {
            http.headers().frameOptions().disable();
            http.csrf().requireCsrfProtectionMatcher(new RequestMatcher() {
                private Pattern allowedMethods = Pattern.compile("^(GET|HEAD|TRACE|OPTIONS)$");
                private RegexRequestMatcher apiMatcher = new RegexRequestMatcher("/h2-console/.*", null);

                @Override
                public boolean matches(HttpServletRequest request) {
                    // No CSRF due to allowedMethod
                    if(allowedMethods.matcher(request.getMethod()).matches())
                        return false;

                    // No CSRF due to api call
                    if(apiMatcher.matches(request))
                        return false;

                    // CSRF for everything else that is not an API call or an allowedMethod
                    return true;
                }
            });
        }
    }
}
