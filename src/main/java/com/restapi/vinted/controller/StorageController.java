package com.restapi.vinted.controller;

import com.restapi.vinted.payload.ClotheDto;
import com.restapi.vinted.service.StorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/storage")
public class StorageController {

    private final StorageService storageService;

    public StorageController(StorageService storageService){
        this.storageService = storageService;
    }

    @GetMapping
    public ResponseEntity<List<ClotheDto>> getStorage(Principal principal) {
        return ResponseEntity.ok(storageService.getStorage(principal.getName()));
    }

    @PostMapping("/{clotheId}")
    public ResponseEntity<Void> addClothe(@PathVariable int clotheId, Principal principal) {

        storageService.addClothe(clotheId, principal.getName());

        return null;
    }

    @PostMapping("/buy")
    public ResponseEntity<Void> buyAllCLothes(Principal principal) {

        storageService.buyAllClothes(principal.getName());
        return ResponseEntity.noContent().build();
    }


    @DeleteMapping("/{clotheId}")
    public ResponseEntity<Void> removeClothe(@PathVariable int clotheId, Principal principal) {

        storageService.removeClothe(clotheId, principal.getName());
        return ResponseEntity.noContent().build();
    }
}
