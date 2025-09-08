package com.example.Login_System2.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class WebController {

    /**
     * Ana sayfa - Dashboard'a yönlendir
     */
    @GetMapping("/")
    public String homePage() {
        return "redirect:/dashboard";
    }

    /**
     * Login sayfası (GET)
     */
    @GetMapping("/login")
    public String loginPage(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            @RequestParam(value = "email", required = false) String email,
            Model model) {
        
        if (error != null) {
            model.addAttribute("error", "Geçersiz e-posta veya şifre!");
        }
        
        if (logout != null) {
            model.addAttribute("message", "Başarıyla çıkış yaptınız.");
        }
        
        if (email != null) {
            model.addAttribute("email", email);
        }
        
        return "index";
    }

    /**
     * Login form submission (POST) - JavaScript tarafından handle edilecek
     * Bu endpoint form fallback için
     */
    @PostMapping("/login")
    public String handleLogin(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("loginType") String loginType,
            RedirectAttributes redirectAttributes) {
        
        // JavaScript API çağrısı başarısız olursa bu endpoint kullanılır
        // Normal durumda /api/auth/login kullanılacak
        redirectAttributes.addAttribute("email", email);
        redirectAttributes.addAttribute("error", "true");
        return "redirect:/login";
    }

    /**
     * Register sayfası (GET)
     */
    @GetMapping("/register")
    public String registerPage(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "success", required = false) String success,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "surname", required = false) String surname,
            @RequestParam(value = "email", required = false) String email,
            Model model) {
        
        if (error != null) {
            model.addAttribute("error", "Kayıt işlemi başarısız! Lütfen bilgilerinizi kontrol edin.");
        }
        
        if (success != null) {
            model.addAttribute("message", "Kayıt başarılı! Giriş yapabilirsiniz.");
        }
        
        // Form verilerini geri doldur
        if (name != null) model.addAttribute("name", name);
        if (surname != null) model.addAttribute("surname", surname);
        if (email != null) model.addAttribute("email", email);
        
        return "register";
    }

    /**
     * Register form submission (POST) - JavaScript tarafından handle edilecek
     * Bu endpoint form fallback için
     */
    @PostMapping("/register")
    public String handleRegister(
            @RequestParam("name") String name,
            @RequestParam("surname") String surname,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            @RequestParam("role") String role,
            RedirectAttributes redirectAttributes) {
        
        // Basit validasyonlar
        if (!password.equals(confirmPassword)) {
            redirectAttributes.addAttribute("name", name);
            redirectAttributes.addAttribute("surname", surname);
            redirectAttributes.addAttribute("email", email);
            redirectAttributes.addAttribute("error", "true");
            return "redirect:/register";
        }
        
        // JavaScript API çağrısı başarısız olursa bu endpoint kullanılır
        // Normal durumda /api/auth/register kullanılacak
        redirectAttributes.addAttribute("error", "true");
        return "redirect:/register";
    }

    @GetMapping("/admin")
    public String adminPage() {
        return "admin";
    }

    /**
     * Dashboard sayfası
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Token kontrolü JavaScript tarafından yapılacak
        return "dashboard";
    }

    /**
     * Manager paneli sayfası (şimdilik basit placeholder)
     */
    @GetMapping("/manager")
    public String managerPage() {
        return "dashboard"; // İleride ayrı manager.html yapılabilir
    }

    @GetMapping("/logs")
    public String logsPage() {
        return "logs";
    }

    @GetMapping("/notifications")
    public String notificationsPage() {
        return "notifications";
    }

    /**
     * Görevler sayfası
     */
    @GetMapping("/tasks")
    public String tasksPage(
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "priority", required = false) String priority,
            @RequestParam(value = "search", required = false) String search,
            Model model) {
        
        // Filtreleme parametrelerini modele ekle
        if (status != null) model.addAttribute("selectedStatus", status);
        if (priority != null) model.addAttribute("selectedPriority", priority);
        if (search != null) model.addAttribute("searchTerm", search);
        
        return "tasks";
    }

    /**
     * Profil sayfası
     */
    @GetMapping("/profile")
    public String profilePage(
            @RequestParam(value = "updated", required = false) String updated,
            @RequestParam(value = "error", required = false) String error,
            Model model) {
        
        if (updated != null) {
            model.addAttribute("message", "Profil başarıyla güncellendi!");
        }
        
        if (error != null) {
            model.addAttribute("error", "Profil güncellenirken hata oluştu!");
        }
        
        return "profile";
    }


    @GetMapping("/logout")
    public String logout() {
        // Token temizleme JavaScript tarafından yapılacak
        return "redirect:/login?logout=true";
    }


    @GetMapping("/error")
    public String errorPage(Model model) {
        model.addAttribute("errorMessage", "Bir hata oluştu. Lütfen tekrar deneyin.");
        return "error";
    }


    @GetMapping("/404")
    public String notFoundPage() {
        return "404";
    }


    @GetMapping("/403")
    public String forbiddenPage() {
        return "403";
    }

    @GetMapping("/about")
    public String aboutPage() {
        return "about";
    }


    @GetMapping("/help")
    public String helpPage() {
        return "help";
    }
}
