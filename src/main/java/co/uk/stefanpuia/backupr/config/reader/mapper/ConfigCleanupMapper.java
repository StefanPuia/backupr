package co.uk.stefanpuia.backupr.config.reader.mapper;

import co.uk.stefanpuia.backupr.config.model.cleanup.Cleanup;
import co.uk.stefanpuia.backupr.config.model.cleanup.ImmutableKeepAllCleanup;
import co.uk.stefanpuia.backupr.config.model.cleanup.ImmutableParameterizedCleanup;
import co.uk.stefanpuia.backupr.config.model.cleanup.KeepAllCleanup;
import co.uk.stefanpuia.backupr.config.model.cleanup.ParameterizedCleanup;
import co.uk.stefanpuia.backupr.config.reader.dto.cleanup.CleanupDto;
import co.uk.stefanpuia.backupr.config.reader.dto.cleanup.KeepAllCleanupDto;
import co.uk.stefanpuia.backupr.config.reader.dto.cleanup.ParameterizedCleanupDto;
import co.uk.stefanpuia.backupr.core.MapstructConfig;
import jakarta.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.SubclassExhaustiveStrategy;
import org.mapstruct.SubclassMapping;

@Mapper(
    config = MapstructConfig.class,
    uses = {CoreDtoMapper.class},
    subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION)
public abstract class ConfigCleanupMapper {
  @SubclassMapping(target = KeepAllCleanup.class, source = KeepAllCleanupDto.class)
  @SubclassMapping(target = ParameterizedCleanup.class, source = ParameterizedCleanupDto.class)
  @Mapping(target = "enabled", source = "disabled", qualifiedByName = "mapDisabledToEnabled")
  public abstract Cleanup mapCleanup(
      CleanupDto source, @Context VariablesWrapper variables, @Context String parentName);

  @Mapping(target = "enabled", source = "disabled", qualifiedByName = "mapDisabledToEnabled")
  protected abstract KeepAllCleanup convert(
      KeepAllCleanupDto source, @Context VariablesWrapper variables, @Context String parentName);

  @Mapping(target = "enabled", source = "disabled", qualifiedByName = "mapDisabledToEnabled")
  protected abstract ParameterizedCleanup convert(
      ParameterizedCleanupDto source,
      @Context VariablesWrapper variables,
      @Context String parentName);

  @AfterMapping
  protected void addContext(
      final @MappingTarget ImmutableKeepAllCleanup.Builder target,
      final @Context String parentName) {
    setNameWhenMissing(target.build(), target::setName, parentName);
  }

  @AfterMapping
  protected void addContext(
      final @MappingTarget ImmutableParameterizedCleanup.Builder target,
      final @Context String parentName) {
    setNameWhenMissing(target.build(), target::setName, parentName);
  }

  private void setNameWhenMissing(
      final Cleanup cleanup,
      final Function<String, ?> nameApplier,
      final @Nullable String parentName) {
    if (Objects.nonNull(parentName) && Optional.ofNullable(cleanup.getName()).isEmpty()) {
      nameApplier.apply("inline[%s/%s]".formatted(parentName, cleanup.getType()));
    }
  }
}
