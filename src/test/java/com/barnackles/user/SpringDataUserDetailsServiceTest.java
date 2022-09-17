package com.barnackles.user;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpringDataUserDetailsServiceTest {

    @Test
    void isLoginUserNameOrEmail() {
        List<String> emails =
                List.of(
"gracja6@gmail.com",
        "adrian.urban94@hotmail.com",
        "walentyna.konieczny90@hotmail.com",
        "stefan.walkowiak68@hotmail.com",
        "maurycy54@gmail.com",
        "galfryd60@hotmail.com",
        "anna15@yahoo.com",
        "bertram.stefanski@yahoo.com",
        "julia.krakowiak@gmail.com",
        "wincenty_bielski@hotmail.com",
        "walerian95@hotmail.com",
        "herbert_adamiak@hotmail.com",
        "karolina_kula@hotmail.com",
        "sebastian2@gmail.com",
        "izajasz82@hotmail.com",
        "juliusz_czechowski89@gmail.com",
        "pankracy.stolarski@yahoo.com",
        "wirginia_stolarczyk@hotmail.com",
        "walentyna17@hotmail.com",
        "zoe.mrozek10@hotmail.com");

        List<String> nonEmails = List.of(
                "nS715YMdoQ",
                "XtAGLZaiyW",
                "FEScrvRlWu",
                "4U56XFXxzG",
                "azjW8oU3Ty",
                "9qlmtsRBkW",
                "ibqNeAm1m1",
                "YC0ioJUjgY",
                "c8lPi0grmr",
                "0odmwQmeTc"
        );

        SpringDataUserDetailsService springDataUserDetailsService = new SpringDataUserDetailsService();


        emails.forEach(t -> assertTrue(springDataUserDetailsService.LoginIsEmail(t)));
        nonEmails.forEach(t -> assertFalse(springDataUserDetailsService.LoginIsEmail(t)));




    }
}