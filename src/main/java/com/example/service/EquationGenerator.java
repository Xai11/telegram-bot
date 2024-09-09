package com.example.service;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@PropertySource("application.properties")
public class EquationGenerator {
    @Value("${equation.easy.url}")
    String equation_easy;

    @Value("${equation.normal.url}")
    String equation_normal;

    @Value("${equation.hard.url}")
    String equation_hard;


    public String getEquationUrl(int number) {
        String url = "";
        switch (number) {
            case 0:
                url = equation_easy;
                break;
            case 1:
                url = equation_normal;
                break;
            case 2:
                url = equation_hard;
                break;
            default:
        }
        return url;
    }
}
