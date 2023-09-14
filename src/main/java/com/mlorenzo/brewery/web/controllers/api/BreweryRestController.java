package com.mlorenzo.brewery.web.controllers.api;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mlorenzo.brewery.domain.Brewery;
import com.mlorenzo.brewery.services.BreweryService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Controller
@RequestMapping("/api/v1/breweries")
public class BreweryRestController {
	private final BreweryService breweryService;

	@GetMapping
	@ResponseBody
    public List<Brewery> getBreweriesJson(){
        return breweryService.getAllBreweries();
    }
}
