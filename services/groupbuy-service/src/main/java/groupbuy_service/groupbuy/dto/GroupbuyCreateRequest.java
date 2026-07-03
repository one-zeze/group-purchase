package groupbuy_service.groupbuy.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class GroupbuyCreateRequest {
    @NotBlank
    private String productId;
    @Min(1)
    private int targetQuantity;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
}
