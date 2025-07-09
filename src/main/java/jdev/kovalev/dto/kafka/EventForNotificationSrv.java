package jdev.kovalev.dto.kafka;

import lombok.Builder;

@Builder
public record EventForNotificationSrv(String email, String confirmationCode) {
}
