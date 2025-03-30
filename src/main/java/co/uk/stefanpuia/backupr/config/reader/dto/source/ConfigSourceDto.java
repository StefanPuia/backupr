package co.uk.stefanpuia.backupr.config.reader.dto.source;

import static co.uk.stefanpuia.backupr.config.reader.dto.BackuprConfigDto.VALID_IDENTIFIER_REGEX;

import co.uk.stefanpuia.backupr.config.reader.dto.IdentifiableConfigDto;
import co.uk.stefanpuia.backupr.config.reader.dto.remote.MixedRemoteArrayDeserializer;
import co.uk.stefanpuia.backupr.config.reader.dto.remote.MixedRemoteDto;
import co.uk.stefanpuia.backupr.config.reader.dto.transformers.MixedTransformerArrayDeserializer;
import co.uk.stefanpuia.backupr.config.reader.dto.transformers.TransformerOptionsDto;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = LocalConfigSourceDto.class, name = "LOCAL"),
})
public interface ConfigSourceDto extends IdentifiableConfigDto {
  @Override
  @NotBlank
  @Pattern(regexp = VALID_IDENTIFIER_REGEX)
  String getName();

  @Nullable
  Boolean isDisabled();

  @JsonDeserialize(using = MixedRemoteArrayDeserializer.class)
  List<@Valid @NotNull MixedRemoteDto> getRemotes();

  @JsonDeserialize(using = MixedTransformerArrayDeserializer.class)
  List<@Valid @NotNull TransformerOptionsDto> getTransformers();
}
