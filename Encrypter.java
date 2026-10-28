public class Encrypter{
    static byte seed = (byte)((Math.random()*(100-10))+1);
    public static String encrypt(String src){// return type is string
        StringBuffer sb = new StringBuffer(src);
        for(int i = 0;i < sb.length();i++){
            int c = (int)sb.charAt(i);
            c = (c*seed)-(c+seed);
            sb.setCharAt(i,(char)c);
        }
        return sb.toString();
    }
    public static void setSeed(byte s){
        if(s < 1){
            return;
        }
        seed = s;
    }
    public static void randomize(){
        seed = (byte)((Math.random()*(100-10))+1);
    }
    public static void test(){
        for(int i = 0;i < 10000;i++){
            if(seed < 10 || seed >  99){
                System.out.println("exception:"+seed);
            }
            seed = (byte)((Math.random()*(100-10))+1);
        }
    }
}