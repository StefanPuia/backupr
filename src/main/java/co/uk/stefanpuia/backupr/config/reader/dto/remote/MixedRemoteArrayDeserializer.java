package co.uk.stefanpuia.backupr.config.reader.dto.remote;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MixedRemoteArrayDeserializer extends JsonDeserializer<List<MixedRemoteDto>> {
  @Override
  public List<MixedRemoteDto> deserialize(
      final JsonParser jsonParser, final DeserializationContext deserializationContext)
      throws IOException {

    final var mapper = (ObjectMapper) jsonParser.getCodec();
    final var rootNode = jsonParser.getCodec().readTree(jsonParser);
    final var result = new ArrayList<MixedRemoteDto>();

    if (rootNode.isArray()) {
      for (JsonNode node : (ArrayNode) rootNode) {
        if (node.isTextual()) {
          result.add(new MixedRemoteDto(node.asText(), null));
        } else if (node.isObject()) {
          result.add(
              new MixedRemoteDto(null, mapper.readValue(node.toString(), ConfigRemoteDto.class)));
        }
      }
    }
    return result;
  }
}
