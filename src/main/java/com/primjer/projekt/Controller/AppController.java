package com.primjer.projekt.Controller;

import com.primjer.projekt.Model.Orders;
import com.primjer.projekt.Model.Product;
import com.primjer.projekt.Model.User;
import com.primjer.projekt.Repository.OrdersRepository;
import com.primjer.projekt.Repository.ProductRepository;
import com.primjer.projekt.Repository.UserRepository;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
public class AppController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrdersRepository ordersRepository;

    @GetMapping("")
    public String viewHomePage(Model model) {
        List<Product> listProducts = productRepository.findAll();
        model.addAttribute("listProducts",listProducts);
        return "index";
    }

    @PostMapping("")
    public ModelAndView showMyCamp(String name) {
        ModelAndView mav = new ModelAndView("index");
        List<Product> listProducts = productRepository.findByName(name);
        mav.addObject("listProducts",listProducts);
        return mav;
    }

    @GetMapping("/register")
    public String registerUser() {
        return "register";
    }

    @RequestMapping("/product/{id}")
    public ModelAndView showProduct(@PathVariable(name = "id") Long id) {
        ModelAndView mav = new ModelAndView("product");
        Product listProducts = productRepository.getById(id);
        mav.addObject("listProducts",listProducts);
        return mav;
    }

    @PostMapping("/process_register")
    public String processRegistration(User user) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);
        return "redirect:/";
    }

    @GetMapping("/add_product")
    public String addProduct() {
        return "add_product";
    }

    @RequestMapping(value = "/process_product", method = RequestMethod.POST)
    public String saveSlika(@RequestParam("image1") MultipartFile multipartFile, Product product) throws Exception {
        String extension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
        String fileName = product.getName(); //multipartFile.getOriginalFilename();
        String userDirectory = Paths.get("")
                .toAbsolutePath()
                .toString();
        String saveToSlika = userDirectory+"/src/main/resources/static/"+fileName+"."+extension;
        System.out.println(saveToSlika);
        product.setImage(fileName+"."+extension);
        multipartFile.transferTo(new File(saveToSlika));
        productRepository.save(product);
        return "redirect:/";
    }

    @PostMapping("/process_orders")
    public String processOrder(Orders orders) {
        ordersRepository.save(orders);
        return "redirect:/";
    }

    @RequestMapping(value = "/cart", method = RequestMethod.POST)
    public ModelAndView showCartPost(@RequestParam(name = "id") Integer id) {
        ModelAndView mav = new ModelAndView("cart");
        List<Orders> listOrders = ordersRepository.findCartOrders(id);
        mav.addObject("listOrders",listOrders);
        List<Product> listProducts = productRepository.findAll();
        mav.addObject("listProducts",listProducts);

        float totalPrice = 0;

        for(Orders singleOrder : listOrders){
            for(Product singleProduct : listProducts)
                if(singleProduct.id == singleOrder.productId)
                    totalPrice += singleOrder.quantity*singleProduct.price;
        }

        mav.addObject("totalPrice",totalPrice);

        return mav;
    }

    @PostMapping("/create_orders")
    public String createOrders(Integer id) {
        LocalDate date = LocalDate.now();
        List<Orders> listOrders = ordersRepository.findCartOrders(id);
        for(Orders singleOrder : listOrders){
            singleOrder.setDateCreated(date.toString());
            singleOrder.setCreated(true);
            ordersRepository.save(singleOrder);
        }
        return "redirect:/";
    }

    @RequestMapping(value = "/cart_created", method = RequestMethod.POST)
    public ModelAndView showCartCreated(@RequestParam(name = "id") Integer id) {
        ModelAndView mav = new ModelAndView("cart_created");
        List<Orders> listOrders = ordersRepository.findCartOrdersCreated(id);
        mav.addObject("listOrders",listOrders);
        List<Product> listProducts = productRepository.findAll();
        mav.addObject("listProducts",listProducts);

        float totalPrice = 0;

        for(Orders singleOrder : listOrders){
            for(Product singleProduct : listProducts)
                if(singleProduct.id == singleOrder.productId)
                    totalPrice += singleOrder.quantity*singleProduct.price;
        }

        mav.addObject("totalPrice",totalPrice);

        return mav;
    }

    @PostMapping("/complete_orders")
    public String completeOrders(Integer id) {
        LocalDate date = LocalDate.now();
        List<Orders> listOrders = ordersRepository.findCartOrdersCreated(id);
        for(Orders singleOrder : listOrders){
            singleOrder.setDateCompleted(date.toString());
            singleOrder.setCompleted(true);
            ordersRepository.save(singleOrder);
        }
        return "redirect:/";
    }

    @RequestMapping(value = "/cart_completed", method = RequestMethod.POST)
    public ModelAndView showCartCompleted(@RequestParam(name = "id") Integer id) {
        ModelAndView mav = new ModelAndView("cart_completed");
        List<Orders> listOrders = ordersRepository.findCartOrdersCompleted(id);
        mav.addObject("listOrders",listOrders);
        List<Product> listProducts = productRepository.findAll();
        mav.addObject("listProducts",listProducts);

        float totalPrice = 0;

        for(Orders singleOrder : listOrders){
            for(Product singleProduct : listProducts)
                if(singleProduct.id == singleOrder.productId)
                    totalPrice += singleOrder.quantity*singleProduct.price;
        }

        mav.addObject("totalPrice",totalPrice);

        return mav;
    }

    @PostMapping("/delete_orders")
    public ModelAndView deleteOrders(@RequestParam Long id, @RequestParam Long idorder, HttpServletRequest request) {
        ordersRepository.deleteById(idorder);
        request.setAttribute(
                View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);
        return new ModelAndView("redirect:/cart");
    }


}
