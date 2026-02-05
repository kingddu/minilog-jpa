package com.asdf.minilog.dto;

import lombok.Data;
import lombok.NonNull;

@Data
public class FollowRequestDto {
    @NonNull private Long follwerId;
    @NonNull private Long followeeId;
}
