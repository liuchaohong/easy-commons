package com.github.easy.commons.encrypt;

import static org.junit.Assert.assertTrue;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.github.easy.commons.util.encrypt.EncryptUtil;


public class EncryptUtilTest {

	private String decodeStr = "https://www.baidu.com/";
	private String encodeStr = "ktxW/eQiOApcYGPDgTh021yttLsie7Ed";
	private String password = "1ceFqSNA4Dc6qekab3a2";

	
	@Test
	public void testEncrypt() {
		String result = null;
		try {
			result = EncryptUtil.DESEncrypt(password, decodeStr).trim();
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
		assertTrue(StringUtils.equals(encodeStr, result));
	}
	
	
	@Test
	public void testDecrypt() {
		String result = null;
		try {
			result = EncryptUtil.DESDecrypt(password, encodeStr);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
		assertTrue(StringUtils.equals(decodeStr, result));
	}
	
}
