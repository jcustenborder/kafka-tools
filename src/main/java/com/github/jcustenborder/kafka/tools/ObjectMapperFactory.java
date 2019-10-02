package com.github.jcustenborder.kafka.tools;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import org.apache.kafka.common.TopicPartition;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class ObjectMapperFactory {
  public static final ObjectMapper INSTANCE;

  static {
    INSTANCE = new ObjectMapper();
    INSTANCE.configure(SerializationFeature.INDENT_OUTPUT, true);
    INSTANCE.registerModule(new GuavaModule());
    INSTANCE.registerModule(new KafkaModule());
  }


  public static void write(File outputFile, Object value) throws IOException {
    File parentDirectory = outputFile.getParentFile();
    if (null != parentDirectory && !parentDirectory.isDirectory()) {
      parentDirectory.mkdirs();
    }
    INSTANCE.writeValue(outputFile, value);
  }

  static class KafkaModule extends SimpleModule {
    public KafkaModule() {
      addSerializer(TopicPartition.class, new TopicPartitionSerializer());
    }
  }

  static class TopicPartitionSerializer extends JsonSerializer<TopicPartition> {
    @Override
    public void serialize(TopicPartition topicPartition, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
      Map<String, Object> result = new LinkedHashMap<>();
      result.put("topic", topicPartition.topic());
      result.put("partition", topicPartition.partition());
      jsonGenerator.writeObject(result);
    }
  }
}
