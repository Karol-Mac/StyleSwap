package com.restapi.styleswap.controller;

import com.restapi.styleswap.payload.ClotheDto;
import com.restapi.styleswap.payload.ClotheResponse;
import com.restapi.styleswap.service.ClothesService;
import com.restapi.styleswap.utils.Constant;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/api/clothes")
public class ClotheController {
    private final ClothesService clothesService;

    public ClotheController(ClothesService clothesService) {
        this.clothesService = clothesService;
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ClotheResponse> getAllClothesFromCategory(
                        @PathVariable Long categoryId,
                        @RequestParam(required = false, defaultValue = Constant.PAGE_NO) int pageNo,
                        @RequestParam(required = false, defaultValue = Constant.PAGE_SIZE_LARGE) int pageSize,
                        @RequestParam(required = false, defaultValue = Constant.SORT_BY) String sortBy,
                        @RequestParam(required = false, defaultValue = Constant.DIRECTION) String direction){

        var response = clothesService.getAllClothesByCategory(
                            categoryId, pageNo, pageSize, sortBy, direction);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClotheDto> getClothe(@PathVariable long id, Principal principal){

        var clothe = clothesService.getClotheById(id, Optional.ofNullable(principal));
        return ResponseEntity.ok(clothe);
    }


    //OWNER-ONLY ACTIONS
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/my")
    public ResponseEntity<ClotheResponse> getAllUserClothes(
            @RequestParam(required = false, defaultValue = Constant.PAGE_NO) int pageNo,
            @RequestParam(required = false, defaultValue = Constant.PAGE_SIZE_SMALL) int pageSize,
            @RequestParam(required = false, defaultValue = Constant.SORT_BY) String sortBy,
            @RequestParam(required = false, defaultValue = Constant.DIRECTION) String direction,
            Principal principal) {

        var response = clothesService.getMyClothes(pageNo, pageSize, sortBy, direction, principal.getName());

        return ResponseEntity.ok(response);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<ClotheDto> createClothe(@RequestBody @Valid ClotheDto clotheDto,
                                                               Principal principal) {

        var createdClothe = clothesService.addClothe(clotheDto, principal.getName());

        return ResponseEntity
                .created(getLocation(createdClothe.getId()))
                .body(createdClothe);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PutMapping(value = "/{id}")
    public ResponseEntity<ClotheDto> updateClothe(@PathVariable Long id,
                                                  @RequestBody @Valid ClotheDto clotheDto,
                                                  Principal principal) {

        var updatedClothe = clothesService.updateClothe(id, clotheDto, principal.getName());

        return ResponseEntity.ok(updatedClothe);
    }

    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClothe(@PathVariable long id, Principal principal){
        clothesService.deleteClothe(id, principal.getName());

        return ResponseEntity.noContent().build();
    }

    private URI getLocation(Object resourceId) {
        return ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{resourceId}")
                .buildAndExpand(resourceId)
                .toUri();
    }
}