package co.uk.stefanpuia.backupr.shell;

import co.uk.stefanpuia.backupr.config.exception.ConfigFileReadException;
import co.uk.stefanpuia.backupr.engine.remote.RemoteHandlerException;
import org.eclipse.jgit.api.errors.TransportException;
import org.springframework.shell.command.CommandExceptionResolver;
import org.springframework.shell.command.CommandHandlingResult;

public class ShellExceptionResolver implements CommandExceptionResolver {
  @Override
  public CommandHandlingResult resolve(final Exception ex) {
    return CommandHandlingResult.of(getResultMessage(ex) + "\n\n", 1);
  }

  private String getResultMessage(final Throwable ex) {

    if (ex instanceof ConfigFileReadException || ex instanceof TransportException) {
      return getResultMessage(ex.getCause());
    }

    if (ex instanceof RemoteHandlerException && ex.getCause() != null) {
      return getResultMessage(ex.getCause());
    }

    return ex.getMessage();
  }
}
