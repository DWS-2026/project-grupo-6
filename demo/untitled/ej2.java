import java.util.Scanner;
import java.util.regex.*;

class ej1 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String text = sc.nextLine();
        Pattern p = Pattern.compile("\\b(E[- ]?)?\\d{4}[- ]?[A-Z]{3}\\b");
        Matcher m = p.matcher(text);
        while(m.find()){
            System.out.println(m.group());
        }
    }
}

