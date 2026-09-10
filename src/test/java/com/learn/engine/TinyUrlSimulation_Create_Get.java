//package com.learn.engine;
//
//import io.gatling.javaapi.core.*;
//import io.gatling.javaapi.http.*;
//
//import java.time.Duration;
//import java.util.Map;
//import java.util.stream.IntStream;
//
//import static io.gatling.javaapi.core.CoreDsl.*;
//import static io.gatling.javaapi.http.HttpDsl.*;
//
//public class TinyUrlSimulation_Create_Get extends Simulation {
//
//    // 1. Configure HTTP Protocol (Disable redirects)
//    HttpProtocolBuilder httpProtocol = http
//            .baseUrl("http://localhost:8080")
//            .acceptHeader("application/json")
//            .contentTypeHeader("application/json")
//            .disableFollowRedirect(); // Prevents following 301/302 redirects
//
//    // 2. Feeder generating unique alias keys for each iteration
//    FeederBuilder<Object> customAliasFeeder = listFeeder(
//            IntStream.rangeClosed(3000, 4000)
//                    .mapToObj(i -> Map.<String, Object>of(
//                            "alias", "tinyurl_" + i,
//                            "targetUrl", "https://www.google.com"
//                    ))
//                    .toList()
//    );
//    // 3. Define the Scenario
//    ScenarioBuilder scn = scenario("URL Shortener POST & GET Test")
//            .feed(customAliasFeeder)
//
//            // Step A: POST request to create short URL
//            .exec(
//                    http("POST - Create Short URL")
//                            .post("/url")
//                            .body(StringBody("""
//                    {
//                      "longUrl": "#{targetUrl}",
//                      "customerAlias": "#{alias}",
//                      "expireAt": "2030-01-01T00:00:00Z"
//                    }
//                """))
//                            .check(status().is(201))
//                            .check(jsonPath("$.shortKey").saveAs("createdKey"))
//            )
//
//            .exitHereIfFailed()
//
//            .pause(Duration.ofMillis(100))
//
//            // Step B: GET request using the key returned from POST
//            .exec(
//                    http("GET - Fetch Short URL")
//                            .get("/#{createdKey}")
//                            // Expect redirect status without following it
//                            .check(status().in(301, 302))
//            );
//
//    // 4. Set Load Profile (Runs 1000 sequential/concurrent iterations)
//    {
//        setUp(
//                scn.injectOpen(
//                        atOnceUsers(1000) // Triggers 1,000 total request pairs
//                )
//        ).protocols(httpProtocol);
//    }
//
//
//}
