package com.restapi.styleswap.controller;

import com.restapi.styleswap.payload.ClotheDto;
import com.restapi.styleswap.payload.ClotheModelResponse;
import com.restapi.styleswap.payload.ClotheResponse;
import com.restapi.styleswap.service.ClothesService;
import com.restapi.styleswap.utils.Constant;
import com.restapi.styleswap.utils.assemblers.ClotheModelAssembler;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/clothes")
public class ClotheController {
    private final ClothesService clothesService;
    private final ClotheModelAssembler assembler;

    public ClotheController(ClothesService clothesService, ClotheModelAssembler assembler) {
        this.clothesService = clothesService;
        this.assembler = assembler;
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ClotheModelResponse> getAllClothesFromCategory(
                        @PathVariable Long categoryId,
                        @RequestParam(required = false, defaultValue = Constant.PAGE_NO) int pageNo,
                        @RequestParam(required = false, defaultValue = Constant.PAGE_SIZE_LARGE) int pageSize,
                        @RequestParam(required = false, defaultValue = Constant.SORT_BY) String sortBy,
                        @RequestParam(required = false, defaultValue = Constant.DIRECTION) String direction){

        var response = clothesService.getAllClothesByCategory(
                            categoryId, pageNo, pageSize, sortBy, direction);
        ClotheModelResponse modelResponse = getClotheModelResponse(response);

        return ResponseEntity.ok(modelResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ClotheDto>> getClothe(@PathVariable long id, Principal principal){

        var clothe = clothesService.getClotheById(id, Optional.ofNullable(principal));
        return ResponseEntity.ok(assembler.toModel(clothe));
    }


    //OWNER-ONLY ACTIONS
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/my")
    public ResponseEntity<ClotheModelResponse> getAllUserClothes(
            @RequestParam(required = false, defaultValue = Constant.PAGE_NO) int pageNo,
            @RequestParam(required = false, defaultValue = Constant.PAGE_SIZE_SMALL) int pageSize,
            @RequestParam(required = false, defaultValue = Constant.SORT_BY) String sortBy,
            @RequestParam(required = false, defaultValue = Constant.DIRECTION) String direction,
            Principal principal) {

        var response = clothesService.getMyClothes(pageNo, pageSize, sortBy, direction, principal.getName());
        ClotheModelResponse modelResponse = getClotheModelResponse(response);

        return ResponseEntity.ok(modelResponse);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<EntityModel<ClotheDto>> createClothe(@RequestPart("clothe") @Valid ClotheDto clotheDto,
                                                               Principal principal) {

        var createdClothe = clothesService.addClothe(clotheDto, principal.getName());

        return ResponseEntity
                .created(getLocation(createdClothe.getId()))
                .body(assembler.toModel(createdClothe));
    }

//    @PostMapping("/{id}/images")
//    public ResponseEntity<Void> addImagesToClothe(@PathVariable Long id,
//                                                 @RequestPart("images") List<MultipartFile> images,
//                                                 Principal principal) {
//
//        clothesService.addImagesToClothe(id, images, principal.getName());
//        return ResponseEntity.noContent().build();
//    }

    @SecurityRequirement(name = "bearerAuth")
    @PutMapping(value = "/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public ResponseEntity<EntityModel<ClotheDto>> updateClothe(@PathVariable Long id,
                                                  @RequestPart("clothe") @Valid ClotheDto clotheDto,
                                                  @RequestPart(name = "newImages", required = false) List<MultipartFile> newImages,
                                                  @RequestPart(name = "deletedImages", required = false) List<String> deletedImages,
                                                  Principal principal) {

        var updatedClothe = clothesService.updateClothe(id, clotheDto, newImages, deletedImages, principal.getName());

        return ResponseEntity.ok(assembler.toModel(updatedClothe));
    }

    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClothe(@PathVariable long id, Principal principal){
        clothesService.deleteClothe(id, principal.getName());

        return ResponseEntity.noContent().build();
    }

    private ClotheModelResponse getClotheModelResponse(ClotheResponse clotheResponse) {
        ClotheModelResponse modelResponse = ClotheModelResponse.from(clotheResponse);
        modelResponse.setClothes(assembler.toCollectionModel(clotheResponse.getClothes()));
        return modelResponse;
    }

    private URI getLocation(Object resourceId) {
        return ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{resourceId}")
                .buildAndExpand(resourceId)
                .toUri();
    }
}