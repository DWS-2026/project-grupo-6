import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ej4 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String text = sc.nextLine();

        String regex = "\\b(?:([a-z])\\.([a-z]{2,})\\.(\\d+)@alumnos\\.urjc\\.es|([a-z]+)\\.([a-z]+)@urjc\\.es)\\b";
        
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(text);

        while (m.find()) {

            if (m.group(1) != null) {
                String apellido = m.group(2);
                String anio = m.group(3);
                System.out.println("alumno " + apellido + " matriculado en " + anio);
            } 

            else if (m.group(4) != null) {
                String nombre = m.group(4);
                String apellidoProfe = m.group(5);
                System.out.println("profesor " + nombre + " apellido " + apellidoProfe);
            }
        }
    }
}