package br.com.ecommerce.ecom.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Dashboard summary counts")
public class DashboardSummaryDTO {

    @Schema(description = "Total number of products", example = "120")
    private long products;

    @Schema(description = "Total number of brands", example = "15")
    private long brands;

    @Schema(description = "Total number of categories", example = "10")
    private long categories;

    @Schema(description = "Total number of orders", example = "350")
    private long orders;

    @Schema(description = "Total number of users", example = "500")
    private long users;
}
