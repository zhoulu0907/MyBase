package com.cmsr.onebase.module.app.core.enums.auth;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuthDefaultEnum {

//   (name = "is_page_allowed")
     DefaultAllViewsAllowed(0),

//    @(name = "is_all_views_allowed")
     DefaultPageAllowed(0),

//    (name = "is_all_fields_allowed")
     DefaultAllFieldsAllowed(0);

     private final Integer defaultValue;

}
