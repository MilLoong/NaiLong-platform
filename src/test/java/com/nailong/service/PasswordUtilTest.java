package com.nailong.service;

import com.nailong.common.utils.PasswordUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @brief 密码工具单元测试
 */
class PasswordUtilTest {

    @Test
    void encodeAndMatches() {
        String encoded = PasswordUtil.encode("Password123");
        assertNotNull(encoded);
        assertTrue(PasswordUtil.matches("Password123", encoded));
        assertFalse(PasswordUtil.matches("WrongPass", encoded));
    }

    @Test
    void isStrongPassword() {
        assertTrue(PasswordUtil.isStrongPassword("Password123"));
        assertFalse(PasswordUtil.isStrongPassword("short"));
        assertFalse(PasswordUtil.isStrongPassword("alllowercase1"));
        assertFalse(PasswordUtil.isStrongPassword("ALLUPPERCASE1"));
        assertFalse(PasswordUtil.isStrongPassword("NoDigitsHere"));
    }

    @Test
    void generateRandomPassword() {
        String pwd = PasswordUtil.generateRandomPassword(12);
        assertEquals(12, pwd.length());
    }
}
