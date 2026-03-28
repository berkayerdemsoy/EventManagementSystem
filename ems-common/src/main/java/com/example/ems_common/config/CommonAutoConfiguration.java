package com.example.ems_common.config;

import com.example.ems_common.exceptions.GlobalExceptionHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import(GlobalExceptionHandler.class)
public class CommonAutoConfiguration {
}