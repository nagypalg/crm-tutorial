package com.vaadin.tutorial.crm.backend.service;

import com.vaadin.tutorial.crm.backend.entity.Company;
import com.vaadin.tutorial.crm.backend.entity.Contact;
import com.vaadin.tutorial.crm.backend.repository.CompanyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

@Service
@Slf4j
public class CompanyService {

    private CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public List<Company> findAll() {
        return companyRepository.findAll();
    }

    public List<Company> findAll(String filterText) {
        if(filterText == null || filterText.isEmpty()) {
            return companyRepository.findAll();
        } else  {
            return  companyRepository.search(filterText);
        }
    }

    public Map<String, Integer> getStats() {
        HashMap<String, Integer> stats = new HashMap<>();
        findAll().forEach(company ->
            stats.put(company.getName(), company.getEmployees().size()));
        return stats;
    }

    public void delete(Company company) {
        companyRepository.delete(company);
    }

    public void save(Company company) {
        if (company == null) {
            log.error(
                    "Company is null. Are you sure you have connected your form to the application?");
            return;
        }
        companyRepository.save(company);
    }
}
