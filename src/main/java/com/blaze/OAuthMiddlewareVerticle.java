package com.blaze;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.OAuth2AuthHandler;
import io.vertx.ext.auth.oauth2.OAuth2Auth;
import io.vertx.ext.auth.oauth2.providers.KeycloakAuth;
import io.vertx.ext.auth.oauth2.OAuth2FlowType;


public class OAuthMiddlewareVerticle extends AbstractVerticle {
    @Override
    public void start() {
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);

        // Configure your OAuth provider (Keycloak in this example)
        OAuth2Auth oauth2 = KeycloakAuth.create(vertx, OAuth2FlowType.PASSWORD, new JsonObject()
            .put("client_id", "YOUR_CLIENT_ID")
            .put("client_secret", "YOUR_CLIENT_SECRET")
            .put("username", "USER")
            .put("password", "PASSWORD")
        );

        // Set up the OAuth2AuthHandler
        OAuth2AuthHandler authHandler = OAuth2AuthHandler.create(vertx, oauth2);

        // Use the auth handler as middleware for protected routes
        router.route("/protected/*").handler(authHandler);

        // Define your protected routes
        router.get("/protected/resource").handler(routingContext -> {
            routingContext.response().end("Access to protected resource granted.");
        });

        // Start the server
        server.requestHandler(router).listen(8080);
    }
}
