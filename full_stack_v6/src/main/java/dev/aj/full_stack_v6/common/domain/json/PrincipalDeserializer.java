package dev.aj.full_stack_v6.common.domain.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.stream.StreamSupport;

public class PrincipalDeserializer extends JsonDeserializer<Principal> {

    @Override
    public Principal deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {

        JsonNode node = p.getCodec().readTree(p);

        String username = node.get("name").asText();
        boolean authenticated = node.get("authenticated").asBoolean();

        List<SimpleGrantedAuthority> authorities = Collections.emptyList();
        if (node.has("authorities")) {
            authorities = StreamSupport.stream(node.get("authorities").spliterator(), false)
                    .map(authNode -> authNode.get("authority"))
                    .map(JsonNode::asText)
                    .map(SimpleGrantedAuthority::new)
                    .toList();
        }

        if(authenticated){
            return new UsernamePasswordAuthenticationToken(username,null,authorities);
        }

        return null;
    }
}
