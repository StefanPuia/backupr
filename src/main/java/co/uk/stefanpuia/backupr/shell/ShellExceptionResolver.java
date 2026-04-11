package co.uk.stefanpuia.backupr.shell;

import co.uk.stefanpuia.backupr.config.exception.ConfigFileReadException;
import co.uk.stefanpuia.backupr.engine.adapters.AdapterException;
import co.uk.stefanpuia.backupr.engine.remote.RemoteHandlerException;
import co.uk.stefanpuia.backupr.engine.source.SourceHandlerException;
import com.azure.storage.blob.implementation.models.BlobStorageError;
import com.azure.storage.blob.models.BlobStorageException;
import java.lang.reflect.InvocationTargetException;
import org.eclipse.jgit.api.errors.TransportException;
import org.springframework.shell.core.command.ExitStatus;
import org.springframework.shell.core.command.exit.ExitStatusExceptionMapper;
import org.springframework.stereotype.Component;

@Component(ShellExceptionResolver.SHELL_EXCEPTION_RESOLVER)
public class ShellExceptionResolver implements ExitStatusExceptionMapper {

  public static final String SHELL_EXCEPTION_RESOLVER = "shellExceptionResolver";

  @Override
  public ExitStatus apply(final Exception ex) {
    return new ExitStatus(1, "\nERROR: %s\n\n".formatted(getResultMessage(ex)));
  }

  private String getResultMessage(final Throwable ex) {

    if (ex instanceof InvocationTargetException invocationTargetException) {
      return getResultMessage(invocationTargetException.getTargetException());
    }

    if (ex instanceof ConfigFileReadException || ex instanceof TransportException) {
      return getResultMessage(ex.getCause());
    }

    if ((ex instanceof RemoteHandlerException
            || ex instanceof SourceHandlerException
            || ex instanceof AdapterException)
        && ex.getCause() != null) {
      return getResultMessage(ex.getCause());
    }

    if (ex instanceof final BlobStorageException blobStorageException) {
      return handleBlobStorageException(blobStorageException);
    }

    return ex.getMessage();
  }

  private String handleBlobStorageException(final BlobStorageException ex) {
    String message = ex.getMessage();
    if (ex.getValue() instanceof final BlobStorageError blobStorageError) {
      message = blobStorageError.getMessage();
      if ("BlobAlreadyExists".equals(blobStorageError.getCode())) {
        message =
            "Target file already exists. Did you mean to use the 'overwrite' option, or set up a"
                + " different 'blobPrefixPattern'?";
      }
    }
    return message;
  }
}
