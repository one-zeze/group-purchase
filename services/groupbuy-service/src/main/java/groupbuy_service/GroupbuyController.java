package groupbuy_service;

import groupbuy_service.groupbuy.domain.Groupbuy;
import groupbuy_service.groupbuy.service.GroupbuyService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/groupbuy")
@RequiredArgsConstructor
public class GroupbuyController {

    private final GroupbuyService groupbuyService;

    @PostMapping
    public String createGroupbuy(@RequestBody GroupbuyCreateRequest request) {
        Groupbuy groupbuy = groupbuyService.createGroupbuy(
                request.getProductId(),
                request.getTargetQuantity(),
                request.getStartAt(),
                request.getEndAt()
        );
        return "공동구매가 등록되었습니다. (ID: " + groupbuy.getGroupbuyId() + ")";
    }

    @PostMapping("/{groupbuyId}/open")
    public String openGroupbuy(@PathVariable String groupbuyId) {
        groupbuyService.openGroupbuy(groupbuyId);
        return "공동구매가 활성화(OPEN) 되었습니다.";
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class GroupbuyCreateRequest {
        private String productId;
        private int targetQuantity;
        private LocalDateTime startAt;
        private LocalDateTime endAt;
    }
}
