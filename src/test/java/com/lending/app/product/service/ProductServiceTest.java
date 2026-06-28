package com.lending.app.product.service;

import com.lending.app.product.api.dto.FeeConfigRequest;
import com.lending.app.product.api.dto.ProductRequest;
import com.lending.app.product.domain.model.*;
import com.lending.app.product.domain.repository.ProductRepository;
import com.lending.app.product.mapper.ProductMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    @Test
    void shouldCreateProduct() {
        ProductRequest request = new ProductRequest(
                "Test Product", "Description", 30, TenureType.DAYS,
                new BigDecimal("100"), new BigDecimal("5000"),
                new BigDecimal("15"), LoanType.LUMP_SUM);

        LoanProduct product = LoanProduct.builder()
                .name("Test Product")
                .tenureConfig(TenureConfig.of(30, TenureType.DAYS))
                .minAmount(new BigDecimal("100"))
                .maxAmount(new BigDecimal("5000"))
                .interestRate(new BigDecimal("15"))
                .loanType(LoanType.LUMP_SUM)
                .build();

        when(productMapper.toEntity(request)).thenReturn(product);
        when(productRepository.save(any())).thenReturn(product);

        LoanProduct result = productService.createProduct(request);

        assertNotNull(result);
        assertEquals("Test Product", result.getName());
        verify(productRepository).save(any());
    }

    @Test
    void shouldAddFeeConfiguration() {
        Long productId = 1L;
        FeeConfigRequest request = new FeeConfigRequest(
                FeeType.SERVICE, FeeConfiguration.CalculationMethod.FIXED,
                new BigDecimal("25"), null);

        LoanProduct product = LoanProduct.builder()
                .id(productId)
                .name("Test")
                .build();
        product.setFeeConfigurations(new java.util.ArrayList<>());

        FeeConfiguration fee = FeeConfiguration.builder()
                .feeType(FeeType.SERVICE)
                .calculationMethod(FeeConfiguration.CalculationMethod.FIXED)
                .amount(new BigDecimal("25"))
                .build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productMapper.toEntity(request)).thenReturn(fee);
        when(productRepository.save(any())).thenReturn(product);

        LoanProduct result = productService.addFeeConfiguration(productId, request);

        assertNotNull(result);
        assertEquals(1, result.getFeeConfigurations().size());
    }

    @Test
    void shouldFindAllProducts() {
        when(productRepository.findAll()).thenReturn(Arrays.asList(
                LoanProduct.builder().name("Product 1").build(),
                LoanProduct.builder().name("Product 2").build()
        ));

        var result = productService.findAll();

        assertEquals(2, result.size());
    }
}
