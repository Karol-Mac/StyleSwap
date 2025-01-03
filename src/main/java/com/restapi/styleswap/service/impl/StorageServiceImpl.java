package com.restapi.styleswap.service.impl;

import com.restapi.styleswap.entity.Storage;
import com.restapi.styleswap.exception.ApiException;
import com.restapi.styleswap.exception.ResourceNotFoundException;
import com.restapi.styleswap.payload.ClotheDto;
import com.restapi.styleswap.repository.StorageRepository;
import com.restapi.styleswap.service.OrderService;
import com.restapi.styleswap.service.StorageService;
import com.restapi.styleswap.utils.ClotheUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StorageServiceImpl implements StorageService {

    private final StorageRepository storageRepository;
    private final ClotheUtils clotheUtils;
    private final OrderService orderService;

    public StorageServiceImpl(StorageRepository storageRepository, ClotheUtils clotheUtils,
                              OrderService orderService) {
        this.storageRepository = storageRepository;
        this.clotheUtils = clotheUtils;
        this.orderService = orderService;
    }

    @Override
    public List<ClotheDto> getStorage(String email) {
        var storage = getStorageFromDB(email);

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

    // FIXME: this might ba a bug - change loadData.sql file - every user should has a storage
    private Storage getStorageFromDB(String email) {
        return storageRepository.findByUserEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "unexpected one"));
    }
}