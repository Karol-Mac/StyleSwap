package com.restapi.styleswap.service.impl;

import com.restapi.styleswap.entity.Category;
import com.restapi.styleswap.entity.Clothe;
import com.restapi.styleswap.entity.User;
import com.restapi.styleswap.exception.ResourceNotFoundException;
import com.restapi.styleswap.payload.ClotheDto;
import com.restapi.styleswap.payload.ClotheResponse;
import com.restapi.styleswap.repository.CategoryRepository;
import com.restapi.styleswap.repository.ClotheRepository;
import com.restapi.styleswap.service.ImageService;
import com.restapi.styleswap.service.ClothesService;
import com.restapi.styleswap.utils.ClotheUtils;

import com.restapi.styleswap.utils.UserUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
public class ClothesServiceImpl implements ClothesService {

    private final ClotheRepository clotheRepository;
    private final ImageService imageService;
    private final CategoryRepository categoryRepository;
    private final ClotheUtils clotheUtils;
    private final UserUtils userUtils;


    public ClothesServiceImpl(ClotheRepository clotheRepository, ImageService imageService,
                              CategoryRepository categoryRepository, ClotheUtils clotheUtils, UserUtils userUtils) {
        this.clotheRepository = clotheRepository;
        this.imageService = imageService;
        this.categoryRepository = categoryRepository;
        this.clotheUtils = clotheUtils;
        this.userUtils = userUtils;
    }


    @Override
    @PreAuthorize("permitAll()")
    public ClotheResponse getAllClothesByCategory(long categoryId, int pageNo, int pageSize,
                                                  String sortBy, String direction) {

        if (!categoryRepository.existsById(categoryId))
            throw new ResourceNotFoundException("Category", "id", categoryId);

        //create Page<Clothe> with custom DB method
        Pageable page = getPageable(pageNo, pageSize, sortBy, direction);
        Page<Clothe> clothes = clotheRepository.findByCategoryIdAndIsAvailableTrue(categoryId, page);

        return clotheUtils.getClotheResponse(pageNo, pageSize, clothes);
    }

    @Override
    @Transactional
    @PreAuthorize("permitAll()")
    public ClotheDto getClotheById(long clotheId, Optional<Principal> principal) {

        Clothe clothe = clotheRepository.findById(clotheId)
                .orElseThrow( ()-> new ResourceNotFoundException("Clothe", "id", clotheId));

        if(principal.isEmpty() || !clotheUtils.isOwner(clotheId, principal.get().getName())) {
            clothe.setViews(clothe.getViews() + 1);
            clotheRepository.save(clothe);
        }

        return clotheUtils.mapToDto(clothe);
    }
    
    @Override
    @Transactional
    public ClotheDto addClothe(ClotheDto clotheDto, List<MultipartFile> images, String email) {
        User user = userUtils.getUser(email);       //TODO: can I get rid of it?

        Clothe clothe = clotheUtils.mapToEntity(clotheDto);
        clothe.setUser(user);
        clothe.setAvailable(true);

        var imageNames = images.stream().map(imageService::saveImage).toList();
        clothe.setImages(imageNames);

        Clothe savedClothe = clotheRepository.save(clothe);

        return clotheUtils.mapToDto(savedClothe);
    }

    @Override
    @Transactional(readOnly = true)
    public ClotheResponse getMyClothes(int pageNo, int pageSize, String sortBy,
                                       String direction, String email) {

        Pageable page = getPageable(pageNo, pageSize, sortBy, direction);

        //getting page of clothes owned by logged-in user
        Page<Clothe> clothes = clotheRepository.findByUserEmail(email, page);
        return clotheUtils.getClotheResponse(pageNo, pageSize, clothes);
    }

    @Override
    @PreAuthorize("@clotheUtils.isOwner(#id, #email)")
    @Transactional
    public ClotheDto updateClothe(long id, ClotheDto clotheDto,
                                  List<MultipartFile> newImages, List<String> deletedImages,String email) {

        Clothe clothe = clotheUtils.getClotheFromDB(id);

        clothe.setName(clotheDto.getName());
        clothe.setDescription(clotheDto.getDescription());
        clothe.setPrice(clotheDto.getPrice());
        clothe.setSize(clotheDto.getSize());
        clothe.setMaterial(clotheDto.getMaterial());
        clothe.setAvailable(clotheDto.isAvailable());

        clothe.setCategory(new Category(clotheDto.getCategoryId()));

        imageService.updateImages(clothe, newImages, deletedImages);

        Clothe updatedClothe = clotheRepository.save(clothe);
        return clotheUtils.mapToDto(updatedClothe);
    }

    @Override
    @PreAuthorize("@clotheUtils.isOwner(#id, #email)")
    @Transactional
    public void deleteClothe(long id, String email) {

        Clothe clothe = clotheUtils.getClotheFromDB(id);
        clothe.setAvailable(false);

        clotheRepository.save(clothe);
    }

    private static Pageable getPageable(int pageNo, int pageSize, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        return PageRequest.of(pageNo, pageSize, sort);
    }
}