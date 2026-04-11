package co.uk.stefanpuia.backupr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackuprApplication {

  public static void main(final String[] args) {
    String[] appArgs = args;
    if (args.length == 0) {
      final var envPrimaryCommand = System.getenv("PRIMARY_COMMAND");
      if (envPrimaryCommand != null) {
        appArgs = new String[] {envPrimaryCommand};
      } else {
        appArgs = new String[] {"help"};
      }
    }
    SpringApplication.run(BackuprApplication.class, appArgs);
  }
}
