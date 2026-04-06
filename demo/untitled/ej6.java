import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ej6 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String text = sc.nextLine();

        Pattern p = Pattern.compile("");
        Matcher m = p.matcher(text);

        StringBuffer result = new StringBuffer();

        while (m.find()) {
            String street = m.group(1);
            String number = m.group(2);
            String cp = m.group(3);

            String newDate = cp + "-" + street + "-" + number;

            m.appendReplacement(result, newDate);
        }

        m.appendTail(result);

        System.out.println(result.toString());
    }
}

