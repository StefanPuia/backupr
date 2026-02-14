package co.uk.stefanpuia.backupr.core;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.time.Instant;

public class InstantSerializer extends JsonSerializer<Instant> {

  @Override
  public void serialize(Instant instant, JsonGenerator json, SerializerProvider provider)
      throws IOException {
    json.writeString(instant.toString());
  }
}
