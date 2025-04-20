package co.uk.stefanpuia.backupr.engine.source;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.ExposedPorts;
import com.github.dockerjava.core.DockerConfigFile;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.context.annotation.Configuration;

@Configuration
@RegisterReflectionForBinding({
  // com.github.docker-java:docker-java
  DockerConfigFile.class,
  InspectContainerResponse.class,
  ExposedPorts.class,
})
public class SourceRuntimeHints {}
