package edu.java.api.domain.dto;

import java.net.URI;
import java.time.OffsetDateTime;

public record LinkDto(long linkId, URI url, OffsetDateTime lastUpdate, OffsetDateTime lastCheck) {
}
