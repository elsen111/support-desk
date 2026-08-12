package com.supportdesk.web.dto.request;

import jakarta.validation.constraints.NotNull;

public record ChangeStatusRequest(
        @NotNull String status
) {}