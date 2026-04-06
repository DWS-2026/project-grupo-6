import java.util.Scanner;
import java.util.regex.*;

class ej1 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String text = sc.nextLine();
        //String text = "Estamos en el año 2022.";
        Pattern p = Pattern.compile("\\b\\d{4}\\b");
        Matcher m = p.matcher(text);
        while(m.find()){
            System.out.println(m.group());
        }
    }
}

