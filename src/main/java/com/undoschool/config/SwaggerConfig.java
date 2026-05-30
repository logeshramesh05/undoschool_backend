package com.undoschool.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                // Info 
                .info(new Info()
                        .title("🎓 UndoSchool Booking System API")
                        .version("1.0.0")
                        .description("""
                                ## Global Live-Learning Platform
                                Backend service for teachers and parents across different timezones.

                                ### Base URL
                                - **Production:** https://undoschool-backend.onrender.com
                                - **Local:** http://localhost:10000

                                ### Key Features
                                - ✅ Timezone-safe session scheduling
                                - ✅ Conflict detection for overlapping bookings
                                - ✅ Pessimistic locking for concurrent requests
                                """)
                        .contact(new Contact()
                                .name("UndoSchool Engineering")
                                .email("engineering@undoschool.com")
                                .url("https://undoschool.com"))
                        .license(new License()
                                .name("Private")
                                .url("https://undoschool.com")))

                //Servers 
                .servers(List.of(
                        new Server()
                                .url("https://undoschool-backend.onrender.com")
                                .description("🌐 Production"),
                        new Server()
                                .url("http://localhost:10000")
                                .description("💻 Local Development")))

                // Tags (controls sidebar order)
                .tags(List.of(
                        new Tag().name("Courses")
                                .description("📚 Create and list courses"),
                        new Tag().name("Teacher — Registration")
                                .description("👨‍🏫 Teacher account management"),
                        new Tag().name("Teacher — Offerings")
                                .description("📅 Create offerings and manage sessions"),
                        new Tag().name("Parent — Registration")
                                .description("👪 Parent account management"),
                        new Tag().name("Parent — Offerings")
                                .description("🔍 Browse offerings with timezone conversion"),
                        new Tag().name("Parent — Bookings")
                                .description("🎟️ Book offerings and view history")));
    }
}
