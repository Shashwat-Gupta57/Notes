public class Decrypter{
    public static String decrypt(String src,byte seed){
        StringBuffer sb = new StringBuffer(src);
        for (int i = 0; i < sb.length(); i++) {
            int c = (int) sb.charAt(i);
            c = (c+seed)/(seed-1);
            //(c*seed)-(c+seed)  (32*10)-(32+10) = 278
            //c = (278+10)/(10-1) = 32
            //c = 288/9
            //c = 32
            sb.setCharAt(i, (char) c);
        }
        return sb.toString();
    }

    public static StringBuffer test(StringBuffer sb,byte seed){
        for (int i = 0; i < sb.length(); i++) {
            int c = (int) sb.charAt(i);
            c = (c+seed)/(seed-1);
            //(c*seed)-(c+seed)  (32*10)-(32+10) = 278
            //c = (278+10)/(10-1) = 32
            //c = 288/9
            //c = 32
            sb.setCharAt(i, (char) c);
        }
        return sb;
    }
}