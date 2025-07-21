package br.com.ecommerce.ecom.controller;

import br.com.ecommerce.ecom.dto.responses.DashboardSummaryDTO;
import br.com.ecommerce.ecom.dto.responses.StandardResponse;
import br.com.ecommerce.ecom.factory.ResponseFactory;
import br.com.ecommerce.ecom.service.BrandService;
import br.com.ecommerce.ecom.service.CategoryService;
import br.com.ecommerce.ecom.service.LocalUserService;
import br.com.ecommerce.ecom.service.OrderService;
import br.com.ecommerce.ecom.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private static final String DASHBOARD_BASE_PATH = "/api/dashboard";

    private final ProductService productService;
    private final BrandService brandService;
    private final CategoryService categoryService;
    private final OrderService orderService;
    private final LocalUserService userService;
    private final ResponseFactory responseFactory;

    @Operation(summary = "Get dashboard summary", description = "Returns total counts of products, brands, categories, orders, and users")
    @GetMapping("/summary")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponse<DashboardSummaryDTO>> getDashboardSummary() {
        DashboardSummaryDTO dto = DashboardSummaryDTO.builder()
                .products(productService.getProductCount())
                .brands(brandService.getBrandCount())
                .categories(categoryService.getCategoryCount())
                .orders(orderService.getOrderCount())
                .users(userService.getUserCount())
                .build();

        return responseFactory.okResponse(dto, "Dashboard summary fetched", DASHBOARD_BASE_PATH + "/summary");
    }
}