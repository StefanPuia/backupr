package co.uk.stefanpuia.backupr.config.reader.dto.source.transformers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MixedTransformerArrayDeserializer
    extends JsonDeserializer<List<TransformerOptionsDto>> {
  @Override
  public List<TransformerOptionsDto> deserialize(
      final JsonParser jsonParser, final DeserializationContext deserializationContext)
      throws IOException {

    final var mapper = (ObjectMapper) jsonParser.getCodec();
    final var rootNode = jsonParser.getCodec().readTree(jsonParser);
    final var result = new ArrayList<TransformerOptionsDto>();

    if (rootNode.isArray()) {
      for (JsonNode node : (ArrayNode) rootNode) {
        if (node.isTextual()) {
          result.add(
              getDefaultOptions(mapper.convertValue(node.asText(), SourceTransformer.class)));
        } else if (node.isObject()) {
          result.add(mapper.readValue(node.toString(), TransformerOptionsDto.class));
        }
      }
    }
    return result;
  }

  private TransformerOptionsDto getDefaultOptions(final SourceTransformer sourceTransformer) {
    return switch (sourceTransformer) {
      case ZIP -> ImmutableZipTransformerOptionsDto.builder().build();
    };
  }
}
