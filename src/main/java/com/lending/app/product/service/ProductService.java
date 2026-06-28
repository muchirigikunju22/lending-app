package com.lending.app.product.service;

import com.lending.app.common.exception.ResourceNotFoundException;
import com.lending.app.product.api.dto.FeeConfigRequest;
import com.lending.app.product.api.dto.ProductRequest;
import com.lending.app.product.domain.model.FeeConfiguration;
import com.lending.app.product.domain.model.LoanProduct;
import com.lending.app.product.domain.repository.ProductRepository;
import com.lending.app.product.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional
    public LoanProduct createProduct(ProductRequest request) {
        log.info("Creating new product: {}", request.name());
        LoanProduct product = productMapper.toEntity(request);
        return productRepository.save(product);
    }

    public List<LoanProduct> findAll() {
        return productRepository.findAll();
    }

    public List<LoanProduct> findAllActive() {
        return productRepository.findByActiveTrue();
    }

    public LoanProduct findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LoanProduct", id));
    }

    @Transactional
    public LoanProduct updateProduct(Long id, ProductRequest request) {
        log.info("Updating product: {}", id);
        LoanProduct product = findById(id);
        productMapper.updateEntityFromRequest(request, product);
        return productRepository.save(product);
    }

    @Transactional
    public LoanProduct addFeeConfiguration(Long productId, FeeConfigRequest request) {
        log.info("Adding fee configuration to product: {}", productId);
        LoanProduct product = findById(productId);
        FeeConfiguration fee = productMapper.toEntity(request);
        product.addFeeConfiguration(fee);
        return productRepository.save(product);
    }

    @Transactional
    public void removeFeeConfiguration(Long productId, Long feeId) {
        log.info("Removing fee configuration: {} from product: {}", feeId, productId);
        LoanProduct product = findById(productId);
        FeeConfiguration fee = product.getFeeConfigurations().stream()
                .filter(f -> f.getId().equals(feeId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("FeeConfiguration", feeId));
        product.removeFeeConfiguration(fee);
        productRepository.save(product);
    }

    public List<FeeConfiguration> getFeeConfigurations(Long productId) {
        LoanProduct product = findById(productId);
        return product.getFeeConfigurations();
    }
}
