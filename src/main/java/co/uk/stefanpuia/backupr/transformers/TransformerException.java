package co.uk.stefanpuia.backupr.transformers;

public class TransformerException extends RuntimeException {
  public TransformerException(String message) {
    super(message);
  }

  public TransformerException(final Throwable cause) {
    super(cause);
  }
}
