package org.niias.asrb.kn.util;

/**
 * Created at 31.10.12 by V.Zubchevskiy
 */
public class CryptHelper {
    private int LValid = 32;
    private int HValid = 126;

    public CryptHelper() {
    }

    // -----------------------------------------------------------
    private boolean ValidChar(char c) {
        return (((int) c >= LValid) && ((int) c <= HValid));

    }

    // -----------------------------------------------------------
    private char ShiftLeft(char c, int p) {
        char res;
        res = (char) (c + HValid - p);
        return ((int) res < LValid + HValid) ? (char) ((int) res - LValid + 1) : (char) ((int) res - HValid);
    }

    // -----------------------------------------------------------
    private char ShiftRight(char c, int p) {
        char res;
        res = (char) ((int) c + p);
        if ((int) res > HValid) res = (char) ((int) res - HValid - 1 + LValid);
        return res;
    }

    // -----------------------------------------------------------
    private int GetKey(int idUser, int num) {
        return 1967834 + idUser * 351 + 146 * num;
    }

    /**
     * Расшифровка строки
     *
     * @param s   String Строка
     * @param Key int Ключ
     * @return String
     */
    public String DecodeStr(String s, int Key) {
        int i;
        String res = "";
        int[] SplitKey = new int[4];
        if (s.length() == 0) return "";
        //    s=s.replace('ф',' ');
        s = s.replace('д', ' ');
        try {
            for (i = 0; i < s.length(); i++)
                if (!ValidChar(s.charAt(i))) throw (new Exception());
            for (i = 0; i < 4; i++)
                SplitKey[i] = (((Key >> i) * 8) & 0x000000FF) % (HValid - LValid + 1);
            res += ShiftLeft(s.charAt(0), SplitKey[0]);
            for (i = 1; i < s.length(); i++)
                res += ShiftRight(ShiftLeft(s.charAt(i), (SplitKey[i % 4])), (HValid - (int) s.charAt(i - 1)));
            res = res.replace(' ', 'д');
            return res;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Расшифровка строки
     *
     * @param s      String строка
     * @param idUser int ИД пользователя (IXU0.U_USERS)
     * @param num    int номер
     * @return String
     */
    public String DecodeStr(String s, int idUser, int num) {
        return DecodeStr(s, GetKey(idUser, num));
    }

    /**
     * Шифрование строки
     *
     * @param s   String Строка
     * @param Key int Ключ
     * @return String
     */
    public String EncodeStr(String s, int Key) {
        int i;
        String res = "";
        int[] SplitKey = new int[4];
        if (s.length() == 0) return "";
        s = s.replace('д', ' ');
        try {
            for (i = 0; i < s.length(); i++)
                if (!ValidChar(s.charAt(i))) throw (new Exception());
            for (i = 0; i < 4; i++)
                SplitKey[i] = (((Key >> i) * 8) & 0x000000FF) % (HValid - LValid + 1);
            res += ShiftRight(s.charAt(0), SplitKey[0]);
            for (i = 1; i < s.length(); i++)
                res += ShiftLeft(ShiftRight(s.charAt(i), (SplitKey[i % 4])), (HValid - (int) res.charAt(i - 1)));
            res = res.replace(' ', 'д');
            return res;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Шифрование строки
     *
     * @param s      String Строка
     * @param idUser int ИД пользователя (IXU0.U_USERS)
     * @param num    int номер
     * @return String
     */
    public String EncodeStr(String s, int idUser, int num) {
        return EncodeStr(s, GetKey(idUser, num));
    }

}
