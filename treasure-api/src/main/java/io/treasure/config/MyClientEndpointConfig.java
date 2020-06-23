package io.treasure.config;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.Decoder;
import javax.websocket.Encoder;
import javax.websocket.Extension;
import java.util.List;
import java.util.Map;

public class MyClientEndpointConfig implements ClientEndpointConfig {
    @Override
    public List<String> getPreferredSubprotocols() {
        return null;
    }

    @Override
    public List<Extension> getExtensions() {
        return null;
    }

    @Override
    public Configurator getConfigurator() {
        return null;
    }

    @Override
    public List<Class<? extends Encoder>> getEncoders() {
        return null;
    }

    @Override
    public List<Class<? extends Decoder>> getDecoders() {
        return null;
    }

    @Override
    public Map<String, Object> getUserProperties() {
        return null;
    }
}
