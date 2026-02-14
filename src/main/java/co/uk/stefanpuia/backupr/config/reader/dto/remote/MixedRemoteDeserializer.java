package co.uk.stefanpuia.backupr.config.reader.dto.remote;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

public class MixedRemoteDeserializer extends JsonDeserializer<MixedRemoteDto> {
  @Override
  public MixedRemoteDto deserialize(
      final JsonParser jsonParser, final DeserializationContext deserializationContext)
      throws IOException {

    final var mapper = (ObjectMapper) jsonParser.getCodec();
    final var rootNode = jsonParser.getCodec().readTree(jsonParser);

    if (((JsonNode) rootNode).isTextual()) {
      return new MixedRemoteDto(((JsonNode) rootNode).asText(), null);
    } else if (rootNode.isObject()) {
      return new MixedRemoteDto(null, mapper.readValue(rootNode.toString(), ConfigRemoteDto.class));
    }
    return null;
  }
}
