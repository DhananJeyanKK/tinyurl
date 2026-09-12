package com.learn.engine;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.core.CoreDsl.atOnceUsers;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class Get_hotpath_url extends Simulation {

    // 1. Configure HTTP Protocol (Disable redirects)
    HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:8080")
            .acceptHeader("application/json")
            .contentTypeHeader("application/json")
            .disableFollowRedirect(); // Prevents following 301/302 redirects

    // 2. Define Scenario (All users request the exact same URL)
    ScenarioBuilder scn = scenario("Fetch Same Short URL Concurrently")
            .exec(
                    http("GET - Fetch Same Short URL")
                            .get("/tinyurl_3000") // Static endpoint for all 1,000 users
                            .check(status().in(301, 302))
            );

    // 3. Inject 1,000 concurrent users at once
    {
        setUp(
                scn.injectOpen(
                        atOnceUsers(1000)
                )
        ).protocols(httpProtocol);
    }
}
