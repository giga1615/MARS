package com.mars.config;

import io.lettuce.core.dynamic.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.mars.model.CapsuleListDto;


@Configuration
@EnableRedisRepositories(basePackages = {"com.mars.dao"})
public class config {


    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory("localhost", 7005);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {

//        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
//        redisTemplate.setConnectionFactory(redisConnectionFactory());
//        return redisTemplate;
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer(CapsuleListDto.class));
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        redisTemplate.setValueSerializer(new StringRedisSerializer());
//        // Hash Operation 사용 시
//        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
//        redisTemplate.setHashValueSerializer(new StringRedisSerializer());
//
        // 혹은 아래 설정으로 모든 Key / Value Serialization을 변경할 수 있음
//        redisTemplate.setDefaultSerializer(new StringRedisSerializer());

        return redisTemplate;
    }

}