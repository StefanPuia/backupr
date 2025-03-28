package co.uk.stefanpuia.backupr.config.reader.dto.remote.credentials;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

public class MixedCredentialsDeserializer extends JsonDeserializer<MixedCredentialsDto> {
  @Override
  public MixedCredentialsDto deserialize(
      final JsonParser jsonParser, final DeserializationContext deserializationContext)
      throws IOException {

    final var mapper = (ObjectMapper) jsonParser.getCodec();
    final JsonNode node = jsonParser.getCodec().readTree(jsonParser);

    if (node.isTextual()) {
      return new MixedCredentialsDto(node.asText(), null);
    } else {
      return new MixedCredentialsDto(null, mapper.readValue(node.toString(), CredentialsDto.class));
    }
  }
}
