package co.uk.stefanpuia.backupr.engine.adapters;

import co.uk.stefanpuia.backupr.config.model.credentials.Credentials;
import co.uk.stefanpuia.backupr.engine.remote.mapper.AzureCredentialMapper;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class AzureBlobClientProvider {
  private final AzureCredentialMapper credentialMapper;

  public BlobContainerClient buildClient(
      final Credentials credentials, final String endpoint, final String container) {
    log.debug("Using credential '{}'", credentials.getName());
    final var credential = credentialMapper.convert(credentials);
    return new BlobServiceClientBuilder()
        .endpoint(endpoint)
        .credential(credential)
        .buildClient()
        .getBlobContainerClient(container);
  }
}
