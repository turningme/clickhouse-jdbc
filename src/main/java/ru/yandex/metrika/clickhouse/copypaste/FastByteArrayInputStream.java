package ru.yandex.metrika.clickhouse.copypaste;

import java.io.IOException;
import java.io.InputStream;

/**
 * Несинхронизированная быстрая версия {@link java.io.ByteArrayInputStream}, с бриджем и поэтессами
 * @author Artur
 * @version $Id: FastByteArrayInputStream.java 4065 2009-08-10 14:04:26Z artur $
 * @since 07.05.2008
 */
public final class FastByteArrayInputStream extends InputStream {
    private final byte[] buf;

    private int pos;

    private final int count;

    public FastByteArrayInputStream(byte[] buf) {
        this.buf = buf;
        pos = 0;
        count = buf.length;
    }

    /**
     * Специальный конструктор для создания InputStream поверх не до конца заполненного массива
     * @param buf Массив байт
     * @param count Кол-во заполненных эл-тов массива
     */
    public FastByteArrayInputStream(byte[] buf, int count) {
        this.buf = buf;
        pos = 0;
        this.count = count;
    }


    @Override
    public int read() {
	return pos < count ? buf[pos++] & 0xff : -1;
    }


    @Override
    public int read(byte[] b, int off, int len) {
	if (off < 0 || len < 0 || len > b.length - off) {
	    throw new IndexOutOfBoundsException();
	}
	if (pos >= count) {
	    return -1;
	}
	if (pos + len > count) {
        //noinspection AssignmentToMethodParameter
        len = count - pos;
	}
	if (len <= 0) {
	    return 0;
	}
	System.arraycopy(buf, pos, b, off, len);
	pos += len;
	return len;
    }


    @Override
    public long skip(long n) {
	if (pos + n > count) {
        //noinspection AssignmentToMethodParameter
	    n = count - pos;
	}
	if (n < 0) {
	    return 0;
	}
	pos += (int) n;
	return n;
    }


    @Override
    public int available() {
	return count - pos;
    }

    @Override
    public boolean markSupported() {
	return false;
    }


    @Override
    public void close() throws IOException {
    }

    public int getPos() {
        return pos;
    }

    public int getCount() {
        return count;
    }

    public byte[] getBuf() {
        return buf;
    }

    public byte[] getData() {
        if (buf.length > count) {
            byte[] data = new byte[count];
            System.arraycopy(buf, 0, data, 0, count);
            return data;
        } else {
            return buf;
        }
    }

    @Override
    public void reset() {
        pos = 0;
    }


}
