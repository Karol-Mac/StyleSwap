package com.restapi.styleswap.controller;

import com.restapi.styleswap.payload.ClotheDto;
import com.restapi.styleswap.service.StorageService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/storage")
@SecurityRequirement(name = "bearerAuth")
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
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{clotheId}")
    public ResponseEntity<Void> removeClothe(@PathVariable int clotheId, Principal principal) {

        storageService.removeClothe(clotheId, principal.getName());
        return ResponseEntity.noContent().build();
    }

    //TODO: zakomentowane, bo nie ma obsługi kupna kilku przedmiotó na raz (w stripe)
//    @PostMapping("/buy")
//    public ResponseEntity<Void> buyAllCLothes(Principal principal) {
//
//        storageService.buyAllClothes(principal.getName());
//        return ResponseEntity.noContent().build();
//    }
}
