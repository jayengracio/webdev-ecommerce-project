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
        if (loggedIn) model.addAttribute("user", loggedUser);
        else model.addAttribute("user", userRepository.findById(1).get());

        return "index.html";
    }

    @GetMapping("/login")
    public String login(Model model) {
        if (loggedIn) {
            model.addAttribute("user", userRepository.findById(loggedUser.getId()).get());
            if (loggedUser.isOwner()) return "owners.html";
            else return "profile.html";
        }

        else {
            model.addAttribute("user", userRepository.findById(1).get());
            return "login.html";
        }
    }

    @PostMapping("/login")
    public String loggedIn(HttpServletResponse response, @RequestParam String username, @RequestParam String password, Model model) {
        if (loggedIn) model.addAttribute("user", loggedUser);
        else model.addAttribute("user", userRepository.findById(1).get());

        List<User> users = userRepository.findAll();
        
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                loggedUser = userRepository.findById(user.getId()).get();
                loggedIn = true;

                if (loggedUser == userRepository.findById(1).get())
                loggedIn = false;

                try {
                    response.sendRedirect("/");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return "loginfailure.html";
    }

    @GetMapping("/logout")
    public void logout(HttpServletResponse response) {
        loggedUser = new User();
        loggedIn = false;
        try {
            response.sendRedirect("/login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/register")
    public String register(Model model) {
        if (loggedIn) model.addAttribute("user", loggedUser);
        else model.addAttribute("user", userRepository.findById(1).get());

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
    public String products(Model productModel, Model userModel) {
        if (loggedIn) userModel.addAttribute("user", loggedUser);
        else userModel.addAttribute("user", userRepository.findById(1).get());

        productModel.addAttribute("products", productRepository.findAll());
        return "products.html";
    }

    @PostMapping("/product/add")
    public @ResponseBody Product addProduct(@RequestBody Product product) {
        productRepository.save(product);
        return product;
    }

    @GetMapping("/products/addtocart/{id}")
    public void addToCart(@PathVariable("id") int id, Model model, HttpServletResponse response) throws IOException {
        if (loggedIn) {
            Product product = productRepository.findById(id).get();
            User user = userRepository.findById(loggedUser.getId()).get();
            int stock = product.getStock()-1;
            user.cart.add(product);
            product.setStock(stock);
            userRepository.save(user);
            productRepository.save(product);
            response.sendRedirect("/products");
        } else response.sendRedirect("/products");
    }

    @GetMapping("/cart/remove")
    public @ResponseBody Integer removeProduct(@RequestParam Integer id) {
        User user = userRepository.findById(loggedUser.getId()).get();
        Product product = productRepository.findById(id).get();
        user.getCart().remove(product);
        userRepository.save(user);
        return id;
    }

    @GetMapping("/cart")
    public String cart(Model model) {
        if (loggedIn) {
            model.addAttribute("user", userRepository.findById(loggedUser.getId()).get());
            return "cart.html";
        } else return "cart.html";
    }

    @GetMapping("/details/{id}")
    public String details(@PathVariable("id") int id, Model productModel, Model userModel) {
        userModel.addAttribute("user", loggedUser);
        productModel.addAttribute("product", productRepository.findById(id).get());
        return "details.html";
    }

    @GetMapping("/checkout")
    public String checkout(Model model) {
        model.addAttribute("user", userRepository.findById(loggedUser.getId()).get());
        return "checkout.html";
    }

    @GetMapping("/purchase")
    public String purchase() {
        User user = userRepository.findById(loggedUser.getId()).get();
        List<Product> cart = user.getCart();
        user.getOrders().addAll(cart);
        user.getCart().clear();
        userRepository.save(user);
        return "purchase.html";
    }

    @GetMapping("/orders")
    public String orders(Model model) {
        model.addAttribute("user", userRepository.findById(loggedUser.getId()).get());
        return "orders.html";
    }
}
