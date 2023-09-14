package com.mlorenzo.brewery.web.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import com.mlorenzo.brewery.domain.Beer;
import com.mlorenzo.brewery.repositories.BeerRepository;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@SessionAttributes("beer")
@Controller
@RequestMapping("/beers")
public class BeerController {
    private final BeerRepository beerRepository;

    @RequestMapping("/find")
    public String findBeers(Model model) {
        model.addAttribute("beer", Beer.builder().build());
        return "beers/findBeers";
    }

    @GetMapping
    public String processFindFormReturnMany(Beer beer, BindingResult result, Model model) {
        // find beers by name
        //ToDO: Add Service
        //ToDO: Get paging data from view
        Page<Beer> pagedResult = beerRepository.findAllByBeerNameIsLike("%" + beer.getBeerName() + "%", createPageRequest(0, 10, Sort.Direction.DESC, "beerName"));
        List<Beer> beerList = pagedResult.getContent();
        if (beerList.isEmpty()) {
            // no beers found
            result.rejectValue("beerName", "notFound", "not found");
            return "beers/findBeers";
        }
        else if (beerList.size() == 1) {
            // 1 beer found
            beer = beerList.get(0);
            return "redirect:/beers/" + beer.getId();
        }
        else {
            // multiple beers found
            model.addAttribute("selections", beerList);
            return "beers/beerList";
        }
    }

    @GetMapping("/{beerId}")
    public ModelAndView showBeer(@PathVariable UUID beerId) {
        ModelAndView mav = new ModelAndView("beers/beerDetails");
        //ToDO: Add Service
        mav.addObject(beerRepository.findById(beerId).get());
        return mav;
    }

    @GetMapping("/new")
    public String initCreationForm(Model model) {
        model.addAttribute("beer", Beer.builder().build());
        return "beers/createOrUpdateBeer";
    }

    @GetMapping("/{beerId}/edit")
    public String initUpdateBeerForm(@PathVariable UUID beerId, Model model) {
    	Optional<Beer> optionalBeer = beerRepository.findById(beerId);
        if (optionalBeer.isPresent())
            model.addAttribute("beer", optionalBeer.get());
        return "beers/createOrUpdateBeer";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public String processCreationOrUpdationForm(@Valid Beer beer, BindingResult result, SessionStatus sessionStatus) {
        if (result.hasErrors())
            return "beers/createOrUpdateBeer";
        else {
        	//ToDO: Add Service
            Beer savedBeer = beerRepository.save(beer);
            sessionStatus.setComplete();
            return "redirect:/beers/" + savedBeer.getId();
        }
    }

    private PageRequest createPageRequest(int page, int size, Sort.Direction sortDirection, String propertyName) {
        return PageRequest.of(page, size, Sort.by(sortDirection, propertyName));
    }
}


