import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class HashGen {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println("Admin Hash: " + encoder.encode("AdminMars2026!"));
        System.out.println("Director Hash: " + encoder.encode("DirectorMars2026!"));
    }
}
