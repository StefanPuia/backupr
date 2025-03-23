package co.uk.stefanpuia.backupr.config.reader.mapper;

import co.uk.stefanpuia.backupr.config.model.source.transformer.ConfigTransformerOptions;
import co.uk.stefanpuia.backupr.config.model.source.transformer.ZipConfigTransformerOptions;
import co.uk.stefanpuia.backupr.config.reader.dto.source.transformers.TransformerOptionsDto;
import co.uk.stefanpuia.backupr.config.reader.dto.source.transformers.ZipTransformerOptionsDto;
import co.uk.stefanpuia.backupr.core.MapstructConfig;
import java.util.List;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.SubclassExhaustiveStrategy;
import org.mapstruct.SubclassMapping;

@Mapper(
    config = MapstructConfig.class,
    subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION)
public interface ConfigTransformerMapper {

  List<ConfigTransformerOptions> mapTransformerOptions(
      List<TransformerOptionsDto> source, @Context VariablesWrapper variables);

  @SubclassMapping(
      target = ZipConfigTransformerOptions.class,
      source = ZipTransformerOptionsDto.class)
  ConfigTransformerOptions mapZipTransformerOptions(
      TransformerOptionsDto source, @Context VariablesWrapper variables);

  ZipConfigTransformerOptions convert(ZipTransformerOptionsDto source);
}
