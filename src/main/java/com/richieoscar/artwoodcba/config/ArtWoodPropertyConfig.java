package com.richieoscar.artwoodcba.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({ArtWoodProperties.class})
public class ArtWoodPropertyConfig {
}
