package com.sorbonneuniversite.stl.daar2022.searchengine.routes

import mu.KLogging
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.model.rest.RestBindingMode
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class SearchEngineEndpoint : RouteBuilder() {

    @Value("\${netty.server.port}")
    private val serverPort: String? = null

    companion object : KLogging()

    override fun configure() {

        restConfiguration()
            .component("netty-http")
            .host("0.0.0.0")
            .port(serverPort)
            .bindingMode(RestBindingMode.auto)

    }


}