package co.uk.stefanpuia.backupr.config.reader.dto.source.transformers;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonSubTypes({
  @JsonSubTypes.Type(value = ZipTransformerOptionsDto.class, name = "zip"),
})
public interface TransformerOptionsDto {}
