package com.example.complaint_box.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.complaint_box.dto.ComplaintCreateDTO;
import com.example.complaint_box.dto.ComplaintStatusUpdateDTO;
import com.example.complaint_box.model.Category;
import com.example.complaint_box.model.Complaint;
import com.example.complaint_box.model.ComplaintStatus;
import com.example.complaint_box.model.Resident;
import com.example.complaint_box.model.Staff;
import com.example.complaint_box.model.StaffRole;
import com.example.complaint_box.service.CategoryService;
import com.example.complaint_box.service.ComplaintService;
import com.example.complaint_box.service.ResidentService;
import com.example.complaint_box.service.StaffService;

@Controller
public class WebViewController {

    private final ComplaintService complaintService;
    private final ResidentService residentService;
    private final CategoryService categoryService;
    private final StaffService staffService;

    public WebViewController(
            ComplaintService complaintService,
            ResidentService residentService,
            CategoryService categoryService,
            StaffService staffService) {
        this.complaintService = complaintService;
        this.residentService = residentService;
        this.categoryService = categoryService;
        this.staffService = staffService;
    }

    // ----------------- Dashboard -----------------
    @GetMapping("/")
    public String dashboard(
            @RequestParam(value = "filter", defaultValue = "all") String filter,
            Model model) {

        List<Complaint> allComplaints = complaintService.getAllComplaints();
        List<Complaint> openComplaints = complaintService.getOpenComplaintsSortedByAge();
        List<Complaint> overdueComplaints = complaintService.getOverdueComplaints();

        long totalCount = allComplaints.size();
        long openCount = allComplaints.stream().filter(c -> c.getStatus() == ComplaintStatus.OPEN).count();
        long inProgressCount = allComplaints.stream().filter(c -> c.getStatus() == ComplaintStatus.IN_PROGRESS).count();
        long resolvedCount = allComplaints.stream().filter(c -> c.getStatus() == ComplaintStatus.RESOLVED).count();
        long overdueCount = overdueComplaints.size();

        // Listing based on filter
        List<Complaint> displayComplaints;
        if ("open".equalsIgnoreCase(filter)) {
            displayComplaints = openComplaints;
        } else if ("overdue".equalsIgnoreCase(filter)) {
            displayComplaints = overdueComplaints;
        } else {
            displayComplaints = allComplaints;
        }

        model.addAttribute("filter", filter);
        model.addAttribute("complaints", displayComplaints);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("openCount", openCount);
        model.addAttribute("inProgressCount", inProgressCount);
        model.addAttribute("resolvedCount", resolvedCount);
        model.addAttribute("overdueCount", overdueCount);

        return "index";
    }

    // ----------------- Complaints -----------------
    @GetMapping("/complaints/new")
    public String newComplaintForm(Model model) {
        model.addAttribute("residents", residentService.getAllResidents());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "complaints/new";
    }

    @PostMapping("/complaints/new")
    public String submitComplaint(
            @RequestParam Long residentId,
            @RequestParam Long categoryId,
            @RequestParam String roomNumber,
            @RequestParam String description,
            RedirectAttributes redirectAttributes) {
        try {
            ComplaintCreateDTO dto = new ComplaintCreateDTO(residentId, categoryId, roomNumber, description);
            complaintService.createComplaint(dto);
            redirectAttributes.addFlashAttribute("successMessage", "Complaint submitted successfully!");
            return "redirect:/";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error submitting complaint: " + ex.getMessage());
            return "redirect:/complaints/new";
        }
    }

    @GetMapping("/complaints/{id}")
    public String complaintDetails(@PathVariable Long id, Model model) {
        Complaint complaint = complaintService.getComplaintById(id);
        model.addAttribute("complaint", complaint);
        model.addAttribute("allStaff", staffService.getAllStaff());
        return "complaints/details";
    }

    @PostMapping("/complaints/{id}/assign")
    public String assignStaff(
            @PathVariable Long id,
            @RequestParam Long staffId,
            RedirectAttributes redirectAttributes) {
        try {
            complaintService.assignStaff(id, staffId);
            redirectAttributes.addFlashAttribute("successMessage", "Staff member assigned successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/complaints/" + id;
    }

    @PostMapping("/complaints/{id}/status")
    public String updateStatus(
            @PathVariable Long id,
            @RequestParam ComplaintStatus status,
            @RequestParam(required = false) String remark,
            @RequestParam(required = false) Long actingStaffId,
            RedirectAttributes redirectAttributes) {
        try {
            ComplaintStatusUpdateDTO dto = new ComplaintStatusUpdateDTO(status, remark, actingStaffId);
            complaintService.updateComplaintStatus(id, dto);
            redirectAttributes.addFlashAttribute("successMessage", "Complaint status updated successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/complaints/" + id;
    }

    @PostMapping("/complaints/{id}/delete")
    public String deleteComplaint(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            complaintService.deleteComplaint(id);
            redirectAttributes.addFlashAttribute("successMessage", "Complaint deleted successfully.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/";
    }

    // ----------------- Residents -----------------
    @GetMapping("/residents")
    public String residentsPage(Model model) {
        model.addAttribute("residents", residentService.getAllResidents());
        return "residents/list";
    }

    @PostMapping("/residents/new")
    public String addResident(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String roomNumber,
            RedirectAttributes redirectAttributes) {
        try {
            Resident resident = new Resident(name, email, roomNumber);
            residentService.addResident(resident);
            redirectAttributes.addFlashAttribute("successMessage", "Resident added successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/residents";
    }

    @PostMapping("/residents/{id}/delete")
    public String deleteResident(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            residentService.deleteResident(id);
            redirectAttributes.addFlashAttribute("successMessage", "Resident removed.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/residents";
    }

    // ----------------- Categories -----------------
    @GetMapping("/categories")
    public String categoriesPage(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "categories/list";
    }

    @PostMapping("/categories/new")
    public String addCategory(
            @RequestParam String name,
            @RequestParam(required = false) String description,
            RedirectAttributes redirectAttributes) {
        try {
            Category category = new Category(name, description);
            categoryService.addCategory(category);
            redirectAttributes.addFlashAttribute("successMessage", "Category added successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/categories";
    }

    @PostMapping("/categories/{id}/delete")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("successMessage", "Category removed.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/categories";
    }

    // ----------------- Staff -----------------
    @GetMapping("/staff")
    public String staffPage(Model model) {
        model.addAttribute("staffList", staffService.getAllStaff());
        return "staff/list";
    }

    @PostMapping("/staff/new")
    public String addStaff(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam StaffRole role,
            RedirectAttributes redirectAttributes) {
        try {
            Staff staff = new Staff(name, email, role);
            staffService.addStaff(staff);
            redirectAttributes.addFlashAttribute("successMessage", "Staff member added successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/staff";
    }

    @PostMapping("/staff/{id}/delete")
    public String deleteStaff(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            staffService.deleteStaff(id);
            redirectAttributes.addFlashAttribute("successMessage", "Staff member removed.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/staff";
    }
}
