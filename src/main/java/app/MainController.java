package app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class MainController {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    private List<Product> newProducts = new ArrayList<>();

    private User loggedUser = new User();
    private boolean loggedIn;
    private Product tempProduct = new Product();

    @GetMapping("/")
    public String index(Model model) {
        if (loggedIn)
            model.addAttribute("user", loggedUser);
        else
            model.addAttribute("user", userRepository.findById(1).get());

        return "index.html";
    }

    @GetMapping("/login")
    public String login(Model model) {
        if (loggedIn) {
            model.addAttribute("user", userRepository.findById(loggedUser.getId()).get());
            if (loggedUser.isOwner())
                return "/owners";
            else
                return "profile.html";
        } else {
            model.addAttribute("user", userRepository.findById(1).get());
            return "login.html";
        }
    }

    @PostMapping("/login")
    public String loggedIn(HttpServletResponse response, @RequestParam String username, @RequestParam String password,
                           Model model) {
        if (loggedIn)
            model.addAttribute("user", loggedUser);
        else
            model.addAttribute("user", userRepository.findById(1).get());

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
        if (loggedIn)
            model.addAttribute("user", loggedUser);
        else
            model.addAttribute("user", userRepository.findById(1).get());

        return "register.html";
    }

    @PostMapping("/register")
    public void registerUser(HttpServletResponse response, @RequestParam String username,
                             @RequestParam String password) {
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

    @GetMapping("/owners")
    public String owners(Model productModel) {
        productModel.addAttribute("newProduct", productRepository);
        return "owners.html";
    }

    @GetMapping("/products")
    public String products(Model productModel, Model userModel) {
        productModel.addAttribute("products", productRepository.findAll());

        if (loggedIn)
            userModel.addAttribute("user", userRepository.findById(loggedUser.getId()).get());
        if (loggedUser.isOwner())
            return "products_owner.html";
        else
            userModel.addAttribute("user", userRepository.findById(1).get());
        return "products.html";
    }

    @PostMapping("/product/add")
    public @ResponseBody
    Product addProduct(@RequestBody Product product) {
        productRepository.save(product);
        newProducts.add(product);
        return product;
    }

    @GetMapping("/products/addtocart/{id}")
    public void addToCart(@PathVariable("id") int id, Model model, HttpServletResponse response) throws IOException {
        Product product = productRepository.findById(id).get();
        int stock = product.getStock();

        if (stock <= 0) {
            response.sendRedirect("/stock");
        } else {
            if (loggedIn) {
                User user = userRepository.findById(loggedUser.getId()).get();
                user.cart.add(product);
                product.setStock(stock - 1);
                userRepository.save(user);
                productRepository.save(product);
                response.sendRedirect("/products");
            } else
                response.sendRedirect("/forbid");
        }
    }

    @GetMapping("/cart/remove")
    public @ResponseBody
    Integer removeProduct(@RequestParam Integer id) {
        User user = userRepository.findById(loggedUser.getId()).get();
        Product product = productRepository.findById(id).get();
        int stock = product.getStock() + 1;
        product.setStock(stock);
        user.getCart().remove(product);
        userRepository.save(user);
        productRepository.save(product);
        return id;
    }

    @GetMapping("/cart")
    public String cart(Model model) {
        if (loggedIn) {
            model.addAttribute("user", userRepository.findById(loggedUser.getId()).get());
            if (!userRepository.findById(loggedUser.getId()).get().getCart().isEmpty()) {
                return "cart.html";
            } else {
                return "empty.html";
            }

        } else
            return "/forbid";
    }

    @GetMapping("/details/{id}")
    public String details(@PathVariable("id") int id, Model productModel, Model userModel) {
        userModel.addAttribute("user", loggedUser);
        productModel.addAttribute("product", productRepository.findById(id).get());
        return "details.html";
    }

    @GetMapping("/checkout")
    public String checkout(Model model, Model total) {
        model.addAttribute("user", userRepository.findById(loggedUser.getId()).get());

        List<Product> products = userRepository.findById(loggedUser.getId()).get().getCart();
        double sum = 0;

        for (Product product : products) {
            sum += product.getPrice();
        }

        total.addAttribute("total", sum);
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

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") int id, Model model) {
        model.addAttribute("product", productRepository.findById(id).get());

        if (!loggedUser.isOwner())
            return "forbid.html";
        else
            tempProduct = productRepository.findById(id).get();
        return "edit.html";
    }

    @PostMapping("/edit/product")
    public void editProduct(HttpServletResponse response, @RequestParam(required = false) String details,
                            @RequestParam(required = false) Double price, @RequestParam(required = false) int stock) {
        Product product = tempProduct;
        product.setDetails(details);
        product.setPrice(price);
        product.setStock(stock);
        productRepository.save(product);

        try {
            response.sendRedirect("/products");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/forbid")
    public String forbid() {
        return "forbid.html";
    }

    @GetMapping("/hide/product")
    public @ResponseBody
    Integer hideProduct(@RequestParam Integer id) {
        return id;
    }

    @GetMapping("/view/orders")
    public String viewOrder(Model model) {
        User guest = userRepository.findById(1).get();
        List<User> userList = userRepository.findAll();
        userList.remove(guest);

        model.addAttribute("users", userList);
        return "view_order.html";
    }

    @GetMapping("/orders/{id}")
    public String orders(@PathVariable("id") int id, Model model) {
        model.addAttribute("user", userRepository.findById(id).get());

        if (!loggedUser.isOwner())
            return "forbid.html";

        return "orders.html";
    }

    @GetMapping("/stock")
    public String stock() {
        return "stock.html";
    }

}
