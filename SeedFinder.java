public class SeedFinder {
    public static void main(String[] args){
        for (int s = 1; s <= 1000; s++) {
            boolean valid = true;
            for (int c = 32; c <= 126; c++) { // ASCII printable characters range
                long intermediate = (c * s) + (s * s * s) - (c * c * 5) + (s * 7);
                long result = (((intermediate * intermediate) - ((c * s + 17) * (s - 11) + 23) + ((s * s) * (c + 5)) + (s * (c * c)) + 13 * (s * c)) % 1114112);
                System.out.println(c+" : "+s+" = "+result);
                if (result >= 1114111 || result == 0 || result < 0) {
                    valid = false;
                    break;
                }
            }
            if(valid) {
                System.out.println("Valid seed: " + s);
            }
        }
    }
}
