import java.util.List;
import java.util.Scanner;

public class App {

    public static void main(String[] args) {
        Administrator.initializeBooks();

        Scanner scanner = new Scanner(System.in);
        boolean next = true;

        while (next) {

            User authenticatedUser = authenticateUser(scanner);
            if (authenticatedUser != null) {
                System.out.println("Inicio de sesión exitoso como " +
                        authenticatedUser.getClass().getSimpleName() + ": " + authenticatedUser.getNameUser());

                if (authenticatedUser instanceof Administrator) {
                    administratorMenu((Administrator) authenticatedUser, scanner);
                }

                else if (authenticatedUser instanceof Student) {
                    studentMenu((Student) authenticatedUser, scanner);
                }
                next = false;
            } else {
                System.out.println("Inicio de sesión fallido. Usuario no encontrado o contraseña incorrecta.");

            }
        }
        scanner.close();

    }

    private static User authenticateUser(Scanner scanner) {
        System.out.print("Ingrese nombre de usuario: ");
        String username = scanner.next();
        System.out.print("Ingrese contraseña: ");
        String password = scanner.next();

        if (Administrator.administrators.isEmpty()) {
            Administrator admin1 = new Administrator("Admin1", "1234");
            Administrator.addAdministrator(admin1);
        }

        for (Administrator admin : Administrator.administrators) {
            if (admin.getNameUser().equals(username) && admin.getPassword().equals(password)) {
                return admin;
            }
        }

        if (Student.students.isEmpty()) {
            Student student1 = new Student("MariaJose", "Maria1234");
            Student student2 = new Student("MarioJose", "Mario1234");
            Student.addStudent(student1);
            Student.addStudent(student2);

        }

        for (Student student : Student.students) {
            if (student.getNameUser().equals(username) && student.getPassword().equals(password)) {
                return student;
            }
        }

        return null;
    }

    private static void administratorMenu(Administrator admin, Scanner scanner) {
         boolean next = true;
        while (next) {
        System.out.println("Acciones del Administrador:");
        System.out.println("1. Agregar nuevo libro");
        System.out.println("2. Buscar libro por código");
        System.out.println("3. Salir");
        System.out.print("Seleccione una opción: ");

        int opcionAdmin = -1;

       

        try {
            opcionAdmin = scanner.nextInt();
        } catch (Exception e) {
            System.out.println("Entrada inválida. Por favor, ingrese un número.");
            
        }

        switch (opcionAdmin) {
            case 1:
                actionAddBook(admin, scanner);
                break;
            case 2:
                searchBookByCode(admin, scanner);
   
                break;
            case 3:
                next = false;
                break;
            default:
                System.out.println("Opción no válida");
                break;
        }
        }
    }

    private static void studentMenu(Student student, Scanner scanner) {

        boolean next = true;
        while (next) {
        System.out.println("Acciones del Estudiante:");
        System.out.println("1. Ver libros disponibles y hacer préstamo");
        System.out.println("2. Ver libros prestados");
        System.out.println("3. Salir");
        System.out.print("Seleccione una opción: ");

        int opcionEstudiante = -1;
        try {
            opcionEstudiante = scanner.nextInt(); 
            
        } catch (Exception e) {
            System.out.println("Entrada inválida. Por favor, ingrese un número.");
     
        }
        switch (opcionEstudiante) {
            case 1:
                showAvailableBooksAndBorrow(student, scanner);
                
                break;
            case 2:
                student.showBorrowedBooks();
               
                break;
            case 3: 
             next = false;
            break;
            
            default:
                System.out.println("Opción no válida");
                
               
                break;
        } 
    }
    }

    private static void actionAddBook(Administrator admin, Scanner scanner) {

        boolean next = true;
        while (next) {
            
        
        try {
            System.out.print("Ingrese el código: ");
            String code = scanner.next();
            System.out.print("Ingrese el título del libro: ");
            String title = scanner.next();
            System.out.print("Ingrese el autor del libro: ");
            String author = scanner.next();
            System.out.print("Ingrese la cantidad: ");
            int quantity = scanner.nextInt();

            Book newBook = new Book(title, author, code, quantity);
            admin.addNewBook(newBook);
            next = false;

        } catch (java.util.InputMismatchException e) {
            System.out.println("Error: Los espacios no son aceptados.");
            
            scanner.nextLine();
        }
    }
    }

    private static void searchBookByCode(Administrator admin, Scanner scanner) {

        System.out.print("Ingrese el código del libro a buscar: ");
        String codeToSearch = scanner.next();
        admin.searchBookByCode(codeToSearch);
    }

    private static void showAvailableBooksAndBorrow(Student student, Scanner scanner) {
        List<Book> availableBooks = student.getAvailableBooks();

        if (availableBooks.isEmpty()) {
            System.out.println("No hay libros disponibles en este momento. Por favor, regrese más tarde.");
        } else {

            student.showAvailableBooks();
            System.out.println("Escriba el código del libro: ");
            String codeToBorrow = scanner.next();
            student.borrowBookByCode(codeToBorrow,scanner);
        }
       
    }
}
