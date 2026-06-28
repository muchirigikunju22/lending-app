package com.lending.app.product.mapper;

import com.lending.app.product.api.dto.*;
import com.lending.app.product.domain.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "feeConfigurations", ignore = true)
    @Mapping(target = "tenureConfig", expression = "java(TenureConfig.of(request.tenureValue(), request.tenureType()))")
    LoanProduct toEntity(ProductRequest request);

    @Mapping(source = "tenureConfig.value", target = "tenureValue")
    @Mapping(source = "tenureConfig.type", target = "tenureType")
    ProductResponse toResponse(LoanProduct product);

    List<ProductResponse> toResponseList(List<LoanProduct> products);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "loanProduct", ignore = true)
    FeeConfiguration toEntity(FeeConfigRequest request);

    FeeConfigResponse toResponse(FeeConfiguration fee);

    List<FeeConfigResponse> toFeeResponseList(List<FeeConfiguration> fees);

    @Mapping(target = "tenureConfig", expression = "java(TenureConfig.of(request.tenureValue(), request.tenureType()))")
    void updateEntityFromRequest(ProductRequest request, @MappingTarget LoanProduct product);
}
