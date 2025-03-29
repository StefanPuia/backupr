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
    newContext.put("nowYear", String.valueOf(now.getYear()));
    newContext.put("nowMonth", String.valueOf(now.getMonthValue()));
    newContext.put("nowDay", String.valueOf(now.getDayOfMonth()));
    newContext.put("nowHour", String.valueOf(now.getHour()));
    newContext.put("nowMinute", String.valueOf(now.getMinute()));
    newContext.put("nowSecond", String.valueOf(now.getSecond()));

    return new VariablesWrapper(variables(), environment(), newContext);
  }
}
