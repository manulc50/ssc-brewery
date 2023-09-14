package com.mlorenzo.brewery.web.controllers;

import lombok.RequiredArgsConstructor;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import com.mlorenzo.brewery.domain.Customer;
import com.mlorenzo.brewery.repositories.CustomerRepository;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@SessionAttributes("customer")
@Controller
@RequestMapping("/customers")
public class CustomerController {
    //ToDO: Add service
    private final CustomerRepository customerRepository;

    @RequestMapping("/find")
    public String findCustomers(Model model){
        model.addAttribute("customer", Customer.builder().build());
        return "customers/findCustomers";
    }

    // Esta anotación es más antigua que las anotaciones de seguridad @PreAuthorize y @PostAuthorize y tiene menos funcionalidades que éstas
    @Secured({"ROLE_ADMIN", "ROLE_CUSTOMER"})
    @GetMapping
    public String processFindFormReturnMany(Customer customer, BindingResult result, Model model){
        // find customers by name
        //ToDO: Add Service
        List<Customer> customers = customerRepository.findAllByCustomerNameLike("%" + customer.getCustomerName() + "%");
        if (customers.isEmpty()) {
            // no customers found
            result.rejectValue("customerName", "notFound", "not found");
            return "customers/findCustomers";
        }
        else if (customers.size() == 1) {
            // 1 customer found
            customer = customers.get(0);
            return "redirect:/customers/" + customer.getId();
        }
        else {
            // multiple customers found
            model.addAttribute("selections", customers);
            return "customers/customerList";
        }
    }
    
    @GetMapping("/{customerId}")
    public ModelAndView showCustomer(@PathVariable UUID customerId) {
        ModelAndView mav = new ModelAndView("customers/customerDetails");
        //ToDO: Add Service
        mav.addObject(customerRepository.findById(customerId).get());
        return mav;
    }

    @GetMapping("/new")
    public String initCreationForm(Model model) {
        model.addAttribute("customer", Customer.builder().apiKey(UUID.randomUUID()).build());
        return "customers/createOrUpdateCustomer";
    }

    @GetMapping("/{customerId}/edit")
    public String initUpdateCustomerForm(@PathVariable UUID customerId, Model model) {
    	Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
       if(optionalCustomer.isPresent())
          model.addAttribute("customer", optionalCustomer.get());
       return "customers/createOrUpdateCustomer";
   }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public String processCreationOrUpdationForm(@Valid Customer customer, BindingResult result, SessionStatus sessionStatus) {
    	if (result.hasErrors()) 
            return "beers/createOrUpdateCustomer";
        else {
            //ToDO: Add Service
            Customer savedCustomer =  customerRepository.save(customer);
        	sessionStatus.setComplete();
            return "redirect:/customers/" + savedCustomer.getId();
        }
    }

}
