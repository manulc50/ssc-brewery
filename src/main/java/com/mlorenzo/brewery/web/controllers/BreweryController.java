package com.mlorenzo.brewery.web.controllers;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.mlorenzo.brewery.security.annotations.BreweryReadPermission;
import com.mlorenzo.brewery.services.BreweryService;

@RequiredArgsConstructor
@Controller
public class BreweryController {
    private final BreweryService breweryService;

    // Anotación personalizada que contiene la anotación de Spring Security @PreAuthorize
    @BreweryReadPermission
    @GetMapping({"/breweries", "/breweries/index", "/breweries/index.html", "/breweries.html"})
    public String listBreweries(Model model) {
        model.addAttribute("breweries", breweryService.getAllBreweries());
        return "breweries/index";
    }

}
