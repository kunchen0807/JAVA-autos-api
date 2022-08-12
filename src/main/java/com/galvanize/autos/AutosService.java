package com.galvanize.autos;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AutosService {
    AutosRepository autosRepository;

    public AutosService(AutosRepository autosRepository) {
        this.autosRepository = autosRepository;
    }

    public AutosList getAutos() {
        // Query: select * from autos;
        // Put that in a list
        // Return a new AutoList with the list
        return new AutosList(autosRepository.findAll());
    }

    public AutosList getAutos(String color, String make) {
        List<Automobile> automobiles = autosRepository.findByColorContainsAndMakeContains(color, make);
        if (!automobiles.isEmpty()) {
            return new AutosList(automobiles);
        }
        return new AutosList();
    }

    public AutosList getAutosWithMatchingColor(String color) {
        List<Automobile> automobiles = autosRepository.findByColorContains(color);
        if (!automobiles.isEmpty()) {
            return new AutosList(automobiles);
        }
        return new AutosList();
    }

    public AutosList getAutosWithMatchingMake(String make) {
        List<Automobile> automobiles = autosRepository.findByMakeContains(make);
        if (!automobiles.isEmpty()) {
            return new AutosList(automobiles);
        }
        return new AutosList();
    }

    public Automobile addAuto(Automobile auto) {
        Automobile automobile = autosRepository.save(auto);
        return automobile;
    }

    public Automobile getAuto(String vin) {
        return autosRepository.findByVin(vin).orElse(null);
    }

    public Automobile updateAuto(String vin, String color, String owner) {
        Optional<Automobile> automobile = autosRepository.findByVin(vin);
        if(automobile.isPresent()) {
            automobile.get().setColor(color);
            automobile.get().setOwner(owner);
            return autosRepository.save(automobile.get());
        }
        return null;
    }

    public void deleteAuto(String vin) {
        Optional<Automobile> automobile = autosRepository.findByVin(vin);
        if(automobile.isPresent()){
            autosRepository.delete(automobile.get());
        } else {
            throw new AutoNotFoundException();
        }
    }
}
