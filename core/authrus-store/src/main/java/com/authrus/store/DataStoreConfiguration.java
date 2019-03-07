package com.authrus.store;

import com.authrus.store.tuple.TupleStoreBuilder;
import com.authrus.store.tuple.TupleStoreConfiguration;

import java.util.LinkedHashSet;
import java.util.Set;

import lombok.AllArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.authrus.attribute.AttributeSerializer;
import com.authrus.attribute.CombinationBuilder;
import com.authrus.attribute.ObjectBuilder;
import com.authrus.attribute.ReflectionBuilder;
import com.authrus.attribute.SerializationBuilder;
import com.authrus.message.serialize.AttributeMarshaller;

@Configuration
@AllArgsConstructor
@Import(TupleStoreConfiguration.class)
@ComponentScan(basePackageClasses = DataStoreConfiguration.class)
public class DataStoreConfiguration {
   
   private final TupleStoreBuilder builder;

   @Bean
   public DataStoreBuilder dataStoreBuilder() {
      Set<ObjectBuilder> sequence = new LinkedHashSet<ObjectBuilder>();
      ObjectBuilder reflection = new ReflectionBuilder();
      ObjectBuilder serialization = new SerializationBuilder();
      ObjectBuilder combination = new CombinationBuilder(sequence);
      AttributeSerializer serializer = new AttributeSerializer(combination);
      AttributeMarshaller marshaller = new AttributeMarshaller(serializer);

      sequence.add(reflection);
      sequence.add(serialization);

      return new DataStoreBuilder(builder, marshaller);
   }
}
