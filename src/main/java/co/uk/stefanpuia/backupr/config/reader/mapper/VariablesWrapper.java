package co.uk.stefanpuia.backupr.config.reader.mapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public record VariablesWrapper(
    Map<String, String> variables, Map<String, String> environment, Map<String, String> context) {

  public VariablesWrapper(Map<String, String> variables, Map<String, String> environment) {
    this(variables, environment, Map.of());
  }

  public VariablesWrapper withContext(final Map<String, String> context) {
    final var now = LocalDateTime.now();
    final var nowDate = now.toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
    final var nowTime = now.toLocalTime().format(DateTimeFormatter.ofPattern("HH-mm-ss"));
    final var nowDateTime = "%s-%s".formatted(nowDate, nowTime);
    final var newContext = new HashMap<>(context);
    newContext.put("now", nowDateTime);
    newContext.put("nowDate", nowDate);
    newContext.put("nowTime", nowTime);
    return new VariablesWrapper(variables(), environment(), newContext);
  }
}
