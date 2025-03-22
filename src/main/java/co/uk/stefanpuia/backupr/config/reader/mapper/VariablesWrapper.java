package co.uk.stefanpuia.backupr.config.reader.mapper;

import java.util.Map;

public record VariablesWrapper(Map<String, String> variables, Map<String, String> environment) {}
