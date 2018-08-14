package com.gy.sched.common.util;

import com.gy.sched.common.constants.Constants;
import com.gy.sched.common.exception.BytesException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;


/**
 * 字节数组工具
 */
public class BytesUtil implements Constants {

    /**
     * 判断字节数组是否为空
     *
     * @param bytes
     * @return
     */
    public static boolean isEmpty(byte[] bytes) {
        if (null == bytes) {
            return true;
        }
        if (bytes.length <= 0) {
            return true;
        }
        return false;
    }

    /**
     * 将对象转换为byte数组
     *
     * @param object
     * @return
     * @throws BytesException
     */
    public static byte[] objectToBytes(Object object)
            throws BytesException {
        if (null == object) {
            throw new BytesException("object is null");
        }

        byte[] bytes = null;
        try {
            ByteArrayOutputStream byteArrayOutputStream =
                    new ByteArrayOutputStream();


            ObjectOutputStream oo = new ObjectOutputStream(byteArrayOutputStream);
            oo.writeObject(object);

            bytes = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.close();
            oo.close();
        } catch (Exception e) {
            System.out.println("translation" + e.getMessage());
            e.printStackTrace();
        }

//		HessianOutput hessianOutput =
//				new HessianOutput(byteArrayOutputStream);
//		try {
//			hessianOutput.writeObject(object);
//		} catch (Exception e) {
//			throw new BytesException("write object error", e);
//		}
        //return byteArrayOutputStream.toByteArray();
        return bytes;
    }

    /**
     * 将byte数组转换成对象
     *
     * @param bytes
     * @return
     * @throws BytesException
     */
    public static Object bytesToObject(byte[] bytes)
            throws BytesException {
        if (null == bytes) {
            throw new BytesException("bytes is null");
        }
        Object object = null;
        try {
            ByteArrayInputStream byteArrayInputStream =
                    new ByteArrayInputStream(bytes);
            ObjectInputStream oi = new ObjectInputStream(byteArrayInputStream);
            object = oi.readObject();
            byteArrayInputStream.close();
            oi.close();
        } catch (Exception e) {

        }

//		HessianInput hessianInput =
//				new HessianInput(byteArrayInputStream);
//		Object object = null;
//		try {
//			object = hessianInput.readObject();
//		} catch (Exception e) {
//			throw new BytesException("read object error", e);
//		}
        return object;
    }

    /**
     * 将byte数组计算MD5
     *
     * @param bytes
     * @return
     * @throws BytesException
     */
    public static String md5(byte[] bytes) throws BytesException {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            throw new BytesException("message digest error", e);
        }
        messageDigest.update(bytes);
        byte[] resultBytes = messageDigest.digest();
        return bytesToHex(resultBytes);
    }

    /**
     * 把byte数组转换成字符串
     *
     * @param bytes
     * @return
     */
    public static String bytesToHex(byte[] bytes) {
        char[] hexDigits = {
                '0', '1', '2', '3', '4',
                '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
        char[] resultCharArray = new char[CHAR_AMOUNT * 2];
        int index = 0;
        for (int i = 0; i < CHAR_AMOUNT; i++) {
            resultCharArray[index++] = hexDigits[bytes[i] >>> 4 & 0xf];
            resultCharArray[index++] = hexDigits[bytes[i] & 0xf];
        }
        return new String(resultCharArray);
    }

}
