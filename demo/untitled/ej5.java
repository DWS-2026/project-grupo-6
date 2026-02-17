import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ej3 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String text = sc.nextLine();

        Pattern p = Pattern.compile("(\\b\\d{4}\\b)-(\\d{2})-(\\d{2}\\b)");
        Matcher m = p.matcher(text);
        //String text: 2024-09-09
        StringBuffer result = new StringBuffer();

        while (m.find()) {
            String year = m.group(1);
            String month = m.group(2);
            String day = m.group(3);

            String newDate = day + "." + month + "." + year;

            m.appendReplacement(result, newDate);
        }

        m.appendTail(result);

        System.out.println(result.toString());
    }
}

