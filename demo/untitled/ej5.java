import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ej5 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        sc.useDelimiter("\\A");   //Reads all content
        String text = sc.nextLine();

        Pattern p = Pattern.compile("\\b(C\\/|Calle)\\s([A-ZÁÉÍÓÚÑ][a-záéíóúñüA-ZÁÉÍÓÚÑÜ]*),?\\s+(?:[Nn]º?\\s?)?(\\d+),\\s+(\\d{5})\\b");
        Matcher m = p.matcher(text);

        while (m.find()) {
            String street = m.group(2);
            String number = m.group(3);
            String cp = m.group(4);

            String newDir = cp + "-" + street + "-" + number;
            System.out.println(newDir);
        }
    }
}
