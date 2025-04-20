package co.uk.stefanpuia.backupr.shell;

import co.uk.stefanpuia.backupr.config.exception.ConfigFileReadException;
import co.uk.stefanpuia.backupr.engine.adapters.AdapterException;
import co.uk.stefanpuia.backupr.engine.remote.RemoteHandlerException;
import co.uk.stefanpuia.backupr.engine.source.SourceHandlerException;
import org.eclipse.jgit.api.errors.TransportException;
import org.springframework.shell.command.CommandExceptionResolver;
import org.springframework.shell.command.CommandHandlingResult;
import org.springframework.stereotype.Component;

@Component
public class ShellExceptionResolver implements CommandExceptionResolver {
  @Override
  public CommandHandlingResult resolve(final Exception ex) {
    return CommandHandlingResult.of("\nERROR: %s\n\n".formatted(getResultMessage(ex)), 1);
  }

  private String getResultMessage(final Throwable ex) {

    if (ex instanceof ConfigFileReadException || ex instanceof TransportException) {
      return getResultMessage(ex.getCause());
    }

    if ((ex instanceof RemoteHandlerException
            || ex instanceof SourceHandlerException
            || ex instanceof AdapterException)
        && ex.getCause() != null) {
      return getResultMessage(ex.getCause());
    }

    return ex.getMessage();
  }
}
