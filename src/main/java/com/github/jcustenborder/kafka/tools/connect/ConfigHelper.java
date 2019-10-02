package com.github.jcustenborder.kafka.tools.connect;

import com.github.jcustenborder.kafka.tools.ObjectMapperFactory;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ConfigHelper {
  private static final Logger log = LoggerFactory.getLogger(ConfigHelper.class);
  final OutputFormat defaultOutputFormat;
  final OutputHandler defaultOutputHandler;


  public ConfigHelper(OutputFormat defaultOutputFormat) {
    this.defaultOutputFormat = defaultOutputFormat;
    this.defaultOutputHandler = outputHandler(this.defaultOutputFormat);
  }

  public File fileName(File outputPath, String connectorName) {
    return this.defaultOutputHandler.fileName(outputPath, connectorName);
  }

  public String connectorName(Map<String, String> config) {
    return config.get("name");
  }

  public String connectorClass(Map<String, String> config) {
    return config.get("connector.class");
  }

  public enum OutputFormat {
    Json,
    Properties
  }

  interface OutputHandler {
    OutputFormat outputFormat();

    String extension();

    void write(File configPath, Map<String, String> config) throws IOException;

    Map<String, String> read(File configPath) throws IOException;

    default File fileName(File outputPath, String connectorName) {
      String fileName = String.format("%s.%s", connectorName, extension());
      return null == outputPath ? new File(fileName) : new File(outputPath, fileName);
    }
  }

  static final Map<String, OutputHandler> outputHandlerByExtension;
  static final Map<OutputFormat, OutputHandler> outputHandlerByOutputFormat;

  static class JsonOutputHandler implements OutputHandler {

    @Override
    public OutputFormat outputFormat() {
      return OutputFormat.Json;
    }

    @Override
    public String extension() {
      return "json";
    }

    @Override
    public void write(File configPath, Map<String, String> config) throws IOException {
      ObjectMapperFactory.INSTANCE.writeValue(configPath, config);
    }

    @Override
    public Map<String, String> read(File configPath) throws IOException {
      return ObjectMapperFactory.INSTANCE.readValue(configPath, Map.class);
    }
  }

  static class PropertiesOutputHandler implements OutputHandler {

    @Override
    public OutputFormat outputFormat() {
      return OutputFormat.Properties;
    }

    @Override
    public String extension() {
      return "properties";
    }

    @Override
    public void write(File configPath, Map<String, String> config) throws IOException {
      Properties properties = new Properties();
      properties.putAll(config);
      try (OutputStream outputStream = new FileOutputStream(configPath)) {
        properties.store(outputStream, "Example properties file");
      }
    }

    @Override
    public Map<String, String> read(File configPath) throws IOException {
      Properties properties = new Properties();
      try (InputStream inputStream = new FileInputStream(configPath)) {
        properties.load(inputStream);
      }
      Map<String, String> result = new LinkedHashMap<>();
      properties.forEach((key, value) -> result.put(key.toString(), value != null ? value.toString() : ""));
      return result;
    }
  }

  static {
    final Map<String, OutputHandler> handlersByExtension = new LinkedHashMap<>();
    final Map<OutputFormat, OutputHandler> handlersByOutputFormat = new LinkedHashMap<>();

    List<OutputHandler> outputHandlers = Arrays.asList(
        new JsonOutputHandler(),
        new PropertiesOutputHandler()
    );

    outputHandlers
        .forEach(outputHandler -> handlersByExtension.put(outputHandler.extension(), outputHandler));
    outputHandlers
        .forEach(outputHandler -> handlersByOutputFormat.put(outputHandler.outputFormat(), outputHandler));
    outputHandlerByExtension = ImmutableMap.copyOf(handlersByExtension);
    outputHandlerByOutputFormat = ImmutableMap.copyOf(handlersByOutputFormat);
  }

  public static boolean hasHandler(File configPath) {
    final String extension = Files.getFileExtension(configPath.getName()).toLowerCase();
    final OutputHandler outputHandler = outputHandlerByExtension.get(extension);
    return null != outputHandler;
  }

  private static OutputHandler outputHandler(File configPath) {
    final String extension = Files.getFileExtension(configPath.getName()).toLowerCase();
    final OutputHandler outputHandler = outputHandlerByExtension.get(extension);

    if (null == outputHandler) {
      throw new UnsupportedOperationException(
          String.format("Unknown file format. Extension = '%s'", extension)
      );
    }

    return outputHandler;
  }

  private static OutputHandler outputHandler(OutputFormat outputFormat) {
    final OutputHandler outputHandler = outputHandlerByOutputFormat.get(outputFormat);

    if (null == outputHandler) {
      throw new UnsupportedOperationException(
          String.format("Unknown file format. OutputFormat = '%s'", outputFormat)
      );
    }

    return outputHandler;
  }

  public void save(Map<String, String> config, File configPath) throws IOException {
    File parentDirectory = configPath.getParentFile();
    if (null != parentDirectory && !parentDirectory.isDirectory()) {
      parentDirectory.mkdirs();
    }
    log.info("Writing config to {}", configPath);
    this.defaultOutputHandler.write(configPath, config);
  }

  public Map<String, String> load(File configPath) throws IOException {
    final OutputHandler outputHandler = outputHandler(configPath);
    log.info("Reading config from {}", configPath);
    return outputHandler.read(configPath);
  }


}
