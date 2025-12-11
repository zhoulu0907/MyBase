package com.cmsr.api.license;

import com.cmsr.api.license.dto.LicenseRequest;
//import com.cmsr.license.bo.F2CLicResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface LicenseApi {


    @PostMapping("/update")
    //F2CLicResult update(@RequestBody LicenseRequest request);
    void update(@RequestBody LicenseRequest request);

    @PostMapping("/validate")
    //F2CLicResult validate(@RequestBody LicenseRequest request);
    void validate(@RequestBody LicenseRequest request);

    @GetMapping("/version")
    String version();
}
