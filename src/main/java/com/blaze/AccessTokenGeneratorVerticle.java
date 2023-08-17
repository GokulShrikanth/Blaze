package com.blaze;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.auth.oauth2.OAuth2Auth;
import io.vertx.ext.auth.oauth2.OAuth2Options;
import io.vertx.core.json.JsonObject;

public class AccessTokenGeneratorVerticle extends AbstractVerticle {
    @Override
    public void start() {
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);

        OAuth2Options oauth2Options = new OAuth2Options()
            .setClientId("YOUR_CLIENT_ID")
            .setClientSecret("YOUR_CLIENT_SECRET")
            .setTokenPath("THIRD_PARTY_TOKEN_ENDPOINT");
            // Set other OAuth2 options

        OAuth2Auth oauth2 = OAuth2Auth.create(vertx, oauth2Options);

        router.route("/generate-token").handler(generateTokenHandler(oauth2));

        server.requestHandler(router).listen(8080);
    }

    private Handler<RoutingContext> generateTokenHandler(OAuth2Auth oauth2) {
        return routingContext -> {
            JsonObject tokenRequest = new JsonObject()
                // Construct your token request payload here
                .put("username", "YOUR_USERNAME")
                .put("password", "YOUR_PASSWORD")
                .put("grant_type", "password");

            oauth2.authenticate(tokenRequest, res -> {
                if (res.succeeded()) {
                    // Successfully authenticated, send access token in response
                    routingContext.response()
                        .putHeader("content-type", "application/json")
                        .end(res.result().principal().encode());
                } else {
                    // Authentication failed
                    routingContext.response().setStatusCode(401).end("Unauthorized");
                }
            });
        };
    }
}
