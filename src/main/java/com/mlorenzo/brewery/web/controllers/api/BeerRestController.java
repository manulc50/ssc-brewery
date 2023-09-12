package com.mlorenzo.brewery.web.controllers.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mlorenzo.brewery.services.BeerService;
import com.mlorenzo.brewery.web.models.BeerDto;
import com.mlorenzo.brewery.web.models.BeerPagedList;
import com.mlorenzo.brewery.web.models.BeerStyleEnum;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/beers")
@RestController
public class BeerRestController {
    private static final Integer DEFAULT_PAGE_NUMBER = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 25;

    private final BeerService beerService;

    @GetMapping(produces = { "application/json" })
    public ResponseEntity<BeerPagedList> listBeers(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                   @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                   @RequestParam(value = "beerName", required = false) String beerName,
                                                   @RequestParam(value = "beerStyle", required = false) BeerStyleEnum beerStyle,
                                                   @RequestParam(value = "showInventoryOnHand", required = false) Boolean showInventoryOnHand){
        log.debug("Listing Beers");
        if (showInventoryOnHand == null) 
            showInventoryOnHand = false;
        if (pageNumber == null || pageNumber < 0)
            pageNumber = DEFAULT_PAGE_NUMBER;
        if (pageSize == null || pageSize < 1)
            pageSize = DEFAULT_PAGE_SIZE;
        BeerPagedList beerList = beerService.listBeers(beerName, beerStyle, PageRequest.of(pageNumber, pageSize), showInventoryOnHand);
        return new ResponseEntity<>(beerList, HttpStatus.OK);
    }

    @GetMapping(path = {"/{beerId}"}, produces = { "application/json" })
    public ResponseEntity<BeerDto> getBeerById(@PathVariable("beerId") UUID beerId,
                                               @RequestParam(value = "showInventoryOnHand", required = false) Boolean showInventoryOnHand){
        log.debug("Get Request for BeerId: " + beerId);
        if (showInventoryOnHand == null)
            showInventoryOnHand = false;
        return new ResponseEntity<>(beerService.findBeerById(beerId, showInventoryOnHand), HttpStatus.OK);
    }

    @GetMapping(path = {"upc/{upc}"}, produces = { "application/json" })
    public ResponseEntity<BeerDto> getBeerByUpc(@PathVariable("upc") String upc){
        return new ResponseEntity<>(beerService.findBeerByUpc(upc), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Void> saveNewBeer(@Valid @RequestBody BeerDto beerDto){
        BeerDto savedDto = beerService.saveBeer(beerDto);
        HttpHeaders httpHeaders = new HttpHeaders();
        //todo hostname for uri
        httpHeaders.add("Location", "/api/v1/beers/" + savedDto.getId().toString());
        return new ResponseEntity<>(httpHeaders, HttpStatus.CREATED);
    }

    @PutMapping(path = {"/{beerId}"}, produces = { "application/json" })
    public ResponseEntity<Void> updateBeer(@PathVariable("beerId") UUID beerId, @Valid @RequestBody BeerDto beerDto){
        beerService.updateBeer(beerId, beerDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping({"/{beerId}"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBeer(@PathVariable("beerId") UUID beerId){
        beerService.deleteById(beerId);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<List<String>> badReqeustHandler(ConstraintViolationException e){
        List<String> errors = new ArrayList<>(e.getConstraintViolations().size());
        e.getConstraintViolations().forEach(constraintViolation -> {
            errors.add(constraintViolation.getPropertyPath().toString() + " : " + constraintViolation.getMessage());
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

}
