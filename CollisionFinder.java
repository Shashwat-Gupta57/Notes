import java.util.HashMap;
import java.util.Map;

public class CollisionFinder {
    public static void main(String[] args) {
        Map<Long, String> resultMap = new HashMap<>();
        int rs = 0;
        for (int s = 1; s <= 100; s++){
            if(s%8 == 0  || s%3 == 0 || s%2 == 0 || s == 23 || s == 19){
                rs++;
                continue;
            }
            for (int c = 32; c <= 126; c++) { // Printable ASCII range
                long intermediate = (c * s) + (s * s * s) - (c * c * 5) + (s * 7);
                long result = (((intermediate * intermediate) - ((c * s + 17) * (s - 11) + 23) + ((s * s) * (c + 5)) + (s * (c * c)) + 13 * (s * c)) % 1114112);

                if (resultMap.containsKey(result)) {
                    System.out.println("Collision found: Character " + c + " with seed " + s + " and " + resultMap.get(result));
                } else {
                    resultMap.put(result, "Character " + c + " with seed " + s);
                }
            }
        }
    }
}
