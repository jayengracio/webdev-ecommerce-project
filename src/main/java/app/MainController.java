package app;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainController {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    private User loggedUser = new User();
    private boolean loggedIn;

    @GetMapping("/")
    public String index(Model model) {
        if (!loggedIn)
        return "index.html";

        model.addAttribute("user", loggedUser);
        return "home.html";
    }

    @GetMapping("/login")
    public String login() {
        return "login.html";
    }

    @PostMapping("/login")
    public String loggedIn(HttpServletResponse response, @RequestParam String username, @RequestParam String password, Model model) {
        List<User> users = userRepository.findAll();
        
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                loggedUser.setPassword(user.getPassword());
                loggedUser.setUsername(user.getUsername());
                loggedUser.setId(user.getId());

                loggedIn = true;
                try {
                    response.sendRedirect("/");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return "loginfailure.html";
    }

    @PostMapping("/logout")
    public void logout(HttpServletResponse response) {
        loggedUser = new User();
        loggedIn = false;
        try {
            response.sendRedirect("/login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/profile")
    public String profile() {
        return "profile.html";
    }

    @GetMapping("/owners")
    public String owners(){ return "owners.html";}

    @GetMapping("/register")
    public String register() {
        return "register.html";
    }

    @PostMapping("/register")
    public void registerUser(HttpServletResponse response, @RequestParam String username, @RequestParam String password) {
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);
        userRepository.save(newUser);

        try {
            response.sendRedirect("/");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @GetMapping("/products")
    public String products(Model model) {
        model.addAttribute("products", productRepository.findAll());
        return "products.html";
    }

    @PostMapping("/product/add")
    public @ResponseBody Product addProduct(@RequestBody Product product) {
        productRepository.save(product);
        return product;
    }

    @GetMapping("/cart")
    public String cart() {
        return "cart.html";
    }

    @GetMapping("/details/{id}")
    public String details(@PathVariable("id") int id, Model model) {
        model.addAttribute("product", productRepository.findById(id).get());
        return "details.html";
    }
}