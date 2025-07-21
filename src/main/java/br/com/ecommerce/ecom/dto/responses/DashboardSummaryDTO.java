package br.com.ecommerce.ecom.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Dashboard summary counts")
public class DashboardSummaryDTO {
    private long products;
    private long brands;
    private long categories;
    private long orders;
    private long users;
}
