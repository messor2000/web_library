package kpi.diploma.ovcharenko.config;

import kpi.diploma.ovcharenko.service.user.LibraryUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private LibraryUserService userDetailsService;

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf()
                .disable()
                .authorizeRequests()
                    .antMatchers("/registration").not().fullyAuthenticated()
                    .antMatchers("/add", "/files/upload", "/delete/*", "/edit/*", "/delete/category/*" , "/book/delete/tag/*",
                            "/book/delete/category/*" , "/admin/allUserBooks/*", "/admin/allUsers", "/admin/update/user", "/admin/approve/book/*",
                            "/admin/putBackIntoTheLibrary/*",
                            "/admin/delete/user/*", "/admin/delete/bookCard/*", "/admin/showBookingCards")
                    .hasRole("ADMIN")
                    .antMatchers("/profile", "/user/change/password", "/user/update/password", "/user/update/profile").hasAnyRole("USER", "ADMIN")
                    .antMatchers("/",
                            "/{category}",
                            "/find/*",
                            "/table/**",
                            "/forgetPassword/*",
                            "/resetPassword/*",
                            "/user/save/password",
                            "/update/password",
                            "/user/reset/password/**",
                            "/registration/confirm/**",
                            "/user/resendRegistrationToken",
                            "/bad/user",
                            "/download/book/*",
                            "/download/pdf/**",
                            "/covers/**",
                            "/js/**",
                            "/css/**",
                            "/img/**",
                            "/webjars/**").permitAll()
                .anyRequest().authenticated()
                    .and()
                .formLogin()
                    .loginPage("/login")
                .defaultSuccessUrl("/")
                    .permitAll()
                .and()
                    .logout()
                    .permitAll()
                    .logoutSuccessUrl("/");
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userDetailsService);
        auth.setPasswordEncoder(PasswordEncoder.passwordEncoder());
        return auth;
    }

    @Bean
    public AuthenticationManager customAuthenticationManager() throws Exception {
        return authenticationManager();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(PasswordEncoder.passwordEncoder());
    }
}
