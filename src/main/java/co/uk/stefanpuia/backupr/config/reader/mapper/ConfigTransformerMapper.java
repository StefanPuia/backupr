package co.uk.stefanpuia.backupr.config.reader.mapper;

import co.uk.stefanpuia.backupr.config.model.transformer.ConfigTransformerOptions;
import co.uk.stefanpuia.backupr.config.model.transformer.TarGzConfigTransformerOptions;
import co.uk.stefanpuia.backupr.config.model.transformer.ZipConfigTransformerOptions;
import co.uk.stefanpuia.backupr.config.reader.dto.transformers.TarGzTransformerOptionsDto;
import co.uk.stefanpuia.backupr.config.reader.dto.transformers.TransformerOptionsDto;
import co.uk.stefanpuia.backupr.config.reader.dto.transformers.ZipTransformerOptionsDto;
import co.uk.stefanpuia.backupr.core.MapstructConfig;
import java.util.List;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
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
  @SubclassMapping(
      target = TarGzConfigTransformerOptions.class,
      source = TarGzTransformerOptionsDto.class)
  ConfigTransformerOptions mapZipTransformerOptions(
      TransformerOptionsDto source, @Context VariablesWrapper variables);

  @Mapping(target = "variables", expression = "java(variables)")
  ZipConfigTransformerOptions convert(
      ZipTransformerOptionsDto source, @Context VariablesWrapper variables);

  @Mapping(target = "variables", expression = "java(variables)")
  TarGzConfigTransformerOptions convert(
      TarGzTransformerOptionsDto source, @Context VariablesWrapper variables);
}
