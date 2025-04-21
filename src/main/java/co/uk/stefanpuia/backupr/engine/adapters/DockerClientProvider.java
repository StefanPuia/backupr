package co.uk.stefanpuia.backupr.engine.adapters;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.transport.DockerHttpClient;
import com.github.dockerjava.zerodep.ZerodepDockerHttpClient;
import java.io.IOException;
import java.net.URI;
import lombok.Getter;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

@Getter
@Component
public class DockerClientProvider implements DisposableBean {
  private static final String DEFAULT_DOCKER_HOST = "unix:///var/run/docker.sock";
  private static final String WINDOWS_DEFAULT_DOCKER_HOST = "npipe:////./pipe/docker_engine";
  private final DockerHttpClient dockerHttpClient;
  private final DockerClient dockerClient;

  public DockerClientProvider() {
    try {
      this.dockerHttpClient = buildHttpClient();
      this.dockerClient = buildDockerClient();
    } catch (IOException e) {
      throw new AdapterException(e);
    }
  }

  private DockerClient buildDockerClient() throws IOException {
    return DockerClientImpl.getInstance(
        DefaultDockerClientConfig.createDefaultConfigBuilder().build(), dockerHttpClient);
  }

  private DockerHttpClient buildHttpClient() {
    return new ZerodepDockerHttpClient.Builder()
        .dockerHost(
            URI.create(
                SystemUtils.IS_OS_WINDOWS ? WINDOWS_DEFAULT_DOCKER_HOST : DEFAULT_DOCKER_HOST))
        .build();
  }

  @Override
  public void destroy() throws IOException {
    dockerClient.close();
    dockerHttpClient.close();
  }
}
