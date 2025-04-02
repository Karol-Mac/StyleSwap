package com.restapi.styleswap.config;

import com.restapi.styleswap.payload.ClotheDto;
import com.restapi.styleswap.payload.ClotheResponse;
import com.restapi.styleswap.utils.assemblers.ClotheModelAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class HateoasResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    private final ClotheModelAssembler assembler;

    @Autowired
    public HateoasResponseBodyAdvice(ClotheModelAssembler assembler) {
        this.assembler = assembler;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {

        String acceptHeader = request.getHeaders().getFirst(HttpHeaders.ACCEPT);
//        String path = request.getURI().getPath();

        Object actualBody = body;
        if (body instanceof ResponseEntity<?> responseEntity) {
            actualBody = responseEntity.getBody();
        }

        if (acceptHeader != null && (
                        acceptHeader.toLowerCase().contains("application/hal+json") ||
                        acceptHeader.toLowerCase().contains("application/hal+forms+json"))) {

            if (actualBody instanceof ClotheDto dto) {
                return assembler.toModel(dto);
            } else if (actualBody instanceof ClotheResponse responseDto) {

                return assembler.toCollectionModel(responseDto);
            }
        }
        return body;
    }
}