package co.uk.stefanpuia.backupr.config.reader.dto.source;

import static co.uk.stefanpuia.backupr.config.reader.dto.BackuprConfigDto.VALID_IDENTIFIER_REGEX;

import co.uk.stefanpuia.backupr.config.model.SourceTransformer;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = LocalConfigSourceDto.class, name = "LOCAL"),
})
public interface ConfigSourceDto {
  @NotBlank
  @Pattern(regexp = VALID_IDENTIFIER_REGEX)
  String getName();

  @Nullable
  Boolean isDisabled();

  List<@NotBlank String> getRemotes();

  List<@NotNull SourceTransformer> getTransformers();
}
