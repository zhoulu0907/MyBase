package com.cmsr.onebase.module.app.enums.protocol.application;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResourceGroups {
    private Infrastructure infrastructure;

    private BusinessEntities businessEntities;

    private Pages pages;

    private Navigation navigation;
}
