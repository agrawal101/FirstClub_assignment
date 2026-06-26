package com.firstclub.membership.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * Exposes the system clock as a bean so any time-dependent logic (expiry, window resets)
 * can be driven by a fixed clock in tests instead of calling LocalDate.now() directly.
 */
@Configuration
public class ClockConfig {

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
