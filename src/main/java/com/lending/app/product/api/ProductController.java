package com.lending.app.product.api;

import com.lending.app.product.api.dto.*;
import com.lending.app.product.domain.model.FeeConfiguration;
import com.lending.app.product.domain.model.LoanProduct;
import com.lending.app.product.mapper.ProductMapper;
import com.lending.app.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Loan product management APIs")
public class ProductController {

    private final ProductService productService;
    private final ProductMapper productMapper;

    @PostMapping
    @Operation(summary = "Create a new loan product")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        LoanProduct product = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(productMapper.toResponse(product));
    }

    @GetMapping
    @Operation(summary = "List all products")
    public ResponseEntity<List<ProductResponse>> listProducts() {
        List<LoanProduct> products = productService.findAll();
        return ResponseEntity.ok(productMapper.toResponseList(products));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product details")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable @Parameter(example = "1") Long id) {
        LoanProduct product = productService.findById(id);
        return ResponseEntity.ok(productMapper.toResponse(product));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update product")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable @Parameter(example = "1") Long id,
                                                          @Valid @RequestBody ProductRequest request) {
        LoanProduct product = productService.updateProduct(id, request);
        return ResponseEntity.ok(productMapper.toResponse(product));
    }

    @PostMapping("/{id}/fees")
    @Operation(summary = "Add fee configuration to product")
    public ResponseEntity<ProductResponse> addFee(@PathVariable @Parameter(example = "1") Long id,
                                                   @Valid @RequestBody FeeConfigRequest request) {
        LoanProduct product = productService.addFeeConfiguration(id, request);
        return ResponseEntity.ok(productMapper.toResponse(product));
    }

    @DeleteMapping("/{id}/fees/{feeId}")
    @Operation(summary = "Remove fee configuration from product")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFee(@PathVariable @Parameter(example = "1") Long id,
                          @PathVariable @Parameter(example = "1") Long feeId) {
        productService.removeFeeConfiguration(id, feeId);
    }

    @GetMapping("/{id}/fees")
    @Operation(summary = "Get product fee configurations")
    public ResponseEntity<List<FeeConfigResponse>> getFees(@PathVariable @Parameter(example = "1") Long id) {
        List<FeeConfiguration> fees = productService.getFeeConfigurations(id);
        return ResponseEntity.ok(productMapper.toFeeResponseList(fees));
    }
}
