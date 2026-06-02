package groupbuy_service.participation.domain;

public record ParticipationRequest(
    String productId,
    String userId,
    int quantity
) {
}
