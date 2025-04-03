package com.restapi.styleswap.aspect;

import com.restapi.styleswap.payload.ClotheDto;
import com.restapi.styleswap.repository.ClotheRepository;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ClotheViewAspect {
    private final ClotheRepository clotheRepository;

    public ClotheViewAspect(ClotheRepository clotheRepository) {
        this.clotheRepository = clotheRepository;
    }

    @AfterReturning(pointcut = "execution(* com.restapi.styleswap.service.impl.ClothesServiceImpl.getClotheById(..))",
                    returning = "clotheDto")
    public ClotheDto increaseViewCount(ClotheDto clotheDto) {

        clotheRepository.increaseViewCount(clotheDto.getId());
        clotheDto.setViews(clotheDto.getViews() + 1);
        return clotheDto;
    }
}