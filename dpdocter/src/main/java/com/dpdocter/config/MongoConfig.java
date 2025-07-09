//package com.dpdocter.config;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.convert.converter.Converter;
//import org.springframework.data.convert.CustomConversions;
//import org.springframework.data.convert.JodaTimeConverters;
//import org.springframework.data.convert.Jsr310Converters;
//import org.springframework.data.convert.ThreeTenBackPortConverters;
//import org.springframework.data.mongodb.MongoDbFactory;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
//import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
//import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
//import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
//import org.springframework.data.mongodb.gridfs.GridFsTemplate;
//
//import com.dpdocter.exceptions.BusinessException;
//import com.dpdocter.exceptions.ServiceError;
//
//@Configuration
//public class MongoConfig {
//
//@Autowired
//MongoDbFactory mongoDbFactory;
//
////@Autowired(required = true)
////MappingMongoConverter converter;
//
//@Bean
//public MongoTemplate mongoTemplate() {
//  try{
//	  MappingMongoConverter converter = new MappingMongoConverter(new DefaultDbRefResolver(mongoDbFactory), new MongoMappingContext());
//	  converter.setTypeMapper(new DefaultMongoTypeMapper(null));
//
//	  List<Converter<?, ?>> converters = new ArrayList<Converter<?, ?>>();
////	  converters.add(new OAuth2AuthenticationReadConverter());
//	  System.out.println("OAuth2AuthenticationReadConverter added");
//	  converters.addAll(Jsr310Converters.getConvertersToRegister());
//	  converters.addAll(JodaTimeConverters.getConvertersToRegister());
//	  converters.addAll(ThreeTenBackPortConverters.getConvertersToRegister());
//
//	  converter.setCustomConversions(new CustomConversions(CustomConversions.StoreConversions.NONE, converters));
//	  converter.afterPropertiesSet();
//	  MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory, converter);
//
//	  return mongoTemplate;
//  }catch (Exception e) {
//	  throw new BusinessException(ServiceError.Unknown, e.getMessage());
//  }
//}
//
//@Bean
//public GridFsTemplate gridFsTemplate() throws Exception {
//  // NO NEED TO INSTANTIATE NEW MappingMongoConverter 
//  MappingMongoConverter converter = new MappingMongoConverter(new DefaultDbRefResolver(mongoDbFactory), new MongoMappingContext());
//
//  converter.setMapKeyDotReplacement("_");
//
//  List<Converter<?, ?>> converters = new ArrayList<Converter<?, ?>>();
////  converters.add(new OAuth2AuthenticationReadConverter());
//
//  converters.addAll(Jsr310Converters.getConvertersToRegister());
//  converters.addAll(JodaTimeConverters.getConvertersToRegister());
//  converters.addAll(ThreeTenBackPortConverters.getConvertersToRegister());
//
//  converter.setCustomConversions(new CustomConversions(CustomConversions.StoreConversions.NONE, converters));
//  converter.afterPropertiesSet();
//  GridFsTemplate gridFsTemplate = new GridFsTemplate(mongoDbFactory, converter);
//
//  return gridFsTemplate;
//
//}
//}