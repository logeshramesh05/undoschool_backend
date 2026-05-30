package com.undoschool.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(buildInfo())
                .servers(buildServers())
                .tags(buildTags());
    }

    private Info buildInfo() {
        return new Info()
                .title("UndoSchool Booking System API")
                .version("1.0.0")
                .description("""
                        ## Overview
                        Backend service for a **global live-learning platform** where teachers
                        conduct online classes for students across different countries and timezones.

                        ## Timezone Handling
                        - All timestamps are **stored internally as UTC**
                        - Teachers submit session times in their **own local timezone** (IANA format)
                        - Parents receive session times **converted to their local timezone**
                        - Use valid IANA timezone strings: `Asia/Kolkata`, `America/New_York`

                        ## Booking Rules
                        1. Booking is at the **offering level** — books all sessions together
                        2. A parent **cannot book overlapping sessions** across different offerings
                        3. Concurrent booking attempts handled with **pessimistic locking**

                        ## Error Format
                        ```json
                        {
                          "status": 409,
                          "message": "Schedule conflict: ...",
                          "details": null,
                          "timestamp": "2025-06-07T12:30:00Z"
                        }
                        ```
                        """)
                .contact(new Contact()
                        .name("UndoSchool Engineering")
                        .email("engineering@undoschool.com"))
                .license(new License()
                        .name("Private"));
    }

    private List<Server> buildServers() {
        return List.of(
                new Server()
                        .url("http://localhost:10000")
                        .description("Local Development"),
                new Server()
                        .url("https://undoschool-app.onrender.com")
                        .description("Production (Render)")
        );
    }

    private List<Tag> buildTags() {
        return List.of(
                new Tag().name("Courses")
                        .description("Create and list courses/subjects"),
                new Tag().name("Teacher — Registration")
                        .description("Teacher account management"),
                new Tag().name("Teacher — Offerings")
                        .description("Create offerings and manage sessions"),
                new Tag().name("Parent — Registration")
                        .description("Parent account management"),
                new Tag().name("Parent — Offerings")
                        .description("Browse available offerings with timezone conversion"),
                new Tag().name("Parent — Bookings")
                        .description("Book offerings and view booking history")
        );
    }
}
