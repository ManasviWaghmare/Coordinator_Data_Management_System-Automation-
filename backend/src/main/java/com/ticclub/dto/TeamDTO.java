package com.ticclub.dto;

import lombok.Data;
import java.util.List;

@Data
public class TeamDTO {
    private Long id;
    private String name;
    private String description;
    private Long createdById;
    private String createdByUsername;
    private List<Long> memberIds;
}
