package com.restapi.styleswap.service.impl;

import com.restapi.styleswap.entity.Storage;
import com.restapi.styleswap.exception.ApiException;
import com.restapi.styleswap.exception.ResourceNotFoundException;
import com.restapi.styleswap.payload.ClotheDto;
import com.restapi.styleswap.repository.StorageRepository;
import com.restapi.styleswap.service.StorageService;
import com.restapi.styleswap.utils.ClotheUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StorageServiceImpl implements StorageService {

    private static final Logger log = LoggerFactory.getLogger(StorageServiceImpl.class);
    private final StorageRepository storageRepository;
    private final ClotheUtils clotheUtils;

    public StorageServiceImpl(StorageRepository storageRepository, ClotheUtils clotheUtils) {
        this.storageRepository = storageRepository;
        this.clotheUtils = clotheUtils;
    }

    @Override
    public List<ClotheDto> getStorage(String email) {
        var storage = getStorageFromDB(email);

        log.info("User {} has {} clothes in storage", email, storage.getClothes().size());

        return storage.getClothes()
                .stream()
                .map(clotheUtils::mapToDto)
                .toList();
    }

    @Override
    public void addClothe(int clotheId, String email) {
        var storage = getStorageFromDB(email);

        var clothe = clotheUtils.getClotheFromDB(clotheId);

        if(clotheUtils.isOwner(clothe.getId(), email))
                throw new ApiException(HttpStatus.BAD_REQUEST, "You can't add your own clothe to storage");

        storage.getClothes().add(clothe);
        storageRepository.save(storage);
    }

    @Override
    public void removeClothe(int clotheId, String email) {
        var storage = getStorageFromDB(email);

        var clothe = clotheUtils.getClotheFromDB(clotheId);

        if (!storage.getClothes().remove(clothe))
            throw new ResourceNotFoundException("Clothe", "id", clotheId);

        storageRepository.save(storage);
    }

    //TODO: jak na razie nie mam obsługi kupna kilku rzeczy na raz
//    @Override
//    public void buyAllClothes(String email) {
//        var storage = getStorageFromDB(email);
//
//        storage.getClothes().forEach(clothe -> orderService.order(clothe.getId(), email));
//    }

    private Storage getStorageFromDB(String email) {
        return storageRepository.findByUserEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "user doesn't have storage"));
    }
}