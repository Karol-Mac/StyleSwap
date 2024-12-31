package com.restapi.styleswap.controller;

import com.restapi.styleswap.exception.ApiException;
import com.restapi.styleswap.payload.ClotheDto;
import com.restapi.styleswap.payload.ClotheResponse;
import com.restapi.styleswap.service.ClothesService;
import com.restapi.styleswap.utils.Constant;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/clothes")
public class ClotheController {
    private final ClothesService clothesService;


    public ClotheController(ClothesService clothesService) {
        this.clothesService = clothesService;
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<?> getAllClothesFromCategory(
                                        @PathVariable Long categoryId,
                                        @RequestParam(required = false, defaultValue = Constant.PAGE_NO) int pageNo,
                                        @RequestParam(required = false, defaultValue = Constant.PAGE_SIZE_LARGE) int pageSize,
                                        @RequestParam(required = false, defaultValue = Constant.SORT_BY) String sortBy,
                                        @RequestParam(required = false, defaultValue = Constant.DIRECTION) String direction){

        return ResponseEntity.ok(
                clothesService.getAllClothesByCategory(categoryId, pageNo, pageSize, sortBy, direction));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClotheDto> getClothe(@PathVariable long id, Principal principal){
        return ResponseEntity.ok(clothesService.getClotheById(id, Optional.ofNullable(principal)));
    }


    //OWNER-ONLY ACTIONS
    @GetMapping("/my")
    public ResponseEntity<ClotheResponse> getAllUserClothes(
            @RequestParam(required = false, defaultValue = Constant.PAGE_NO) int pageNo,
            @RequestParam(required = false, defaultValue = Constant.PAGE_SIZE_SMALL) int pageSize,
            @RequestParam(required = false, defaultValue = Constant.SORT_BY) String sortBy,
            @RequestParam(required = false, defaultValue = Constant.DIRECTION) String direction,
            Principal principal) {

        return ResponseEntity.ok(clothesService.getMyClothes(pageNo, pageSize, sortBy, direction, principal.getName()));
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE })
    public ResponseEntity<ClotheDto> createClothe(@RequestPart("clothe") @Valid ClotheDto clotheDto,
                                                  @RequestPart("images") List<MultipartFile> images,
                                                  Principal principal) {
        if(images.size() > 5) throw new ApiException(HttpStatus.BAD_REQUEST, Constant.IMAGES_VALIDATION_FAILED);

        return new ResponseEntity<>(clothesService.addClothe(clotheDto, images, principal.getName()), HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public ResponseEntity<ClotheDto> updateClothe(@PathVariable Long id,
                                                  @RequestPart("clothe") @Valid ClotheDto clotheDto,
                                                  @RequestPart(name = "newImages", required = false) List<MultipartFile> newImages,
                                                  @RequestPart(name = "deletedImages", required = false) List<String> deletedImages,
                                                  Principal principal) {

        return ResponseEntity.ok(clothesService.updateClothe(id, clotheDto, newImages, deletedImages, principal.getName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClothe(@PathVariable long id, Principal principal){
        clothesService.deleteClothe(id, principal.getName());

        return ResponseEntity.noContent().build();
    }
}