import java.util.StringTokenizer;

public class kk{
    public static void main(){
        String s = "H\u200CE\u200CL\u200CL\u200CO\u200C\u200D";
        StringTokenizer st = new StringTokenizer(s.substring(0,s.indexOf("\u200D")),"\u200C");
        String[] k = new String[st.countTokens()];
        for(int i = 0;st.hasMoreTokens();i++){
            k[i] = st.nextToken();
            System.out.print(k[i]);
        }
        if(k[0] == "H"){
            System.out.println("\n1");
        }else{
            System.out.println("\n0");
        }
    }
}