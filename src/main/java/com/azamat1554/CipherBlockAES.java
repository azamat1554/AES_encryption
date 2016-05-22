package com.azamat1554;

import java.util.Arrays;

/**
 * Класс реализующий шифрование и расшифровку блоков байтов с помощью алгоритма
 * AES (Rijndael)
 *
 * @author Azamat Abidokov
 */
public class CipherBlockAES {
    public static final int NB = 4; //количество столбцов в массиве state
    public static final int NK = 4; //число 32-битных слов в ключе, в данном случае 128-битный ключ
    public static final int NR = 10; //число раундов

    //матрица замен байтов, используется при шифровке в методе subBytes()
    private int[] sbox = {
            0x63, 0x7c, 0x77, 0x7b, 0xf2, 0x6b, 0x6f, 0xc5, 0x30, 0x01, 0x67, 0x2b, 0xfe, 0xd7, 0xab, 0x76,
            0xca, 0x82, 0xc9, 0x7d, 0xfa, 0x59, 0x47, 0xf0, 0xad, 0xd4, 0xa2, 0xaf, 0x9c, 0xa4, 0x72, 0xc0,
            0xb7, 0xfd, 0x93, 0x26, 0x36, 0x3f, 0xf7, 0xcc, 0x34, 0xa5, 0xe5, 0xf1, 0x71, 0xd8, 0x31, 0x15,
            0x04, 0xc7, 0x23, 0xc3, 0x18, 0x96, 0x05, 0x9a, 0x07, 0x12, 0x80, 0xe2, 0xeb, 0x27, 0xb2, 0x75,
            0x09, 0x83, 0x2c, 0x1a, 0x1b, 0x6e, 0x5a, 0xa0, 0x52, 0x3b, 0xd6, 0xb3, 0x29, 0xe3, 0x2f, 0x84,
            0x53, 0xd1, 0x00, 0xed, 0x20, 0xfc, 0xb1, 0x5b, 0x6a, 0xcb, 0xbe, 0x39, 0x4a, 0x4c, 0x58, 0xcf,
            0xd0, 0xef, 0xaa, 0xfb, 0x43, 0x4d, 0x33, 0x85, 0x45, 0xf9, 0x02, 0x7f, 0x50, 0x3c, 0x9f, 0xa8,
            0x51, 0xa3, 0x40, 0x8f, 0x92, 0x9d, 0x38, 0xf5, 0xbc, 0xb6, 0xda, 0x21, 0x10, 0xff, 0xf3, 0xd2,
            0xcd, 0x0c, 0x13, 0xec, 0x5f, 0x97, 0x44, 0x17, 0xc4, 0xa7, 0x7e, 0x3d, 0x64, 0x5d, 0x19, 0x73,
            0x60, 0x81, 0x4f, 0xdc, 0x22, 0x2a, 0x90, 0x88, 0x46, 0xee, 0xb8, 0x14, 0xde, 0x5e, 0x0b, 0xdb,
            0xe0, 0x32, 0x3a, 0x0a, 0x49, 0x06, 0x24, 0x5c, 0xc2, 0xd3, 0xac, 0x62, 0x91, 0x95, 0xe4, 0x79,
            0xe7, 0xc8, 0x37, 0x6d, 0x8d, 0xd5, 0x4e, 0xa9, 0x6c, 0x56, 0xf4, 0xea, 0x65, 0x7a, 0xae, 0x08,
            0xba, 0x78, 0x25, 0x2e, 0x1c, 0xa6, 0xb4, 0xc6, 0xe8, 0xdd, 0x74, 0x1f, 0x4b, 0xbd, 0x8b, 0x8a,
            0x70, 0x3e, 0xb5, 0x66, 0x48, 0x03, 0xf6, 0x0e, 0x61, 0x35, 0x57, 0xb9, 0x86, 0xc1, 0x1d, 0x9e,
            0xe1, 0xf8, 0x98, 0x11, 0x69, 0xd9, 0x8e, 0x94, 0x9b, 0x1e, 0x87, 0xe9, 0xce, 0x55, 0x28, 0xdf,
            0x8c, 0xa1, 0x89, 0x0d, 0xbf, 0xe6, 0x42, 0x68, 0x41, 0x99, 0x2d, 0x0f, 0xb0, 0x54, 0xbb, 0x16
    };

    //матрица замен байтов, используется при расшифровке в методе subBytes()
    private int[] invSbox = {
            0x52, 0x09, 0x6a, 0xd5, 0x30, 0x36, 0xa5, 0x38, 0xbf, 0x40, 0xa3, 0x9e, 0x81, 0xf3, 0xd7, 0xfb,
            0x7c, 0xe3, 0x39, 0x82, 0x9b, 0x2f, 0xff, 0x87, 0x34, 0x8e, 0x43, 0x44, 0xc4, 0xde, 0xe9, 0xcb,
            0x54, 0x7b, 0x94, 0x32, 0xa6, 0xc2, 0x23, 0x3d, 0xee, 0x4c, 0x95, 0x0b, 0x42, 0xfa, 0xc3, 0x4e,
            0x08, 0x2e, 0xa1, 0x66, 0x28, 0xd9, 0x24, 0xb2, 0x76, 0x5b, 0xa2, 0x49, 0x6d, 0x8b, 0xd1, 0x25,
            0x72, 0xf8, 0xf6, 0x64, 0x86, 0x68, 0x98, 0x16, 0xd4, 0xa4, 0x5c, 0xcc, 0x5d, 0x65, 0xb6, 0x92,
            0x6c, 0x70, 0x48, 0x50, 0xfd, 0xed, 0xb9, 0xda, 0x5e, 0x15, 0x46, 0x57, 0xa7, 0x8d, 0x9d, 0x84,
            0x90, 0xd8, 0xab, 0x00, 0x8c, 0xbc, 0xd3, 0x0a, 0xf7, 0xe4, 0x58, 0x05, 0xb8, 0xb3, 0x45, 0x06,
            0xd0, 0x2c, 0x1e, 0x8f, 0xca, 0x3f, 0x0f, 0x02, 0xc1, 0xaf, 0xbd, 0x03, 0x01, 0x13, 0x8a, 0x6b,
            0x3a, 0x91, 0x11, 0x41, 0x4f, 0x67, 0xdc, 0xea, 0x97, 0xf2, 0xcf, 0xce, 0xf0, 0xb4, 0xe6, 0x73,
            0x96, 0xac, 0x74, 0x22, 0xe7, 0xad, 0x35, 0x85, 0xe2, 0xf9, 0x37, 0xe8, 0x1c, 0x75, 0xdf, 0x6e,
            0x47, 0xf1, 0x1a, 0x71, 0x1d, 0x29, 0xc5, 0x89, 0x6f, 0xb7, 0x62, 0x0e, 0xaa, 0x18, 0xbe, 0x1b,
            0xfc, 0x56, 0x3e, 0x4b, 0xc6, 0xd2, 0x79, 0x20, 0x9a, 0xdb, 0xc0, 0xfe, 0x78, 0xcd, 0x5a, 0xf4,
            0x1f, 0xdd, 0xa8, 0x33, 0x88, 0x07, 0xc7, 0x31, 0xb1, 0x12, 0x10, 0x59, 0x27, 0x80, 0xec, 0x5f,
            0x60, 0x51, 0x7f, 0xa9, 0x19, 0xb5, 0x4a, 0x0d, 0x2d, 0xe5, 0x7a, 0x9f, 0x93, 0xc9, 0x9c, 0xef,
            0xa0, 0xe0, 0x3b, 0x4d, 0xae, 0x2a, 0xf5, 0xb0, 0xc8, 0xeb, 0xbb, 0x3c, 0x83, 0x53, 0x99, 0x61,
            0x17, 0x2b, 0x04, 0x7e, 0xba, 0x77, 0xd6, 0x26, 0xe1, 0x69, 0x14, 0x63, 0x55, 0x21, 0x0c, 0x7d
    };

    //матриза замен степеней числа 3, на соответствующие им числа в поле Галуа
    private int[] expGF = {
            0x01, 0x03, 0x05, 0x0f, 0x11, 0x33, 0x55, 0xff, 0x1a, 0x2e, 0x72, 0x96, 0xa1, 0xf8, 0x13, 0x35,
            0x5f, 0xe1, 0x38, 0x48, 0xd8, 0x73, 0x95, 0xa4, 0xf7, 0x02, 0x06, 0x0a, 0x1e, 0x22, 0x66, 0xaa,
            0xe5, 0x34, 0x5c, 0xe4, 0x37, 0x59, 0xeb, 0x26, 0x6a, 0xbe, 0xd9, 0x70, 0x90, 0xab, 0xe6, 0x31,
            0x53, 0xf5, 0x04, 0x0c, 0x14, 0x3c, 0x44, 0xcc, 0x4f, 0xd1, 0x68, 0xb8, 0xd3, 0x6e, 0xb2, 0xcd,
            0x4c, 0xd4, 0x67, 0xa9, 0xe0, 0x3b, 0x4d, 0xd7, 0x62, 0xa6, 0xf1, 0x08, 0x18, 0x28, 0x78, 0x88,
            0x83, 0x9e, 0xb9, 0xd0, 0x6b, 0xbd, 0xdc, 0x7f, 0x81, 0x98, 0xb3, 0xce, 0x49, 0xdb, 0x76, 0x9a,
            0xb5, 0xc4, 0x57, 0xf9, 0x10, 0x30, 0x50, 0xf0, 0x0b, 0x1d, 0x27, 0x69, 0xbb, 0xd6, 0x61, 0xa3,
            0xfe, 0x19, 0x2b, 0x7d, 0x87, 0x92, 0xad, 0xec, 0x2f, 0x71, 0x93, 0xae, 0xe9, 0x20, 0x60, 0xa0,
            0xfb, 0x16, 0x3a, 0x4e, 0xd2, 0x6d, 0xb7, 0xc2, 0x5d, 0xe7, 0x32, 0x56, 0xfa, 0x15, 0x3f, 0x41,
            0xc3, 0x5e, 0xe2, 0x3d, 0x47, 0xc9, 0x40, 0xc0, 0x5b, 0xed, 0x2c, 0x74, 0x9c, 0xbf, 0xda, 0x75,
            0x9f, 0xba, 0xd5, 0x64, 0xac, 0xef, 0x2a, 0x7e, 0x82, 0x9d, 0xbc, 0xdf, 0x7a, 0x8e, 0x89, 0x80,
            0x9b, 0xb6, 0xc1, 0x58, 0xe8, 0x23, 0x65, 0xaf, 0xea, 0x25, 0x6f, 0xb1, 0xc8, 0x43, 0xc5, 0x54,
            0xfc, 0x1f, 0x21, 0x63, 0xa5, 0xf4, 0x07, 0x09, 0x1b, 0x2d, 0x77, 0x99, 0xb0, 0xcb, 0x46, 0xca,
            0x45, 0xcf, 0x4a, 0xde, 0x79, 0x8b, 0x86, 0x91, 0xa8, 0xe3, 0x3e, 0x42, 0xc6, 0x51, 0xf3, 0x0e,
            0x12, 0x36, 0x5a, 0xee, 0x29, 0x7b, 0x8d, 0x8c, 0x8f, 0x8a, 0x85, 0x94, 0xa7, 0xf2, 0x0d, 0x17,
            0x39, 0x4b, 0xdd, 0x7c, 0x84, 0x97, 0xa2, 0xfd, 0x1c, 0x24, 0x6c, 0xb4, 0xc7, 0x52, 0xf6, 0x01
    };

    //матриза замен чисел, на соответствующие им степени 3 в поле Галуа
    private int[] logGF = {
            0x00, 0x00, 0x19, 0x01, 0x32, 0x02, 0x1a, 0xc6, 0x4b, 0xc7, 0x1b, 0x68, 0x33, 0xee, 0xdf, 0x03,
            0x64, 0x04, 0xe0, 0x0e, 0x34, 0x8d, 0x81, 0xef, 0x4c, 0x71, 0x08, 0xc8, 0xf8, 0x69, 0x1c, 0xc1,
            0x7d, 0xc2, 0x1d, 0xb5, 0xf9, 0xb9, 0x27, 0x6a, 0x4d, 0xe4, 0xa6, 0x72, 0x9a, 0xc9, 0x09, 0x78,
            0x65, 0x2f, 0x8a, 0x05, 0x21, 0x0f, 0xe1, 0x24, 0x12, 0xf0, 0x82, 0x45, 0x35, 0x93, 0xda, 0x8e,
            0x96, 0x8f, 0xdb, 0xbd, 0x36, 0xd0, 0xce, 0x94, 0x13, 0x5c, 0xd2, 0xf1, 0x40, 0x46, 0x83, 0x38,
            0x66, 0xdd, 0xfd, 0x30, 0xbf, 0x06, 0x8b, 0x62, 0xb3, 0x25, 0xe2, 0x98, 0x22, 0x88, 0x91, 0x10,
            0x7e, 0x6e, 0x48, 0xc3, 0xa3, 0xb6, 0x1e, 0x42, 0x3a, 0x6b, 0x28, 0x54, 0xfa, 0x85, 0x3d, 0xba,
            0x2b, 0x79, 0x0a, 0x15, 0x9b, 0x9f, 0x5e, 0xca, 0x4e, 0xd4, 0xac, 0xe5, 0xf3, 0x73, 0xa7, 0x57,
            0xaf, 0x58, 0xa8, 0x50, 0xf4, 0xea, 0xd6, 0x74, 0x4f, 0xae, 0xe9, 0xd5, 0xe7, 0xe6, 0xad, 0xe8,
            0x2c, 0xd7, 0x75, 0x7a, 0xeb, 0x16, 0x0b, 0xf5, 0x59, 0xcb, 0x5f, 0xb0, 0x9c, 0xa9, 0x51, 0xa0,
            0x7f, 0x0c, 0xf6, 0x6f, 0x17, 0xc4, 0x49, 0xec, 0xd8, 0x43, 0x1f, 0x2d, 0xa4, 0x76, 0x7b, 0xb7,
            0xcc, 0xbb, 0x3e, 0x5a, 0xfb, 0x60, 0xb1, 0x86, 0x3b, 0x52, 0xa1, 0x6c, 0xaa, 0x55, 0x29, 0x9d,
            0x97, 0xb2, 0x87, 0x90, 0x61, 0xbe, 0xdc, 0xfc, 0xbc, 0x95, 0xcf, 0xcd, 0x37, 0x3f, 0x5b, 0xd1,
            0x53, 0x39, 0x84, 0x3c, 0x41, 0xa2, 0x6d, 0x47, 0x14, 0x2a, 0x9e, 0x5d, 0x56, 0xf2, 0xd3, 0xab,
            0x44, 0x11, 0x92, 0xd9, 0x23, 0x20, 0x2e, 0x89, 0xb4, 0x7c, 0xb8, 0x26, 0x77, 0x99, 0xe3, 0xa5,
            0x67, 0x4a, 0xed, 0xde, 0xc5, 0x31, 0xfe, 0x18, 0x0d, 0x63, 0x8c, 0x80, 0xc0, 0xf7, 0x70, 0x07
    };

    //режим работы методов (шифрование/расшифровка)
    private enum Mode {
        ENCRYPT, DECRYPT
    }

    //хранит текущий режим
    private Mode mode;

    //матрица над которой будут производиться преобразования
    private byte[][] state = new byte[4][NB];

    //ссыка на класс для генерации раундовых ключей
    private Key keyObj;

    /**
     * @param secretKey Секретный ключ для шифрования данных
     */
    public CipherBlockAES(byte[] secretKey) {
        keyObj = new Key(secretKey);
        keyObj.keyExpansion();
    }

    /**
     * Шифрует входную последовательнойть байт и возвращает рузультат.
     *
     * @param plainText Исходный (открытый) массив данных
     * @return Зашифрованный блок данных
     */
    public byte[] encryptBlock(byte[] plainText) {
        mode = Mode.ENCRYPT; //encryption mode

        fillState(plainText);

        //---------------Инициализация--------------------------
        //long start = System.currentTimeMillis();
        addRoundKey();

        //----------------nr-1 раундов--------------------------
        for (int i = 0; i < NR - 1; i++) {
            subBytes();
            shiftRows();
            mixColumns();
            addRoundKey();
        }

        //------------------Последний раунд---------------------
        subBytes();
        shiftRows();
        addRoundKey();

        return output();
    }

    /**
     * Расшифровывает входную последовательнойть байт и возвращает рузультат.
     *
     * @param cipherText Массив хранящий зашифрованные байты, в методе <code>encrypt()</code>
     * @return Расшифрованный массив данных
     */
    public byte[] decryptBlock(byte[] cipherText) {
        mode = Mode.DECRYPT; //decryption mode

        fillState(cipherText);

        //---------------Инициализация--------------------------
        addRoundKey();

        //----------------nr-1 раундов--------------------------
        for (int i = NR - 1; i > 0; i--) {
            shiftRows();
            subBytes();
            addRoundKey();
            mixColumns();
        }

        //-----------------Последний раунд---------------------
        shiftRows();
        subBytes();
        addRoundKey();

        return output();
    }

    //-----------------------------------------------------------------------------------------------------------
    //                                      Методы для шифрования/расшифрования
    //-----------------------------------------------------------------------------------------------------------

    //заменяет значения в state на соответствующие из таблицы sbox
    private void subBytes() {
        int row, column;
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < NB; c++) {
                row = (state[r][c] & 0xf0) >> 4;
                column = (state[r][c] & 0x0f);

                if (mode == Mode.ENCRYPT)
                    state[r][c] = (byte) sbox[16 * row + column];
                else
                    state[r][c] = (byte) invSbox[16 * row + column];
            }
        }
    }

    //Сдвигает элементы в строках state
    private void shiftRows() {
        for (int r = 1; r < 4; r++) {
            if (mode == Mode.ENCRYPT)
                shiftArray(state[r], -r); //сдвиг влево
            else
                shiftArray(state[r], r); //сдвиг вправо
        }
    }

    /* Умножает каждый столбец с state на соответсующие коэффициенты,
     * умножение производится по правилам умножения в поле Галуа (GF) */
    private void mixColumns() {
        byte s0, s1, s2, s3;
        for (int c = 0; c < NB; c++) {
            if (mode == Mode.ENCRYPT) {
                s0 = (byte) (multiply(state[0][c], 0x02) ^ multiply(state[1][c], 0x03) ^ state[2][c] ^ state[3][c]);
                s1 = (byte) (state[0][c] ^ multiply(state[1][c], 0x02) ^ multiply(state[2][c], 0x03) ^ state[3][c]);
                s2 = (byte) (state[0][c] ^ state[1][c] ^ multiply(state[2][c], 0x02) ^ multiply(state[3][c], 0x03));
                s3 = (byte) (multiply(state[0][c], 0x03) ^ state[1][c] ^ state[2][c] ^ multiply(state[3][c], 0x02));
            } else {
                s0 = (byte) (multiply(state[0][c], 0x0e) ^ multiply(state[1][c], 0x0b) ^ multiply(state[2][c], 0x0d) ^ multiply(state[3][c], 0x09));
                s1 = (byte) (multiply(state[0][c], 0x09) ^ multiply(state[1][c], 0x0e) ^ multiply(state[2][c], 0x0b) ^ multiply(state[3][c], 0x0d));
                s2 = (byte) (multiply(state[0][c], 0x0d) ^ multiply(state[1][c], 0x09) ^ multiply(state[2][c], 0x0e) ^ multiply(state[3][c], 0x0b));
                s3 = (byte) (multiply(state[0][c], 0x0b) ^ multiply(state[1][c], 0x0d) ^ multiply(state[2][c], 0x09) ^ multiply(state[3][c], 0x0e));
            }

            state[0][c] = s0;
            state[1][c] = s1;
            state[2][c] = s2;
            state[3][c] = s3;
        }
    }


    //счетчик раундов
    int countRound = 0;

    /* производит операцию XOR между state и roundKey
     * roundKey получается из secretKey в методе keyExpantion внутреннего класса Key */
    private void addRoundKey() {
        byte[][] roundKey;
        if (mode == Mode.ENCRYPT) {
            roundKey = keyObj.getRoundKey(countRound++);
        } else {
            roundKey = keyObj.getRoundKey((NR - countRound++));
        }

        if (countRound == 11) countRound = 0;

        for (int c = 0; c < NB; c++) {
            for (int r = 0; r < 4; r++) {
                state[r][c] = (byte) (state[r][c] ^ roundKey[r][c]);
            }
        }
    }

    //Внутренний класс, генерирует раундовые ключи
    private class Key {
        //храниц все ключи для всех рауднов
        byte[][] keySchedule = new byte[4][NB * (NR + 1)]; //матрица раудовых ключей

        //используется для столбцов номера которых кратны nk
        int[][] rcon = {
                {0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80, 0x1b, 0x36},
                {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00},
                {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00},
                {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}
        };

        Key(byte[] secretKey) {
            for (int r = 0; r < 4; r++) {
                for (int c = 0; c < NB; c++) {
                    keySchedule[r][c] = secretKey[r + 4 * c];
                }
            }
        }

        //хранит значение столбца из массива keySchedule для дальнейших преобразований
        byte[] temp = new byte[4];

        //генерирует все раундовые ключи на основе начального ключа secretKey (передается в конструктор)
        void keyExpansion() {
            // index - указатель на текущий столбец
            for (int index = NK; index < keySchedule[0].length; index++) {
                if (index % NK == 0) {
                    temp = getColumn(index - 1); //возвращяет предыдущий столбец таблицы keySchedule
                    rotWord(); //сдвиг на один элемент
                    subWord(); //замена байтов значениями из таллицы sbox
                    for (int r = 0; r < 4; r++) {
                        keySchedule[r][index] = (byte) (temp[r] ^ keySchedule[r][index - NK] ^ rcon[r][index / NK - 1]);
                    }
                } else {
                    for (int r = 0; r < 4; r++) {
                        keySchedule[r][index] = (byte) (keySchedule[r][index - 1] ^ keySchedule[r][index - NK]);
                    }
                }
            }
        }

        //восвращает столбец под указанным номером (индексом)
        byte[] getColumn(int index) {
            byte[] column = new byte[4];
            for (int i = 0; i < 4; i++) {
                column[i] = keySchedule[i][index];
            }
            return column;
        }

        //возвращает раундовый ключ roundKey
        byte[][] getRoundKey(int startColumn) {
            byte[][] block = new byte[4][NB];
            for (int r = 0; r < 4; r++)
                for (int c = 0; c < NK; c++)
                    block[r][c] = keySchedule[r][startColumn * NB + c];

            return block;
        }

        //осуществляет сдвиг элементов массива temp влево на один элемент
        void rotWord() {
            shiftArray(temp, -1);
        }

        //заменяет элементы массива temp на соответствующие из таблицы sbox
        void subWord() {
            int row, column;
            for (int r = 0; r < 4; r++) {
                row = (temp[r] & 0xf0) >> 4;
                column = (temp[r] & 0x0f);

                temp[r] = (byte) sbox[16 * row + column];
            }
        }
    }

    //---------------------------------------------------------------------------------------
    //                               Вспомогательные методы
    //---------------------------------------------------------------------------------------

    //заносит массив, переданный методам encrypt()/decrypt() в массив state[][]
    private void fillState(byte[] bytes) {
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < NB; c++) {
                state[r][c] = bytes[r + 4 * c];
            }
        }
    }

    //Сдвигает элементы массива array вправо/влево на n элементов
    private void shiftArray(byte[] array, int n) {
        byte[] temp;
        if (n <= 0) { // сдвиг влево
            n = -n;
            temp = Arrays.copyOf(array, n);

            for (int c = n; c < array.length; c++)
                array[c - n] = array[c];

            int index = 0;
            for (int c = array.length - n; c < array.length; c++)
                array[c] = temp[index++];

        } else { //сдвиг вправо
            temp = Arrays.copyOfRange(array, array.length - n, array.length);

            for (int c = (array.length - 1) - n; c >= 0; c--)
                array[c + n] = array[c];

            int index = 0;
            for (int c = 0; c < n; c++)
                array[c] = temp[index++];
        }
    }

    //Выполняет умножение чисел в поле Галуа
    private byte multiply(int a, int b) {
        int degreeA, degreeB;
        int row, column;

        if (a == 0x00 || b == 0x00) return 0x00;

        row = (a & 0xf0) >> 4;
        column = (a & 0x0f);
        degreeA = logGF[16*row + column];

        row = (b & 0xf0) >> 4;
        column = (b & 0x0f);
        degreeB = logGF[16*row + column];

        return (byte) expGF[((degreeA + degreeB) % 255)];
    }

    //преобразует матрицу state к одномерному массиву
    private byte[] output() {
        byte[] outArr = new byte[4 * NB];
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < NB; c++) {
                outArr[r + 4 * c] = state[r][c];
            }
        }
        return outArr;
    }
}