package com.jingdong.mall.service.impl;

import com.jingdong.mall.common.config.SmsConfig;
import com.jingdong.mall.common.exception.BusinessException;
import com.jingdong.mall.common.exception.ErrorCode;
import com.jingdong.mall.common.utils.AliyunSmsClient;
import com.jingdong.mall.model.dto.request.SmsCodeRequest;
import com.jingdong.mall.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class SmsServiceImpl implements SmsService {

    @Autowired
    private SmsConfig smsConfig;

    @Autowired
    private AliyunSmsClient aliyunSmsClient;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    // Redis key前缀
    private static final String SMS_CODE_PREFIX = "sms:code:";
    private static final String SMS_COUNT_PREFIX = "sms:count:";

    @Override
    public void sendVerificationCode(SmsCodeRequest request) {
        String phone = request.getPhone();
        String type = request.getType();

        log.info("开始发送验证码: phone={}, type={}", phone, type);


        // 2. 检查验证码类型
        if (!isValidSmsType(type)) {
            throw new BusinessException(ErrorCode.SMS_TYPE_INVALID);
        }

        // 3. 检查发送频率限制
        checkSendFrequency(phone, type);

        // 4. 生成验证码
        String code = generateVerificationCode();
        log.info("生成验证码: phone={}, code={}", phone, code);

        // 5. 调用阿里云短信服务
        try {
            sendSmsByAliyun(phone, code);
        } catch (Exception e) {
            log.error("调用阿里云短信服务失败: phone={}, error={}", phone, e.getMessage());
            throw new BusinessException(ErrorCode.SMS_SEND_FAILED);
        }

        // 6. 保存验证码到Redis（带过期时间）
        saveCodeToRedis(phone, type, code);

        // 7. 更新发送次数
        updateSendCount(phone, type);

        log.info("验证码发送完成: phone={}, type={}", phone, type);
    }

    /**
     * 验证验证码类型
     */
    private boolean isValidSmsType(String type) {
        return "register".equals(type) || "login".equals(type) || "reset".equals(type);
    }

    /**
     * 检查发送频率限制
     */
    private void checkSendFrequency(String phone, String type) {
        // 检查1分钟内是否已发送
        String recentKey = SMS_CODE_PREFIX + "recent:" + type + ":" + phone;
        if (redisTemplate.hasKey(recentKey)) {
            throw new BusinessException(ErrorCode.SMS_SEND_TOO_FREQUENT);
        }

        // 检查当天发送次数
        String countKey = SMS_COUNT_PREFIX + type + ":" + phone + ":" + getTodayDate();
        String countStr = redisTemplate.opsForValue().get(countKey);
        int count = countStr != null ? Integer.parseInt(countStr) : 0;

        if (count >= smsConfig.getMaxSendPerDay()) {
            throw new BusinessException(ErrorCode.SMS_SEND_MAX);
        }
    }

    /**
     * 生成验证码（6位数字）
     */
    private String generateVerificationCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(999999));
    }

    /**
     * 调用阿里云发送短信
     */
    private void sendSmsByAliyun(String phone, String code) throws Exception {
        // 调用阿里云SDK
        aliyunSmsClient.sendSmsVerifyCode(phone, code);
    }

    /**
     * 保存验证码到Redis
     */
    private void saveCodeToRedis(String phone, String type, String code) {
        String key = SMS_CODE_PREFIX + type + ":" + phone;
        redisTemplate.opsForValue().set(key, code, smsConfig.getExpireMinutes(), TimeUnit.MINUTES);

        // 设置1分钟内禁止重复发送的标记
        String recentKey = SMS_CODE_PREFIX + "recent:" + type + ":" + phone;
        redisTemplate.opsForValue().set(recentKey, "1", 1, TimeUnit.MINUTES);
    }

    /**
     * 更新发送次数
     */
    private void updateSendCount(String phone, String type) {
        String countKey = SMS_COUNT_PREFIX + type + ":" + phone + ":" + getTodayDate();
        redisTemplate.opsForValue().increment(countKey, 1);

        // 设置过期时间为当天结束
        long expireSeconds = getSecondsUntilTomorrow();
        redisTemplate.expire(countKey, expireSeconds, TimeUnit.SECONDS);
    }

    /**
     * 验证验证码是否正确
     */
    public boolean verifyCode(String phone, String inputCode, String type) {


        String key = SMS_CODE_PREFIX + type + ":" + phone;
        String storedCode = redisTemplate.opsForValue().get(key);

        if (storedCode == null) {
            return false;
        }

        boolean isValid = storedCode.equals(inputCode);

        // 验证成功后删除验证码（防止重复使用）
        if (isValid) {
            redisTemplate.delete(key);
        }

        return isValid;
    }

    /**
     * 获取今天的日期字符串（用于计数）
     */
    private String getTodayDate() {
        return java.time.LocalDate.now().toString(); // yyyy-MM-dd格式
    }

    /**
     * 获取到明天0点的秒数
     */
    private long getSecondsUntilTomorrow() {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.time.LocalDateTime tomorrow = now.toLocalDate().plusDays(1).atStartOfDay();
        return java.time.Duration.between(now, tomorrow).getSeconds();
    }
}
