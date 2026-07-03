package groupbuy_service.groupbuy.service;

import groupbuy_service.groupbuy.domain.Groupbuy;
import java.time.LocalDateTime;

public interface GroupbuyService {
    Groupbuy createGroupbuy(String productId, int targetQuantity, LocalDateTime startAt, LocalDateTime endAt);
    void openGroupbuy(String groupbuyId);
}
