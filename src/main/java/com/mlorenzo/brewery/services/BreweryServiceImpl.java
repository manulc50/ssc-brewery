package com.mlorenzo.brewery.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.mlorenzo.brewery.domain.Brewery;
import com.mlorenzo.brewery.repositories.BreweryRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BreweryServiceImpl implements BreweryService{
    private final BreweryRepository breweryRepository;

    @Override
    public List<Brewery> getAllBreweries() {
        return breweryRepository.findAll();
    }
}
