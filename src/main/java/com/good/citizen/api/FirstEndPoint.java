package com.good.citizen.api;

import com.good.citizen.api.request.EmployeeRecordRequest;
import com.good.citizen.api.request.EmployeeRequest;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("${application.endpoints.first}")
public class FirstEndPoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(FirstEndPoint.class);

    @PostMapping("employee")
    @ApiOperation("This endpoint does something")
    public String getHello(@RequestBody EmployeeRequest request) {
        LOGGER.info("Got request: {}", request);

        return "hello";
    }

    @PostMapping("employee-record")
    @ApiOperation("This endpoint does something")
    public String getHello(@RequestBody EmployeeRecordRequest request) {
        LOGGER.info("Got request: {}", request);

        return "hello";
    }
}