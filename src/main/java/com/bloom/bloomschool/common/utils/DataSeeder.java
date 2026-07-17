package com.bloom.bloomschool.common.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@Order(1)
public class DataSeeder implements CommandLineRunner {

    private final SeedService seedService;

    @Override
    public void run(String... args) {
        log.info("Running DataSeeder — seeding modules, permissions, roles...");
        seedService.seedAll();
        log.info("DataSeeder complete");
    }
}
