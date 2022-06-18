package net.flawe.practical.accounting.controller;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.annotation.security.RolesAllowed;
import java.util.stream.Collectors;

@Controller
@RolesAllowed("USER")
public class GeneralController {

    @ModelAttribute
    public void addAttributes(@AuthenticationPrincipal UserDetails details, Model model) {
        model.addAttribute("roles", details.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
    }

    @GetMapping("/")
    public String home() {
        return "/stock";
    }

    @GetMapping("/stock")
    public String stock() {
        return "/stock";
    }

    @GetMapping("/invoice")
    public String invoice() {
        return "/invoice";
    }

    @GetMapping("/products")
    public String products() {
        return "/products";
    }

}
