package com.dpdocter.config;

import java.util.Base64;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;



import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
//@Configuration
//public class MongoConfigure extends AbstractMongoClientConfiguration{
//
//	private static final byte[] secretKey = Base64.getDecoder().decode("hqHKBLV83LpCqzKpf8OvutbCs+O5wX5BPu3btWpEvXA=");
//	private static final byte[] oldKey = Base64.getDecoder().decode("cUzurmCcL+K252XDJhhWI/A/+wxYXLgIm678bwsE2QM=");
//
//
//	@Bean
//	public CryptVault cryptVault() {
//		System.out.println("in valut...............");
//		return new CryptVault()
//				.with256BitAesCbcPkcs5PaddingAnd128BitSaltKey(0, oldKey)
//				.with256BitAesCbcPkcs5PaddingAnd128BitSaltKey(1, secretKey)
//				// can be omitted if it's the highest version
//				.withDefaultKeyVersion(1);
//	}
//
//	@Bean
//	public CachedEncryptionEventListener encryptionEventListener(CryptVault cryptVault) {
//		System.out.println("in listen.............");
//		return new CachedEncryptionEventListener(cryptVault);
//	}
//
//	@Override
//    protected String getDatabaseName() {
//        return "dpdocter_db";
//    }
//
//    @Override
//    public MongoClient mongoClient() {
//        return MongoClients.create();
//    }
//}
